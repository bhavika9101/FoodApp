package model.user;

public class Admin extends BaseUser {

    private static volatile Admin instance;

    private Admin(String adminName, String password, String phoneNumber) {
        super(adminName, password, phoneNumber);
    }

    private Admin(int userId, String username, String password, String phoneNumber) {
        super(userId, username, password, phoneNumber);
    }

    public static Admin getInstance(String adminName, String password, String phoneNumber) {
        if (instance == null) {
            synchronized (Admin.class) {
                if (instance == null) {
                    instance = new Admin(adminName, password, phoneNumber);
                }
            }
        }
        return instance;
    }

    public static Admin getInstanceFromDB(int userId, String username, String password, String phoneNumber) {
        if (instance == null) {
            synchronized (Admin.class) {
                if (instance == null) {
                    instance = new Admin(userId, username, password, phoneNumber);
                }
            }
        }
        return instance;
    }
}
