package br.faesa.javaclinic_lpoo_c3.utils;

import br.faesa.javaclinic_lpoo_c3.conexion.ConexaoMySQL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ValidatorUtils {
    public boolean existePaciente(ConexaoMySQL conexao, String cpf) throws SQLException {
        String sql = "SELECT 1 FROM paciente WHERE cpf=?";

        PreparedStatement ps = conexao.getConn().prepareStatement(sql);
        ps.setString(1, cpf);

        ResultSet rs = ps.executeQuery();
        return rs.next(); // true se existe
    }

    public boolean existeMedico(ConexaoMySQL conexao, String crm) throws SQLException {
        String sql = "SELECT 1 FROM medico WHERE crm=?";

        PreparedStatement ps = conexao.getConn().prepareStatement(sql);
        ps.setString(1, crm);

        ResultSet rs = ps.executeQuery();
        return rs.next();
    }

    public boolean existeConsulta(ConexaoMySQL conexao, long id) throws SQLException {
        String sql = "SELECT 1 FROM consulta WHERE id_consulta=?";

        PreparedStatement ps = conexao.getConn().prepareStatement(sql);
        ps.setString(1, String.valueOf(id));

        ResultSet rs = ps.executeQuery();
        return rs.next();
    }
}

