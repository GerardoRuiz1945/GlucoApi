/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.lasalle.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.lasalle.connection.ConnectionMysql;
import org.lasalle.model.Usuario;

/**
 *
 * @author ferto
 */
public class UsuarioController {

    public List<Usuario> getUsuarios() throws SQLException {
        //Declaramos la estructura de datos
        List<Usuario> lista = new ArrayList<>();
        //Declaramos la sentencia ejecutar
        String sql = "SELECT idUsuario, nombre, primerApellido, segundoApellido, email FROM usuario;";
        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();

        try (PreparedStatement pstm = conn.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {
                Usuario user = new Usuario();
                user.setIdUsuario(rs.getInt("idUsuario"));
                user.setNombre(rs.getString("nombre"));
                user.setPrimerApellido(rs.getString("primerApellido"));
                user.setSegundoApellido(rs.getString("segundoApellido"));
                user.setEmail(rs.getString("email"));
                lista.add(user);
            }
        } finally {
            connMysql.close();
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        }

        return lista;
    }

    /**
     * Registra un usuario en la tabla usuario.
     *
     * @return el idUsuario generado por MySQL.
     */
    public int registrarUsuario(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (nombre, primerApellido, segundoApellido, email) "
                + "VALUES (?, ?, ?, ?);";
        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();

        try (PreparedStatement pstm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstm.setString(1, usuario.getNombre());
            pstm.setString(2, usuario.getPrimerApellido());
            pstm.setString(3, usuario.getSegundoApellido());
            pstm.setString(4, usuario.getEmail());
            pstm.executeUpdate();

            try (ResultSet rs = pstm.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } finally {
            connMysql.close();
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        }

        return -1;
    }
}
