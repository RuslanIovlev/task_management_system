package task.management.system.taskmanagementsystem.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import taskmanagementsystem.dto.task.TaskCreateDto;
import taskmanagementsystem.dto.task.TaskDto;
import taskmanagementsystem.dto.task.TaskFilterDto;
import taskmanagementsystem.dto.task.TaskUpdateDto;
import taskmanagementsystem.entity.*;
import taskmanagementsystem.exception.UserRoleException;
import taskmanagementsystem.mapper.TaskMapper;
import taskmanagementsystem.repository.TaskRepository;
import taskmanagementsystem.repository.UserRepository;
import taskmanagementsystem.service.task.TaskServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskMapper taskMapper;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private TaskServiceImpl taskService;

    private final Long userId = 1L;
    private final Long taskId = 2L;
    private final Long assigneeId = 3L;

    private User user;
    private User assignee;
    private Task task;
    private TaskCreateDto taskCreateDto;
    private TaskUpdateDto taskUpdateDto;
    private TaskDto taskDto;
    private TaskFilterDto taskFilterDto;

    @BeforeEach
    void init() {
        user = new User();
        user.setId(userId);
        user.setRole(Role.ADMIN);
        assignee = new User();
        assignee.setId(assigneeId);
        assignee.setRole(Role.USER);
        task = new Task();
        task.setId(taskId);
        task.setTitle("New Task");
        task.setDescription("This is a new task");
        taskCreateDto = new TaskCreateDto(
                "New Task",
                "This is a new task",
                Status.TODO,
                Priority.HIGH,
                userId,
                assigneeId
        );
        taskDto = new TaskDto(
                taskId,
                "New Task",
                "This is a new task",
                Status.TODO,
                Priority.HIGH,
                userId,
                assigneeId,
                null
        );
    }

    @Test
    void testCreateTaskSuccess() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskMapper.toTask(taskCreateDto)).thenReturn(task);
        when(userRepository.findById(assigneeId)).thenReturn(Optional.of(assignee));
        when(taskMapper.toTaskDto(task)).thenReturn(taskDto);
        when(taskRepository.save(task)).thenReturn(task);

        TaskDto result = taskService.createTask(taskCreateDto);

        assertNotNull(result);
        assertEquals(taskDto.id(), result.id());
        assertEquals(taskDto.title(), result.title());
        verify(userRepository).findById(userId);
        verify(taskMapper).toTask(taskCreateDto);
        verify(taskMapper).toTaskDto(task);
        verify(taskRepository).save(task);
    }

    @Test
    void testCreateTaskUserNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> taskService.createTask(taskCreateDto));

        assertEquals("User with id 1 not found", exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    void testCreateTaskUserNotAdminException() {
        user = new User();
        user.setId(userId);
        user.setRole(Role.USER);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserRoleException exception = assertThrows(UserRoleException.class,
                () -> taskService.createTask(taskCreateDto));

        assertEquals("Only admin can create or delete a task", exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    void testUpdateTaskSuccess() {
        task = new Task();
        task.setId(taskId);
        task.setTitle("Old Title");
        task.setDescription("Old Description");

        taskUpdateDto = TaskUpdateDto.builder()
                .userId(userId)
                .title("New Title")
                .description("Updated Description")
                .priority(Priority.MEDIUM)
                .status(Status.IN_PROGRESS)
                .assigneeId(assigneeId)
                .build();

        taskDto = new TaskDto(
                taskId,
                "Old Title",
                "Updated Description",
                Status.IN_PROGRESS,
                Priority.MEDIUM,
                userId,
                assigneeId,
                null
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findById(assigneeId)).thenReturn(Optional.of(assignee));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toTaskDto(task)).thenReturn(taskDto);

        TaskDto result = taskService.updateTask(taskId, taskUpdateDto);

        assertNotNull(result);
        assertEquals(taskDto.id(), result.id());
        assertEquals(taskDto.description(), result.description());
        verify(userRepository).findById(userId);
        verify(taskRepository).findById(taskId);
        verify(taskMapper).toTaskDto(task);
    }

    @Test
    void deleteTask_Success() {
        user = new User();
        user.setId(userId);
        user.setRole(Role.ADMIN);
        task = new Task();
        task.setId(taskId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        taskService.deleteTask(userId, taskId);

        verify(userRepository).findById(userId);
        verify(taskRepository).findById(taskId);
        verify(taskRepository).delete(task);
    }

    @Test
    void deleteTask_UserNotAdmin_ThrowsException() {
        user = new User();
        user.setId(userId);
        user.setRole(Role.USER);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserRoleException exception = assertThrows(UserRoleException.class,
                () -> taskService.deleteTask(userId, taskId));

        assertEquals("Only admin can create or delete a task", exception.getMessage());
        verify(userRepository).findById(userId);
        verifyNoInteractions(taskRepository);
    }

    @Test
    void getTask_Success() {
        task = new Task();
        task.setId(taskId);
        task.setTitle("Task Title");
        taskDto = new TaskDto(
                taskId,
                "Task Title",
                "Task Description",
                Status.TODO,
                Priority.HIGH,
                userId,
                assigneeId,
                null
        );

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toTaskDto(task)).thenReturn(taskDto);

        TaskDto result = taskService.getTask(taskId);

        assertNotNull(result);
        assertEquals(taskDto.id(), result.id());
        assertEquals(taskDto.title(), result.title());
        verify(taskRepository).findById(taskId);
        verify(taskMapper).toTaskDto(task);
    }
}
