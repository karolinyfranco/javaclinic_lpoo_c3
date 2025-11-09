package br.faesa.javaclinic_lpoo_c3.utils;

import br.faesa.javaclinic_lpoo_c3.conexion.ConexaoMySQL;

import java.sql.*;

public class SplashScreen {
    ConexaoMySQL conexao = new ConexaoMySQL();

    private final String PROFESSOR = "Prof. M.Sc. Howard Roatti";
    private final String DISCIPLINA = "Banco de Dados";
    private final String SEMESTRE = "2025/2";

    public static void clearConsole() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 10; i++) System.out.println();
        }
    }

    private int getTotalPacientes() {
        return contarRegistros("paciente");
    }

    private int getTotalMedicos() {
        return contarRegistros("medico");
    }

    private int getTotalConsultas() {
        return contarRegistros("consulta");
    }

    private int contarRegistros(String tabela) {
        String sql = "SELECT COUNT(*) FROM " + tabela;
        int total = 0;
        try {
            conexao.connect();
            PreparedStatement ps = conexao.getConn().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao contar registros da tabela " + tabela + ": " + e.getMessage());
        } finally {
            conexao.close();
        }
        return total;
    }

    public void mostrarTela() {
        clearConsole();

        int totalPacientes = getTotalPacientes();
        int totalMedicos = getTotalMedicos();
        int totalConsultas = getTotalConsultas();

        String tela = """
                ########################################################
                #                                                      #
                #                      JAVACLINIC                      #
                #        SISTEMA DE GESTÃO DE CONSULTAS MÉDICAS        #
                #                                                      #
                #  TOTAL DE REGISTROS:                                 #
                #                                                      #
                #    1 - PACIENTES:     %29d  #
                #    2 - MÉDICOS:       %29d  #
                #    3 - CONSULTAS:     %29d  #
                #                                                      #
                #  CRIADO POR: %-39s #
                #              %-39s #
                #                                                      #
                #  PROFESSOR:  %-39s #
                #                                                      #
                #  DISCIPLINA: %-39s #
                #              %-39s #
                ########################################################
                """.formatted(
                totalPacientes,
                totalMedicos,
                totalConsultas,
                "Karoliny Franco, Ana Luiza Menelli,",
                "Gustavo Rissoli e Felipe Valério",
                PROFESSOR,
                DISCIPLINA,
                SEMESTRE
        );

        System.out.println(tela);
        System.out.println("\nCarregando sistema...");

        try {
            Thread.sleep(4000); // Espera 4 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

