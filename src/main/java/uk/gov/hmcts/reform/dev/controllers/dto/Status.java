package uk.gov.hmcts.reform.dev.controllers.dto;

public enum Status {
    OPEN,
    CLOSED;

    public static Status from(uk.gov.hmcts.reform.dev.services.dto.Status  status) {
        return valueOf(status.name());
    }
}
