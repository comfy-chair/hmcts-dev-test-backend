package uk.gov.hmcts.reform.dev.services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;
import uk.gov.hmcts.reform.dev.repositories.entities.Task;
import uk.gov.hmcts.reform.dev.services.dto.CreateTaskDTO;
import uk.gov.hmcts.reform.dev.services.dto.Status;
import uk.gov.hmcts.reform.dev.services.dto.TaskDTO;
import uk.gov.hmcts.reform.dev.services.dto.UpdateTaskDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Nested
    class WhenCreateTask {

        @Test
        void shouldCreateTask() {
            final CreateTaskDTO createTaskDTO = getCreateTaskDTO();
            final Task entity = getTask(1);
            when(taskRepository.save(any(Task.class))).thenReturn(entity);

            final TaskDTO dto = taskService.createTask(createTaskDTO);

            assertDTOMatchesEntity(entity, dto);
            verify(taskRepository).save(any(Task.class));
        }

        @Test
        void shouldCreateTaskIfMissingDescription() {
            final CreateTaskDTO createTaskDTO = CreateTaskDTO.builder()
                    .title("Test Title")
                    .status(Status.OPEN)
                    .build();
            final Task entity = Task.builder()
                    .id(1)
                    .title("Test Title")
                    .status(uk.gov.hmcts.reform.dev.repositories.entities.Status.OPEN)
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .version(0)
                    .build();
            when(taskRepository.save(any(Task.class))).thenReturn(entity);

            final TaskDTO dto = taskService.createTask(createTaskDTO);

            assertDTOMatchesEntity(entity, dto);
            verify(taskRepository).save(any(Task.class));
        }

        @Test
        void shouldThrowIfMissingTitle() {
            final CreateTaskDTO createTaskDTO = CreateTaskDTO.builder()
                    .description("Test Description")
                    .status(Status.OPEN)
                    .build();

            Assertions.assertThrows(IllegalArgumentException.class, () -> taskService.createTask(createTaskDTO));
        }

        @Test
        void shouldThrowIfMissingStatus() {
            final CreateTaskDTO createTaskDTO = CreateTaskDTO.builder()
                    .title("Test Title")
                    .description("Test Description")
                    .build();

            Assertions.assertThrows(IllegalArgumentException.class, () -> taskService.createTask(createTaskDTO));
        }
    }

    @Nested
    class WhenUpdateTask {

        @Test
        void shouldUpdateTask() {
            final UpdateTaskDTO updateTaskDTO = getUpdateTaskDTO();
            final Task entity = getTask(1);
            when(taskRepository.findById(updateTaskDTO.getId())).thenReturn(Optional.of(entity));
            when(taskRepository.save(entity)).thenReturn(entity);

            final TaskDTO dto = taskService.updateTask(updateTaskDTO);

            assertDTOMatchesEntity(entity, dto);
            verify(taskRepository).findById(updateTaskDTO.getId());
            verify(taskRepository).save(entity);
        }

        @Test
        void shouldThrowIfMissingID() {
            final UpdateTaskDTO updateTaskDTO = UpdateTaskDTO.builder()
                    .title("Test Title")
                    .description("Test Description")
                    .status(Status.OPEN)
                    .build();

            Assertions.assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(updateTaskDTO));
        }

        @Test
        void shouldThrowIfMissingTitle() {
            final UpdateTaskDTO updateTaskDTO = UpdateTaskDTO.builder()
                    .id(1)
                    .description("Test Description")
                    .status(Status.OPEN)
                    .build();

            Assertions.assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(updateTaskDTO));
        }

        @Test
        void shouldThrowIfMissingStatus() {
            final UpdateTaskDTO updateTaskDTO = UpdateTaskDTO.builder()
                    .id(1)
                    .title("Test Title")
                    .description("Test Description")
                    .build();

            Assertions.assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(updateTaskDTO));
        }

        @Test
        void shouldUpdateTaskIfMissingDescription() {
            final UpdateTaskDTO updateTaskDTO = UpdateTaskDTO.builder()
                    .id(1)
                    .title("Test Title")
                    .status(Status.OPEN)
                    .build();
            final Task entity = getTask(1);
            when(taskRepository.findById(updateTaskDTO.getId())).thenReturn(Optional.of(entity));
            when(taskRepository.save(entity)).thenReturn(entity);

            final TaskDTO updated = taskService.updateTask(updateTaskDTO);

            assertDTOMatchesEntity(entity, updated);
            verify(taskRepository).findById(updateTaskDTO.getId());
            verify(taskRepository).save(entity);
        }
    }

    @Nested
    class WhenDeleteTask {
        @Test
        void shouldDeleteTask() {
            taskService.deleteTask(1);
            verify(taskRepository).deleteById(1);
        }
    }

    @Nested
    class WhenGetTask {
        @Test
        void shouldGetTask() {
            final Task entity = getTask(1);
            when(taskRepository.findById(entity.getId())).thenReturn(Optional.of(entity));

            final TaskDTO dto = taskService.getTask(entity.getId());

            assertDTOMatchesEntity(entity, dto);
            verify(taskRepository).findById(entity.getId());
        }

        @Test
        void shouldThrowIfNotFound() {
            final Task entity = getTask(1);
            when(taskRepository.findById(entity.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(EntityNotFoundException.class, () -> taskService.getTask(entity.getId()));
        }
    }

    @Nested
    class WhenGetAllTasks {
        @Test
        void shouldGetAllTasks() {
            final Task entity1 = getTask(1);
            final Task entity2 = getTask(2);
            when(taskRepository.findAllByOrderByIdAsc()).thenReturn(List.of(entity1, entity2));

            final List<TaskDTO> dtos = taskService.getAllTasks();
            assertDTOMatchesEntity(entity1, dtos.get(0));
            assertDTOMatchesEntity(entity2, dtos.get(1));
            verify(taskRepository).findAllByOrderByIdAsc();
        }
    }

    private CreateTaskDTO getCreateTaskDTO() {
        return CreateTaskDTO.builder()
                .title("Test Title")
                .description("Test Description")
                .status(Status.OPEN)
                .build();
    }

    private UpdateTaskDTO getUpdateTaskDTO() {
        return UpdateTaskDTO.builder()
                .id(1)
                .title("Test Title")
                .description("Test Description")
                .status(Status.OPEN)
                .build();
    }

    private Task getTask(final int id) {
        return Task.builder()
                .id(id)
                .title("Test Title")
                .description("Test Description")
                .status(uk.gov.hmcts.reform.dev.repositories.entities.Status.OPEN)
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .version(0)
                .build();
    }

    private static void assertDTOMatchesEntity(final Task expected, final TaskDTO actual) {
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getCaseNumber(), actual.getCaseNumber());
        Assertions.assertEquals(expected.getTitle(), actual.getTitle());
        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
        Assertions.assertEquals(expected.getStatus().name(), actual.getStatus().name());
        Assertions.assertEquals(expected.getCreatedDate(), actual.getCreatedDate());
        Assertions.assertEquals(expected.getLastModifiedDate(), actual.getLastModifiedDate());
    }
}
