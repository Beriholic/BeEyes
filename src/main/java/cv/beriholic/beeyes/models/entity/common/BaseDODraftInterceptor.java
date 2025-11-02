package cv.beriholic.beeyes.models.entity.common;

import cn.dev33.satoken.stp.StpUtil;
import org.babyfish.jimmer.ImmutableObjects;
import org.babyfish.jimmer.sql.DraftInterceptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BaseDODraftInterceptor implements DraftInterceptor<BaseDO, BaseDODraft> {
    @Override
    public void beforeSave(@NotNull BaseDODraft draft, @Nullable BaseDO original) {
        if (!ImmutableObjects.isLoaded(draft, BaseDOProps.UPDATED_AT)) {
            draft.setUpdatedAt(LocalDateTime.now());
        }
        if (original == null) {
            if (!ImmutableObjects.isLoaded(draft, BaseDOProps.CREATED_AT)) {
                draft.setCreatedAt(LocalDateTime.now());
            }
            if (!ImmutableObjects.isLoaded(draft, BaseDOProps.CREATED_BY)) {
                long loginIdAsLong = StpUtil.getLoginIdAsLong();
                draft.setCreatedBy(loginIdAsLong);
            }
        }
    }
}
