package taskmanagementsystem.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PaginatedResponse<T>(List<T> content,
                                   int pageNumber,
                                   int pageSize,
                                   long totalElements,
                                   int totalPages,
                                   boolean first,
                                   boolean last
) {
}
