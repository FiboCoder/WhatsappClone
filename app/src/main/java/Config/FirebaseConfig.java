package Config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseConfig {

    private static DatabaseReference databaseReference;
    private static FirebaseAuth firebaseAuth;
    private static StorageReference firebaseStorage;

    public static DatabaseReference getReference(){

        if(databaseReference == null){

            databaseReference = FirebaseDatabase.getInstance().getReference();

        }
        return databaseReference;
    }

    public static FirebaseAuth getFirebaseAuth(){

        if(firebaseAuth == null){

            firebaseAuth = FirebaseAuth.getInstance();
        }

        return firebaseAuth;
    }

    public static StorageReference getFirebaseStorage(){

        if(firebaseStorage == null){

            firebaseStorage = FirebaseStorage.getInstance().getReference();
        }

        return firebaseStorage;
    }
}
