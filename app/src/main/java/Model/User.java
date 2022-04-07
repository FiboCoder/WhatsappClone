package Model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import Config.FirebaseConfig;
import Helper.FirebaseUser;

public class User implements Serializable {

    private String userID;
    private String name;
    private String email;
    private String pass;
    private String photo;

    public User() {
    }

    public void save(){

        DatabaseReference reference = FirebaseConfig.getReference();
        DatabaseReference user = reference.child("Users").child(getUserID());
        user.setValue(this);
    }

    public void update(){

        String userID = FirebaseUser.getUserID();
        DatabaseReference firebaseDatabase = FirebaseConfig.getReference();
        DatabaseReference userRef = firebaseDatabase.child("Users")
                .child(userID);

        Map<String, Object> userData = convertToMap();
        userRef.updateChildren(userData);

    }

    @Exclude
    public Map<String, Object> convertToMap(){

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("email", getEmail());
        userMap.put("name", getName());
        userMap.put("photo", getPhoto());

        return userMap;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Exclude
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
