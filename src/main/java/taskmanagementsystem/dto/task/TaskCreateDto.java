package taskmanagementsystem.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import taskmanagementsystem.entity.Priority;
import taskmanagementsystem.entity.Status;

public record TaskCreateDto(
        @Schema(description = "Title of the task", requiredMode = Schema.RequiredMode.REQUIRED, example = "Implement feature X")
        @NotNull
        String title,

        @Schema(description = "Description of the task", example = "This task involves implementing feature X.")
        @NotNull
        String description,

        @Schema(description = "Status of the task", example = "TODO")
        @NotNull
        Status status,

        @Schema(description = "Priority of the task", example = "HIGH")
        @NotNull
        Priority priority,

        @NotNull
        Long authorId,

        @Schema(description = "ID of the assignee", example = "1")
        Long assigneeId) {
}
