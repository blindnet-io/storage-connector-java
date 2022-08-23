package io.blindnet.storageconnectors.java.example;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.sql.ResultSet;
import java.sql.SQLException;

public record User(String firstName, String lastName, String email, byte[] proof) {
    public String fullName() {
        return firstName() + " " + lastName();
    }

    public interface Dao {
        @SqlUpdate("CREATE TABLE users (first_name varchar, last_name varchar, email varchar, proof varbinary)")
        void createTable();

        @SqlUpdate("INSERT INTO users (first_name, last_name, email, proof) VALUES (?, ?, ?, ?)")
        void insert(String firstName, String lastName, String email, byte[] proof);

        @SqlQuery("SELECT * FROM users WHERE email=?")
        User findByEmail(String email);
    }

    public static class Mapper implements RowMapper<User> {
        @Override
        public User map(ResultSet rs, StatementContext ctx) throws SQLException {
            return new User(
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getBytes("proof")
            );
        }
    }
}
