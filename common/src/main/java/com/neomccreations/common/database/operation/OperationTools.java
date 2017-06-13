package com.neomccreations.common.database.operation;

import com.neomccreations.common.database.mutate.Mutators;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Contains stateless utilities relating
 * to code contained in this package.
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/12/2017 (4:10 PM)
 */
class OperationTools
{

    /**
     * Create a {@link PreparedStatement} for
     * the provided SQL and transfer data over
     * to it.
     *
     * @param connection The connection
     * @param sql        Raw SQL statement
     * @param data       Data for the SQL
     *
     * @return The statement
     * @throws SQLException In case something goes wrong
     */
    static PreparedStatement statement(Connection connection, String sql, Object[] data) throws SQLException
    {
        return transferData(connection.prepareStatement(sql), data);
    }

    /**
     * Prepare a statement with multiple
     * different operations.
     * <p>
     * We assume that the SQL and data are
     * both in the order to be used. In other
     * words, the SQL string in the {@code sql}
     * parameter at an index of {@code 1} should
     * match up with the data in the index of {@code 1}
     * in the {@code data} parameter.
     * <p>
     * Note that it is CRUCIAL for you to call {@link Connection#commit()}
     * after executing this statement.
     *
     * @param connection The connection
     * @param sql        SQL to use
     * @param data       Data to use
     *
     * @return The statement
     * @throws SQLException In the event that something goes wrong
     */
    static PreparedStatement statementBatch(Connection connection, String[] sql, Object[][] data) throws SQLException
    {
        // force the database transaction to
        // go in a group instead of one-per
        connection.setAutoCommit(false);

        // first one, used to create statement
        PreparedStatement statement = connection.prepareStatement(sql[0]);

        // remaining SQL statements
        for (int i = 1; i < sql.length; i++)
        {
            statement.addBatch(sql[i]);
            transferData(statement, data[i]);
        }

        return statement;
    }

    /**
     * Iterate over the data provided and
     * assign it to the {@link PreparedStatement} provided.
     * <p>
     * We will mutate provided into a data type
     * we deem compatible with SQL statements as well.
     *
     * @param statement The statement
     * @param data      The data to use
     *
     * @throws SQLException In the event that something goes wrong
     */
    @SuppressWarnings ( "unchecked" )
    static PreparedStatement transferData(PreparedStatement statement, Object[] data) throws SQLException
    {
        if (data != null)
        {
            for (int i = 0; i < data.length; i++)
            {
                Object _obj = data[i];

                if (Mutators.hasMutator(_obj.getClass()))
                    Mutators.of(_obj.getClass()).to(_obj, i + 1, statement);
                else
                    statement.setObject(i + 1, _obj);
            }
        }

        return statement;
    }

}
