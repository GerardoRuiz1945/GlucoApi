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
        String url = "jdbc:mysql://mysql-6400ebc-ruiztorresluisgerardo-1b9e.a.aivencloud.com:11991/defaultdb?sslMode=REQUIRED&allowPublicKeyRetrieval=true";
        String user = "avnadmin";
        String password = "AVNS_7Ni3-2Qz3uy0Aeu9DIX";
        String bdName = "gluco_api";
        String parametros = "?allowPublicKeyRetrieval=true&useSSL=false&useUnicode=true&characterEncoding=utf-8";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url + parametros, user, password);
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
