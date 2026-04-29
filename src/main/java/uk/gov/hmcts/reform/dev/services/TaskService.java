package uk.gov.hmcts.reform.dev.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.dev.repositories.entities.Task;
import uk.gov.hmcts.reform.dev.services.dto.CreateTaskDTO;
import uk.gov.hmcts.reform.dev.services.dto.Status;
import uk.gov.hmcts.reform.dev.services.dto.TaskDTO;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;
import uk.gov.hmcts.reform.dev.services.dto.UpdateTaskDTO;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public TaskDTO createTask(final CreateTaskDTO createTaskDTO) {
        if (isMissingFieldsForCreation(createTaskDTO)) {
            throw new IllegalArgumentException();
        }

        final Task task = Task.builder()
            .title(createTaskDTO.getTitle())
            .description(createTaskDTO.getDescription())
            .status(Status.toEntity(createTaskDTO.getStatus()))
            .build();

        final Task created = taskRepository.save(task);
        return TaskDTO.from(created);
    }

    @Transactional
    public TaskDTO updateTask(final UpdateTaskDTO updateTaskDTO) {
        if (isMissingFieldsForUpdate(updateTaskDTO)) {
            throw new IllegalArgumentException();
        }

        final Task existing = taskRepository.findById(updateTaskDTO.getId())
            .orElseThrow(EntityNotFoundException::new);

        existing.setTitle(updateTaskDTO.getTitle());
        existing.setDescription(updateTaskDTO.getDescription());
        existing.setStatus(Status.toEntity(updateTaskDTO.getStatus()));

        final Task updated = taskRepository.save(existing);
        return TaskDTO.from(updated);
    }

    @Transactional
    public void deleteTask(final int id) {
        taskRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public TaskDTO getTask(final int id) {
        return taskRepository.findById(id)
            .map(TaskDTO::from)
            .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAllByOrderByIdAsc()
            .stream()
            .map(TaskDTO::from)
            .toList();
    }

    private boolean isMissingFieldsForCreation(final CreateTaskDTO createTaskDTO) {
        return createTaskDTO.getTitle() == null
            || createTaskDTO.getTitle().isBlank()
            || createTaskDTO.getStatus() == null;
    }

    private boolean isMissingFieldsForUpdate(final UpdateTaskDTO updateTaskDTO) {
        return updateTaskDTO.getId() == null
            || updateTaskDTO.getTitle() == null
            || updateTaskDTO.getTitle().isBlank()
            || updateTaskDTO.getStatus() == null;
    }
}
