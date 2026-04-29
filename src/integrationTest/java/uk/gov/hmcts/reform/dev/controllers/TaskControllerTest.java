package uk.gov.hmcts.reform.dev.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.hmcts.reform.dev.services.TaskService;
import uk.gov.hmcts.reform.dev.services.dto.CreateTaskDTO;
import uk.gov.hmcts.reform.dev.services.dto.TaskDTO;
import uk.gov.hmcts.reform.dev.services.dto.UpdateTaskDTO;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TaskController.class)
public class TaskControllerTest {

    @MockitoBean
    private TaskService taskService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTaskEndpoint() throws Exception {
        final TaskDTO created = getTaskDTO(1);
        when(taskService.createTask(any(CreateTaskDTO.class))).thenReturn(created);

        mockMvc.perform(
                post("/tasks/create")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "Test Task")
                        .param("description", "Test Description")
                        .param("status", "OPEN"))
                .andExpect(result -> status().is3xxRedirection());

        verify(taskService).createTask(any(CreateTaskDTO.class));
    }

    @Test
    void updateTaskEndpoint() throws Exception {
        final int id = 1;
        when(taskService.updateTask(any(UpdateTaskDTO.class))).thenReturn(getTaskDTO(id));

        mockMvc.perform(
                post(String.format("/tasks/%d/update", id))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "Test Task")
                        .param("description", "Test Description")
                        .param("status", "OPEN"))
                .andExpect(result -> status().is3xxRedirection());

        verify(taskService).updateTask(any(UpdateTaskDTO.class));
    }

    @Test
    void getTaskEndpoint() throws Exception {
        final TaskDTO expected = getTaskDTO(1);
        when(taskService.getTask(expected.getId())).thenReturn(expected);

        final MvcResult response = mockMvc.perform(
                get("/tasks/" + expected.getId()))
                .andExpect(result -> status().isOk())
                .andReturn();
        final TaskDTO returned = objectMapper.readValue(response.getResponse().getContentAsString(), TaskDTO.class);

        assertTaskEquals(expected, returned);
        verify(taskService).getTask(expected.getId());
    }

    @Test
    void getTasksEndpoint() throws Exception {
        final TaskDTO expected1 = getTaskDTO(1);
        final TaskDTO expected2 = getTaskDTO(2);
        when(taskService.getAllTasks()).thenReturn(List.of(expected1, expected2));

        final MvcResult response = mockMvc.perform(
                get("/tasks"))
                .andExpect(result -> status().isOk())
                .andReturn();
        final List<TaskDTO> returned = objectMapper.readValue(
                response.getResponse().getContentAsString(), new TypeReference<List<TaskDTO>>() {
                });

        assertTaskEquals(expected1, returned.get(0));
        assertTaskEquals(expected2, returned.get(1));
        verify(taskService).getAllTasks();
    }

    @Test
    void deleteTaskEndpoint() throws Exception {
        final int id = 1;
        mockMvc.perform(
                delete("/tasks/" + id))
                .andExpect(result -> status().is3xxRedirection());

        verify(taskService).deleteTask(id);
    }

    private static TaskDTO getTaskDTO(final int id) {
        return TaskDTO.builder()
                .id(id)
                .caseNumber("CASE-000001")
                .title("Test Task")
                .description("Test Description")
                .status(uk.gov.hmcts.reform.dev.services.dto.Status.OPEN)
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();
    }

    private static void assertTaskEquals(final TaskDTO expected, final TaskDTO actual) {
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getCaseNumber(), actual.getCaseNumber());
        Assertions.assertEquals(expected.getTitle(), actual.getTitle());
        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
        Assertions.assertEquals(expected.getCreatedDate(), actual.getCreatedDate());
        Assertions.assertEquals(expected.getLastModifiedDate(), actual.getLastModifiedDate());
    }
}
