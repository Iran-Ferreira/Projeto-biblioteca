package com.biblioteca;

import com.biblioteca.exception.BibliotecaException;
import com.biblioteca.model.*;
import com.biblioteca.service.BibliotecaService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

public class Main {

    // Criamos o service e o scanner como estáticos
    // para que todos os métodos da Main possam usá-los.
    private static final BibliotecaService biblioteca = new BibliotecaService();
    private static final Scanner scanner = new Scanner(System.in);

    // Precisamos de um Admin para gerenciar o acervo.
    // Em um sistema real, haveria um login, mas aqui vamos criar um.
    private static Administrador adminGlobal;

    public static void main(String[] args) {
        // 1. Configuração inicial
        setupInicial();

        // 2. Loop principal do menu
        loopPrincipal();

        // 3. Fechar o scanner ao sair
        scanner.close();
        System.out.println("Obrigado por usar a Biblioteca Online. Volte sempre!");
    }

    /**
     * Prepara dados iniciais (como o admin) para o sistema funcionar.
     */
    private static void setupInicial() {
        System.out.println("=== Bem-vindo ao Sistema de Biblioteca Online ===");
        adminGlobal = biblioteca.cadastrarAdministrador("Admin Padrão");

        // Adiciona alguns livros para teste
        biblioteca.adicionarLivro(adminGlobal, "Java 21: A Revolução", "Autor Java");
        biblioteca.adicionarLivro(adminGlobal, "Clean Code", "Robert C. Martin");
        System.out.println("--------------------------------------------------\n");
    }

    /**
     * O loop principal que exibe o menu e aguarda a entrada.
     */
    private static void loopPrincipal() {
        boolean rodando = true;

        while (rodando) {
            exibirMenuPrincipal();
            int opcao = lerOpcao(); // Método seguro para ler números

            try {
                switch (opcao) {
                    case 1: // Gerenciar Acervo (Admin)
                        menuGerenciarAcervo();
                        break;
                    case 2: // Gerenciar Usuários
                        menuGerenciarUsuarios();
                        break;
                    case 3: // Gerenciar Empréstimos
                        menuGerenciarEmprestimos();
                        break;
                    case 0:
                        rodando = false;
                        break;
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                        break;
                }
            } catch (BibliotecaException e) {
                // Captura exceções de negócio (Ex: Livro indisponível)
                System.out.println("\nERRO: " + e.getMessage());
            } catch (Exception e) {
                // Captura exceções inesperadas
                System.out.println("\nERRO INESPERADO: " + e.getMessage());
            }

            pressioneEnterParaContinuar();
        }
    }

    private static void exibirMenuPrincipal() {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("1. Gerenciar Acervo (Livros)");
        System.out.println("2. Gerenciar Usuários (Leitores)");
        System.out.println("3. Gerenciar Empréstimos");
        System.out.println("0. Sair do Sistema");
        System.out.print("Escolha uma opção: ");
    }

    // --- Sub-Menus ---

    private static void menuGerenciarAcervo() {
        System.out.println("\n--- Gerenciar Acervo ---");
        System.out.println("1. Adicionar Livro");
        System.out.println("2. Remover Livro");
        System.out.println("3. Listar Todos os Livros");
        System.out.println("9. Voltar ao Menu Principal");
        System.out.print("Escolha uma opção: ");

        int opcao = lerOpcao();
        switch (opcao) {
            case 1: handleAdicionarLivro(); break;
            case 2: handleRemoverLivro(); break;
            case 3: biblioteca.exibirCatalogo(); break;
            case 9: break; // Volta
            default: System.out.println("Opção inválida.");
        }
    }

    private static void menuGerenciarUsuarios() {
        System.out.println("\n--- Gerenciar Usuários ---");
        System.out.println("1. Cadastrar Novo Leitor");
        System.out.println("2. Listar Todos os Usuários");
        System.out.println("3. Ver Histórico de Empréstimos de um Leitor");
        System.out.println("9. Voltar ao Menu Principal");
        System.out.print("Escolha uma opção: ");

        int opcao = lerOpcao();
        switch (opcao) {
            case 1: handleCadastrarLeitor(); break;
            case 2: handleListarUsuarios(); break;
            case 3: handleVerHistoricoLeitor(); break;
            case 9: break; // Volta
            default: System.out.println("Opção inválida.");
        }
    }

    private static void menuGerenciarEmprestimos() {
        System.out.println("\n--- Gerenciar Empréstimos ---");
        System.out.println("1. Realizar Empréstimo");
        System.out.println("2. Realizar Devolução");
        System.out.println("3. Listar Empréstimos Ativos");
        System.out.println("9. Voltar ao Menu Principal");
        System.out.print("Escolha uma opção: ");

        int opcao = lerOpcao();
        switch (opcao) {
            case 1: handleRealizarEmprestimo(); break;
            case 2: handleRealizarDevolucao(); break;
            case 3: handleListarEmprestimosAtivos(); break;
            case 9: break; // Volta
            default: System.out.println("Opção inválida.");
        }
    }

    // --- Lógica (Handlers) ---

    private static void handleAdicionarLivro() {
        System.out.print("Digite o Título do livro: ");
        String titulo = scanner.nextLine();
        System.out.print("Digite o Autor do livro: ");
        String autor = scanner.nextLine();
        biblioteca.adicionarLivro(adminGlobal, titulo, autor);
        System.out.println("Livro adicionado com sucesso!");
    }

    private static void handleRemoverLivro() {
        Livro livro = selecionarLivro(false); // false = selecionar qualquer livro
        if (livro != null) {
            biblioteca.removerLivro(adminGlobal, livro.getId());
            System.out.println("Livro removido com sucesso!");
        }
    }

