package login;

import java.io.Serializable;

/**
 * Created by Tautvydas on 22/04/2016.
 */
public class Token implements Serializable{
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
