package org.acme;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
public class WelcomeResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String welcome() {
        return "This a Quarkus application running in K3S to test CI/CD pipeline";
    }
}
