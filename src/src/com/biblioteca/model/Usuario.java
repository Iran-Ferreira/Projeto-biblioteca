package com.biblioteca.model;

import java.util.UUID;

/**
 * Classe base abstrata e SELADA para Usuários.
 * 'permits' restringe a herança apenas para Leitor e Administrador.
 */
public abstract sealed class Usuario permits Leitor, Administrador {

    private final UUID id;
    private String nome;

    public Usuario(String nome) {
        this.id = UUID.randomUUID(); // Gera um ID único
        this.nome = nome;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    // Setters
    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        // 'getClass().getSimpleName()' mostrará "Leitor" ou "Administrador"
        return getClass().getSimpleName() + " [ID=" + id + ", Nome=" + nome + "]";
    }
}