package taskmanagementsystem.service.task;

import org.springframework.data.domain.Page;
import taskmanagementsystem.dto.PaginatedResponse;
import taskmanagementsystem.dto.comment.CommentDto;
import taskmanagementsystem.dto.task.TaskCreateDto;
import taskmanagementsystem.dto.task.TaskDto;
import taskmanagementsystem.dto.task.TaskFilterDto;
import taskmanagementsystem.dto.task.TaskUpdateDto;

import java.util.List;

public interface TaskService {

    TaskDto createTask(TaskCreateDto taskDto);

    TaskDto updateTask(Long taskId, TaskUpdateDto taskDto);

    void deleteTask(Long taskId, Long userId);

    TaskDto getTask(Long id);

    PaginatedResponse<TaskDto> getAllTasks(Long userId, TaskFilterDto filterDto, int page, int size);
}