package uk.gov.hmcts.reform.dev.controllers.dto;

import lombok.Builder;
import lombok.Getter;
import uk.gov.hmcts.reform.dev.services.dto.CreateTaskDTO;

@Getter
@Builder
public class CreateTaskRequest {
    private String title;
    private String description;
    private Status status;

    public CreateTaskDTO toCreateTaskDTO() {
        return CreateTaskDTO.builder()
            .title(getTitle())
            .description(getDescription())
            .status(uk.gov.hmcts.reform.dev.services.dto.Status.valueOf(getStatus().name()))
            .build();
    }
}
