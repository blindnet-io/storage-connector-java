package io.blindnet.storageconnector.example;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.sql.ResultSet;
import java.sql.SQLException;

public record User(String email, String firstName, String lastName, byte[] proof) {
    public String fullName() {
        return firstName() + " " + lastName();
    }

    public interface Dao {
        @SqlUpdate("CREATE TABLE users (email varchar primary key, first_name varchar, last_name varchar, proof varbinary)")
        void createTable();

        @SqlUpdate("MERGE INTO users u USING (VALUES (?, ?, ?, ?)) AS v (email, first_name, last_name, proof) " +
                "ON u.email = v.email " +
                "WHEN NOT MATCHED THEN INSERT VALUES (v.email, v.first_name, v.last_name, v.proof) " +
                "WHEN MATCHED THEN UPDATE SET u.first_name = v.first_name, u.last_name=v.last_name, u.proof = v.proof")
        void upsert(String email, String firstName, String lastName, byte[] proof);

        @SqlQuery("SELECT * FROM users WHERE email=?")
        User findByEmail(String email);
    }

    public static class Mapper implements RowMapper<User> {
        @Override
        public User map(ResultSet rs, StatementContext ctx) throws SQLException {
            return new User(
                    rs.getString("email"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getBytes("proof")
            );
        }
    }
}
