package com.biblioteca.exception;

/**
 * Exceção base para erros de negócio da biblioteca.
 * Usamos RuntimeException para simplificar o código (não exigindo 'throws').
 */
public class BibliotecaException extends RuntimeException {
    public BibliotecaException(String message) {
        super(message);
    }
}