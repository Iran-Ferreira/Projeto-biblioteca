package com.biblioteca.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Representa um empréstimo ATIVO.
 * Usamos um Record para imutabilidade e simplicidade.
 */
public record Emprestimo(
        UUID id,
        Livro livro,
        Leitor leitor,
        LocalDate dataEmprestimo,
        LocalDate dataPrazoDevolucao
) {

    // O método 'calcularMulta' foi movido para o 'BibliotecaService',
    // pois ele precisa da 'dataDevolucaoReal' (que não existe neste record)
    // para fazer o cálculo no momento da devolução.

    @Override
    public String toString() {
        return "Empréstimo [ID=" + id.toString().substring(0, 8) +
                ", Leitor=" + leitor.getNome() +
                ", Livro=" + livro.getTitulo() +
                ", Prazo=" + dataPrazoDevolucao + "]";
    }
}