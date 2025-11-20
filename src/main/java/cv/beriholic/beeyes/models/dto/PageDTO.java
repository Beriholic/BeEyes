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
    private Integer total;
    private Integer totalPages;

    public static <T> PageDTO<T> of(T data) {
        return new PageDTO<>(data, 0, 10, null, null);
    }

    public static <T> PageDTO<T> of(T data, int pageIndex, int pageSize) {
        return new PageDTO<>(data, pageIndex, pageSize, null, null);
    }

    public static <T> PageDTO<List<T>> paginate(List<T> sourceList, Integer pageIndex, Integer pageSize) {
        if (sourceList == null) {
            sourceList = Collections.emptyList();
        }

        int total = sourceList.size();
        int offset = pageIndex * pageSize;

        int totalPages = (int) Math.ceil((double) total / pageSize);

        if (offset >= total) {
            return new PageDTO<>(Collections.emptyList(), pageIndex, pageSize, total, totalPages);
        }

        List<T> pageData = sourceList.stream()
                .skip(offset)
                .limit(pageSize)
                .collect(Collectors.toList());

        return new PageDTO<>(pageData, pageIndex, pageSize, total, totalPages);
    }
}
