package service;

import dao.AdminDAO;
import dao.CustomerDAO;
import dao.DeliveryAgentDAO;
import dao.UserDAO;
import model.enums.DeliveryAgentStatus;
import model.user.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public abstract class BaseService {
    private final UserDAO userDAO = new UserDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final AdminDAO adminDAO = new AdminDAO();
    private final DeliveryAgentDAO deliveryAgentDAO = new DeliveryAgentDAO();

    private final Set<Integer> loggedInUserSet = new HashSet<>();

    public User signUp(String type, String username, String password, String phone) {
        try {
            if (userDAO.isUsernameTaken(username)) {
                System.out.println("Username '" + username + "' is already taken.");
                return null;
            }
            if (userDAO.isPhoneNumberTaken(phone)) {
                System.out.println("Phone number '" + phone + "' is already registered.");
                return null;
            }

            String dbUserType = type.toUpperCase();
            int userId = userDAO.insertUser(username, password, phone, dbUserType);

            User user;
            switch (dbUserType) {
                case "CUSTOMER":
                    customerDAO.insertCustomer(userId, null);
                    user = new Customer(userId, username, password, phone, null);
                    break;
                case "ADMIN":
                    adminDAO.insertAdmin(userId);
                    user = Admin.getInstanceFromDB(userId, username, password, phone);
                    break;
                case "DELIVERY_AGENT":
                    deliveryAgentDAO.insertAgent(userId, "UNAVAILABLE", 0.0, 0.0, 0.0);
                    user = new DeliveryAgent(userId, username, password, phone,
                            DeliveryAgentStatus.UNAVAILABLE, 0.0, 0.0, 0.0);
                    break;
                default:
                    System.out.println("Unknown user type: " + type);
                    return null;
            }

            loggedInUserSet.add(userId);
            System.out.println(type + " signed up successfully. User ID: " + userId);
            return user;

        } catch (SQLException e) {
            System.out.println("Error during signup: " + e.getMessage());
            return null;
        }
    }

    public User login(String username, String password) {
        try {
            try (ResultSet rs = userDAO.validateUser(username, password)) {
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String phone = rs.getString("phone_number");
                    String userType = rs.getString("user_type");

                    if (loggedInUserSet.contains(userId)) {
                        System.out.println("User is already logged in.");
                        return null;
                    }

                    User user;
                    switch (userType) {
                        case "CUSTOMER":
                            String address = customerDAO.getAddress(userId);
                            user = new Customer(userId, username, password, phone, address);
                            break;
                        case "ADMIN":
                            user = Admin.getInstanceFromDB(userId, username, password, phone);
                            break;
                        case "DELIVERY_AGENT":
                            try (ResultSet agentRs = deliveryAgentDAO.getAgentById(userId)) {
                                if (agentRs.next()) {
                                    user = new DeliveryAgent(userId, username, password, phone,
                                            DeliveryAgentStatus.valueOf(agentRs.getString("agent_status")),
                                            agentRs.getDouble("gross_earning"),
                                            agentRs.getDouble("base_salary"),
                                            agentRs.getDouble("commission_rate"));
                                } else {
                                    System.out.println("Delivery agent data missing.");
                                    return null;
                                }
                            }
                            break;
                        default:
                            System.out.println("Unknown user type.");
                            return null;
                    }

                    loggedInUserSet.add(userId);
                    System.out.println("Login successful. Welcome, " + username + "!");
                    return user;
                } else {
                    System.out.println("Invalid credentials.");
                    return null;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
            return null;
        }
    }

    public void logout(User user) {
        if (user == null) {
            System.out.println("No user to logout.");
            return;
        }
        loggedInUserSet.remove(user.getUserId());
        System.out.println(user.getUsername() + " logged out successfully.");
    }

    public void editUserProfile(User user, String newUsername, String newPassword, String newPhone) {
        try {
            if (!newUsername.equals(user.getUsername()) && userDAO.isUsernameTaken(newUsername)) {
                System.out.println("Username '" + newUsername + "' is already taken.");
                return;
            }
            if (!newPhone.equals(user.getPhoneNumber()) && userDAO.isPhoneNumberTaken(newPhone)) {
                System.out.println("Phone number '" + newPhone + "' is already registered.");
                return;
            }
            userDAO.updateUser(user.getUserId(), newUsername, newPassword, newPhone);
            user.setUsername(newUsername);
            user.setPassword(newPassword);
            user.setPhoneNumber(newPhone);
            System.out.println("Profile updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating profile: " + e.getMessage());
        }
    }

    public boolean isLoggedIn(User user) {
        return user != null && loggedInUserSet.contains(user.getUserId());
    }

    public java.util.List<java.util.Map<String, String>> getAllUsersByType(String userType) {
        java.util.List<java.util.Map<String, String>> users = new java.util.ArrayList<>();
        try (ResultSet rs = userDAO.getAllByType(userType)) {
            while (rs.next()) {
                java.util.Map<String, String> user = new java.util.LinkedHashMap<>();
                user.put("user_id", String.valueOf(rs.getInt("user_id")));
                user.put("username", rs.getString("username"));
                user.put("phone_number", rs.getString("phone_number"));
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching users: " + e.getMessage());
        }
        return users;
    }
}
