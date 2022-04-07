package Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import Config.FirebaseConfig;
import Helper.FirebaseUser;
import Helper.Permission;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class Configurations extends AppCompatActivity {

    private String[] permissions = new String[]{

            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private AppCompatImageButton ibCamera, ibGallery;

    private static final int CAMERA_SELECTION = 100;
    private static final int GALLERY_SELECTION = 200;

    private CircleImageView civProfile;

    private AppCompatEditText etName;
    private AppCompatImageView ivUpdateName;

    private StorageReference storageReference;

    private String userID;
    private User loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configurations);

        storageReference = FirebaseConfig.getFirebaseStorage();
        userID = FirebaseUser.getUserID();
        loggedUser = FirebaseUser.getCurrentUserData();

        Permission.validatePermissions(permissions, this, 1);

        ibCamera = findViewById(R.id.ibCamera);
        ibGallery = findViewById(R.id.ibGallery);

        civProfile = findViewById(R.id.civProfile);
        etName = findViewById(R.id.etNameC);
        ivUpdateName = findViewById(R.id.ivUpdateName);

        Toolbar toolbar = findViewById(R.id.tbMain);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        com.google.firebase.auth.FirebaseUser user = FirebaseUser.getCurrentUser();
        Uri url = user.getPhotoUrl();

        if(url != null){

            Glide.with(Configurations.this)
                    .load(url)
                    .into(civProfile);

        }else{

            civProfile.setImageResource(R.drawable.padrao);

        }

        etName.setText(user.getDisplayName());

        ivUpdateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = etName.getText().toString();
                boolean _return = FirebaseUser.updateUserName(name);

                if(_return){

                    loggedUser.setName(name);
                    loggedUser.update();

                    Toast.makeText(Configurations.this, "Nome alterado com sucesso.", Toast.LENGTH_SHORT).show();

                }
            }
        });

        ibCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if(intent.resolveActivity(getPackageManager()) != null){

                    startActivityForResult(intent, CAMERA_SELECTION);
                }
            }
        });

        ibGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if(intent.resolveActivity(getPackageManager()) != null){

                    startActivityForResult(intent, GALLERY_SELECTION);

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            Bitmap image = null;

            try{

                switch (requestCode){

                    case CAMERA_SELECTION:
                        image = (Bitmap) data.getExtras().get("data");
                        break;

                    case GALLERY_SELECTION:
                        Uri localImage = data.getData();
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), localImage);
                        break;
                }

                if(image != null){

                    civProfile.setImageBitmap(image);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] imageData = baos.toByteArray();

                    final StorageReference imageRef = storageReference
                            .child("Images")
                            .child("Profile")
                            .child(userID + ".jpeg");

                    UploadTask uploadTask = imageRef.putBytes(imageData);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(Configurations.this, "Erro ao fazer upload da imagem.", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(Configurations.this, "Sucesso ao fazer upload da imagem.", Toast.LENGTH_SHORT).show();

                            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    Uri url = task.getResult();
                                    updateProfileImage(url);

                                }
                            });

                        }
                    });
                }

            }catch (Exception e){

                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int resultPermission : grantResults){

            if(resultPermission == PackageManager.PERMISSION_DENIED){

                alertNeededPermissions();

            }

        }

    }

    private void alertNeededPermissions(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para utilizar o WhatsApp é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void updateProfileImage(Uri url){

        boolean _return = FirebaseUser.updateProfileImage(url);
        if(_return){

            loggedUser.setPhoto(url.toString());
            loggedUser.update();

            Toast.makeText(Configurations.this, "Sua foto de perfil foi alterada!", Toast.LENGTH_SHORT).show();

        }


    }
}