package info.serxan.trackerprojectmanager.models;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.Map;

public class UserModel implements Serializable {

    private String id;
    private String email;
    private String password;
    private String token;

    public UserModel(String email, String password, String token) {
        this.email = email;
        this.password = password;
        this.token = token;
    }

    public UserModel() {

    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
