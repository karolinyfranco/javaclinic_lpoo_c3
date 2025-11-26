package br.faesa.javaclinic_lpoo_c3.model;

public class Medico extends Pessoa {
    private String crm;
    private Especialidade especialidade;

    // Construtor com todos os parâmetros
    public Medico(String nome, String email, String endereco, String telefone, String crm, Especialidade especialidade) {
        super(nome, email, endereco, telefone);
        this.crm = crm;
        this.especialidade = especialidade;
    }

    // Métodos getters e setters
    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public Especialidade getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(Especialidade especialidade) {
        this.especialidade = especialidade;
    }

    @Override
    public String getIdentificacao() {
        return "Médico: " + getNome() + " (CRM: " + crm + ")";
    }

    // Método toString para representar o médico como uma string
    @Override
    public String toString() {
        return "---------------------\n" +
                super.toString() +
                "CRM: " + crm + "\n" +
                "Especialidade: " + especialidade;
    }
}
