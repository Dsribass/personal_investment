package com.example.personal_investment.application.data.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import org.sqlite.SQLiteDataSource;

public class ConnectionFactory implements AutoCloseable{

    private static Connection connection;
    private static PreparedStatement preparedStatement;
    private static Statement statement;

    public static Connection createConnection(){
        try {
            instantiateConnectionIfNull();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private static void instantiateConnectionIfNull() throws SQLException {
        SQLiteDataSource ds = new SQLiteDataSource();
        // TODO criar database
        //ds.setUrl("jdbc:sqlite:name_bd.db");
        if(connection == null)
            connection = ds.getConnection();
    }


    public static PreparedStatement createPreparedStatement(String sql){
        try{
            preparedStatement = createConnection().prepareStatement(sql);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return preparedStatement;
    }

    public static Statement createStatement(){
        try{
            statement = createConnection().createStatement();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return statement;
    }

    private void closeConnectionIfNotNull() throws SQLException {
        if(connection != null)
            connection.close();
    }

    private void closeStatementsIfNotNull() throws SQLException {
        if(preparedStatement != null)
            preparedStatement.close();
        if(statement != null)
            statement.close();
    }

    @Override
    public void close() throws Exception {
        closeStatementsIfNotNull();
        closeConnectionIfNotNull();
    }
}
