package service;

import model.user.User;
import model.user.UserFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseService {
    private static final Set<String> globalUsernameRegistry = new HashSet<>();

    private final Map<String, User> allUserMap = new HashMap<>();
    private final Set<User> loggedInUserSet = new HashSet<>();

    public User signUp(String type, String username, String password) {
        if (globalUsernameRegistry.contains(username)) {
            System.out.println("Username '" + username + "' is already taken. Please choose a different username.");
            return null;
        }
        if (allUserMap.containsKey(username)) {
            System.out.println("User already exists. Please login.");
            return null;
        }
        User user = UserFactory.createUser(type.toUpperCase(), username, password);
        if (user == null) {
            System.out.println("Failed to create user.");
            return null;
        }
        globalUsernameRegistry.add(username);
        allUserMap.put(username, user);
        loggedInUserSet.add(user);
        return user;
    }

    public User login(String username, String password) {
        User user = allUserMap.get(username);
        if (user == null) {
            System.out.println("No such user. Please sign up first.");
            return null;
        }
        if (loggedInUserSet.contains(user)) {
            System.out.println("User already logged in.");
            return user;
        }
        if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
            loggedInUserSet.add(user);
            return user;
        }
        System.out.println("Login failed. Incorrect password.");
        return null;
    }

    public void logout(String username) {
        User user = findLoggedInUser(username);
        if (user == null) {
            System.out.println("User not logged in.");
            return;
        }
        loggedInUserSet.remove(user);
        System.out.println(username + " logged out successfully.");
    }

    public void printProfile(String username) {
        User user = allUserMap.get(username);
        if (user == null) {
            System.out.println("No such user.");
            return;
        }
        System.out.println("----------------------------------------------------------------");
        System.out.printf("|%-15s %-45s |%n", " User ID: ", user.getUserId());
        System.out.printf("|%-15s %-45s |%n", " Username: ", user.getUsername());
        System.out.printf("|%-15s %-45s |%n", " User type: ", user.getClass().getSimpleName());
        System.out.println("----------------------------------------------------------------");
    }

    public User findLoggedInUser(String username) {
        User user = allUserMap.get(username);
        if (user == null)
            return null;
        return loggedInUserSet.stream()
                .filter(u -> u.getUserId().equals(user.getUserId()))
                .findFirst()
                .orElse(null);
    }

    public User getUserByUsername(String username) {
        return allUserMap.get(username);
    }

    public Boolean isAnyUserLoggedIn() {
        return !loggedInUserSet.isEmpty();
    }

    public Set<User> getLoggedInUsers() {
        return loggedInUserSet;
    }

    public Map<String, User> getAllUserMap() {
        return allUserMap;
    }
}
