package org.acme;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

@Path("/trace")
public class TraceResource {


    private static final Logger LOG = Logger.getLogger(TraceResource.class);



    @Inject
    Tracer tracer;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        LOG.info("hello");
        return "hello";
    }


    @GET
    @Path("/simulate")
    @Produces(MediaType.TEXT_PLAIN)
    public String simulateComplexTrace() {
        LOG.info("simulateComplexTrace - start");

        simulateDatabaseCall();
        simulateExternalServiceCall();

        // Manual span
        Span span = tracer.spanBuilder("custom-manual-span").startSpan();
        try {
            Thread.sleep(100); // simulate latency
            LOG.info("Inside manual span");
        } catch (InterruptedException e) {
            span.recordException(e);
            Thread.currentThread().interrupt();
        } finally {
            span.end();
        }

        return "Tracing simulation complete";
    }

    private void simulateDatabaseCall() {
        Span dbSpan = tracer.spanBuilder("simulate-database-call").startSpan();
        try {
            Thread.sleep(150); // simulate DB delay
            LOG.info("Simulated DB call");
        } catch (InterruptedException e) {
            dbSpan.recordException(e);
            Thread.currentThread().interrupt();
        } finally {
            dbSpan.end();
        }
    }

    private void simulateExternalServiceCall() {
        Span serviceSpan = tracer.spanBuilder("simulate-external-service").startSpan();
        try {
            Thread.sleep(200); // simulate API delay
            LOG.info("Simulated external service call");
        } catch (InterruptedException e) {
            serviceSpan.recordException(e);
            Thread.currentThread().interrupt();
        } finally {
            serviceSpan.end();
        }
    }

}
