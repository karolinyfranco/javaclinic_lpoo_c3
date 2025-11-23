package br.faesa.javaclinic_lpoo_c3.principal;

import br.faesa.javaclinic_lpoo_c3.controller.*;
import br.faesa.javaclinic_lpoo_c3.model.*;
import br.faesa.javaclinic_lpoo_c3.utils.MenuUtils;
import br.faesa.javaclinic_lpoo_c3.utils.SplashScreen;
import br.faesa.javaclinic_lpoo_c3.reports.Relatorios;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class Principal {
    public static Scanner sc = new Scanner(System.in);
    public static ControllerMedico ctrlMedico = new ControllerMedico();
    public static ControllerPaciente ctrlPaciente = new ControllerPaciente();
    public static ControllerConsulta ctrlConsulta = new ControllerConsulta();
    public static Relatorios relatorios = new Relatorios();
    public static MenuUtils menuUtils = new MenuUtils();

    public static void main(String[] args) {
        SplashScreen splash = new SplashScreen();
        splash.mostrarTela();

        int opcao;
        do {
            menuUtils.mostrarMenuPrincipal();
            System.out.print("Escolha uma opção: ");
            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1 -> menuMedicos();
                case 2 -> menuPacientes();
                case 3 -> menuConsultas();
                case 4 -> menuRelatorios();
                case 0 -> System.out.println("Encerrando o sistema...");
                default -> System.out.println("Opção inválida.");
            }

        } while (opcao != 0);

        //menuPrincipal();

        sc.close();
    }

    // ================== RELATÓRIOS ==================
    public static void menuRelatorios() {
        int opcao;
        do {
            menuUtils.mostrarMenuRelatorios();
            System.out.print("Escolha uma opção: ");
            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1 -> relatorios.gerarRelatorioConsultasPorEspecialidade();
                case 2 -> relatorios.gerarRelatorioHistoricoPaciente(sc);
                case 3 -> relatorios.gerarRelatorioConsultasPorMedico();
                case 4 -> relatorios.gerarRelatorioConsultasDetalhadas();
                case 5 -> relatorios.gerarRelatorioGeralPessoas(ctrlMedico, ctrlPaciente);
                case 0 -> System.out.println("Voltando...");
                default -> System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    // ================== MÉDICOS ==================
    public static void menuMedicos() {
        int opcao;
        do {
            menuUtils.mostrarMenuMedicos();
            System.out.print("Escolha uma opção: ");
            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1 -> inserirMedico();
                case 2 -> listarMedicos();
                case 3 -> atualizarMedico();
                case 4 -> removerMedico();
                case 0 -> System.out.println("Voltando...");
                default -> System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    public static void inserirMedico() {
        String continuar = "";

        do {
            System.out.println("\n=== CADASTRO DE MÉDICO ===");
            System.out.print("CRM: ");
            String crm = sc.nextLine();
            System.out.print("Nome: ");
            String nome = sc.nextLine();
            System.out.print("Email: ");
            String email = sc.nextLine();
            System.out.print("Telefone: ");
            String telefone = sc.nextLine();
            System.out.print("Endereço: ");
            String endereco = sc.nextLine();
            System.out.print("Especialidade (ex: CARDIOLOGIA, PEDIATRIA...): ");
            String esp = sc.nextLine().toUpperCase();

            try {
                Medico medico = new Medico(nome, email, endereco, telefone, crm, Especialidade.valueOf(esp));
                ctrlMedico.inserir(medico);

                System.out.print("Deseja inserir outro médico? (S/N): ");
                continuar = sc.nextLine();
            } catch (IllegalArgumentException e) {
                System.out.println("Especialidade inválida!");
            }
        } while(continuar.equalsIgnoreCase("S"));
    }

    public static void atualizarMedico() {
        String continuar = "";

        do {
            System.out.println("\nAtualizar Médico");
            ArrayList<Medico> medicos = ctrlMedico.listar();
            medicos.forEach(m -> System.out.println(m.getCrm() + " - " + m.getNome()));

            System.out.print("Informe o CRM do médico a atualizar: ");
            String crm = sc.nextLine();

            System.out.println("Selecione o campo para atualizar:");
            System.out.println("1 - Nome\n2 - Email\n3 - Telefone\n4 - Endereço\n5 - Especialidade");
            int opc = Integer.parseInt(sc.nextLine());
            System.out.print("Novo valor: ");
            String novo = sc.nextLine();

            String campo = switch (opc) {
                case 1 -> "nome";
                case 2 -> "email";
                case 3 -> "telefone";
                case 4 -> "endereco";
                case 5 -> "especialidade";
                default -> "";
            };
            try {
                if (!campo.isEmpty()) {
                    ctrlMedico.atualizar(crm, campo, novo);
                }

                System.out.println("\nMédico atualizado:");
                ctrlMedico.listar().stream()
                        .filter(md -> md.getCrm().equals(crm))
                        .forEach(System.out::println);

                System.out.print("Deseja atualizar outro médico? (S/N): ");
                continuar = sc.nextLine();
            } catch (IllegalArgumentException e) {
                System.out.println("Especialidade inválida!");
            }
        } while(continuar.equalsIgnoreCase("S"));
    }

    public static void removerMedico() {
        String continuar;

        do {
            System.out.println("\nExcluir Médico");
            ctrlMedico.listar().forEach(m -> System.out.println(m.getCrm() + " - " + m.getNome()));

            System.out.print("CRM do médico a excluir: ");
            String crm = sc.nextLine();

            if (ctrlMedico.medicoTemConsulta(crm)) {
                System.out.println("Este médico possui consultas e não pode ser removido!");
                return;
            }

            System.out.print("Tem certeza que deseja excluir? (S/N): ");
            String confirmacao = sc.nextLine();
            if (confirmacao.equalsIgnoreCase("S")) {
                ctrlMedico.excluir(crm);
            }

            System.out.print("Deseja excluir outro médico? (S/N): ");
            continuar = sc.nextLine();
        } while (continuar.equalsIgnoreCase("S"));
    }

    public static void listarMedicos() {
        ArrayList<Medico> medicos = ctrlMedico.listar();
        System.out.println("\n=== MÉDICOS CADASTRADOS ===");
        for (Medico m : medicos) {
            System.out.println(m);
        }
    }

    // ================== PACIENTES ==================
    public static void menuPacientes() {
        int opcao;
        do {
            menuUtils.mostrarMenuPacientes();
            System.out.print("Escolha uma opção: ");
            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1 -> inserirPaciente();
                case 2 -> listarPacientes();
                case 3 -> atualizarPaciente();
                case 4 -> removerPaciente();
                case 0 -> System.out.println("Voltando...");
                default -> System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    public static void inserirPaciente() {
        String continuar;

        do {
            System.out.println("\n=== CADASTRO DE PACIENTE ===");
            System.out.print("CPF: ");
            String cpf = sc.nextLine();
            System.out.print("Nome: ");
            String nome = sc.nextLine();
            System.out.print("Email: ");
            String email = sc.nextLine();
            System.out.print("Telefone: ");
            String telefone = sc.nextLine();
            System.out.print("Endereço: ");
            String endereco = sc.nextLine();

            Paciente paciente = new Paciente(nome, email, endereco, telefone, cpf);
            ctrlPaciente.inserir(paciente);

            System.out.print("Deseja inserir outro paciente? (S/N): ");
            continuar = sc.nextLine();
        } while (continuar.equalsIgnoreCase("S"));
    }

    public static void atualizarPaciente() {
        String continuar;

        do {
            System.out.println("\nAtualizar Paciente");
            ArrayList<Paciente> pacientes = ctrlPaciente.listar();
            pacientes.forEach(p -> System.out.println(p.getCpf() + " - " + p.getNome()));

            System.out.print("Informe o CPF do paciente a atualizar: ");
            String cpf = sc.nextLine();

            System.out.println("Selecione o campo para atualizar:");
            System.out.println("1 - Nome\n2 - Email\n3 - Telefone\n4 - Endereço");
            int opc = Integer.parseInt(sc.nextLine());
            System.out.print("Novo valor: ");
            String novo = sc.nextLine();

            String campo = switch (opc) {
                case 1 -> "nome";
                case 2 -> "email";
                case 3 -> "telefone";
                case 4 -> "endereco";
                default -> "";
            };

            if (!campo.isEmpty()) {
                ctrlPaciente.atualizar(cpf, campo, novo);
            }

            System.out.println("\nPaciente atualizado:");
            ctrlPaciente.listar().stream()
                    .filter(pc -> pc.getCpf().equals(cpf))
                    .forEach(System.out::println);

            System.out.print("Deseja atualizar outro paciente? (S/N): ");
            continuar = sc.nextLine();
        } while (continuar.equalsIgnoreCase("S"));
    }

    public static void removerPaciente() {
        String continuar;

        do {
            System.out.println("\nExcluir Paciente");
            ctrlPaciente.listar().forEach(p -> System.out.println(p.getCpf() + " - " + p.getNome()));

            System.out.print("CPF do paciente a excluir: ");
            String cpf = sc.nextLine();

            if (ctrlPaciente.pacienteTemConsulta(cpf)) {
                System.out.println("Este paciente possui consultas e não pode ser removido!");
                return;
            }

            System.out.print("Tem certeza que deseja excluir? (S/N): ");
            if (sc.nextLine().equalsIgnoreCase("S")) {
                ctrlPaciente.excluir(cpf);
            }

            System.out.print("Deseja excluir outro paciente? (S/N): ");
            continuar = sc.nextLine();
        } while (continuar.equalsIgnoreCase("S"));
    }

    public static void listarPacientes() {
        ArrayList<Paciente> pacientes = ctrlPaciente.listar();
        System.out.println("\n=== PACIENTES CADASTRADOS ===");
        for (Paciente p : pacientes) {
            System.out.println(p);
        }
    }

    // ================== CONSULTAS ==================
    public static void menuConsultas() {
        int opcao;
        do {
            menuUtils.mostrarMenuConsultas();
            System.out.print("Escolha uma opção: ");
            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1 -> agendarConsulta();
                case 2 -> listarConsultas();
                case 3 -> cancelarConsulta();
                case 0 -> System.out.println("Voltando...");
                default -> System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    public static void agendarConsulta() {
        String continuar = "";

        do {
            System.out.println("\n=== AGENDAR CONSULTA ===");
            System.out.print("CPF do Paciente: ");
            String cpf = sc.nextLine();
            System.out.print("CRM do Médico: ");
            String crm = sc.nextLine();
            System.out.print("Especialidade (ex: CARDIOLOGIA, PEDIATRIA...): ");
            String esp = sc.nextLine().toUpperCase();
            System.out.print("Data e hora da consulta (formato: dd/MM/yyyy HH:mm): ");
            String dataStr = sc.nextLine();

            try {
                LocalDateTime data = LocalDateTime.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                Consulta consulta = new Consulta(0, crm, cpf, Especialidade.valueOf(esp), data);
                ctrlConsulta.inserir(consulta);

                System.out.print("Deseja agendar outra consulta? (S/N): ");
                continuar = sc.nextLine();
            } catch (Exception e) {
                System.out.println("Data ou especialidade inválida!");
            }
        } while (continuar.equalsIgnoreCase("S"));
    }

    public static void cancelarConsulta() {
        String continuar;

        do {
            System.out.println("\nCancelar Consulta");
            ctrlConsulta.listar().forEach(c -> System.out.println(c.getId() + " - " +
                    c.getCpfPaciente() + " (" + c.getEspecialidade() + ")"));

            System.out.print("Informe o ID da consulta a cancelar: ");
            long id = Long.parseLong(sc.nextLine());

            System.out.print("Tem certeza que deseja cancelar a consulta? (S/N): ");
            if (sc.nextLine().equalsIgnoreCase("S")) {
                ctrlConsulta.excluir(id);
            }

            System.out.print("Deseja cancelar outra consulta? (S/N): ");
            continuar = sc.nextLine();
        } while (continuar.equalsIgnoreCase("S"));
    }

    public static void listarConsultas() {
        ArrayList<Consulta> consultas = ctrlConsulta.listar();
        System.out.println("\n=== CONSULTAS ===");
        for (Consulta c : consultas) {
            System.out.println(c);
        }
    }
}
