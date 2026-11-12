/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.lasalle.connection;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author ruizt
 */
public class ConnectionMysql {

    private Connection conn;

    public Connection open() {
        String host = "mysql-6400ebc-ruiztorresluisgerardo-1b9e.a.aivencloud.com";
        int port = 11991;
        String bdName = "gluco_api";
        String url = "jdbc:mysql://" + host + ":" + port + "/" + bdName
                + "?sslMode=REQUIRED"
                + "&allowPublicKeyRetrieval=TRUE"
                + "&useUnicode=true&characterEncoding=utf-8";
        String user = System.getenv().getOrDefault("DB_USER", "avnadmin");
        String password = System.getenv().getOrDefault("DB_PASSWORD", "AVNS_7Ni3-2Qz3uy0Aeu9DIX");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
