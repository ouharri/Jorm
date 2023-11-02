package org.jorm.Jpa;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RepositoryI<T> {

    /**
     * Retrieves an entity by its unique identifier.
     *
     * @param id The unique identifier of the entity.
     * @return An Optional containing the retrieved entity, or an empty Optional if not found.
     * @throws SQLException If a database access error occurs.
     */
    Optional<T> get(UUID id);

    /**
     * Retrieves all entities of type T.
     *
     * @return A list of all entities.
     */
    List<T> getAll();

    /**
     * Creates a new entity in the database.
     *
     * @param entity The entity to be created.
     * @return An Optional containing the created entity, or an empty Optional if there's an error.
     * @throws SQLException If a database access error occurs.
     */
    Optional<T> create(T entity) throws SQLException;

    /**
     * Updates an existing entity in the database.
     *
     * @param entity The entity to be updated.
     * @return An Optional containing the updated entity, or an empty Optional if there's an error.
     */
    Optional<T> update(T entity);

    /**
     * Finds entities based on certain search criteria.
     *
     * @param criteria The search criteria.
     * @return A list of entities that match the criteria.
     */
    Optional<T> find(Object criteria);

    /**
     * Specifies a "WHERE" condition for queries.
     *
     * @param key   The name of the column.
     * @param value The value to compare.
     * @return An instance of the model with the "WHERE" condition added.
     */
    RepositoryI<T> where(String key, Object value);

    /**
     * Specifies a "WHERE" condition with a custom operator for queries.
     *
     * @param key      The name of the column.
     * @param operator The custom comparison operator (e.g., "=", "<").
     * @param value    The value to compare.
     * @return An instance of the model with the "WHERE" condition added.
     */
    RepositoryI<T> where(String key, String operator, Object value);

    /**
     * Specifies an "AND" condition for queries.
     *
     * @param key   The name of the column.
     * @param value The value to compare.
     * @return An instance of the model with the "AND" condition added.
     */
    RepositoryI<T> and(String key, Object value);

    /**
     * Specifies an "AND" condition with a custom operator for queries.
     *
     * @param key      The name of the column.
     * @param operator The custom comparison operator (e.g., "=", "<").
     * @param value    The value to compare.
     * @return An instance of the model with the "AND" condition added.
     */
    RepositoryI<T> and(String key, String operator, Object value);

    /**
     * Specifies an "OR" condition for queries.
     *
     * @param key   The name of the column.
     * @param value The value to compare.
     * @return An instance of the model with the "OR" condition added.
     */
    RepositoryI<T> or(String key, Object value);

    /**
     * Specifies an "OR" condition with a custom operator for queries.
     *
     * @param key      The name of the column.
     * @param operator The custom comparison operator (e.g., "=", "<").
     * @param value    The value to compare.
     * @return An instance of the model with the "OR" condition added.
     */
    RepositoryI<T> or(String key, String operator, Object value);

    /**
     * Specifies a "LIKE" condition for queries.
     *
     * @param key   The name of the column.
     * @param value The value to search for.
     * @return An instance of the model with the "LIKE" condition added.
     */
    RepositoryI<T> like(String key, Object value);

    /**
     * Limits the number of results returned by the query.
     *
     * @param limit The maximum number of records to return.
     * @return An instance of the model with the limit added.
     */
    RepositoryI<T> limit(int limit);

    /**
     * Sets an offset for query results.
     *
     * @param offset The number of records to skip before starting to return results.
     * @return An instance of the model with the offset added.
     */
    RepositoryI<T> offset(int offset);

    /**
     * Specifies the sorting order of query results.
     *
     * @param key       The column name for sorting.
     * @param direction The sorting direction (ascending or descending).
     * @return An instance of the model with the specified sorting order.
     */
    RepositoryI<T> orderBy(String key, String direction);

    /**
     * Executes the query and returns a list of results.
     *
     * @return A list of models corresponding to the query.
     */
    List<T> find();

    /**
     * Executes the query and returns a single result.
     *
     * @return A model corresponding to the query.
     */
    Optional<T> findOne();

    /**
     * Counts the number of records returned by the query.
     *
     * @return The number of records returned by the query.
     */
    Long count();

    /**
     * Deletes an entity from the database.
     *
     * @param entity The entity to be deleted.
     * @return True if the deletion is successful, otherwise false.
     */
    boolean delete(T entity);
}
