package com.projarc.trabalhofinal.malhaaerea.domain.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource) {
        super(String.format("%s não encontrado(a)", resource));
    }
}
