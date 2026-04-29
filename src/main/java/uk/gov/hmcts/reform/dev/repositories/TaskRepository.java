package uk.gov.hmcts.reform.dev.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.dev.repositories.entities.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findAllByOrderByIdAsc();
}
