package uk.gov.hmcts.reform.dev.controllers.dto;

import lombok.Builder;
import lombok.Getter;
import uk.gov.hmcts.reform.dev.services.dto.UpdateTaskDTO;

@Getter
@Builder
public class UpdateTaskRequest {
    private String title;
    private String description;
    private Status status;

    public UpdateTaskDTO toUpdateTaskDTO() {
        return UpdateTaskDTO.builder()
                .title(getTitle())
                .description(getDescription())
                .status(uk.gov.hmcts.reform.dev.services.dto.Status.valueOf(getStatus().name()))
                .build();
    }
}
