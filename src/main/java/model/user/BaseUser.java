package model.user;

import util.IdGenerator;

import java.util.Objects;

public abstract class BaseUser implements User {
    private static final IdGenerator idGenerator = new IdGenerator();
    private final Integer userId;
    private String username;
    private String password;
    private String phoneNumber;
    public BaseUser(){
        this.userId = idGenerator.generateId();
    }
    public BaseUser(String customerName, String password, String phoneNumber){
        this();
        this.username = customerName;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }
    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public Integer getUserId() {
        return userId;
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    //
    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null || getClass() != obj.getClass())
            return false;
        User user = (User) obj;
        return user.getUserId().equals(this.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
