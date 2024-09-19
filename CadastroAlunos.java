package entrada.alunos;

import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CadastroAlunos implements Serializable {
    private static final long serialVersionUID = 1L;  // Necessário para a serialização
    private Map<String, Aluno> alunos = new HashMap<>();
    private Map<String, LocalDateTime> presenca = new HashMap<>();
    private static final String ARQUIVO_ALUNOS = "alunos.dat";

    // Construtor: carrega os alunos do arquivo ao iniciar
    public CadastroAlunos() {
        carregarAlunosDoArquivo();
    }

    // Cadastrar um aluno e salvar no arquivo
    public synchronized boolean cadastrarAluno(Aluno aluno) {
        if (alunos.containsKey(aluno.getCodigo())) {
            return false;  // Código já existe
        }
        alunos.put(aluno.getCodigo(), aluno);
        salvarAlunosNoArquivo();  // Atualiza o arquivo sempre que cadastrar um novo aluno
        return true;
    }

    // Buscar um aluno pelo código
    public synchronized Aluno buscarAlunoPorCodigo(String codigo) {
        return alunos.get(codigo);
    }

    // Registrar a chegada de um aluno
    public synchronized boolean registrarChegada(String codigo) {
        Aluno aluno = buscarAlunoPorCodigo(codigo);

        if (aluno != null && !presenca.containsKey(codigo)) {
            presenca.put(codigo, LocalDateTime.now());
            return true;
        }
        return false;  // Se o aluno não existe ou já está registrado, retorna falso
    }

    // Atualizar um aluno
    public synchronized boolean atualizarAluno(String codigoAntigo, Aluno alunoAtualizado) {
        if (alunos.containsKey(codigoAntigo)) {
            alunos.remove(codigoAntigo);  // Remove o aluno com o código antigo
            alunos.put(alunoAtualizado.getCodigo(), alunoAtualizado);  // Adiciona o aluno com o novo código ou dados atualizados
            salvarAlunosNoArquivo();  // Atualiza o arquivo
            return true;
        }
        return false;
    }

    // Verificar se um código já está cadastrado
    public synchronized boolean codigoJaExiste(String codigo) {
        return alunos.containsKey(codigo);
    }

    // Retornar o número de alunos presentes
    public synchronized int getNumeroDePresentes() {
        return presenca.size();
    }

    // Retornar a lista de alunos presentes
    public synchronized Set<Aluno> getAlunosPresentes() {
        Set<Aluno> presentes = new HashSet<>();
        for (String codigo : presenca.keySet()) {
            presentes.add(alunos.get(codigo));
        }
        return presentes;
    }

    // Retornar a lista de todos os alunos cadastrados
    public synchronized Set<Aluno> getAlunosCadastrados() {
        return new HashSet<>(alunos.values());
    }

    // Salvar os alunos no arquivo
    private synchronized void salvarAlunosNoArquivo() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_ALUNOS))) {
            oos.writeObject(alunos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Carregar os alunos do arquivo
    private synchronized void carregarAlunosDoArquivo() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARQUIVO_ALUNOS))) {
            alunos = (Map<String, Aluno>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            alunos = new HashMap<>();  // Se o arquivo não existir ou houver erro, inicializa um novo mapa vazio
        }
    }
}
