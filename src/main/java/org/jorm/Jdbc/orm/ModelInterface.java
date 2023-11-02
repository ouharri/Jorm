package org.jorm.Jdbc.orm;


import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The `Model` interface defines the standard CRUD (Create, Read, Update, Delete) operations
 * that can be performed on a database table.
 *
 * @param <T> The type associated with this interface.
 */
public interface ModelInterface<T> {

    /**
     * Associates a model object with this ORM instance.
     *
     * @param object The model object to associate.
     * @return An instance of the model with the object associated.
     */
    ModelInterface<T> setObject(T object);

    /**
     * Starts a database transaction.
     *
     * @return An instance of the model with a transaction in progress.
     */
    ModelInterface<T> beginTransaction();

    /**
     * Commits and validates the ongoing transaction.
     *
     * @return An instance of the model after successfully committing the transaction.
     */
    ModelInterface<T> commitTransaction();

    /**
     * Rolls back and cancels the ongoing transaction.
     *
     * @return An instance of the model after canceling the transaction.
     */
    ModelInterface<T> rollbackTransaction();

    /**
     * Retrieves all records from the associated database table.
     *
     * @return A list of maps, where each map represents a row of data with column names as keys.
     */
    List<T> getAll();

    /**
     * Retrieves a specific record from the database table.
     *
     * @return A model representing the specific record.
     */
    T get(Object[] key);

    /**
     * Saves the associated model object to the database.
     *
     * @return The saved model.
     */
    T save();

    /**
     * Inserts a new record into the associated database table.
     *
     * @param obj The model object to insert.
     * @return The inserted model.
     */
    T insert(T obj);

    /**
     * Specifies a "WHERE" condition for queries.
     *
     * @param key   The name of the column.
     * @param value The value to compare.
     * @return An instance of the model with the "WHERE" condition added.
     */
    ModelInterface<T> where(String key, Object value);

    /**
     * Specifies a "WHERE" condition with a custom operator for queries.
     *
     * @param key      The name of the column.
     * @param operator The custom comparison operator (e.g., "=", "<").
     * @param value    The value to compare.
     * @return An instance of the model with the "WHERE" condition added.
     */
    ModelInterface<T> where(String key, String operator, Object value);

    /**
     * Specifies an "AND" condition for queries.
     *
     * @param key   The name of the column.
     * @param value The value to compare.
     * @return An instance of the model with the "AND" condition added.
     */
    ModelInterface<T> and(String key, Object value);

    /**
     * Specifies an "AND" condition with a custom operator for queries.
     *
     * @param key      The name of the column.
     * @param operator The custom comparison operator (e.g., "=", "<").
     * @param value    The value to compare.
     * @return An instance of the model with the "AND" condition added.
     */
    ModelInterface<T> and(String key, String operator, Object value);

    /**
     * Specifies an "OR" condition for queries.
     *
     * @param key   The name of the column.
     * @param value The value to compare.
     * @return An instance of the model with the "OR" condition added.
     */
    ModelInterface<T> or(String key, Object value);

    /**
     * Specifies an "OR" condition with a custom operator for queries.
     *
     * @param key      The name of the column.
     * @param operator The custom comparison operator (e.g., "=", "<").
     * @param value    The value to compare.
     * @return An instance of the model with the "OR" condition added.
     */
    ModelInterface<T> or(String key, String operator, Object value);

    /**
     * Specifies a "LIKE" condition for queries.
     *
     * @param key   The name of the column.
     * @param value The value to search for.
     * @return An instance of the model with the "LIKE" condition added.
     */
    ModelInterface<T> like(String key, Object value);

    /**
     * Limits the number of results returned by the query.
     *
     * @param limit The maximum number of records to return.
     * @return An instance of the model with the limit added.
     */
    ModelInterface<T> limit(int limit);

    /**
     * Sets an offset for query results.
     *
     * @param offset The number of records to skip before starting to return results.
     * @return An instance of the model with the offset added.
     */
    ModelInterface<T> offset(int offset);

    /**
     * Specifies the sorting order of query results.
     *
     * @param key       The column name for sorting.
     * @param direction The sorting direction (ascending or descending).
     * @return An instance of the model with the specified sorting order.
     */
    ModelInterface<T> orderBy(String key, String direction);

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
    T findOne();

    /**
     * Updates records in the associated database table with the provided data based on specified primary key values.
     *
     * @return True if the update operation is successful; otherwise, false.
     */
    boolean update();

    /**
     * Updates records in the associated database table with the provided data based on specified primary key values.
     *
     * @param obj The model object to update.
     * @return True if the update operation is successful; otherwise, false.
     */
    boolean update(T obj);

    /**
     * Deletes records from the associated database table based on specified primary key values.
     *
     * @return True if the delete operation is successful; otherwise, false.
     */
    boolean delete();

    /**
     * Deletes records from the associated database table based on specified primary key values.
     *
     * @return True if the delete operation is successful; otherwise, false.
     */
    boolean softDelete();

    /**
     * Deletes records from the associated database table based on specified primary key values.
     *
     * @return True if the delete operation is successful; otherwise, false.
     */
    boolean deleteByIDs(Object[] ids);

    /**
     * Counts the number of records returned by the query.
     *
     * @return The number of records returned by the query.
     */
    Long count();

    /**
     * Executes a raw SQL query.
     *
     * @param sql The SQL query to execute.
     * @return A list of maps, where each map represents a row of data with column names as keys.
     */
    List<Map<String, Object>> query(final String sql);

    /**
     * Executes a raw SQL query.
     *
     * @param sql    The SQL query to execute.
     * @param params The parameters to bind to the query.
     * @return A list of maps, where each map represents a row of data with column names as keys.
     */
    List<Map<String, Object>> query(final String sql, final Object[] params);
}
