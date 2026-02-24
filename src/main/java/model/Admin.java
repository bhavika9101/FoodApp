package model;

public class Admin extends BaseUser {

    private static volatile Admin instance;

    private Admin(String adminName, String password) {
        super(adminName, password);
    }

    public static Admin getInstance(String adminName, String password) {
        if (instance == null) {
            synchronized (Admin.class) {
                if (instance == null) {
                    instance = new Admin(adminName, password);
                }
            }
        }
        return instance;
    }
}
