package taskmanagementsystem.dto.task;

import jakarta.validation.constraints.NotNull;
import taskmanagementsystem.entity.Priority;
import taskmanagementsystem.entity.Status;

public record TaskCreateDto(
        @NotNull
        String title,
        @NotNull
        String description,
        @NotNull
        Status status,
        @NotNull
        Priority priority,
        @NotNull
        Long authorId,
        Long assigneeId) {
}
