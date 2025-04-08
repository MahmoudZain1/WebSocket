package com.example.websocketdemo.config;

import com.example.websocketdemo.model.User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CustomUserRegistry {

    private final Map<String, User> usernameToUser = new ConcurrentHashMap<>();
    private final Map<String, User> sessionIdToUser = new ConcurrentHashMap<>();

    public void register(User user) {
        usernameToUser.put(user.getUsername(), user);
        sessionIdToUser.put(user.getSessionId(), user);
    }

    public void removeBySessionId(String sessionId) {
        User user = sessionIdToUser.remove(sessionId);
        if (user != null) {
            usernameToUser.remove(user.getUsername());
        }
    }

    public User getUserByUsername(String username) {
        return usernameToUser.get(username);
    }

    public User getUserBySessionId(String sessionId) {
        return sessionIdToUser.get(sessionId);
    }

    public boolean isUserOnline(String username) {
        return usernameToUser.containsKey(username);
    }

    public Map<String, User> getAllUsers() {
        return usernameToUser;
    }
}