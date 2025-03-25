package taskmanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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

/**
 * Controller for managing tasks in the system.
 * <p>
 * This class provides REST endpoints for creating, updating, deleting, and retrieving tasks.
 * It is secured and requires user authentication for most operations.
 * </p>
 * @see TaskService
 */

@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "Tasks", description = "Operations related to task management")
@RequestMapping("api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    /**
     * Creates a new task for the authenticated user.
     *
     * @param taskDto the data transfer object containing task details
     * @return the created task as a {@link TaskDto}
     * @throws IllegalArgumentException if the provided task data is invalid
     */

    @PostMapping
    @Operation(summary = "Create a new task", description = "Creates a new task for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task created successfully", content = @Content(schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "User does not have permission")
    })
    public TaskDto createTask(@RequestBody TaskCreateDto taskDto) {
        return taskService.createTask(taskDto);
    }

    /**
     * Updates an existing task by its ID.
     * <p>
     * The user must be authorized to update the task. Only the owner or an admin can update the task.
     * </p>
     *
     * @param taskId  the ID of the task to update
     * @param taskDto the data transfer object containing updated task details
     * @return the updated task as a {@link TaskDto}
     * @throws EntityNotFoundException if the task with the given ID does not exist
     * @throws AccessDeniedException if the user is not authorized to update the task
     */

    @PutMapping("/{taskId}")
    @Operation(summary = "Update an existing task",
            description = "Updates an existing task by its ID. The user must be authorized to update the task.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully", content = @Content(schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "User does not have permission"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public TaskDto updateTask(@PathVariable Long taskId,
                              @RequestBody TaskUpdateDto taskDto) {
        return taskService.updateTask(taskId, taskDto);
    }

    /**
     * Deletes a task by its ID.
     * <p>
     * The user must be authorized to delete the task. Only the owner or an admin can delete the task.
     * </p>
     *
     * @param userId the ID of the user who owns the task
     * @param taskId the ID of the task to delete
     * @throws EntityNotFoundException if the task with the given ID does not exist
     * @throws AccessDeniedException if the user is not authorized to delete the task
     */

    @DeleteMapping("/{userId}/{taskId}")
    @Operation(summary = "Delete a task",
            description = "Deletes a task by its ID. The user must be authorized to delete the task.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "403", description = "User does not have permission"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public void deleteTask(@PathVariable @Positive Long userId,
                           @PathVariable @Positive Long taskId) {
        taskService.deleteTask(userId, taskId);
    }

    /**
     * Retrieves a task by its ID.
     * <p>
     * The user must be authorized to access the task. Only the owner or an admin can retrieve the task.
     * </p>
     *
     * @param taskId the ID of the task to retrieve
     * @return the task as a {@link TaskDto}
     * @throws EntityNotFoundException if the task with the given ID does not exist
     * @throws AccessDeniedException     if the user is not authorized to access the task
     */

    @GetMapping("/{taskId}")
    @Operation(summary = "Get a task by ID",
            description = "Retrieves a task by its ID. The user must be authorized to access the task.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task retrieved successfully", content = @Content(schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "403", description = "User does not have permission"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public TaskDto getTask(@PathVariable @Positive Long taskId) {
        return taskService.getTask(taskId);
    }

    /**
     * Retrieves all tasks assigned to or created by the specified user.
     * <p>
     * Supports filtering by status and priority, as well as pagination.
     * </p>
     *
     * @param userId    the ID of the user whose tasks are being retrieved
     * @param status    (optional) the status to filter tasks by
     * @param priority  (optional) the priority to filter tasks by
     * @param page      the page number for pagination (0-based index)
     * @param size      the number of tasks per page
     * @return a paginated response containing a list of tasks as {@link TaskDto}
     * @throws EntityNotFoundException if the user with the given ID does not exist
     */

    @GetMapping("/{userId}/all")
    @Operation(summary = "Get all tasks for a user",
            description = "Retrieves all tasks assigned to or created by the specified user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskDto.class)))),
            @ApiResponse(responseCode = "403", description = "User does not have permission"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public PaginatedResponse<TaskDto> getAllTasks(@PathVariable @Positive Long userId,
                                                  @RequestParam(required = false) Status status,
                                                  @RequestParam(required = false) Priority priority,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        TaskFilterDto filter = new TaskFilterDto(status, priority);
        return taskService.getAllTasks(userId, filter, page, size);
    }
}
