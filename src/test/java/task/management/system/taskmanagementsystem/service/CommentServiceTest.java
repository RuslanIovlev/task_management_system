package task.management.system.taskmanagementsystem.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import taskmanagementsystem.dto.comment.CommentCreateDto;
import taskmanagementsystem.dto.comment.CommentDto;
import taskmanagementsystem.entity.Comment;
import taskmanagementsystem.entity.Task;
import taskmanagementsystem.entity.User;
import taskmanagementsystem.mapper.CommentMapper;
import taskmanagementsystem.repository.CommentRepository;
import taskmanagementsystem.repository.TaskRepository;
import taskmanagementsystem.repository.UserRepository;
import taskmanagementsystem.service.comment.CommentServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private CommentMapper commentMapper;
    @InjectMocks
    private CommentServiceImpl commentService;

    private final Long userId = 1L;
    private final Long taskId = 2L;
    private final Long commentId = 3L;

    private User user;
    private Task task;
    private Comment comment;
    private CommentCreateDto commentCreateDto;
    private CommentDto commentDto;

    @BeforeEach
    void init() {
        user = new User();
        user.setId(userId);
        task = new Task();
        task.setId(taskId);
        comment = new Comment();
        comment.setId(commentId);
        comment.setText("This is a comment");
        commentCreateDto = new CommentCreateDto(userId, taskId, "This is a comment");
    }

    @Test
    void testCreateCommentSuccess() {
        commentCreateDto = new CommentCreateDto(userId, taskId, "This is a comment");
        commentDto = new CommentDto(commentId, userId, taskId, "This is a comment");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(commentMapper.toComment(commentCreateDto)).thenReturn(comment);
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);
        when(commentRepository.save(comment)).thenReturn(comment);

        CommentDto result = commentService.createComment(commentCreateDto);

        assertNotNull(result);
        assertEquals(commentDto.id(), result.id());
        assertEquals(commentDto.text(), result.text());
        verify(userRepository).findById(userId);
        verify(taskRepository).findById(taskId);
        verify(commentRepository).save(comment);
        verify(commentMapper).toComment(commentCreateDto);
        verify(commentMapper).toCommentDto(comment);
    }

    @Test
    void testCreateCommentUserNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentService.createComment(commentCreateDto));

        assertEquals("User with id 1 not found", exception.getMessage());
        verify(userRepository).findById(userId);
        verifyNoInteractions(taskRepository, commentRepository, commentMapper);
    }

    @Test
    void testCreateCommentTaskNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentService.createComment(commentCreateDto));

        assertEquals("Task with id 2 not found", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(taskRepository).findById(taskId);
        verifyNoInteractions(commentRepository, commentMapper);
    }

    @Test
    void testDeleteCommentSuccess() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        commentService.deleteComment(commentId);

        verify(commentRepository).findById(commentId);
        verify(commentRepository).delete(comment);
    }

    @Test
    void testDeleteCommentCommentNotFoundException() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentService.deleteComment(commentId));

        assertEquals("comment with id 3 not found", exception.getMessage());
        verify(commentRepository).findById(commentId);
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void testGetCommentsByTaskIdSuccess() {
        task.setComments(new ArrayList<>(List.of(
                new Comment(1L, "Comment 1", task, null),
                new Comment(2L, "Comment 2", task, null))));
        List<CommentDto> expectedDtos = task.getComments().stream()
                .map(c -> new CommentDto(c.getId(), userId, taskId, c.getText()))
                .toList();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(commentMapper.toCommentDto(any(Comment.class))).thenAnswer(invocation -> {
            Comment c = invocation.getArgument(0);
            return new CommentDto(c.getId(), userId, taskId, c.getText());
        });

        List<CommentDto> result = commentService.getCommentsByTaskId(taskId);

        assertNotNull(result);
        assertEquals(expectedDtos.size(), result.size());
        assertEquals(expectedDtos.get(0).text(), result.get(0).text());
        assertEquals(expectedDtos.get(1).text(), result.get(1).text());
        verify(taskRepository).findById(taskId);
        verify(commentMapper, times(task.getComments().size())).toCommentDto(any(Comment.class));
    }
}