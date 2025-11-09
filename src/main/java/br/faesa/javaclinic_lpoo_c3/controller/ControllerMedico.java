package br.faesa.javaclinic_lpoo_c3.controller;

import br.faesa.javaclinic_lpoo_c3.conexion.ConexaoMySQL;
import br.faesa.javaclinic_lpoo_c3.model.Medico;
import br.faesa.javaclinic_lpoo_c3.model.Especialidade;
import br.faesa.javaclinic_lpoo_c3.utils.ValidatorUtils;

import java.sql.*;
import java.util.ArrayList;

public class ControllerMedico {
    ConexaoMySQL conexao = new ConexaoMySQL();
    ValidatorUtils validator = new ValidatorUtils();

    public void inserir(Medico medico) {
        String sql = "INSERT INTO medico (crm, nome, email, especialidade, telefone, endereco) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            conexao.connect();
            PreparedStatement stmt = conexao.getConn().prepareStatement(sql);

            stmt.setString(1, medico.getCrm());
            stmt.setString(2, medico.getNome());
            stmt.setString(3, medico.getEmail());
            stmt.setString(4, medico.getEspecialidade().name());
            stmt.setString(5, medico.getTelefone());
            stmt.setString(6, medico.getEndereco());
            stmt.executeUpdate();

            System.out.println("Médico inserido com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro ao inserir médico: " + e.getMessage());
        } finally {
            conexao.close();
        }
    }

    public void atualizar(String crm, String campo, String novoValor) {
        String sql = "UPDATE medico SET " + campo + " = ? WHERE crm = ?";
        try {
            conexao.connect();
            if (!validator.existeMedico(conexao, crm)) {
                System.out.println("Médico com CRM " + crm + " não encontrado.");
                return;
            }
            PreparedStatement ps = conexao.getConn().prepareStatement(sql);
            ps.setString(1, novoValor);
            ps.setString(2, crm);
            ps.executeUpdate();
            System.out.println("Médico atualizado com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar médico: " + e.getMessage());
        } finally {
            conexao.close();
        }
    }

    public void excluir(String crm) {
        String sql = "DELETE FROM medico WHERE crm=?";

        try {
            conexao.connect();

            // Verifica se existe antes de excluir
            if (!validator.existeMedico(conexao, crm)) {
                System.out.println("Médico com CRM " + crm + " não encontrado.");
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
                        Especialidade.valueOf(rs.getString("especialidade"))
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

    public boolean medicoTemConsulta(String crm) {
        String sql = "SELECT COUNT(*) FROM consulta WHERE crm_medico = ?";
        try {
            conexao.connect();
            PreparedStatement ps = conexao.getConn().prepareStatement(sql);
            ps.setString(1, crm);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return true;
        } catch (SQLException e) {
            System.out.println("Erro ao verificar FKs: " + e.getMessage());
        } finally {
            conexao.close();
        }
        return false;
    }
}
