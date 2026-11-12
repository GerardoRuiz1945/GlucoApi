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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.lasalle.connection.ConnectionMysql;
import org.lasalle.model.Medida;

/**
 *
 * @author ruizt
 */
public class MedidaController {

    /**
     * Registra una medida de glucosa en la tabla medida.
     *
     * @return el idMedida generado por MySQL.
     */
    public int registrarMedida(Medida medida) throws SQLException {
        String sql = "INSERT INTO medida (idUsuario, glucosa, fechaHora, contexto, notas) "
                + "VALUES (?, ?, ?, ?, ?);";
        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();

        try (PreparedStatement pstm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstm.setInt(1, medida.getIdUsuario());
            pstm.setInt(2, medida.getGlucosa());

            if (medida.getFechaHora() == null || medida.getFechaHora().isEmpty()) {
                pstm.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            } else {
                pstm.setTimestamp(3, Timestamp.valueOf(medida.getFechaHora()));
            }

            pstm.setString(4, medida.getContexto());
            pstm.setString(5, medida.getNotas());
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

    /**
     * Obtiene todas las medidas de un usuario.
     */
    public List<Medida> getMedidasPorUsuario(int idUsuario) throws SQLException {
        List<Medida> lista = new ArrayList<>();
        String sql = "SELECT idMedida, idUsuario, glucosa, fechaHora, contexto, notas "
                + "FROM medida WHERE idUsuario = ? ORDER BY fechaHora DESC;";
        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();

        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, idUsuario);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    Medida medida = new Medida();
                    medida.setIdMedida(rs.getInt("idMedida"));
                    medida.setIdUsuario(rs.getInt("idUsuario"));
                    medida.setGlucosa(rs.getInt("glucosa"));
                    Timestamp ts = rs.getTimestamp("fechaHora");
                    medida.setFechaHora(ts != null ? ts.toString() : null);
                    medida.setContexto(rs.getString("contexto"));
                    medida.setNotas(rs.getString("notas"));
                    lista.add(medida);
                }
            }
        } finally {
            connMysql.close();
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        }

        return lista;
    }
}
