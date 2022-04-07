package Helper;

import android.net.Uri;
import android.view.Display;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import Config.FirebaseConfig;
import Model.User;

public class FirebaseUser {

    public static String getUserID(){

        FirebaseAuth user = FirebaseConfig.getFirebaseAuth();
        String email = user.getCurrentUser().getEmail();
        String userID = Base64Custom.encode(email);

        return userID;
    }

    public static com.google.firebase.auth.FirebaseUser getCurrentUser(){

        FirebaseAuth user = FirebaseConfig.getFirebaseAuth();
        return user.getCurrentUser();
    }

    public static boolean updateUserName(String name){

        try{

            com.google.firebase.auth.FirebaseUser user = getCurrentUser();
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(!task.isSuccessful()){



                    }

                }
            });
            return true;

        }catch (Exception e){

            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateProfileImage(Uri url){

        try{

            com.google.firebase.auth.FirebaseUser user = getCurrentUser();
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(url)
                    .build();

            user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(!task.isSuccessful()){



                    }

                }
            });
            return true;

        }catch (Exception e){

            e.printStackTrace();
        }
        return false;
    }

    public static User getCurrentUserData(){

        com.google.firebase.auth.FirebaseUser firebaseUser = getCurrentUser();

        User user = new User();
        user.setEmail(firebaseUser.getEmail());
        user.setName(firebaseUser.getDisplayName());

        if(firebaseUser.getPhotoUrl() == null){

            user.setPhoto("");

        }else{

            user.setPhoto(firebaseUser.getPhotoUrl().toString());
        }

        return user;
    }
}
