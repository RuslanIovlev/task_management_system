package taskmanagementsystem.service.comment;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import taskmanagementsystem.dto.comment.CommentCreateDto;
import taskmanagementsystem.dto.comment.CommentDto;
import taskmanagementsystem.entity.Comment;
import taskmanagementsystem.entity.Task;
import taskmanagementsystem.entity.User;
import taskmanagementsystem.mapper.CommentMapper;
import taskmanagementsystem.repository.CommentRepository;
import taskmanagementsystem.repository.TaskRepository;
import taskmanagementsystem.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final TaskRepository taskRepository;


    @Override
    @Transactional
    public CommentDto createComment(CommentCreateDto commentDto) {
        log.info("trying to create comment on task with id {}",commentDto.taskId());
        User user = checkUserExist(commentDto.userId());
        Task task = checkTaskExist(commentDto.taskId());
        Comment comment = commentMapper.toComment(commentDto);
        comment.setUser(user);
        comment.setTask(task);
        commentRepository.save(comment);
        log.info("comment to task with id {} was created",commentDto.taskId());
        return commentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
       log.info("trying to delete comment with id {}",commentId);
       Comment comment = commentRepository.findById(commentId).orElseThrow(
               () -> new EntityNotFoundException("comment with id %s not found".formatted(commentId)));
       commentRepository.delete(comment);
    }

    @Override
    public List<CommentDto> getCommentsByTaskId(Long taskId) {
        log.info("Trying to get task comments with task id {}", taskId);
        Task task = checkTaskExist(taskId);
        return task.getComments().stream()
                .map(commentMapper::toCommentDto)
                .toList();
    }

    private User checkUserExist(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User with id %s not found".formatted(userId)));
    }

    private Task checkTaskExist(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(
                () -> new EntityNotFoundException("Task with id %S not found".formatted(taskId)));
    }
}
