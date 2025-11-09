package br.faesa.javaclinic_lpoo_c3.controller;

import br.faesa.javaclinic_lpoo_c3.conexion.ConexaoMySQL;
import br.faesa.javaclinic_lpoo_c3.model.Paciente;
import br.faesa.javaclinic_lpoo_c3.utils.ValidatorUtils;

import java.sql.*;
import java.util.ArrayList;

public class ControllerPaciente {
    ConexaoMySQL conexao = new ConexaoMySQL();
    ValidatorUtils validator = new ValidatorUtils();

    public void inserir(Paciente paciente) {
        String sql = "INSERT INTO paciente (cpf, nome, email, telefone, endereco) VALUES (?, ?, ?, ?, ?)";

        try {
            conexao.connect();
            PreparedStatement stmt = conexao.getConn().prepareStatement(sql);

            stmt.setString(1, paciente.getCpf());
            stmt.setString(2, paciente.getNome());
            stmt.setString(3, paciente.getEmail());
            stmt.setString(4, paciente.getTelefone());
            stmt.setString(5, paciente.getEndereco());
            stmt.executeUpdate();

            System.out.println("Paciente inserido com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro ao inserir paciente: " + e.getMessage());
        } finally {
            conexao.close();
        }
    }

    public void atualizar(String cpf, String campo, String novoValor) {
        String sql = "UPDATE paciente SET " + campo + " = ? WHERE cpf = ?";
        try {
            conexao.connect();
            if (!validator.existePaciente(conexao, cpf)) {
                System.out.println("Paciente com CPF " + cpf + " não encontrado.");
                return;
            }
            PreparedStatement ps = conexao.getConn().prepareStatement(sql);
            ps.setString(1, novoValor);
            ps.setString(2, cpf);
            ps.executeUpdate();
            System.out.println("Paciente atualizado com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar paciente: " + e.getMessage());
        } finally {
            conexao.close();
        }
    }

    public void excluir(String cpf) {
        String sql = "DELETE FROM paciente WHERE cpf=?";

        try {
            conexao.connect();

            // Verifica se existe antes de excluir
            if (!validator.existePaciente(conexao, cpf)) {
                System.out.println("Paciente com CPF " + cpf + " não encontrado.");
                return;
            }

            PreparedStatement stmt = conexao.getConn().prepareStatement(sql);
            stmt.setString(1, cpf);
            stmt.executeUpdate();
            System.out.println("Paciente excluído com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro ao excluir paciente: " + e.getMessage());
        } finally {
            conexao.close();
        }
    }

    public ArrayList<Paciente> listar() {
        ArrayList<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT * FROM paciente";

        try {
            conexao.connect();
            PreparedStatement stmt = conexao.getConn().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Paciente paciente = new Paciente(
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("endereco"),
                        rs.getString("telefone"),
                        rs.getString("cpf")
                );
                pacientes.add(paciente);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar pacientes: " + e.getMessage());
        } finally {
            conexao.close();
        }
        return pacientes;
    }

    public boolean pacienteTemConsulta(String cpf) {
        String sql = "SELECT COUNT(*) FROM consulta WHERE cpf_paciente = ?";
        try {
            conexao.connect();
            PreparedStatement ps = conexao.getConn().prepareStatement(sql);
            ps.setString(1, cpf);
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
