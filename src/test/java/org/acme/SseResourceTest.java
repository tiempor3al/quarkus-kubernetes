package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.wildfly.common.Assert.assertTrue;

@QuarkusTest
class SseResourceTest {
    @Test
    void testRootEndpoint() {
        given()
          .when().get("/")
          .then()
             .statusCode(200);

    }


    @Test
    void testSseEndpoint() {
        given()
                .when().get("/sse")
                .then()
                .statusCode(200);

    }

}