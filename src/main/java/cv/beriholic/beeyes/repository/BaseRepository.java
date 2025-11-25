package cv.beriholic.beeyes.repository;

import cv.beriholic.beeyes.models.dto.PageDTO;
import org.babyfish.jimmer.Page;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.spring.repo.support.AbstractJavaRepository;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.MutableDelete;
import org.babyfish.jimmer.sql.ast.mutation.MutableUpdate;
import org.babyfish.jimmer.sql.ast.query.MutableRootQuery;
import org.babyfish.jimmer.sql.ast.query.specification.JSpecification;
import org.babyfish.jimmer.sql.ast.table.spi.TableProxy;
import org.babyfish.jimmer.sql.fetcher.Fetcher;

import java.util.List;

/**
 * 基础仓储类，提供通用的CRUD操作和逻辑删除功能
 *
 * @param <E> 实体类型
 * @param <T> 表类型
 * @param <D> EntityId
 */
public abstract class BaseRepository<E, T extends TableProxy<E>, D> extends AbstractJavaRepository<E, D> {
    protected final T table;

    public BaseRepository(JSqlClient sql, T table) {
        super(sql);
        this.table = table;
    }

    public JSqlClient getSql() {
        return this.sql;
    }

    protected MutableRootQuery<T> createQuery() {
        return sql.createQuery(table);
    }

    protected MutableUpdate createUpdate() {
        return sql.createUpdate(table);
    }

    protected MutableDelete createDelete() {
        return sql.createDelete(table);
    }


    public List<E> findBySpec(JSpecification<E, T> spec, Fetcher<E> fetcher) {
        return createQuery()
                .where(spec)
                .select(table.fetch(fetcher))
                .execute();

    }

    public List<E> findBySpec(PageDTO<? extends JSpecification<E, T>> pageDTO, Fetcher<E> fetcher) {
        return createQuery()
                .where(pageDTO.getData())
                .select(table.fetch(fetcher))
                .fetchPage(pageDTO.getPageIndex(), pageDTO.getPageSize())
                .getRows();
    }


    public E findBySpecOne(JSpecification<E, T> spec, Fetcher<E> fetcher) {
        return createQuery()
                .where(spec)
                .select(table.fetch(fetcher))
                .fetchFirst();
    }

    public List<E> findBySpec(JSpecification<E, T> spec) {
        return createQuery()
                .where(spec)
                .select(table)
                .execute();
    }

    public Page<E> findBySpecFetchPage(JSpecification<E, T> spec, Integer pageIndex, Integer pageSize) {
        return createQuery()
                .where(spec)
                .select(table)
                .fetchPage(pageIndex, pageSize);
    }

    public Page<E> findBySpecFetchPage(JSpecification<E, T> spec, Integer pageIndex, Integer pageSize, Fetcher<E> fetcher) {
        return createQuery()
                .where(spec)
                .select(table.fetch(fetcher))
                .fetchPage(pageIndex, pageSize);
    }

    public <V extends View<E>> Page<V> findBySpecFetchPage(JSpecification<E, T> spec, Integer pageIndex, Integer pageSize, Class<V> viewType) {
        return createQuery()
                .where(spec)
                .select(table.fetch(viewType))
                .fetchPage(pageIndex, pageSize);
    }

    public <V extends View<E>> List<V> findBySpec(JSpecification<E, T> spec, Class<V> viewType) {
        return createQuery()
                .where(spec)
                .select(table.fetch(viewType))
                .execute();
    }

    public <V extends View<E>> V findBySpecOne(JSpecification<E, T> spec, Class<V> viewType) {
        return createQuery()
                .where(spec)
                .select(table.fetch(viewType))
                .fetchOneOrNull();
    }

    public <V extends View<E>> List<V> findBySpec(PageDTO<? extends JSpecification<E, T>> pageDTO, Class<V> viewType) {
        return createQuery()
                .where(pageDTO.getData())
                .select(table.fetch(viewType))
                .fetchPage(pageDTO.getPageIndex(), pageDTO.getPageSize())
                .getRows();
    }

    public List<E> findBySpec(PageDTO<? extends JSpecification<E, T>> pageDTO) {
        return createQuery()
                .where(pageDTO.getData())
                .select(table)
                .fetchPage(pageDTO.getPageIndex(), pageDTO.getPageSize())
                .getRows();
    }

    public E findBySpecOne(JSpecification<E, T> spec) {
        return createQuery()
                .where(spec)
                .select(table)
                .fetchFirst();
    }

    public <V extends View<E>> V findBySpecOne(Class<V> viewType, JSpecification<E, T> spec) {
        return createQuery()
                .where(spec)
                .select(table.fetch(viewType))
                .fetchFirst();
    }
}
