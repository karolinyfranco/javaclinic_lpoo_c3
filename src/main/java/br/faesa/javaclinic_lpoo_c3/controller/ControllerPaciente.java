package br.faesa.javaclinic_lpoo_c3.controller;

import br.faesa.javaclinic_lpoo_c3.conexion.ConexaoMySQL;
import br.faesa.javaclinic_lpoo_c3.model.Paciente;

import java.sql.*;
import java.util.ArrayList;

public class ControllerPaciente {
    ConexaoMySQL conexao = new ConexaoMySQL();

    public boolean inserir(Paciente paciente) {
        String sql = "INSERT INTO paciente (cpf, nome, email, telefone, endereco) VALUES (?, ?, ?, ?, ?)";

        try {
            conexao.connect();

            //verifica se o CPF já existe
            if (existePaciente(paciente.getCpf())) {
                System.out.println("ERRO: Já existe um paciente com esse CPF cadastrado.");
                return false; // ERRO: CPF duplicado
            }

            PreparedStatement stmt = conexao.getConn().prepareStatement(sql);

            stmt.setString(1, paciente.getCpf());
            stmt.setString(2, paciente.getNome());
            stmt.setString(3, paciente.getEmail());
            stmt.setString(4, paciente.getTelefone());
            stmt.setString(5, paciente.getEndereco());
            stmt.executeUpdate();

            System.out.println("Paciente inserido com sucesso!");
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao inserir paciente: " + e.getMessage());
            return false; // ERRO: geral
        } finally {
            conexao.close();
        }
    }

    public boolean atualizar(Paciente paciente) {
        String sql = "UPDATE paciente SET nome=?, email=?, endereco=?, telefone=? WHERE cpf=?";
        try {
            conexao.connect();
            if (!existePaciente(paciente.getCpf())) {
                System.out.println("Paciente com CPF " + paciente.getCpf() + " não encontrado.");
                return false;
            }
            PreparedStatement ps = conexao.getConn().prepareStatement(sql);
            ps.setString(1, paciente.getNome());
            ps.setString(2, paciente.getEmail());
            ps.setString(3, paciente.getEndereco());
            ps.setString(4, paciente.getTelefone());
            ps.setString(5, paciente.getCpf()); // WHERE
            ps.executeUpdate();

            System.out.println("Paciente atualizado com sucesso!");
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar paciente: " + e.getMessage());
        } finally {
            conexao.close();
        }
        return false;
    }

    public void excluir(String cpf) {
        String sql = "DELETE FROM paciente WHERE cpf=?";

        try {
            conexao.connect();

            // Verifica se existe antes de excluir
            if (!existePaciente(cpf)) {
                System.out.println("Paciente com CPF " + cpf + " não encontrado.");
                return;
            }

            // Verifica se o paciente possui consulta vinculada
            if (pacienteTemConsulta(cpf)){
                System.out.println("Paciente com CPF " + cpf + " já possui consulta e não pode ser excluído.");
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
            ResultSet rs = stmt.executeQuery();  // SEM passar SQL de novo

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

    public boolean existePaciente(String cpf) throws SQLException {
        ConexaoMySQL conn = new ConexaoMySQL();
        String sql = "SELECT 1 FROM paciente WHERE cpf=?";

        try {
            conn.connect();
            PreparedStatement ps = conn.getConn().prepareStatement(sql);
            ps.setString(1, cpf);

            ResultSet rs = ps.executeQuery();
            return rs.next(); // true se existe
        } catch (SQLException e) {
            System.out.println("Erro ao verificar existência de paciente: " + e.getMessage());
        } finally {
            conn.close();
        }
        return false;
    }

    public boolean pacienteTemConsulta(String cpf) {
        ConexaoMySQL conn = new ConexaoMySQL();
        String sql = "SELECT COUNT(*) FROM consulta WHERE cpf_paciente = ?";

        try {
            conn.connect();
            PreparedStatement ps = conn.getConn().prepareStatement(sql);
            ps.setString(1, cpf);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao verificar FKs: " + e.getMessage());
        } finally {
            conn.close();
        }
        return false;
    }
}
