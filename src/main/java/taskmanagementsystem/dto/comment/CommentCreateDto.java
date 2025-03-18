package taskmanagementsystem.dto.comment;

import jakarta.validation.constraints.NotNull;

public record CommentCreateDto(
        @NotNull
        Long userId,
        @NotNull
        Long taskId,
        @NotNull
        String text) {
}
