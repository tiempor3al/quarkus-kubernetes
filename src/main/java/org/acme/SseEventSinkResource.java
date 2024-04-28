package org.acme;

import io.quarkus.logging.Log;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.scheduler.Scheduled;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import org.acme.dto.MessageInput;
import org.eclipse.microprofile.context.ManagedExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Path("/sink")
public class SseEventSinkResource {

    @Inject
    Template index;

    @Inject
    Sse sse;

    @Inject
    ManagedExecutor executor;

    private final Map<String, SseEventSink> clients = new ConcurrentHashMap<>();

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance index() {
        return index.data("endpoint", "/sink/sse");
    }


    @GET
    @Path("sse")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void setSse(@Context SseEventSink eventSink) {
        var id = java.util.UUID.randomUUID().toString();
        Log.info("Client with id " + id + " connected");
        clients.put(id, eventSink);
    }



    @POST
    @Path("send")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendMessage(MessageInput input) {
        String id = input.id();
        String message = input.message();

        SseEventSink eventSink = clients.get(id);
        String responseMessage;

        if (eventSink != null) {
            Log.info("EventSink closed:" + eventSink.isClosed());
            if (!eventSink.isClosed()) {
                eventSink.send(sse.newEventBuilder()
                        .name("message")
                        .data(String.class, message)
                        .build());
                responseMessage = "Message sent successfully";
            } else {
                responseMessage = "Message could not be sent. Event Sink was closed.";
            }
        } else {
            responseMessage = "Client not found for provided id";
        }

        Map<String, String> response = new HashMap<>();
        response.put("status", responseMessage);

        return Response.ok(response).build();
    }

    @Scheduled(every = "30s")
    void removeClosedConnections() {

        Log.info("removeClosedConnections");
        clients.keySet().removeIf(key -> {
            SseEventSink sseEventSink = clients.get(key);
            if(sseEventSink.isClosed()){
                Log.info("Removing client " + key);
                return true;
            }
            return false;
        });
    }

}
