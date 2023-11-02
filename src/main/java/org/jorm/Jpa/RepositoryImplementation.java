package org.jorm.Jpa;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.Closeable;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * The Repository class is a generic DAO implementation for CRUD operations on entities.
 * It provides a set of methods for retrieving, creating, updating, and deleting entities.
 * It also provides methods for building custom queries using a fluent API.
 *
 * @param <T> The type of entity to be managed.
 */
@Slf4j
public abstract class RepositoryImplementation<T> implements RepositoryI<T>, Serializable, Closeable {

    private final Class<T> _clazz;
    private final StringBuilder _query = new StringBuilder();
    private final Queue<Object> _queryParam = new LinkedList<>();
    private volatile String _table;

    @PersistenceContext
    private  EntityManagerFactory emf;
    private volatile EntityManager em = null;


    /**
     * Constructs a new Dao instance for a specific entity type.
     */
    @SuppressWarnings("unchecked")
    public RepositoryImplementation() {

        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        Type type = superClass.getActualTypeArguments()[0];
        _clazz = (Class<T>) type;

        _table = _clazz.getSimpleName();

        if (_table == null && _clazz.isAnnotationPresent(Table.class))
            _table = _clazz.getAnnotation(Table.class).name();

    }

    /**
     * Obtains an instance of EntityManager following a singleton pattern.
     * If the instance doesn't exist yet, it is created and initialized.
     *
     * @return The EntityManager instance.
     */
    protected synchronized EntityManager getEntityManager() {
        if (emf != null && em != null) {
            synchronized (Repository.class) {
                if (em == null) {
                    try {
                        em = emf.createEntityManager();
                    } catch (Exception e) {
                        log.error("Error while creating entity manager", e);
                    }
                }
            }
        } else {
            //TODO:add a exception handler for no entity manager factory
        }
        return em;
    }

    /**
     * Retrieve an entity by its ID.
     *
     * @param id The ID of the entity to retrieve.
     * @return An Optional containing the entity if found, or empty if not found.
     */
    @Override
    public Optional<T> get(UUID id) {
        return Optional.ofNullable(getEntityManager().find(_clazz, id));
    }

    /**
     * Retrieve all entities of type T from the database.
     *
     * @return A list of entities of type T.
     */
    @Override
    public List<T> getAll() {
        TypedQuery<T> query = getEntityManager().createQuery("FROM " + _table, _clazz);
        return query.getResultList();
    }

    /**
     * Create a new entity in the database.
     *
     * @param entity The entity to create.
     * @return An Optional containing the created entity, or empty if creation failed.
     */
    @Override
    @Transactional
    public Optional<T> create(T entity) {
        EntityTransaction transaction = getEntityManager().getTransaction();
        try {
            transaction.begin();
            getEntityManager().persist(entity);
            transaction.commit();
            return Optional.of(entity);
        } catch (Exception e) {
            log.error("Error while creating entity", e);
            if (transaction.isActive())
                transaction.rollback();
            return Optional.empty();
        }
    }

    /**
     * Update an entity in the database.
     *
     * @param entity The entity to update.
     * @return An Optional containing the updated entity, or empty if the update failed.
     */
    @Override
    @Transactional
    public Optional<T> update(T entity) {
        EntityTransaction transaction = getEntityManager().getTransaction();
        try {
            transaction.begin();
            T updatedEntity = getEntityManager().merge(entity);
            transaction.commit();
            return Optional.of(updatedEntity);
        } catch (Exception e) {
            log.error("Error while updating entity", e);
            if (transaction.isActive())
                transaction.rollback();
            return Optional.empty();
        }
    }

    /**
     * Find an entity based on custom criteria.
     *
     * @param criteria The criteria for the search.
     * @return An Optional containing the found entity, or empty if not found.
     */
    @Override
    public Optional<T> find(Object criteria) {
        EntityTransaction transaction = getEntityManager().getTransaction();
        try {
            transaction.begin();
            T entity = getEntityManager().find(_clazz, criteria);
            transaction.commit();
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            log.error("Error while finding entity", e);
            if (transaction.isActive())
                transaction.rollback();
            return Optional.empty();
        }
    }

