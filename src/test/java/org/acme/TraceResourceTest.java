package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
class TraceResourceTest {
    @Test
    void testRootEndpoint() {
        given()
          .when().get("/trace")
          .then()
             .statusCode(200);

    }

}