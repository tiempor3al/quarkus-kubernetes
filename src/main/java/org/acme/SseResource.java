package org.acme;

import io.quarkus.logging.Log;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestStreamElementType;

import java.time.Duration;
import java.time.LocalDateTime;

@Path("/")
public class SseResource {

    @Inject
    Template index;

    private Multi<String> ticks = Multi.createFrom().ticks().every(Duration.ofSeconds(1))
            .onItem().transform(tick -> LocalDateTime.now().toString())
            .onSubscription().invoke(sub -> Log.info(sub.toString()))
            .onCancellation().invoke(() -> Log.info("No more ticks"))
            .broadcast()
            .withCancellationAfterLastSubscriberDeparture()
            .toAtLeast(1);
    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance index() {
        return index.data("endpoint","/sse");
    }


    @GET
    @Path("sse")
    @RestStreamElementType(MediaType.TEXT_PLAIN)
    public Multi<String> see() {
        Log.info("New subscriber");
        return ticks.onCancellation().invoke(() -> {
            Log.info("Removing subscriber");
        });
    }



}
