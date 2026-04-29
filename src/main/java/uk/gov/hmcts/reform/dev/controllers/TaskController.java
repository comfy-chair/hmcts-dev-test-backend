package uk.gov.hmcts.reform.dev.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.dev.controllers.dto.CreateTaskRequest;
import uk.gov.hmcts.reform.dev.controllers.dto.UpdateTaskRequest;
import uk.gov.hmcts.reform.dev.services.TaskService;
import uk.gov.hmcts.reform.dev.services.dto.CreateTaskDTO;
import uk.gov.hmcts.reform.dev.services.dto.Status;
import uk.gov.hmcts.reform.dev.services.dto.TaskDTO;
import uk.gov.hmcts.reform.dev.services.dto.UpdateTaskDTO;

import java.net.URI;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(value = "/tasks")
public class TaskController {

    private final TaskService taskService;
    @Value("${FRONTEND_BASE_URL:https://localhost:3100}")
    private String baseUrl;

    public TaskController(final TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping(value = "/get-example-case", produces = "application/json")
    public ResponseEntity<TaskDTO> getExampleCase() {
        return ok(
                TaskDTO.builder()
                        .id(1)
                        .title("Example Task")
                        .description("This is an example task.")
                        .status(Status.OPEN)
                        .build());
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> createTask(@ModelAttribute CreateTaskRequest req) {
        final CreateTaskDTO createTaskDTO = req.toCreateTaskDTO();
        final TaskDTO created = taskService.createTask(createTaskDTO);

        final URI location = URI.create(baseUrl + "/task-details/" + created.getId());

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .location(location)
                .build();
    }

    @PostMapping(value = "/{id}/update", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> updateTask(@PathVariable int id, @ModelAttribute UpdateTaskRequest req) {
        final UpdateTaskDTO updateTaskDTO = req.toUpdateTaskDTO();
        updateTaskDTO.setId(id);
        final TaskDTO updated = taskService.updateTask(updateTaskDTO);

        final URI location = URI.create(baseUrl + "/task-details/" + updated.getId());

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .location(location)
                .build();
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public ResponseEntity<TaskDTO> getTask(@PathVariable int id) {
        final TaskDTO task = taskService.getTask(id);
        return ok(task);
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<TaskDTO>> getTasks() {
        final List<TaskDTO> tasks = taskService.getAllTasks();
        return ok(tasks);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable int id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
