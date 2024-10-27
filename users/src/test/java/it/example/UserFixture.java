package it.example;

import org.example.model.LoginDetails;
import org.example.model.User;

import java.util.UUID;

public class UserFixture {
    public static User aUser() {
        return new User(UUID.randomUUID().toString(), loginDetails());
    }

    private static LoginDetails loginDetails() {
        return new LoginDetails("email@email.com", "password123");
    }
}
