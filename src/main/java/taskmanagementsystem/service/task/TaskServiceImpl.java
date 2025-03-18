package taskmanagementsystem.service.task;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import taskmanagementsystem.dto.PaginatedResponse;
import taskmanagementsystem.dto.task.TaskCreateDto;
import taskmanagementsystem.dto.task.TaskDto;
import taskmanagementsystem.dto.task.TaskFilterDto;
import taskmanagementsystem.dto.task.TaskUpdateDto;
import taskmanagementsystem.entity.Role;
import taskmanagementsystem.entity.Task;
import taskmanagementsystem.entity.User;
import taskmanagementsystem.exception.UserRoleException;
import taskmanagementsystem.mapper.TaskMapper;
import taskmanagementsystem.repository.TaskRepository;
import taskmanagementsystem.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Override
    @Transactional
    public TaskDto createTask(TaskCreateDto taskDto) {
        log.info("Create Task");
        User user = checkUser(taskDto.authorId());
        checkUserRole(user);
        Task task = taskMapper.toTask(taskDto);
        task.setAuthor(user);
        if (taskDto.assigneeId() != null) {
            User assignee = checkAssigneeExist(taskDto.assigneeId());
            task.setAssignee(assignee);
        }
        taskRepository.save(task);
        log.info("Task created");
        return taskMapper.toTaskDto(task);
    }

    @Override
    @Transactional
    public TaskDto updateTask(Long taskId, TaskUpdateDto taskDto) {
        log.info("Trying to update task with id {}", taskId);
        User user = checkUser(taskDto.userId());
        Task task = checkTaskExist(taskId);
        boolean isAdmin = user.getRole().equals(Role.ADMIN);
        boolean isAssignee = user.getRole().equals(Role.USER) && task.getAssignee().equals(user);
        if (!isAdmin && !isAssignee) {
            throw new UserRoleException("User does not have permission to update this task");
        }
        updateTaskFields(task, taskDto, isAdmin);
        taskRepository.save(task);
        log.info("Task with id {} was updated", taskId);
        return taskMapper.toTaskDto(task);
    }

    @Override
    @Transactional
    public void deleteTask(Long userId, Long taskId) {
        log.info("Trying to delete task with id {}", taskId);
        User user = checkUser(userId);
        checkUserRole(user);
        Task task = checkTaskExist(taskId);
        taskRepository.delete(task);
        log.info("Task with id {} was deleted", taskId);
    }

    @Override
    public TaskDto getTask(Long id) {
        log.info("Trying to get task with id {}", id);
        Task task = checkTaskExist(id);
        log.info("Task with id {} was found", id);
        return taskMapper.toTaskDto(task);
    }

    @Override
    public PaginatedResponse<TaskDto> getAllTasks(Long userId, TaskFilterDto filterDto, int page, int size) {
        log.info("Trying to get tasks with filter {}", filterDto);
        User user = checkUser(userId);
        Specification<Task> spec = buildSpecification(user, filterDto);
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> tasksPage = taskRepository.findAll(spec, pageable);
        log.info("Tasks found");
        List<TaskDto> taskDtos = tasksPage.map(taskMapper::toTaskDto).getContent();
        return new PaginatedResponse<>(
                taskDtos,
                tasksPage.getNumber(),
                tasksPage.getSize(),
                tasksPage.getTotalElements(),
                tasksPage.getTotalPages(),
                tasksPage.isFirst(),
                tasksPage.isLast()
        );
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User with id %s not found".formatted(userId)));
    }

    private User checkAssigneeExist(Long assigneeId) {
        return userRepository.findById(assigneeId).orElseThrow(
                () -> new EntityNotFoundException("Assignee with id %s not found".formatted(assigneeId)));
    }

    private void checkUserRole(User user) {
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new UserRoleException("Only admin can create or delete a task");
        }
    }

    private Task checkTaskExist(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(
                () -> new EntityNotFoundException("Task with id %S not found".formatted(taskId)));
    }

    private void updateTaskFields(Task task, TaskUpdateDto taskDto, Boolean isAdmin) {
        if (taskDto.status() != null) {
            task.setStatus(taskDto.status());
        }
        if (taskDto.priority() != null && isAdmin) {
            task.setPriority(taskDto.priority());
        }
        if (taskDto.description() != null && isAdmin) {
            task.setDescription(taskDto.description());
        }
        if (taskDto.assigneeId() != null && isAdmin) {
            User assignee = checkAssigneeExist(taskDto.assigneeId());
            task.setAssignee(assignee);
        }
    }

    private Specification<Task> buildSpecification(User user, TaskFilterDto filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (user.getRole().equals(Role.ADMIN)) {
                predicates.add(criteriaBuilder.equal(root.get("author").get("id"), user.getId()));
            } else if (user.getRole().equals(Role.USER)) {
                predicates.add(criteriaBuilder.equal(root.get("assignee").get("id"), user.getId()));
            }
            if (filter.status() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), filter.status()));
            }
            if (filter.priority() != null) {
                predicates.add(criteriaBuilder.equal(root.get("priority"), filter.priority()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
