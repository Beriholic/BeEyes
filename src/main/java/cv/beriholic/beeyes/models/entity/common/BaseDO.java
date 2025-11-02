package cv.beriholic.beeyes.models.entity.common;

import jakarta.annotation.Nullable;
import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.LogicalDeleted;
import org.babyfish.jimmer.sql.MappedSuperclass;

import java.time.LocalDateTime;

@MappedSuperclass
public interface BaseDO {

    @Column(name = "created_at")
    @Nullable
    LocalDateTime createdAt();

    @Column(name = "updated_at")
    @Nullable
    LocalDateTime updatedAt();

    @Column(name = "is_deleted")
    @LogicalDeleted("true")
    boolean isDeleted();

    @Column(name = "created_by")
    @Nullable
    Long createdBy();
}
