package login;

import java.io.Serializable;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Tautvydas on 21/04/2016.
 */
public class Usuario implements Serializable {

    private  String username;
    private String email;
    private  String password;
    private  Header header [];



    public Usuario(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public  Header [] getHeader() {
        return header;
    }

    public void setHeader(Header[] header) {
        this.header = header;
    }
}
