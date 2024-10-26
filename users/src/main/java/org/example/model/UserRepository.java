package org.example.model;

import java.util.Collection;

public interface UserRepository {
    void addUser(User user);
    void deleteUser(String id);
    Collection<User> getUsers();
    User getUser(String id);
}
