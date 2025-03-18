package taskmanagementsystem.dto.task;

import taskmanagementsystem.entity.Priority;
import taskmanagementsystem.entity.Status;

public record TaskFilterDto(Status status,
                            Priority priority) {
}
