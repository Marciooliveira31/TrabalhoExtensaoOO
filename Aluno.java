package entrada.alunos;

import java.io.Serializable;

public class Aluno implements Serializable {
    private static final long serialVersionUID = 1L;  // Necessário para a serialização
    private String nome;
    private String codigo;

    public Aluno(String nome, String codigo) {
        this.nome = nome;
        this.codigo = codigo;
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    @Override
    public String toString() {
        return "Nome: " + nome + ", Código: " + codigo;
    }
}
