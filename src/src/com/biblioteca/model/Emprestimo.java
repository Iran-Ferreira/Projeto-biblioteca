package com.biblioteca.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Representa um empréstimo ATIVO.
 * Usei um Record para imutabilidade e simplicidade.
 */
public record Emprestimo(
        UUID id,
        Livro livro,
        Leitor leitor,
        LocalDate dataEmprestimo,
        LocalDate dataPrazoDevolucao
) {

    @Override
    public String toString() {
        return "Empréstimo [ID=" + id.toString().substring(0, 8) +
                ", Leitor=" + leitor.getNome() +
                ", Livro=" + livro.getTitulo() +
                ", Prazo=" + dataPrazoDevolucao + "]";
    }
}