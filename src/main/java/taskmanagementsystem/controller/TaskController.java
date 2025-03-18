package taskmanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import taskmanagementsystem.dto.PaginatedResponse;
import taskmanagementsystem.dto.task.TaskCreateDto;
import taskmanagementsystem.dto.task.TaskDto;
import taskmanagementsystem.dto.task.TaskFilterDto;
import taskmanagementsystem.dto.task.TaskUpdateDto;
import taskmanagementsystem.entity.Priority;
import taskmanagementsystem.entity.Status;
import taskmanagementsystem.service.task.TaskService;

@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "Tasks", description = "Operations related to task management")
@RequestMapping("api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Create a new task", description = "Creates a new task for the authenticated user")
    public TaskDto createTask(@RequestBody TaskCreateDto taskDto) {
        return taskService.createTask(taskDto);
    }

    @PutMapping("/{taskId}")
    @Operation(summary = "Update an existing task",
            description = "Updates an existing task by its ID. The user must be authorized to update the task.")
    public TaskDto updateTask(@PathVariable Long taskId,
                              @RequestBody TaskUpdateDto taskDto) {
        return taskService.updateTask(taskId, taskDto);
    }

    @DeleteMapping("/{userId}/{taskId}")
    @Operation(summary = "Delete a task",
            description = "Deletes a task by its ID. The user must be authorized to delete the task.")
    public void deleteTask(@PathVariable @Positive Long userId,
                           @PathVariable @Positive Long taskId) {
        taskService.deleteTask(userId, taskId);
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Get a task by ID",
            description = "Retrieves a task by its ID. The user must be authorized to access the task.")
    public TaskDto getTask(@PathVariable @Positive Long taskId) {
        return taskService.getTask(taskId);
    }

    @GetMapping("/{userId}/all")
    @Operation(summary = "Get all tasks for a user",
            description = "Retrieves all tasks assigned to or created by the specified user.")
    public PaginatedResponse<TaskDto> getAllTasks(@PathVariable @Positive Long userId,
                                                  @RequestParam(required = false) Status status,
                                                  @RequestParam(required = false) Priority priority,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        TaskFilterDto filter = new TaskFilterDto(status, priority);
        return taskService.getAllTasks(userId, filter, page, size);
    }
}
