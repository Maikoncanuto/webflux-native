package io.ceabr.apidocumentvalidation.controllers;

import io.ceabr.apidocumentvalidation.records.RequestEmail;
import io.ceabr.apidocumentvalidation.records.ResponseEmail;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

@Controller
public class EmailSocket {
    private static final Logger log = getLogger(EmailSocket.class.getName());

    @MessageMapping("email")
    public Mono<ResponseEmail> validate(RequestEmail email) {
        log.info("Validando email " + email.email());
        return Mono.just(new ResponseEmail(Boolean.TRUE));
    }

}
