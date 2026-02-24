package service;

import model.BaseUser;
import model.User;
import model.UserFactory;

import javax.print.DocFlavor;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class BaseService {
    private final ConcurrentHashMap<String, User> allUserMap = new ConcurrentHashMap<>();
    private final Set<User> loggedInUserSet = new HashSet<>();


    public User signUp(String type, String username, String password){
        if(allUserMap.containsKey(username)) {
            System.out.println("User already exists. Please login.");
            return null;
        }
//        once an admin user is created, creating it again doesnt show any msg, but it will return old object bcz admin is singleton
        User user = UserFactory.createUser(type.toUpperCase(), username, password);
        allUserMap.put(username, user);
        return user;
    };

//    sends null
    public User login(Integer userId, String username, String password){
        User user = allUserMap.get(username);
        if(user == null){
            System.out.println("No such user. Please sign up first.");
            return null;
        }
        if(loggedInUserSet.contains(user)){
            System.out.println("User already logged in.");
            return user;
        }
        if(user.getUsername().equals(username) && user.getPassword().equals(password)){
            loggedInUserSet.add(user);
            return user;
        }
        System.out.println("Login failed.");
        return null;
    }
    public void logout(String username){
        User user = findLoggedInUser(username);
        if(user == null){
            System.out.println("User not logged in.");
            return;
        }
//        user may not exist in all User map
        loggedInUserSet.remove(user);
    }

    public void printProfile(String username){
        User user = allUserMap.get(username);
        if(user == null){
            System.out.println("No such user.");
            return;
        }
        System.out.println("----------------------------------------------------------------");
        System.out.printf("|%-15s %-45s |%n", " User ID: ", user.getUserId());
        System.out.printf("|%-15s %-45s |%n", " Username: ", user.getUsername());
        System.out.printf("|%-15s %-45s |%n", " User type: ", user.getClass().getSimpleName());
        System.out.println("----------------------------------------------------------------");
    }
    public User findLoggedInUser(String username){
        User user = allUserMap.get(username);
        if(user == null)
            return null;
        return loggedInUserSet.stream()
                .filter(u ->
                        u.getUserId().equals(user.getUserId()))
                .findFirst()
                .orElse(null);
    }
}
