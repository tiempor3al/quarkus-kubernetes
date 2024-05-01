package org.acme.config;

import io.quarkus.logging.Log;
import io.smallrye.common.annotation.Identifier;
import io.vertx.amqp.AmqpClientOptions;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;


@ApplicationScoped
public class AmqpConfig {

    @Produces
    @Identifier("amqp-options")
    public AmqpClientOptions getAmqpClientOptions() {

        Log.info("Using amqp options");
        return new AmqpClientOptions()
                .setPort(61616)
                .setHeartbeat(30);
    }
}

