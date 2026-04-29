package uk.gov.hmcts.reform.dev.services.dto;

public enum Status {
    OPEN,
    CLOSED;

    public static Status from(uk.gov.hmcts.reform.dev.repositories.entities.Status status) {
        return valueOf(status.name());
    }

    public static uk.gov.hmcts.reform.dev.repositories.entities.Status toEntity(Status status) {
        return uk.gov.hmcts.reform.dev.repositories.entities.Status.valueOf(status.name());
    }
}
