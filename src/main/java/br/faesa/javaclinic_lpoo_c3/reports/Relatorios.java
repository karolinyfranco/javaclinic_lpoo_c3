package br.faesa.javaclinic_lpoo_c3.reports;

import br.faesa.javaclinic_lpoo_c3.conexion.ConexaoMySQL;
import br.faesa.javaclinic_lpoo_c3.controller.ControllerPaciente;
import br.faesa.javaclinic_lpoo_c3.controller.ControllerMedico;
import br.faesa.javaclinic_lpoo_c3.model.Pessoa;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class Relatorios {
    ConexaoMySQL conexao = new ConexaoMySQL();

    /*
     * Exibe a contagem de consultas agrupada por especialidade.
     */
    public void gerarRelatorioConsultasPorEspecialidade() {
        String sql = """
            SELECT
                especialidade,
                COUNT(id_consulta) AS total_de_consultas
            FROM consulta
            GROUP BY especialidade
            ORDER BY total_de_consultas DESC;
        """;

        System.out.println("\n===== RELATÓRIO: TOTAL DE CONSULTAS POR ESPECIALIDADE =====");
        System.out.printf("%-20s | %-10s\n", "ESPECIALIDADE", "Nº DE CONSULTAS");
        System.out.println("---------------------------------------");

        try {
            conexao.connect();
            PreparedStatement stmt = conexao.getConn().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String especialidade = rs.getString("especialidade");
                int totalConsultas = rs.getInt("total_de_consultas");
                System.out.printf("%-20s | %-10d\n", especialidade, totalConsultas);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao gerar relatório de consultas por especialidade: " + e.getMessage());
        } finally {
            conexao.close();
        }
    }

    /**
     * Exibe o histórico de consultas de um paciente específico.
     */
    public void gerarRelatorioHistoricoPaciente(Scanner sc) {
        // System.out.print("\nDigite o CPF do paciente para buscar o histórico: ");
        String cpf = sc.nextLine();

        String sql = """
            SELECT
                c.data,
                m.nome AS nome_medico,
                m.especialidade
            FROM consulta c
            JOIN medico m ON c.crm_medico = m.crm
            WHERE c.cpf_paciente = ?
            ORDER BY c.data DESC;
        """;

        System.out.println("\n===== RELATÓRIO: HISTÓRICO DE CONSULTAS DO PACIENTE - CPF " + cpf + " =====");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        boolean encontrouRegistros = false;

        try {
            conexao.connect();
            PreparedStatement stmt = conexao.getConn().prepareStatement(sql);
            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();

            // Imprime o cabeçalho apenas se a consulta for bem-sucedida
            System.out.printf("%-20s | %-30s | %-20s\n", "DATA E HORA", "MÉDICO", "ESPECIALIDADE");
            System.out.println("--------------------------------------------------------------------------");

            while (rs.next()) {
                encontrouRegistros = true;
                Timestamp dataTimestamp = rs.getTimestamp("data");
                String dataFormatada = dataTimestamp.toLocalDateTime().format(formatter);
                String nomeMedico = rs.getString("nome_medico");
                String especialidade = rs.getString("especialidade");
                System.out.printf("%-20s | %-30s | %-20s\n", dataFormatada, nomeMedico, especialidade);
            }

            if (!encontrouRegistros) {
                System.out.println("Nenhum histórico de consulta encontrado para o CPF informado.");
                System.out.println("(Verifique se o CPF está correto ou se o paciente já realizou consultas)");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao gerar histórico do paciente: " + e.getMessage());
        } finally {
            conexao.close();
        }
    }
    /**
     * Exibe a contagem de consultas para cada médico cadastrado.
     */
    public void gerarRelatorioConsultasPorMedico() {
        String sql = """
            SELECT m.crm, m.nome, COUNT(c.id_consulta) AS total_consultas
            FROM medico m LEFT JOIN consulta c ON m.crm = c.crm_medico
            GROUP BY m.crm, m.nome ORDER BY total_consultas DESC;
        """;
        System.out.println("\n===== RELATÓRIO: TOTAL DE CONSULTAS POR MÉDICO =====");
        System.out.printf("%-10s | %-30s | %-10s\n", "CRM", "NOME DO MÉDICO", "Nº DE CONSULTAS");
        System.out.println("---------------------------------------------------------");
        try {
            conexao.connect();
            PreparedStatement stmt = conexao.getConn().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.printf("%-10s | %-30s | %-10d\n", rs.getString("crm"), rs.getString("nome"), rs.getInt("total_consultas"));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao gerar relatório: " + e.getMessage());
        } finally {
            conexao.close();
        }
    }
    /**
     * Exibe uma lista detalhada de todas as consultas, juntando dados de médicos e pacientes.
     */
    public void gerarRelatorioConsultasDetalhadas() {
        String sql = """
            SELECT c.id_consulta, c.data, p.nome AS nome_paciente, m.nome AS nome_medico, m.especialidade
            FROM consulta c JOIN paciente p ON c.cpf_paciente = p.cpf JOIN medico m ON c.crm_medico = m.crm
            ORDER BY c.data;
        """;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        System.out.println("\n===== RELATÓRIO: LISTA DE CONSULTAS DETALHADAS =====");
        System.out.printf("%-5s | %-20s | %-25s | %-25s | %-15s\n", "ID", "DATA E HORA", "PACIENTE", "MÉDICO", "ESPECIALIDADE");
        System.out.println("-----------------------------------------------------------------------------------------------------");
        try {
            conexao.connect();
            PreparedStatement stmt = conexao.getConn().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                long id = rs.getLong("id_consulta");
                Timestamp dataTimestamp = rs.getTimestamp("data");
                String dataFormatada = dataTimestamp.toLocalDateTime().format(formatter);
                System.out.printf("%-5d | %-20s | %-25s | %-25s | %-15s\n", id, dataFormatada, rs.getString("nome_paciente"), rs.getString("nome_medico"), rs.getString("especialidade"));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao gerar relatório: " + e.getMessage());
        } finally {
            conexao.close();
        }
    }

    public void gerarRelatorioGeralPessoas(ControllerMedico ctrlMedico, ControllerPaciente ctrlPaciente) {
        System.out.println("\n=== RELATÓRIO GERAL DE PESSOAS CADASTRADAS ===");

        ArrayList<Pessoa> pessoas = new ArrayList<>();
        pessoas.addAll(ctrlMedico.listar());
        pessoas.addAll(ctrlPaciente.listar());

        for (Pessoa p : pessoas) {
            System.out.println(p.getIdentificacao()); // polimorfismo
            System.out.println("--------------------------");
        }
    }

}

