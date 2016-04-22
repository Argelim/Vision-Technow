package login;

import java.io.Serializable;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.cookie.Cookie;

/**
 * Created by Tautvydas on 21/04/2016.
 */
public class Usuario implements Serializable {

    private  String username;
    private String email;
    private  String password;
    private String token;
    private List<Cookie> cookieList;
    private Token tokenKey;


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

    public  String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Token getTokenKey() {
        return tokenKey;
    }

    public void setTokenKey(Token tokenKey) {
        this.tokenKey = tokenKey;
    }

    public List<Cookie> getCookieList() {
        return cookieList;
    }

    public void setCookieList(List<Cookie> cookieList) {
        this.cookieList = cookieList;
    }
}
