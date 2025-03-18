package taskmanagementsystem.dto.task;

import jakarta.validation.constraints.NotNull;
import taskmanagementsystem.dto.comment.CommentDto;
import taskmanagementsystem.entity.Priority;
import taskmanagementsystem.entity.Status;

import java.util.List;

public record TaskDto(
        @NotNull
        Long id,
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
        Long assigneeId,
        List<CommentDto> comments) {
}
