package io.ceabr.apidocumentvalidation.controllers;

import io.ceabr.apidocumentvalidation.records.RequestEmail;
import io.ceabr.apidocumentvalidation.records.ResponseEmail;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    @Autowired
    private RSocketRequester rSocketRequester;

    @GetMapping("/email")
    public Publisher<ResponseEmail> getEmail() {
        final var request = new RequestEmail("maikoncanuto@gmail.com");

        return rSocketRequester.route("email")
                .data(request)
                .retrieveMono(ResponseEmail.class);

    }

}
