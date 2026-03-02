package model.user;

import java.util.concurrent.Semaphore;

public class Admin extends BaseUser {

    private static volatile Admin instance;

    private Admin(String adminName, String password, String phoneNumber) {
        super(adminName, password, phoneNumber);
    }

    public static Admin getInstance(String adminName, String password, String phoneNumber) {
        if (instance == null) {
            synchronized (Admin.class) {
                if (instance == null) {
                    instance = new Admin(adminName, password,phoneNumber);
                }
            }
        }
        return instance;
    }
}
