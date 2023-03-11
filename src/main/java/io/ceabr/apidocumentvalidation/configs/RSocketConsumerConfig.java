package io.ceabr.apidocumentvalidation.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.codec.DataBufferDecoder;
import org.springframework.core.codec.DataBufferEncoder;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;

import static io.rsocket.frame.decoder.PayloadDecoder.ZERO_COPY;
import static java.time.Duration.ofSeconds;
import static org.springframework.messaging.rsocket.RSocketRequester.builder;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static reactor.util.retry.Retry.fixedDelay;

@Configuration
public class RSocketConsumerConfig {

    @Bean
    public RSocketStrategies rSocketStrategies() {
        return RSocketStrategies
                .builder()
                .encoders(encoders -> {
                    encoders.add(new DataBufferEncoder());
                    encoders.add(new Jackson2JsonEncoder());
                })
                .decoders(decoders -> {
                    decoders.add(new Jackson2JsonDecoder());
                    decoders.add(new DataBufferDecoder());
                })
                .build();
    }

    @Bean
    public RSocketRequester rSocketRequester() {
        return builder()
                .rsocketStrategies(rSocketStrategies())
                .rsocketConnector(connector -> connector
                        .reconnect(fixedDelay(10, ofSeconds(2)))
                        .payloadDecoder(ZERO_COPY)
                        .dataMimeType(APPLICATION_JSON_VALUE)
                        .metadataMimeType(APPLICATION_JSON_VALUE)
                )
                .dataMimeType(APPLICATION_JSON)
                .tcp("localhost", 7000);
    }
}
