package vista;

/**
 * Created by Tautvydas on 23/04/2016.
 */
public class UserBD {
    private String user;
    private String pass;

    public UserBD() {
    }

    public UserBD(String user, String pass) {
        this.user = user;
        this.pass = pass;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
