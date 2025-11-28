# 🏥 JavaClinic — Sistema de Gerenciamento de Consultas Médicas

## Sobre o Projeto

O JavaClinic é um sistema de gerenciamento de consultas médicas desenvolvido em Java, com persistência de dados em MySQL e estrutura organizada segundo boas práticas de separação em pacotes.

O objetivo do projeto é simular um sistema de clínica médica, permitindo o cadastro de médicos e pacientes, e o agendamento de consultas, com base em um banco relacional.

Este projeto foi desenvolvido como parte da disciplina de Linguagem de Programação Orientada a Objetos, com Java + MySQL utilizando Docker e Maven.


## Funcionalidades

- Cadastro, atualização, listagem e exclusão de Médicos
- Cadastro, atualização, listagem e exclusão de Pacientes
- Agendamento e cancelamento de Consultas


## Tecnologias Utilizadas

- **Linguagem:** Java 21+
- **Gerenciador de Dependências:** Maven
- **Banco de Dados:** MySQL 8
- **Ambiente de Banco:** Docker
- **Interface de Gerenciamento do Banco:** phpMyAdmin
- **Driver JDBC:** MySQL Connector/J


## Estrutura de Pastas

```
javaclinic_lpoo_c3/
├── src/
│   ├── main/java/br/faesa/javaclinic_lpoo_c3/
│        ├── model/              # Classes de entidade
│        ├── controller/         # Controle das operações CRUD
│        ├── conexion/           # Conexão com o banco via JDBC
│        ├── reports/            # Geração de relatórios
│        ├── view/               # Telas do sistema
│        └── principal/          # Ponto de entrada da aplicação
│
├── sql/                         # Scripts SQL de criação e inserção de dados  
│   ├── create_tables_javaclinic.sql
│   ├── insert_samples_records.sql
│   └── insert_samples_related_records.sql
│
├── mysql_data/                 # Dados persistentes do banco (criado pelo Docker)
├── docker-compose.yml          # Configuração dos containers MySQL e phpMyAdmin
├── pom.xml                     # Configurações Maven e dependências
└── README.md
```

## Executando o Projeto em Linux

O projeto já inclui um arquivo docker-compose.yml para subir o ambiente completo do banco de dados.

### 1️⃣ Pré-requisitos

Verifique se você tem tudo instalado (e funcionando):

```bash
  java -version             # deve mostrar "openjdk 21" ou superior
  mvn -version              # deve mostrar a versão do Maven
  docker --version          # deve mostrar a versão do Docker
```

Se algum comando não for reconhecido, instale antes de continuar:

```bash
  sudo apt update
  sudo apt install openjdk-21-jdk maven docker.io -y
```

### 2️⃣ Subir o ambiente

No terminal, dentro da pasta raiz do projeto:

```bash
  docker compose up -d
```
Isso criará:
- Um container MySQL (porta 3307)
- Um container phpMyAdmin (porta 8080)

### 3️⃣ Acessar o phpMyAdmin

Abra no navegador: http://localhost:8080

### 4️⃣ Scripts SQL

Os scripts estão em /sql e são automaticamente executados na primeira vez que o container é criado:

- create_tables_javaclinic.sql — Criação do banco e tabelas
- insert_samples_records.sql — Dados iniciais de médicos e pacientes
- insert_samples_related_records.sql — Inserção de consultas

### ️5️⃣ Compilar o Projeto

Ainda na pasta raiz do projeto (onde está o pom.xml), rode:

```bash
  mvn clean compile
```

Isso baixa as dependências (MySQL Connector/J) e compila o código-fonte.

### 6️⃣ Executar a classe Principal

Para executar a classe Principal e iniciar a aplicação, rode:

```bash
  mvn exec:java -Dexec.mainClass="br.faesa.javaclinic_c2.principal.Principal"
```

### 7️⃣ Parar o banco e limpar tudo

Depois que terminar de testar:

```bash
  docker compose down
```


## Dependências (Maven)

No arquivo pom.xml, as dependências do MySQL Connector/J e do Flatlaf foram adicionadas:

```
<dependencies>
    <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <version>9.3.0</version>
     </dependency>
     
     <dependency>
            <groupId>com.formdev</groupId>
            <artifactId>flatlaf</artifactId>
            <version>3.6.2</version>
        </dependency>
</dependencies>
```
