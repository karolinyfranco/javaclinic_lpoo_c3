package br.faesa.javaclinic_lpoo_c3.controller;

import br.faesa.javaclinic_lpoo_c3.conexion.ConexaoMySQL;
import br.faesa.javaclinic_lpoo_c3.model.Consulta;
import br.faesa.javaclinic_lpoo_c3.model.Especialidade;
import br.faesa.javaclinic_lpoo_c3.utils.ValidatorUtils;

import java.sql.*;
import java.util.ArrayList;

public class ControllerConsulta {
    ConexaoMySQL conexao = new ConexaoMySQL();
    ValidatorUtils validator = new ValidatorUtils();
    ArrayList<Consulta> consultas = new ArrayList<>();

    public void inserir(Consulta consulta) {
        String sql = "INSERT INTO consulta (cpf_paciente, crm_medico, especialidade, data) VALUES (?, ?, ?, ?)";

        try {
            conexao.connect();

            if (!validator.existePaciente(conexao, consulta.getCpfPaciente())) {
                System.out.println("Paciente com CPF " + consulta.getCpfPaciente() + " não encontrado.");
                return;
            }

            if (!validator.existeMedico(conexao, consulta.getCrmMedico())) {
                System.out.println("Médico com CRM " + consulta.getCrmMedico() + " não encontrado.");
                return;
            }

            PreparedStatement stmt = conexao.getConn().prepareStatement(sql);

            stmt.setString(1, consulta.getCpfPaciente());
            stmt.setString(2, consulta.getCrmMedico());
            stmt.setString(3, consulta.getEspecialidade().name());
            stmt.setTimestamp(4, Timestamp.valueOf(consulta.getData()));
            stmt.executeUpdate();

            System.out.println("Consulta agendada com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro ao agendar consulta: " + e.getMessage());
        } finally {
            conexao.close();
        }
    }

    public void excluir(long id) {
        String sql = "DELETE FROM consulta WHERE id_consulta=?";

        try {
            conexao.connect();

            // Verifica se existe antes de excluir
            if (!validator.existeConsulta(conexao, id)) {
                System.out.println("Consulta de ID " + id + " não encontrada.");
                return;
            }

            PreparedStatement stmt = conexao.getConn().prepareStatement(sql);
            stmt.setLong(1, id);
            stmt.executeUpdate();
            System.out.println("Consulta cancelada com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro ao excluir consulta: " + e.getMessage());
        } finally {
            conexao.close();
        }
    }

    public ArrayList<Consulta> listar() {
        String sql = "SELECT * FROM consulta";

        try {
            conexao.connect();
            PreparedStatement stmt = conexao.getConn().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Consulta consulta = new Consulta(
                        rs.getLong("id_consulta"),
                        rs.getString("crm_medico"),
                        rs.getString("cpf_paciente"),
                        Especialidade.valueOf(rs.getString("especialidade")),
                        rs.getTimestamp("data").toLocalDateTime()
                );
                consultas.add(consulta);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar consultas: " + e.getMessage());
        } finally {
            conexao.close();
        }
        return consultas;
    }
}

