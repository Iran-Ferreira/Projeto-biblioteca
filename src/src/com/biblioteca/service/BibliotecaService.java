package com.biblioteca.service;

import com.biblioteca.exception.BibliotecaException;
import com.biblioteca.exception.LivroException;
import com.biblioteca.exception.UsuarioException;
import com.biblioteca.model.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BibliotecaService {

    // Constante para a multa
    private static final double MULTA_DIARIA = 2.50;

    // Listas que simulam o banco de dados em memória
    private final List<Livro> catalogoLivros = new ArrayList<>();
    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Emprestimo> emprestimosAtivos = new ArrayList<>();
    private final List<Emprestimo> historicoEmprestimos = new ArrayList<>(); // Histórico total

    // --- Métodos de Administrador ---

    public Livro adicionarLivro(Administrador admin, String titulo, String autor) {
        if (admin == null) {
            throw new UsuarioException("Apenas administradores podem adicionar livros.");
        }
        Livro novoLivro = new Livro(titulo, autor);
        catalogoLivros.add(novoLivro);
        System.out.println("Admin '" + admin.getNome() + "' adicionou o livro: " + novoLivro.getTitulo());
        return novoLivro;
    }

    public void removerLivro(Administrador admin, UUID livroId) {
        if (admin == null) {
            throw new UsuarioException("Apenas administradores podem remover livros.");
        }
        Livro livro = buscarLivroPorId(livroId)
                .orElseThrow(() -> new LivroException("Livro com ID " + livroId + " não encontrado."));

        if (!livro.isDisponivel()) {
            throw new LivroException("Não é possível remover um livro que está emprestado.");
        }

        catalogoLivros.remove(livro);
        System.out.println("Admin '" + admin.getNome() + "' removeu o livro: " + livro.getTitulo());
    }

    // --- Métodos de Usuário ---

    public Leitor cadastrarLeitor(String nome) {
        Leitor novoLeitor = new Leitor(nome);
        usuarios.add(novoLeitor);
        System.out.println("Leitor cadastrado: " + novoLeitor.getNome());
        return novoLeitor;
    }

    public Administrador cadastrarAdministrador(String nome) {
        Administrador novoAdmin = new Administrador(nome);
        usuarios.add(novoAdmin);
        System.out.println("Administrador cadastrado: " + novoAdmin.getNome());
        return novoAdmin;
    }

    // --- Métodos de Empréstimo e Devolução ---

    public Emprestimo realizarEmprestimo(UUID leitorId, UUID livroId, LocalDate dataPrazo) {
        // 1. Validar e encontrar o Leitor
        Leitor leitor = (Leitor) usuarios.stream()
                .filter(u -> u.getId().equals(leitorId) && u instanceof Leitor)
                .findFirst()
                .orElseThrow(() -> new UsuarioException("Leitor com ID " + leitorId + " não encontrado."));

        // 2. Validar e encontrar o Livro
        Livro livro = buscarLivroPorId(livroId)
                .orElseThrow(() -> new LivroException("Livro com ID " + livroId + " não encontrado."));

        // 3. Regra de negócio: Livro precisa estar disponível
        if (!livro.isDisponivel()) {
            throw new LivroException("O livro '" + livro.getTitulo() + "' já está emprestado.");
        }

        // 4. Efetivar o empréstimo
        livro.emprestar(); // Muda o estado do livro
        Emprestimo emprestimo = new Emprestimo(
                UUID.randomUUID(),
                livro,
                leitor,
                LocalDate.now(),
                dataPrazo
        );

        emprestimosAtivos.add(emprestimo);
        System.out.println("EMPRÉSTIMO REALIZADO: " + leitor.getNome() + " pegou '" + livro.getTitulo() + "'");
        return emprestimo;
    }

    public double realizarDevolucao(UUID emprestimoId, LocalDate dataDevolucaoReal) {
        // 1. Encontrar o empréstimo ativo
        Emprestimo emprestimo = emprestimosAtivos.stream()
                .filter(e -> e.id().equals(emprestimoId))
                .findFirst()
                .orElseThrow(() -> new BibliotecaException("Empréstimo com ID " + emprestimoId + " não encontrado ou já devolvido."));

        // 2. Mover da lista de ativos para o histórico
        emprestimosAtivos.remove(emprestimo);
        historicoEmprestimos.add(emprestimo);

        // 3. Devolver o livro ao catálogo
        emprestimo.livro().devolver();

        // 4. Calcular a multa
        double multa = calcularMulta(emprestimo.dataPrazoDevolucao(), dataDevolucaoReal);

        System.out.println("DEVOLUÇÃO REALIZADA: '" + emprestimo.livro().getTitulo() + "'");
        if (multa > 0) {
            System.out.println("ATENÇÃO: Multa por atraso: R$ " + String.format("%.2f", multa));
        } else {
            System.out.println("Devolução dentro do prazo. Sem multas.");
        }
        return multa;
    }

    // Método auxiliar privado (conforme diagrama)
    private double calcularMulta(LocalDate dataPrazo, LocalDate dataDevolucaoReal) {
        if (dataDevolucaoReal.isAfter(dataPrazo)) {
            long diasAtraso = ChronoUnit.DAYS.between(dataPrazo, dataDevolucaoReal);
            return diasAtraso * MULTA_DIARIA;
        }
        return 0.0; // Sem multa
    }

    // --- Métodos de Consulta ---
    public Optional<Livro> buscarLivroPorId(UUID id) {
        return catalogoLivros.stream()
                .filter(l -> l.getId().equals(id))
                .findFirst();
    }

    public void exibirCatalogo() {
        System.out.println("\n--- CATÁLOGO DA BIBLIOTECA ---");
        if (catalogoLivros.isEmpty()) {
            System.out.println("O catálogo está vazio.");
            return;
        }
        catalogoLivros.forEach(System.out::println);
    }

    public void exibirHistoricoLeitor(UUID leitorId) {
        System.out.println("\n--- HISTÓRICO DO LEITOR " + leitorId.toString().substring(0, 8) + " ---");

        //Filtramos a lista de 'Usuario' para achar o 'Leitor'
        Leitor leitor = (Leitor) usuarios.stream()
                .filter(u -> u.getId().equals(leitorId) && u instanceof Leitor)
                .findFirst()
                .orElse(null);

        if (leitor == null) {
            System.out.println("Leitor não encontrado.");
            return;
        }

        System.out.println("Nome: " + leitor.getNome());

        // Busca no histórico geral
        List<Emprestimo> historico = historicoEmprestimos.stream()
                .filter(e -> e.leitor().equals(leitor))
                .toList(); // Java 10+

        if (historico.isEmpty()) {
            System.out.println("Nenhum empréstimo finalizado no histórico.");
        } else {
            historico.forEach(System.out::println);
        }

        // Busca nos empréstimos ativos
        List<Emprestimo> ativos = emprestimosAtivos.stream()
                .filter(e -> e.leitor().equals(leitor))
                .toList();

        if (ativos.isEmpty()) {
            System.out.println("Nenhum empréstimo ativo no momento.");
        } else {
            System.out.println("\nEmpréstimos Ativos:");
            ativos.forEach(System.out::println);
        }
    }

// --- MÉTODOS DE CONSULTA (Getters para o Menu) ---
    /**
     * Retorna uma visão NÃO-MODIFICÁVEL do catálogo.
     * Usado pelo menu para exibir opções ao usuário.
     */
    public List<Livro> getCatalogo() {
        // Retorna uma cópia para proteger a lista original (Encapsulamento)
        return java.util.Collections.unmodifiableList(catalogoLivros);
    }

    /**
     * Retorna uma visão dos usuários.
     */
    public List<Usuario> getUsuarios() {
        return java.util.Collections.unmodifiableList(usuarios);
    }

    /**
     * Retorna uma visão dos empréstimos ativos.
     */
    public List<Emprestimo> getEmprestimosAtivos() {
        return java.util.Collections.unmodifiableList(emprestimosAtivos);
    }

}