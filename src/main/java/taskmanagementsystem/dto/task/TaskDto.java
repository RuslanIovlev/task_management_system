package taskmanagementsystem.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import taskmanagementsystem.dto.comment.CommentDto;
import taskmanagementsystem.entity.Priority;
import taskmanagementsystem.entity.Status;

import java.util.List;

public record TaskDto(
        @Schema(description = "ID of the task", example = "1")
        @NotNull
        Long id,

        @Schema(description = "Title of the task", example = "Implement feature X")
        @NotNull
        String title,

        @Schema(description = "Description of the task", example = "This task involves implementing feature X.")
        @NotNull
        String description,

        @Schema(description = "Status of the task", example = "IN_PROGRESS")
        @NotNull
        Status status,

        @Schema(description = "Priority of the task", example = "HIGH")
        @NotNull
        Priority priority,

        @Schema(description = "ID of the author", example = "1")
        @NotNull
        Long authorId,

        @Schema(description = "ID of the assignee", example = "2")
        Long assigneeId,

        List<CommentDto> comments) {
}
