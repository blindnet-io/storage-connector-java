package io.blindnet.storageconnectors.java.example;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.io.IOException;
import java.util.Objects;

public class Database {
    private static Jdbi jdbi;

    public static User.Dao users;

    static void init() throws IOException {
        jdbi = Jdbi.create("jdbc:h2:mem:blindnet;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false");
        jdbi.installPlugin(new SqlObjectPlugin());

        jdbi.registerRowMapper(new User.Mapper());
        users = jdbi.onDemand(User.Dao.class);
        users.createTable();
        users.insert("John", "Doe", "john.doe@example.com",
                Objects.requireNonNull(Database.class.getResourceAsStream("/john_doe_proof.pdf")).readAllBytes());
    }
}
