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
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.eclipse.paho.client.mqttv3.*;

public class MqttSubscriber {

    private static MqttClient client;

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

                    // status pompa dari device: ON/OFF
                    if (MqttConfig.TOPIC_PUMP_STATUS.equals(topic)) {
                        PumpState.update(payload);
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
                    }
                }

                @Override public void deliveryComplete(IMqttDeliveryToken token) {}
            });

            client.connect(opt);

            // subscribe
            client.subscribe(MqttConfig.TOPIC_SENSOR, MqttConfig.QOS);
            client.subscribe(MqttConfig.TOPIC_PUMP_STATUS, MqttConfig.QOS);

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
        return publish(MqttConfig.TOPIC_AUTO_MODE, auto ? "ON" : "OFF", true);
    }

    public static synchronized void stop() {
        try {
            if (client != null && client.isConnected()) client.disconnect();
        } catch (Exception ignored) {}
    }
}
