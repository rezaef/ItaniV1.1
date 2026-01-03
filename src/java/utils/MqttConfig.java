/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package utils;

/**
 *
 * @author rezaef
 */

public class MqttConfig {
    // Broker kamu (sesuai file kamu sekarang)
//  public static final String BROKER_URI = "tcp://10.218.9.244:1883"; // Wifi Kampus
    public static final String BROKER_URI = "tcp://192.168.0.5:1883"; // Wifi Rumah
    public static final String CLIENT_ID  = "itani-java-subscriber";

    public static final String USERNAME   = "okra";
    public static final String PASSWORD   = "okra123";

    public static final int QOS = 1;

    // === TOPIC SENSOR ===
    public static final String TOPIC_SENSOR = "okra/sensor";

    // === TOPIC PUMP (utama: TANPA leading slash, sesuai ESP paling umum) ===
    public static final String TOPIC_PUMP_CMD    = "okra/pump/cmd";
    public static final String TOPIC_PUMP_STATUS = "okra/pump/status";
    
    // PUMP MODE (AUTO/MANUAL)
    public static final String TOPIC_AUTO_MODE   = "okra/pump/autoMode";

}
