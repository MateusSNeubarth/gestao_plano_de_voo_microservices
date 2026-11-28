package com.projarc.trabalhofinal.gateway;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;
import java.util.concurrent.TimeoutException;

import static org.springframework.cloud.gateway.server.mvc.common.MvcUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR;

@RestController
public class FallbackController {

    @RequestMapping("/fallback/{servico}")
    public ResponseEntity<ProblemDetail> fallback(@PathVariable String servico, HttpServletRequest request) {
        ProblemDetail problema = timeout((Throwable) request.getAttribute(CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR))
                ? ProblemDetail.forStatusAndDetail(HttpStatus.GATEWAY_TIMEOUT, "O serviço " + servico + " não respondeu a tempo")
                : ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, "O serviço " + servico + " está indisponível");
        problema.setProperty("servico", servico);
        return ResponseEntity.status(problema.getStatus()).body(problema);
    }

    // o timeout chega embrulhado (ex.: ResourceAccessException → SocketTimeoutException), então procura na cadeia de causas
    private static boolean timeout(Throwable erro) {
        for (Throwable e = erro; e != null; e = e.getCause()) {
            if (e instanceof SocketTimeoutException || e instanceof HttpTimeoutException || e instanceof TimeoutException) {
                return true;
            }
        }
        return false;
    }
}
