package taskmanagementsystem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import taskmanagementsystem.dto.comment.CommentCreateDto;
import taskmanagementsystem.dto.comment.CommentDto;
import taskmanagementsystem.entity.Comment;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "taskId", target = "task.id")
    Comment toComment(CommentCreateDto commentDto);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "task.id", target = "taskId")
    CommentDto toCommentDto(Comment comment);
}
