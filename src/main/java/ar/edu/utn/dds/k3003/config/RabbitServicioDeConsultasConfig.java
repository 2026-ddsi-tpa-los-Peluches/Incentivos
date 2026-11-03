package ar.edu.utn.dds.k3003.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitServicioDeConsultasConfig {

    public static final String EXCHANGE = "donatrack.eventos";

    @Bean
    TopicExchange exchange() { return new TopicExchange(EXCHANGE); }

    @Bean
    Jackson2JsonMessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}


