package br.faesa.javaclinic_lpoo_c3.controller;

import br.faesa.javaclinic_lpoo_c3.conexion.ConexaoMySQL;
import br.faesa.javaclinic_lpoo_c3.model.Medico;
import br.faesa.javaclinic_lpoo_c3.model.Especialidade;

import java.sql.*;
import java.util.ArrayList;

public class ControllerMedico {
    ConexaoMySQL conexao = new ConexaoMySQL();

    public boolean inserir(Medico medico) {
        String sql = "INSERT INTO medico (crm, nome, email, especialidade, telefone, endereco) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            conexao.connect();
            PreparedStatement stmt = conexao.getConn().prepareStatement(sql);

            if (existeMedico(medico.getCrm())) {
                System.out.println("ERRO: Já existe um médico com esse CRM cadastrado.");
                return false;
            }

            stmt.setString(1, medico.getCrm());
            stmt.setString(2, medico.getNome());
            stmt.setString(3, medico.getEmail());
            stmt.setString(4, medico.getEspecialidade().name());
            stmt.setString(5, medico.getTelefone());
            stmt.setString(6, medico.getEndereco());
            stmt.executeUpdate();

            System.out.println("Médico inserido com sucesso!");
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir médico: " + e.getMessage());
        } finally {
            conexao.close();
        }
        return false;
    }

    public boolean atualizar(Medico medico) {
        String sql = "UPDATE medico SET nome=?, email=?, especialidade=?, telefone=?, endereco=? WHERE crm=?";
        try {
            conexao.connect();

            if (!existeMedico(medico.getCrm())) {
                System.out.println("ERRO: Médico não encontrado!");
                return false;
            }

            PreparedStatement ps = conexao.getConn().prepareStatement(sql);
            ps.setString(1, medico.getNome());
            ps.setString(2, medico.getEmail());
            ps.setString(3, medico.getEspecialidade().name());
            ps.setString(4, medico.getTelefone());
            ps.setString(5, medico.getEndereco());
            ps.setString(6, medico.getCrm()); // WHERE
            ps.executeUpdate();

            System.out.println("Médico atualizado com sucesso!");
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar médico: " + e.getMessage());
        } finally {
            conexao.close();
        }
        return false;
    }

    public void excluir(String crm) {
        String sql = "DELETE FROM medico WHERE crm=?";

        try {
            conexao.connect();

            // Verifica se existe antes de excluir
            if (!existeMedico(crm)) {
                System.out.println("Médico com CRM " + crm + " não encontrado.");
                return;
            }

            // Verifica se o médico possui consulta vinculada
            if (medicoTemConsulta(crm)){
                System.out.println("Médico com CRM " + crm + " já possui consulta e não pode ser excluído.");
                return;
            }

            PreparedStatement stmt = conexao.getConn().prepareStatement(sql);
            stmt.setString(1, crm);
            stmt.executeUpdate();
            System.out.println("Médico excluído com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro ao excluir médico: " + e.getMessage());
        } finally {
            conexao.close();
        }
    }

    public ArrayList<Medico> listar() {
        ArrayList<Medico> medicos = new ArrayList<>();
        String sql = "SELECT * FROM medico";

        try {
            conexao.connect();
            PreparedStatement stmt = conexao.getConn().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Medico medico = new Medico(
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("endereco"),
                        rs.getString("telefone"),
                        rs.getString("crm"),
                        Especialidade.fromString(rs.getString("especialidade"))
                );
                medicos.add(medico);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar médicos: " + e.getMessage());
        } finally {
            conexao.close();
        }
        return medicos;
    }

    public boolean existeMedico(String crm) throws SQLException {
        ConexaoMySQL conn = new ConexaoMySQL();
        String sql = "SELECT 1 FROM medico WHERE crm=?";

        try {
            conn.connect();
            PreparedStatement ps = conn.getConn().prepareStatement(sql);
            ps.setString(1, crm);

            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Erro ao verificar existência de médico: " + e.getMessage());
        } finally {
            conn.close();
        }
        return false;
    }

    public boolean medicoTemConsulta(String crm) {
        ConexaoMySQL conn = new ConexaoMySQL();
        String sql = "SELECT COUNT(*) FROM consulta WHERE crm_medico = ?";

        try {
            conn.connect();
            PreparedStatement ps = conn.getConn().prepareStatement(sql);
            ps.setString(1, crm);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return true;
        } catch (SQLException e) {
            System.out.println("Erro ao verificar FKs: " + e.getMessage());
        } finally {
            conn.close();
        }
        return false;
    }
}