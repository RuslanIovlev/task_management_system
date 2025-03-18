package taskmanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import taskmanagementsystem.dto.comment.CommentCreateDto;
import taskmanagementsystem.dto.comment.CommentDto;
import taskmanagementsystem.service.comment.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "Comments", description = "Operations related to task comments")
@RequestMapping("api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "Create a new comment",
            description = "Creates a new comment for a specific task. The user must be authorized to add comments.")
    public CommentDto createComment(@RequestBody CommentCreateDto comment) {
        return commentService.createComment(comment);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete a comment",
            description = "Deletes a comment by its ID. The user must be authorized to delete the comment.")
    public void deleteComment(@PathVariable @Positive Long commentId) {
        commentService.deleteComment(commentId);
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Get all comments for a task",
            description = "Retrieves all comments associated with a specific task. The user must be authorized to access the task.")
    List<CommentDto> getCommentsByTaskId(@PathVariable @Positive Long taskId) {
        return commentService.getCommentsByTaskId(taskId);
    }
}