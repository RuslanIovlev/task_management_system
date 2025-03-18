package taskmanagementsystem.dto.comment;

import jakarta.validation.constraints.NotNull;

public record CommentDto(
        @NotNull
        Long id,
        @NotNull
        Long userId,
        @NotNull
        Long taskId,
        @NotNull
        String text) {
}