    private static void handleCadastrarLeitor() {
        System.out.print("Digite o Nome do novo leitor: ");
        String nome = scanner.nextLine();
        Leitor novoLeitor = biblioteca.cadastrarLeitor(nome);
        System.out.println("Leitor cadastrado com sucesso! ID: " + novoLeitor.getId());
    }

    private static void handleListarUsuarios() {
        System.out.println("\n--- Lista de Usuários ---");
        List<Usuario> usuarios = biblioteca.getUsuarios();
        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }
        for (Usuario u : usuarios) {
            // Usamos o toString() que definimos na classe Usuario
            System.out.println(u);
        }
    }

    private static void handleVerHistoricoLeitor() {
        Leitor leitor = selecionarLeitor();
        if (leitor != null) {
            biblioteca.exibirHistoricoLeitor(leitor.getId());
        }
    }

    private static void handleRealizarEmprestimo() {
        System.out.println("--- Realizar Empréstimo ---");

        System.out.println("Selecione o Leitor:");
        Leitor leitor = selecionarLeitor();
        if (leitor == null) return; // Cancelado

        System.out.println("\nSelecione o Livro (apenas disponíveis):");
        Livro livro = selecionarLivro(true); // true = apenas disponíveis
        if (livro == null) return; // Cancelado

        System.out.print("Digite o prazo de devolução (em dias): ");
        int diasPrazo = lerOpcao();
        LocalDate dataPrazo = LocalDate.now().plusDays(diasPrazo);

        biblioteca.realizarEmprestimo(leitor.getId(), livro.getId(), dataPrazo);
        System.out.println("Empréstimo realizado com sucesso!");
    }

    private static void handleRealizarDevolucao() {
        System.out.println("--- Realizar Devolução ---");
        List<Emprestimo> ativos = biblioteca.getEmprestimosAtivos();
        if (ativos.isEmpty()) {
            System.out.println("Nenhum empréstimo ativo para devolver.");
            return;
        }

        System.out.println("Selecione o empréstimo para devolver:");
        for (int i = 0; i < ativos.size(); i++) {
            System.out.println((i + 1) + ". " + ativos.get(i));
        }
        System.out.println("0. Cancelar");
        System.out.print("Opção: ");

        int op = lerOpcao();
        if (op <= 0 || op > ativos.size()) {
            System.out.println("Operação cancelada.");
            return;
        }

        Emprestimo emprestimo = ativos.get(op - 1);
        biblioteca.realizarDevolucao(emprestimo.id(), LocalDate.now());
    }

    private static void handleListarEmprestimosAtivos() {
        System.out.println("\n--- Empréstimos Ativos ---");
        List<Emprestimo> ativos = biblioteca.getEmprestimosAtivos();
        if (ativos.isEmpty()) {
            System.out.println("Nenhum empréstimo ativo no momento.");
            return;
        }
        ativos.forEach(System.out::println);
    }

    // --- Métodos Utilitários ---

    /**
     * Exibe uma lista de leitores e permite ao usuário selecionar um.
     * @return O Leitor selecionado, ou null se cancelar.
     */
    private static Leitor selecionarLeitor() {
        List<Leitor> leitores = biblioteca.getUsuarios().stream()
                .filter(u -> u instanceof Leitor)
                .map(u -> (Leitor) u)
                .collect(Collectors.toList());

        if (leitores.isEmpty()) {
            System.out.println("Nenhum leitor cadastrado. Cadastre um leitor primeiro.");
            return null;
        }

        System.out.println("Lista de Leitores:");
        for (int i = 0; i < leitores.size(); i++) {
            System.out.println((i + 1) + ". " + leitores.get(i).getNome());
        }
        System.out.println("0. Cancelar");
        System.out.print("Opção: ");

        int op = lerOpcao();
        if (op <= 0 || op > leitores.size()) {
            System.out.println("Operação cancelada.");
            return null;
        }
        return leitores.get(op - 1);
    }

    /**
     * Exibe uma lista de livros e permite ao usuário selecionar um.
     * @param apenasDisponiveis Se true, mostra apenas livros disponíveis.
     * @return O Livro selecionado, ou null se cancelar.
     */
    private static Livro selecionarLivro(boolean apenasDisponiveis) {
        List<Livro> livros = biblioteca.getCatalogo();

        if (apenasDisponiveis) {
            livros = livros.stream()
                    .filter(Livro::isDisponivel)
                    .collect(Collectors.toList());
        }

        if (livros.isEmpty()) {
            String msg = apenasDisponiveis ? "Nenhum livro disponível no momento." : "Nenhum livro cadastrado.";
            System.out.println(msg);
            return null;
        }

        System.out.println("Lista de Livros:");
        for (int i = 0; i < livros.size(); i++) {
            System.out.println((i + 1) + ". " + livros.get(i).getTitulo() + " (" + (livros.get(i).isDisponivel() ? "Disponível" : "Emprestado") + ")");
        }
        System.out.println("0. Cancelar");
        System.out.print("Opção: ");

        int op = lerOpcao();
        if (op <= 0 || op > livros.size()) {
            System.out.println("Operação cancelada.");
            return null;
        }
        return livros.get(op - 1);
    }

    /**
     * Um leitor de 'int' seguro. Evita que o programa
     * quebre se o usuário digitar letras.
     */
    private static int lerOpcao() {
        while (true) {
            try {
                int opcao = Integer.parseInt(scanner.nextLine());
                return opcao;
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Digite apenas números: ");
            }
        }
    }

    /**
     * Pausa o console, esperando que o usuário pressione Enter.
     */
    private static void pressioneEnterParaContinuar() {
        System.out.println("\n(Pressione [ENTER] para continuar...)");
        scanner.nextLine();
    }
}