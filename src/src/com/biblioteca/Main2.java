package com.biblioteca;

import com.biblioteca.exception.BibliotecaException;
import com.biblioteca.model.Administrador;
import com.biblioteca.model.Emprestimo;
import com.biblioteca.model.Leitor;
import com.biblioteca.model.Livro;
import com.biblioteca.service.BibliotecaService;

import java.time.LocalDate;

public class Main2 {

    public static void main(String[] args) {
        BibliotecaService biblioteca = new BibliotecaService();
        LocalDate hoje = LocalDate.now();

        System.out.println("--- 1. Configuração Inicial (Admin e Leitores) ---");
        Administrador admin = biblioteca.cadastrarAdministrador("Sr. Administrador");
        Leitor leitor1 = biblioteca.cadastrarLeitor("Ana Silva");
        Leitor leitor2 = biblioteca.cadastrarLeitor("Bruno Costa");
        System.out.println("--------------------------------------------------\n");


        System.out.println("--- 2. Teste: Admin Adiciona Livros ---");
        // Agora, capturamos o retorno do método diretamente
        Livro livroJava = biblioteca.adicionarLivro(admin, "Java 21: A Revolução", "Autor Java");
        Livro livroPatterns = biblioteca.adicionarLivro(admin, "Design Patterns", "GoF");
        biblioteca.adicionarLivro(admin, "Clean Code", "Robert C. Martin");

        biblioteca.exibirCatalogo();
        System.out.println("--------------------------------------------------\n");


        System.out.println("--- 3. Teste: Empréstimo (Sucesso) ---");
        // Agora podemos usar as variáveis 'livroJava' e 'livroPatterns'
            // que pegamos de forma segura no passo anterior.
        LocalDate prazoAna = hoje.plusDays(7);
            // O 'livroJava.getId()' vai funcionar perfeitamente
        Emprestimo emprestimoAna = biblioteca.realizarEmprestimo(leitor1.getId(), livroJava.getId(), prazoAna);
        biblioteca.exibirCatalogo(); // Deve mostrar "Java 21" como Emprestado
        System.out.println("--------------------------------------------------\n");


        System.out.println("--- 4. Teste: Exceção (Tentar emprestar livro já pego) ---");
        try {
            // Bruno tenta pegar o mesmo livro que Ana pegou
            biblioteca.realizarEmprestimo(leitor2.getId(), livroJava.getId(), hoje.plusDays(7));
        } catch (BibliotecaException e) {
            System.out.println("ERRO (Esperado): " + e.getMessage());
        }
        System.out.println("--------------------------------------------------\n");


        System.out.println("--- 5. Teste: Devolução (Sem Multa) ---");
        // Ana devolve 5 dias depois (antes do prazo de 7 dias)
        LocalDate dataDevolucaoAna = hoje.plusDays(5);
        biblioteca.realizarDevolucao(emprestimoAna.id(), dataDevolucaoAna);
        biblioteca.exibirCatalogo(); // Livro Java deve estar disponível de novo
        System.out.println("--------------------------------------------------\n");


        System.out.println("--- 6. Teste: Empréstimo e Devolução (Com Multa) ---");
        // Bruno agora pega o livro de Design Patterns, com prazo de 10 dias
        LocalDate prazoBruno = hoje.plusDays(10);
        Emprestimo emprestimoBruno = biblioteca.realizarEmprestimo(leitor2.getId(), livroPatterns.getId(), prazoBruno);

        // Bruno devolve com 3 dias de atraso
        LocalDate dataDevolucaoBruno = hoje.plusDays(13); // Prazo era 10 dias
        System.out.println("Simulando devolução com 3 dias de atraso...");
        double multa = biblioteca.realizarDevolucao(emprestimoBruno.id(), dataDevolucaoBruno);
        System.out.println("Multa calculada: R$ " + String.format("%.2f", multa)); // Deve ser R$ 7.50
        System.out.println("--------------------------------------------------\n");


        System.out.println("--- 7. Teste: Histórico de Empréstimos ---");
        biblioteca.exibirHistoricoLeitor(leitor1.getId()); // Histórico de Ana
        biblioteca.exibirHistoricoLeitor(leitor2.getId()); // Histórico de Bruno
        System.out.println("--------------------------------------------------\n");
    }
}