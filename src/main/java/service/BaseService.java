package service;

import exception.UserNotFoundException;
import model.user.User;
import model.user.UserFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class BaseService {
    private static final Set<String> globalUsernameRegistry = new HashSet<>();
    private static final Set<String> globalPhoneNumberRegistry = new HashSet<>();

    private final Map<String, User> allUserMap = new HashMap<>();
    private final Set<User> loggedInUserSet = new HashSet<>();
    private final String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$";

    public User signUp(String type, String username, String password, String phoneNumber) {
        if(!password.matches(passwordPattern)){
            System.out.println("Password must contain at least 6 characters, a number, a char, a special symbol.");
            return null;
        }
        if (globalUsernameRegistry.contains(username)) {
            System.out.println("Username '" + username + "' is already taken. Please choose a different username.");
            return null;
        }
        if(phoneNumber.length() != 10 ||
                Integer.parseInt(phoneNumber.substring(0, 1)) > 9
                        || Integer.parseInt(phoneNumber.substring(0, 1))<6){
            System.out.println("Enter phone number with length 10 and first 6 <= number <= 9");
            return null;
        }
        if (globalPhoneNumberRegistry.contains(phoneNumber)) {
            System.out.println("Phone number '" + phoneNumber + "' is already registered. Please choose a different phone number.");
            return null;
        }
        if (allUserMap.containsKey(username)) {
            System.out.println("User already exists. Please login.");
            return null;
        }
        User user = UserFactory.createUser(type.toUpperCase(), username, password, phoneNumber);
        if (user == null) {
            System.out.println("Failed to create user.");
            return null;
        }
        globalUsernameRegistry.add(username);
        globalPhoneNumberRegistry.add(phoneNumber);
        allUserMap.put(username, user);
        loggedInUserSet.add(user);
        return user;
    }

    public User login(String username, String password) throws UserNotFoundException {
        User user = allUserMap.get(username);
        if (user == null) {
            throw new UserNotFoundException("No such user. Please sign up first.");
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

    public void printProfile(String username) throws UserNotFoundException{
        User user = allUserMap.get(username);
        if (user == null) {
            throw new UserNotFoundException("No such user in system.");
        }
        System.out.println("----------------------------------------------------------------");
        System.out.printf("|%-15s %-45s |%n", "User ID: ", user.getUserId());
        System.out.printf("|%-15s %-45s |%n", "User type: ", user.getClass().getSimpleName());
        System.out.printf("|%-15s %-45s |%n", "Username: ", user.getUsername());
        System.out.printf("|%-15s %-45s |%n", "Phone No.: ", user.getPhoneNumber());
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

    public void editUserProfile(User user, Map<String, String> updateInfo){
        String username = updateInfo.get("username");
        String password = updateInfo.get("password");
        String phoneNumber = updateInfo.get("phone_number");

        if(!username.equals("0") && !globalUsernameRegistry.contains(username)){
            globalUsernameRegistry.remove(user.getUsername());
            globalUsernameRegistry.add(username);
            allUserMap.remove(user.getUsername());
            allUserMap.put(username, user);
            user.setUsername(username);
        }

        if(!password.equals("0") && password.matches(passwordPattern)){
            user.setPassword(password);
        }

        if(phoneNumber.length() == 10 &&
                Integer.parseInt(phoneNumber.substring(0, 1)) <= 9
                && Integer.parseInt(phoneNumber.substring(0, 1))>=6){
            globalPhoneNumberRegistry.remove(user.getPhoneNumber());
            user.setPhoneNumber(phoneNumber);
            globalPhoneNumberRegistry.add(user.getPhoneNumber());
        }
    }
}
