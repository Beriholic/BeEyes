package cv.beriholic.beeyes.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO<T> {
    private T data;
    private Integer pageIndex;
    private Integer pageSize;
    private Long total;
    private Long totalPages;

    public static <T> PageDTO<T> of(T data) {
        return new PageDTO<>(data, 1, 10, null, null);
    }

    public static <T> PageDTO<T> of(T data, int pageIndex, int pageSize) {
        return new PageDTO<>(data, pageIndex, pageSize, null, null);
    }

    public static <T> PageDTO<List<T>> of(List<T> sourceList, int pageIndex, int pageSize, long total, long totalPages) {
        return new PageDTO<>(sourceList, pageIndex, pageSize, total, totalPages);
    }

    public static <T> PageDTO<List<T>> paginate(List<T> sourceList, int pageIndex, int pageSize) {
        if (sourceList == null) {
            sourceList = Collections.emptyList();
        }

        long total = sourceList.size();

        int offset = (pageIndex - 1) * pageSize;

        long totalPages = (total + pageSize - 1) / pageSize;

        if (offset >= total || offset < 0) {
            return new PageDTO<>(Collections.emptyList(), pageIndex, pageSize, total, totalPages);
        }

        List<T> pageData = sourceList.stream()
                .skip(offset)
                .limit(pageSize)
                .collect(Collectors.toList());

        return new PageDTO<>(pageData, pageIndex, pageSize, total, totalPages);
    }
}