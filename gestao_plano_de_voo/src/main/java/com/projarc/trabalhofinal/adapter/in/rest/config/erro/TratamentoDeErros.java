package com.projarc.trabalhofinal.adapter.in.rest.config.erro;

import com.projarc.trabalhofinal.adapter.in.rest.dto.plano.response.ValidacaoResponse;
import com.projarc.trabalhofinal.adapter.in.rest.mapper.CicloDeVidaRestMapper;
import com.projarc.trabalhofinal.domain.exception.ConflitoDeReservaException;
import com.projarc.trabalhofinal.domain.exception.PlanoInvalidoException;
import com.projarc.trabalhofinal.domain.exception.PlanoNaoEncontradoException;
import com.projarc.trabalhofinal.domain.exception.RedeAereaIndisponivelException;
import com.projarc.trabalhofinal.domain.exception.TransicaoInvalidaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TratamentoDeErros {

    @ExceptionHandler(PlanoNaoEncontradoException.class)
    public ResponseEntity<Void> planoNaoEncontrado() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler({TransicaoInvalidaException.class, ConflitoDeReservaException.class})
    public ResponseEntity<Void> conflito() {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(PlanoInvalidoException.class)
    public ResponseEntity<ValidacaoResponse> planoInvalido(PlanoInvalidoException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(CicloDeVidaRestMapper.toValidacao(e.getProblemas()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> dadosInvalidos() {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
    }

    @ExceptionHandler(RedeAereaIndisponivelException.class)
    public ProblemDetail redeAereaIndisponivel(RedeAereaIndisponivelException e) {
        var problema = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
        problema.setTitle("Rede aérea indisponível");
        return problema;
    }
}
