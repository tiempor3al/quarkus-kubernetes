package org.acme;

import io.quarkus.logging.Log;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

import io.smallrye.mutiny.Multi;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

import java.time.Duration;
@Path("/")
public class WelcomeResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String welcome() {
        return "This a Quarkus application running in K3S to test CI/CD pipeline";
    }

    @Path("sse")
    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void streamEvents(@Context SseEventSink eventSink, @Context Sse sse) {
        Multi<String> events = Multi.createFrom().ticks().every(Duration.ofSeconds(1))
                .onItem().transform(n -> "message " + n);


        events.subscribe().with(
                data -> {
                    if(!eventSink.isClosed()) {
                        eventSink.send(sse.newEvent(data));
                    }
                },
                failure -> {
                    Log.info("failure " + failure.getMessage());
                    if(!eventSink.isClosed()) {
                        eventSink.close();
                    }
                },
                () -> {

                    Log.info("Complete");

                }
        );
    }
}
