/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package utils;

/**
 *
 * @author rezaef
 */

import dao.SensorDAO;
import dao.WateringLogDAO;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.eclipse.paho.client.mqttv3.*;

public class MqttSubscriber {

    private static MqttClient client;

    // dedupe log AUTO agar tidak spam jika device publish status berulang
    private static volatile String lastAutoLoggedAction = null;

    public static synchronized void start() {
        try {
            if (client != null && client.isConnected()) return;

            client = new MqttClient(MqttConfig.BROKER_URI, MqttConfig.CLIENT_ID, null);

            MqttConnectOptions opt = new MqttConnectOptions();
            opt.setAutomaticReconnect(true);
            opt.setCleanSession(true);
            opt.setConnectionTimeout(10);

            if (MqttConfig.USERNAME != null && !MqttConfig.USERNAME.isEmpty()) {
                opt.setUserName(MqttConfig.USERNAME);
                opt.setPassword(MqttConfig.PASSWORD.toCharArray());
            }

            client.setCallback(new MqttCallback() {
                @Override public void connectionLost(Throwable cause) {
                    System.out.println("[MQTT] lost: " + cause);
                }

                @Override public void messageArrived(String topic, MqttMessage message) {
                    String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                    System.out.println("[MQTT] topic=" + topic + " payload=" + payload);

                    // mode otomatis (retained): ON/OFF
                    if (MqttConfig.TOPIC_AUTO_MODE.equals(topic)) {
                        AutoModeState.updateFromPayload(payload);
                        System.out.println("[MQTT] autoMode=" + AutoModeState.getMode());
                        return;
                    }

                    // status pompa dari device: ON/OFF
                    if (MqttConfig.TOPIC_PUMP_STATUS.equals(topic)) {
                        boolean retained = message.isRetained();
                        String action = parsePumpAction(payload);
                        if (action != null) {
                            PumpState.update(action);
                        } else {
                            // fallback: simpan mentah untuk debugging
                            PumpState.update(payload);
                        }

                        // AUTO: status dari device dianggap event watering -> log ke DB sebagai source=ESP
                        // Catatan: jangan insert log kalau message retained (biar restart server tidak bikin log palsu)
                        if (!retained && AutoModeState.isEnabled() && action != null && shouldLogAuto(action)) {
                            boolean ok = new WateringLogDAO().insert(
                                    "AUTO",
                                    action,
                                    "ESP",
                                    "auto via mqtt okra/pump/status"
                            );
                            System.out.println("[MQTT] auto log insert DB = " + ok);
                        }
                        return;
                    }

                    // sensor json
                    if (MqttConfig.TOPIC_SENSOR.equals(topic)) {
                        Map<String, String> j = SimpleJson.parseFlat(payload);

                        Double ph = SimpleJson.getDouble(j, "ph");
                        Double ec = SimpleJson.getDouble(j, "ec");
                        Integer n  = SimpleJson.getInt(j, "n");
                        Integer p  = SimpleJson.getInt(j, "p");
                        Integer k  = SimpleJson.getInt(j, "k");

                        Double soilTemp = SimpleJson.getDouble(j, "soil_temp");
                        if (soilTemp == null) soilTemp = SimpleJson.getDouble(j, "temp");

                        Double soilMoist = SimpleJson.getDouble(j, "soil_moisture");
                        if (soilMoist == null) soilMoist = SimpleJson.getDouble(j, "moisture");
                        if (soilMoist == null) soilMoist = SimpleJson.getDouble(j, "humi");

                        boolean ok = new SensorDAO().insertReading(ph, soilMoist, soilTemp, ec, n, p, k);
                        System.out.println("[MQTT] insert DB = " + ok);

                        // Buat notifikasi WARNING/DANGER dari reading sensor (source=ESP)
                        if (ok) {
                            try {
                                SensorNotifier.onNewReading(ph, soilMoist, soilTemp, ec, n, p, k, "ESP");
                            } catch (Exception ex) {
                                System.out.println("[MQTT] notify error: " + ex.getMessage());
                            }
                        }
                    }
                }

                @Override public void deliveryComplete(IMqttDeliveryToken token) {}
            });

            client.connect(opt);

            // subscribe
            client.subscribe(MqttConfig.TOPIC_SENSOR, MqttConfig.QOS);
            client.subscribe(MqttConfig.TOPIC_PUMP_STATUS, MqttConfig.QOS);
            client.subscribe(MqttConfig.TOPIC_AUTO_MODE, MqttConfig.QOS);

            System.out.println("[MQTT] Connected: " + MqttConfig.BROKER_URI);

        } catch (Exception e) {
            System.out.println("[MQTT] Start error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static synchronized boolean publish(String topic, String payload, boolean retained) {
        try {
            if (client == null || !client.isConnected()) start();
            if (client == null || !client.isConnected()) return false;

            MqttMessage msg = new MqttMessage(payload.getBytes(StandardCharsets.UTF_8));
            msg.setQos(MqttConfig.QOS);
            msg.setRetained(retained);

            client.publish(topic, msg);
            return true;
        } catch (Exception e) {
            System.out.println("[MQTT] publish error: " + e.getMessage());
            return false;
        }
    }

    // manual ON/OFF -> topic okra/pump/cmd
    public static boolean sendPumpCmd(String cmd) {
        String c = (cmd == null) ? "" : cmd.trim().toUpperCase();
        if (!"ON".equals(c) && !"OFF".equals(c)) return false;

        boolean ok = publish(MqttConfig.TOPIC_PUMP_CMD, c, false);
        if (ok) PumpState.update("PENDING_" + c);
        return ok;
    }

    // auto mode -> topic okra/pump/autoMode retained
    public static boolean sendPumpMode(boolean auto) {
        // update state lokal juga biar server konsisten walau retained belum diterima
        AutoModeState.setEnabled(auto);
        return publish(MqttConfig.TOPIC_AUTO_MODE, auto ? "ON" : "OFF", true);
    }

    private static boolean shouldLogAuto(String action) {
        // log hanya jika berubah ON->OFF atau OFF->ON
        String last = lastAutoLoggedAction;
        if (last != null && last.equals(action)) return false;
        lastAutoLoggedAction = action;
        return true;
    }

    private static String parsePumpAction(String payload) {
        if (payload == null) return null;
        String s = payload.trim();
        if (s.isEmpty()) return null;

        // JSON flat: {"status":"ON"}
        if (s.startsWith("{") && s.endsWith("}")) {
            try {
                Map<String, String> j = SimpleJson.parseFlat(s);
                String v = j.get("status");
                if (v == null) v = j.get("state");
                if (v == null) v = j.get("action");
                if (v == null) v = j.get("pump");
                if (v == null) v = j.get("value");
                if (v != null) return parsePumpAction(v);
            } catch (Exception ignored) {
            }
        }

        String up = s.toUpperCase();
        if ("ON".equals(up) || "1".equals(up) || "TRUE".equals(up)) return "ON";
        if ("OFF".equals(up) || "0".equals(up) || "FALSE".equals(up)) return "OFF";
        return null;
    }

    public static synchronized void stop() {
        try {
            if (client != null && client.isConnected()) client.disconnect();
        } catch (Exception ignored) {}
    }
}
