package taskmanagementsystem.service.comment;

import taskmanagementsystem.dto.comment.CommentCreateDto;
import taskmanagementsystem.dto.comment.CommentDto;

import java.util.List;

public interface CommentService {

    CommentDto createComment(CommentCreateDto commentDto);

    void deleteComment(Long commentId);

    List<CommentDto> getCommentsByTaskId(Long taskId);
}
