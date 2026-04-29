package uk.gov.hmcts.reform.dev.services.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.reform.dev.repositories.entities.Task;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TaskDTO {

    private Integer id;
    private String caseNumber;
    private String title;
    private String description;
    private Status status;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public static TaskDTO from(Task task) {
        return TaskDTO.builder()
            .id(task.getId())
            .caseNumber(task.getCaseNumber())
            .title(task.getTitle())
            .description(task.getDescription())
            .status(Status.from(task.getStatus()))
            .createdDate(task.getCreatedDate())
            .lastModifiedDate(task.getLastModifiedDate())
            .build();
    }
}
