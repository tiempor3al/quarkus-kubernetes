package org.acme;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.vertx.core.impl.ConcurrentHashSet;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestStreamElementType;

import java.time.Duration;
import java.util.stream.Collectors;

@Path("/")
public class WelcomeResource {

    private ConcurrentHashSet<String> identifiers = new ConcurrentHashSet<>();

    private Multi<String> ticks = Multi.createFrom().ticks().every(Duration.ofSeconds(1))
            .onItem().transform(tick -> {
                Log.info("tick");
                return identifiers.stream().collect(Collectors.joining(","));
            })
            .onSubscription().invoke(() -> Log.info("Starting to emit ticks"))
            .onCancellation().invoke(() -> Log.info("No more ticks"))
            .broadcast()
            .withCancellationAfterLastSubscriberDeparture()
            .toAtLeast(1);


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String welcome() {
        return "This a Quarkus application running in K3S to test CI/CD pipeline";
    }



    @GET
    @Path("ticks/{id}")
    @RestStreamElementType(MediaType.TEXT_PLAIN)
    public Multi<String> ticks(String id) {
        Log.info("New client with id " + id);
        identifiers.add(id);
        return ticks.onCancellation().invoke(() -> {
            Log.info("Removing client with id " + id);
            identifiers.remove(id);
        });
    }
}
