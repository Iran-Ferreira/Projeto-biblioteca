package com.biblioteca.model;

import com.biblioteca.exception.LivroException;
import java.util.UUID;

public class Livro {

    private final UUID id;
    private String titulo;
    private String autor;
    private boolean disponivel;

    public Livro(String titulo, String autor) {
        this.id = UUID.randomUUID();
        this.titulo = titulo;
        this.autor = autor;
        this.disponivel = true; // Todo livro começa disponível
    }

    // Métodos de controle de estado (conforme solicitado no diagrama)
    public void emprestar() {
        if (!this.disponivel) {
            throw new LivroException("O livro '" + titulo + "' não está disponível.");
        }
        this.disponivel = false;
    }

    public void devolver() {
        this.disponivel = true;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    @Override
    public String toString() {
        String status = disponivel ? "Disponível" : "Emprestado";
        return "Livro [ID=" + id.toString().substring(0, 8) + // ID curto
                ", Titulo=" + titulo +
                ", Autor=" + autor +
                ", Status=" + status + "]";
    }
}