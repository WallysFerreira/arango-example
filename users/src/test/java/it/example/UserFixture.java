package it.example;

import org.example.model.LoginDetails;
import org.example.model.User;

import java.util.List;
import java.util.UUID;

public class UserFixture {
    public static List<User> someUsers() {
        return List.of(
                aUser(),
                aUserWith(UUID.randomUUID().toString(), "some@email.com", "123lol"),
                aUserWith(UUID.randomUUID().toString(), "another@email.com", "pop03030")
        );
    }

    public static User aUser() {
        return new User(UUID.randomUUID().toString(), loginDetails());
    }

    private static User aUserWith(String key, String email, String password) {
        return new User(key, loginDetailsWith(email, password));
    }

    private static LoginDetails loginDetailsWith(String email, String password) {
        return new LoginDetails(email, password);
    }

    private static LoginDetails loginDetails() {
        return new LoginDetails("email@email.com", "password123");
    }
}
