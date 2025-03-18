package taskmanagementsystem.dto.task;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import taskmanagementsystem.dto.comment.CommentDto;
import taskmanagementsystem.entity.Priority;
import taskmanagementsystem.entity.Status;

import java.util.List;

@Builder
public record TaskUpdateDto(
        @NotNull
        Long userId,
        String title,
        String description,
        Status status,
        Priority priority,
        Long assigneeId,
        List<CommentDto> comments) {
}
