package netPacket;

import java.io.Serializable;

public class User implements Serializable{
    private String userName;
    private String password;

    private boolean userNew = false;
    private static final long serialVersionUID = 1L;

    public boolean isUserNew() {
        return userNew;
    }

    public void setUserNew(boolean userNew) {
        this.userNew = userNew;
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
