package uk.gov.hmcts.reform.dev;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SmokeTest {

    @Value("${TEST_URL:http://localhost:4000}")
    private String testUrl;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = testUrl;
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    void smokeTest() {
        when()
            .get("/actuator/health")
            .then()
            .statusCode(200)
            .body("status", Matchers.equalTo("UP"));
    }
}
