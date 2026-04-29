package uk.gov.hmcts.reform.dev;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import uk.gov.hmcts.reform.dev.controllers.dto.CreateTaskRequest;
import uk.gov.hmcts.reform.dev.controllers.dto.UpdateTaskRequest;
import uk.gov.hmcts.reform.dev.services.dto.TaskDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(TestContainerConfig.class)
public class TaskFunctionalTest {

    @Value("${TEST_URL:http://localhost:4000}")
    private String testUrl;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = testUrl;
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    void shouldCreateTask() {
        final CreateTaskRequest req = newCreateTaskRequest();

        final TaskDTO created = createTask(req);

        Assertions.assertNotNull(created.getId());
        Assertions.assertNotNull(created.getCaseNumber());
        Assertions.assertEquals(req.getTitle(), created.getTitle());
        Assertions.assertEquals(req.getDescription(), created.getDescription());
        Assertions.assertEquals(uk.gov.hmcts.reform.dev.services.dto.Status.OPEN, created.getStatus());
        assertDatesNotNull(created);
    }

    @Test
    void shouldGetTaskById() {
        final CreateTaskRequest req = newCreateTaskRequest();
        final TaskDTO created = createTask(req);

        final TaskDTO fetched = getTask(created.getId());

        assertTaskEquals(created, fetched);
        assertDatesEqual(created, fetched);
    }

    @Test
    void shouldUpdateTask() {
        final CreateTaskRequest createReq = newCreateTaskRequest();
        final TaskDTO created = createTask(createReq);

        final UpdateTaskRequest updateReq = newUpdateTaskRequest(
                created.getId(),
                created.getDescription());
        final TaskDTO updated = updateTask(created.getId(), updateReq);

        Assertions.assertEquals(uk.gov.hmcts.reform.dev.services.dto.Status.CLOSED, updated.getStatus());
    }

    @Test
    void shouldDeleteTask() {
        final CreateTaskRequest createReq = newCreateTaskRequest();
        final TaskDTO created = createTask(createReq);

        deleteTask(created.getId());

        getDeletedTask(created.getId());
    }

    private static CreateTaskRequest newCreateTaskRequest() {
        return CreateTaskRequest.builder()
                .title("Test Task")
                .description("Test Description")
                .status(uk.gov.hmcts.reform.dev.controllers.dto.Status.OPEN)
                .build();
    }

    private static UpdateTaskRequest newUpdateTaskRequest(final int id, final String description) {
        return UpdateTaskRequest.builder()
                .title("Updated Task")
                .description(description)
                .status(uk.gov.hmcts.reform.dev.controllers.dto.Status.CLOSED)
                .build();
    }

    private static TaskDTO createTask(final CreateTaskRequest req) {
        RestAssured.given()
                .contentType(ContentType.URLENC)
                .formParam("title", req.getTitle())
                .formParam("description", req.getDescription())
                .formParam("status", req.getStatus().name())
                .when()
                .post("/tasks/create")
                .then()
                .statusCode(303);

        // fetch created task by matching title+description
        final TaskDTO[] tasks = RestAssured.when()
                .get("/tasks")
                .then()
                .statusCode(200)
                .extract()
                .as(TaskDTO[].class);

        for (TaskDTO t : tasks) {
            if (req.getTitle().equals(t.getTitle()) && req.getDescription().equals(t.getDescription())) {
                return t;
            }
        }
        throw new AssertionError("Created task not found");
    }

    private static TaskDTO getTask(final int id) {
        return RestAssured.when()
                .get("/tasks/" + id)
                .then()
                .statusCode(200)
                .extract()
                .response()
                .as(TaskDTO.class);
    }

    private static void getDeletedTask(final int id) {
        RestAssured.when()
                .get("/tasks/" + id)
                .then()
                .statusCode(404);
    }

    private static TaskDTO updateTask(final int id, final UpdateTaskRequest req) {
        RestAssured.given()
                .contentType(ContentType.URLENC)
                .formParam("title", req.getTitle())
                .formParam("description", req.getDescription())
                .formParam("status", req.getStatus().name())
                .when()
                .post("/tasks/" + id + "/update")
                .then()
                .statusCode(303);

        return getTask(id);
    }

    private static void deleteTask(final int id) {
        RestAssured.when()
                .delete("/tasks/" + id)
                .then()
                .statusCode(303);
    }

    private static void assertTaskEquals(final TaskDTO expected, final TaskDTO actual) {
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getTitle(), actual.getTitle());
        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
    }

    private static void assertDatesEqual(final TaskDTO a, final TaskDTO b) {
        Assertions.assertEquals(a.getCreatedDate(), b.getCreatedDate());
        Assertions.assertEquals(a.getLastModifiedDate(), b.getLastModifiedDate());
    }

    private static void assertDatesNotNull(final TaskDTO a) {
        Assertions.assertNotNull(a.getCreatedDate());
        Assertions.assertNotNull(a.getLastModifiedDate());
    }
}
