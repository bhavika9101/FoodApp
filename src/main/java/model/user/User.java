package model.user;

public interface User {
    Integer getUserId();
    String getUsername();
    String getPassword();
    String getPhoneNumber();

    void setUsername(String username);
    void setPassword(String password);
    void setPhoneNumber(String phoneNumber);
}