    /**
     * Constructs a WHERE clause to filter query results based on a column and its value.
     *
     * @param key   The name of the column.
     * @param value The value to compare.
     * @return The current Dao instance with the WHERE clause added.
     */
    @Override
    public RepositoryImplementation<T> where(String key, Object value) {
        if (!_query.isEmpty())
            _query.setLength(0);
        if (!_queryParam.isEmpty())
            _queryParam.clear();
        _queryParam.add(value);
        _query.append(" WHERE ").append(key).append(" = :").append("param").append(_queryParam.size());
        return this;
    }

    /**
     * Constructs a WHERE clause to filter query results based on a column, a custom comparison operator, and a value.
     *
     * @param key      The name of the column.
     * @param operator The custom comparison operator (e.g., "=", "<").
     * @param value    The value to compare.
     * @return The current Dao instance with the WHERE clause added.
     */
    @Override
    public RepositoryImplementation<T> where(String key, String operator, Object value) {
        if (!_query.isEmpty())
            _query.setLength(0);
        if (!_queryParam.isEmpty())
            _queryParam.clear();
        _queryParam.add(value);
        _query.append(" WHERE ").append(key).append(" ").append(operator).append(" :").append("param").append(_queryParam.size());
        return this;
    }

    /**
     * Constructs an AND clause to further filter query results based on a column and its value.
     *
     * @param key   The name of the column.
     * @param value The value to compare.
     * @return The current Dao instance with the AND clause added.
     */
    @Override
    public RepositoryImplementation<T> and(String key, Object value) {
        _queryParam.add(value);
        _query.append(" AND ").append(key).append(" = :").append("param").append(_queryParam.size());
        return this;
    }

    /**
     * Constructs an AND clause to further filter query results based on a column, a custom comparison operator, and a value.
     *
     * @param key      The name of the column.
     * @param operator The custom comparison operator (e.g., "=", "<").
     * @param value    The value to compare.
     * @return The current Dao instance with the AND clause added.
     */
    @Override
    public RepositoryImplementation<T> and(String key, String operator, Object value) {
        _queryParam.add(value);
        _query.append(" AND ").append(key).append(" ").append(operator).append(" :").append("param").append(_queryParam.size());
        return this;
    }

    /**
     * Constructs an OR clause to further filter query results based on a column and its value.
     *
     * @param key   The name of the column.
     * @param value The value to compare.
     * @return The current Dao instance with the OR clause added.
     */
    @Override
    public RepositoryImplementation<T> or(String key, Object value) {
        _queryParam.add(value);
        _query.append(" OR ").append(key).append(" = :").append("param").append(_queryParam.size());
        return this;
    }

    /**
     * Constructs an OR clause to further filter query results based on a column, a custom comparison operator, and a value.
     *
     * @param key      The name of the column.
     * @param operator The custom comparison operator (e.g., "=", "<").
     * @param value    The value to compare.
     * @return The current Dao instance with the OR clause added.
     */
    @Override
    public RepositoryImplementation<T> or(String key, String operator, Object value) {
        _queryParam.add(value);
        _query.append(" OR ").append(key).append(" ").append(operator).append(" :").append("param").append(_queryParam.size());
        return this;
    }

    /**
     * Constructs a LIKE clause to search query results based on a column and a value pattern.
     *
     * @param key   The name of the column.
     * @param value The value to search for.
     * @return The current Dao instance with the LIKE clause added.
     */
    @Override
    public RepositoryImplementation<T> like(String key, Object value) {
        _queryParam.add(value);
        _query.append(" WHERE ").append(key).append(" LIKE :").append("param").append("param").append(_queryParam.size());
        return this;
    }

    /**
     * Limits the maximum number of records to return in the query result.
     *
     * @param limit The maximum number of records to return.
     * @return The current Dao instance with the LIMIT clause added.
     */
    @Override
    public RepositoryImplementation<T> limit(int limit) {
        _query.append(" LIMIT ").append(limit);
        return this;
    }

