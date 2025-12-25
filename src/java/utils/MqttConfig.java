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
    // Ganti sesuai broker kamu
    public static final String BROKER_URI = "tcp://10.218.9.244:1883"; // contoh RabbitMQ MQTT / Mosquitto
    public static final String CLIENT_ID  = "itani-java-subscriber";
    public static final String TOPIC      = "okra/sensor";
    public static final String USERNAME   = "okra"; // isi kalau broker pakai auth
    public static final String PASSWORD   = "okra123"; // isi kalau broker pakai auth

    public static final int QOS = 1;
}
