package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class WelcomeResourceTest {
    @Test
    void testRootEndpoint() {
        given()
          .when().get("/")
          .then()
             .statusCode(200)
             .body(is("This a Quarkus application running in K3S to test CI/CD pipeline"));
    }

}