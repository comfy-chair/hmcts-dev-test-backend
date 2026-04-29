package uk.gov.hmcts.reform.dev.services.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateTaskDTO {
    private Integer id;
    private String title;
    private String description;
    private Status status;
}