    /**
     * Sets the number of records to skip before starting to return results in the query result.
     *
     * @param offset The number of records to skip.
     * @return The current Dao instance with the OFFSET clause added.
     */
    @Override
    public RepositoryImplementation<T> offset(int offset) {
        _query.append(" OFFSET ").append(offset);
        return this;
    }

    /**
     * Specifies the column and sorting direction for ordering query results.
     *
     * @param key       The column name for sorting.
     * @param direction The sorting direction (ascending or descending).
     * @return The current Dao instance with the ORDER BY clause added.
     */
    @Override
    public RepositoryImplementation<T> orderBy(String key, String direction) {
        _query.append(" ORDER BY ").append(key).append(" ").append(direction);
        return this;
    }

    /**
     * Retrieves records from the associated database table based on the constructed query.
     *
     * @return A list of model objects representing the retrieved records.
     */
    @Override
    public List<T> find() {
        if (_query.isEmpty())
            return new ArrayList<>();
        EntityTransaction transaction = getEntityManager().getTransaction();
        try {
            transaction.begin();
            TypedQuery<T> query = getEntityManager().createQuery("FROM " + _table + _query, _clazz);
            int queryParamCount = _queryParam.size();
            for (int i = 0; i < queryParamCount; i++)
                query.setParameter("param" + (i + 1), _queryParam.poll());
            transaction.commit();
            return query.getResultList();
        } catch (Exception e) {
            log.error("Error while updating entity", e);
            if (transaction.isActive())
                transaction.rollback();
        }
        return new ArrayList<>();
    }

    /**
     * Retrieves a single record from the associated database table based on the constructed query.
     *
     * @return The model object representing the retrieved record, or null if not found.
     */
    @Override
    public Optional<T> findOne() {
        if (_query.isEmpty())
            return Optional.empty();
        EntityTransaction transaction = getEntityManager().getTransaction();
        try {
            transaction.begin();
            TypedQuery<T> query = getEntityManager().createQuery("FROM " + _table + _query, _clazz);
            query.setMaxResults(1);
            int queryParamCount = _queryParam.size();
            for (int i = 0; i < queryParamCount; i++)
                query.setParameter("param" + (i + 1), _queryParam.poll());
            transaction.commit();
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            log.error("Error while updating entity", e);
            if (transaction.isActive())
                transaction.rollback();
        }
        return Optional.empty();
    }

    /**
     * Counts the number of records returned by the query.
     *
     * @return The number of records returned by the query.
     */
    @Override
    public Long count() {
        if (_query.isEmpty()) {
            TypedQuery<Long> countQuery = getEntityManager().createQuery("SELECT COUNT(e) FROM " + _table + " e", Long.class);
            return countQuery.getSingleResult();
        } else {
            TypedQuery<Long> countQuery = getEntityManager().createQuery("SELECT COUNT(e) FROM " + _table + " e " + _query, Long.class);
            for (int i = 0; i < _queryParam.size(); i++) {
                countQuery.setParameter(i + 1, _queryParam.poll());
            }
            return countQuery.getSingleResult();
        }
    }


    /**
     * Delete an entity from the database.
     *
     * @param entity The entity to delete.
     * @return true if deletion is successful, false otherwise.
     */
    @Override
    @Transactional
    public boolean delete(T entity) {
        EntityTransaction transaction = getEntityManager().getTransaction();
        try {
            transaction.begin();
            getEntityManager().remove(entity);
            transaction.commit();
            return true;
        } catch (Exception e) {
            log.error("Error while deleting entity", e);
            if (transaction.isActive())
                transaction.rollback();
            return false;
        }
    }

    /**
     * Closes the EntityManager if it's open. This method is called to release resources associated
     * with the EntityManager. It should be called when you have finished using the EntityManager.
     * Any exceptions that may occur during closing are captured and logged.
     */
    @Override
    public void close() {
        try {
            if (em != null && em.isOpen())
                em.close();
        } catch (Exception e) {
            log.error("Error closing EntityManager", e);
        }
    }

}