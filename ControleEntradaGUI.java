package entrada.alunos;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

public class ControleEntradaGUI {
    private static CadastroAlunos cadastro = new CadastroAlunos();
    private JLabel contadorPresentesLabel;

    public ControleEntradaGUI() {
        JFrame frame = new JFrame("Controle de Entrada de Alunos");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Painel principal
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1)); // 6 linhas, 1 coluna

        // Botão para cadastrar aluno
        JButton cadastrarButton = new JButton("Cadastrar Aluno");
        cadastrarButton.addActionListener(e -> cadastrarAluno());
        panel.add(cadastrarButton);

        // Botão para registrar chegada
        JButton registrarChegadaButton = new JButton("Registrar Chegada");
        registrarChegadaButton.addActionListener(e -> registrarChegada());
        panel.add(registrarChegadaButton);

        // Botão para exibir alunos presentes
        JButton exibirPresentesButton = new JButton("Exibir Alunos Presentes");
        exibirPresentesButton.addActionListener(e -> exibirPresentes());
        panel.add(exibirPresentesButton);

        // Botão para exibir alunos cadastrados e editar
        JButton exibirCadastradosButton = new JButton("Exibir Alunos Cadastrados");
        exibirCadastradosButton.addActionListener(e -> exibirAlunosCadastrados());
        panel.add(exibirCadastradosButton);

        // Contador de alunos presentes
        contadorPresentesLabel = new JLabel("Alunos presentes: 0");
        panel.add(contadorPresentesLabel);

        // Botão para sair
        JButton sairButton = new JButton("Sair");
        sairButton.addActionListener(e -> System.exit(0));
        panel.add(sairButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    private void cadastrarAluno() {
        String nome = JOptionPane.showInputDialog("Digite o nome do aluno:");
        String codigo = JOptionPane.showInputDialog("Digite o código do aluno:");

        if (nome != null && codigo != null) {
            Aluno aluno = new Aluno(nome, codigo);

            // Tentar cadastrar até o código ser válido
            while (!cadastro.cadastrarAluno(aluno)) {
                // Exibe opções para digitar o código novamente ou voltar ao menu
                int resposta = JOptionPane.showOptionDialog(null,
                        "Código já existe! O que deseja fazer?",
                        "Erro",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE,
                        null,
                        new String[]{"Tentar Novamente", "Voltar ao Menu"},
                        "Tentar Novamente");

                if (resposta == JOptionPane.YES_OPTION) {
                    // Se o usuário escolher "Tentar Novamente", pedir só o código novamente
                    codigo = JOptionPane.showInputDialog("Digite um novo código para o aluno:");
                    if (codigo == null) {
                        return;  // Se o usuário cancelar, voltar ao menu
                    }
                    aluno = new Aluno(nome, codigo);  // Atualiza o objeto aluno com o novo código
                } else {
                    // Se o usuário escolher "Voltar ao Menu", encerrar o cadastro
                    return;
                }
            }
            JOptionPane.showMessageDialog(null, "Aluno cadastrado com sucesso!");
        }
    }

    private void registrarChegada() {
        boolean continuar = true;

        while (continuar) {
            String codigo = JOptionPane.showInputDialog("Digite o código do aluno para registrar a chegada (ou cancele para parar):");

            if (codigo == null || codigo.trim().isEmpty()) {
                continuar = false; // Sai do loop quando o usuário clica em cancelar ou deixa o campo vazio
            } else {
                Aluno aluno = cadastro.buscarAlunoPorCodigo(codigo);
                if (aluno != null) {
                    // Exibe o nome do aluno e pede confirmação
                    int resposta = JOptionPane.showOptionDialog(null,
                            "Aluno: " + aluno.getNome() + "\nConfirmar chegada?",
                            "Confirmação de Chegada",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            new String[]{"Confirmar", "Tentar Novamente", "Voltar ao Menu"},
                            "Confirmar");

                    if (resposta == JOptionPane.YES_OPTION) {
                        // Confirma chegada
                        if (cadastro.registrarChegada(codigo)) {
                            JOptionPane.showMessageDialog(null, "Chegada registrada com sucesso!");
                            atualizarContadorPresentes();
                        } else {
                            JOptionPane.showMessageDialog(null, "Erro: Aluno já registrado como presente.");
                        }
                    } else if (resposta == JOptionPane.NO_OPTION) {
                        // Tenta novamente, recomeçando o loop
                        continue;
                    } else {
                        // Voltar ao menu anterior (encerra o loop)
                        continuar = false;
                    }
                } else {
                    // Exibe mensagem de erro se o código for inválido
                    int resposta = JOptionPane.showOptionDialog(null,
                            "Código inválido. O que deseja fazer?",
                            "Erro",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.ERROR_MESSAGE,
                            null,
                            new String[]{"Tentar Novamente", "Voltar ao Menu"},
                            "Tentar Novamente");

                    if (resposta == JOptionPane.NO_OPTION) {
                        continuar = false;  // Encerra o loop e volta ao menu
                    }
                }
            }
        }
    }



    private void exibirPresentes() {
        Set<Aluno> presentes = cadastro.getAlunosPresentes();
        StringBuilder listaPresentes = new StringBuilder("Alunos presentes:\n");

        for (Aluno aluno : presentes) {
            listaPresentes.append(aluno.toString()).append("\n\n");
        }

        if (presentes.isEmpty()) {
            listaPresentes.append("Nenhum aluno presente.");
        }

        JOptionPane.showMessageDialog(null, listaPresentes.toString());
    }

    private void exibirAlunosCadastrados() {
        // Obter a lista de alunos
        Set<Aluno> alunos = cadastro.getAlunosCadastrados();

        if (alunos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum aluno cadastrado.");
            return;
        }

        // Converter lista de alunos para um array para exibir na JList
        Aluno[] arrayAlunos = alunos.toArray(new Aluno[0]);

        // Criar a JList para mostrar os alunos
        JList<Aluno> listaAlunos = new JList<>(arrayAlunos);
        listaAlunos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Adicionar um JScrollPane para permitir rolagem se a lista for longa
        JScrollPane scrollPane = new JScrollPane(listaAlunos);

        // Mostrar a lista de alunos em um diálogo
        int resultado = JOptionPane.showOptionDialog(null, scrollPane, "Alunos Cadastrados",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"Editar", "Voltar"}, "Editar");

        // Se o usuário escolheu "Editar" e selecionou um aluno
        if (resultado == 0 && listaAlunos.getSelectedValue() != null) {
            Aluno alunoSelecionado = listaAlunos.getSelectedValue();
            editarAluno(alunoSelecionado);
        }
    }

    private void editarAluno(Aluno alunoSelecionado) {
        // Perguntar ao usuário se ele quer editar o nome ou o código
        String[] opcoes = {"Editar Nome", "Editar Código", "Cancelar"};
        int escolha = JOptionPane.showOptionDialog(null, "O que você deseja editar?",
                "Editar Aluno", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, opcoes, opcoes[0]);

        if (escolha == 0) {  // Editar Nome
            String novoNome = JOptionPane.showInputDialog("Digite o novo nome:", alunoSelecionado.getNome());
            if (novoNome != null && !novoNome.trim().isEmpty()) {
                alunoSelecionado.setNome(novoNome);
                cadastro.atualizarAluno(alunoSelecionado.getCodigo(), alunoSelecionado);  // Atualiza o aluno no cadastro e no arquivo
                JOptionPane.showMessageDialog(null, "Nome alterado com sucesso!");
            }
        } else if (escolha == 1) {  // Editar Código
            String novoCodigo = JOptionPane.showInputDialog("Digite o novo código:", alunoSelecionado.getCodigo());
            if (novoCodigo != null && !novoCodigo.trim().isEmpty()) {
                if (!cadastro.codigoJaExiste(novoCodigo)) {
                    // Armazena o código antigo
                    String codigoAntigo = alunoSelecionado.getCodigo();

                    // Atualiza o código do aluno
                    alunoSelecionado.setCodigo(novoCodigo);

                    // Reinsere o aluno no cadastro usando o código antigo
                    cadastro.atualizarAluno(codigoAntigo, alunoSelecionado);

                    JOptionPane.showMessageDialog(null, "Código alterado com sucesso!");
                } else {
                    JOptionPane.showMessageDialog(null, "Erro: Código já cadastrado.");
                }
            }
        }
    }

    private void atualizarContadorPresentes() {
        contadorPresentesLabel.setText("Alunos presentes: " + cadastro.getNumeroDePresentes());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ControleEntradaGUI::new);
    }
}




