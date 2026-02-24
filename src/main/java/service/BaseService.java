package service;

import model.BaseUser;
import model.User;
import model.UserFactory;

import javax.print.DocFlavor;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class BaseService {
    private User user;
    CopyOnWriteArraySet<User> allUserSet = new CopyOnWriteArraySet<>();
    CopyOnWriteArraySet<User> loggedInUserSet = new CopyOnWriteArraySet<>();

    public User signUp(String type, String username, String password){
        user = UserFactory.createUser(type.toUpperCase(), username, password);
        allUserSet.add(user);
        return user;
    };

//    sends null
    public User login(Integer userId, String username, String password){
        User user = findAllUser(userId);
        if(user == null){
            return null;
        }
        if(user.getUsername().equals(username) && user.getPassword().equals(password)){
            loggedInUserSet.add(user);
            return user;
        }
        System.out.println("Login failed.");
        return null;
    }
    public void logout(Integer userId){
        User user = findLoggedInUser(userId);
        if(user != null)
            loggedInUserSet.remove(user);
    }

    public void printProfile(Integer userId){
        User user = findAllUser(userId);
        System.out.println("----------------------------------------------------------------");
        System.out.printf("|%-15s %-45s |%n", " User ID: ", user.getUserId());
        System.out.printf("|%-15s %-45s |%n", " Username: ", user.getUsername());
        System.out.printf("|%-15s %-45s |%n", " User type: ", user.getClass().getSimpleName());
        System.out.println("----------------------------------------------------------------");
    }
    public User findAllUser(Integer userId){
        return allUserSet.stream()
                .filter(u ->
                        u.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }
    public User findLoggedInUser(Integer userId){
        return loggedInUserSet.stream()
                .filter(u ->
                        u.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }
}
