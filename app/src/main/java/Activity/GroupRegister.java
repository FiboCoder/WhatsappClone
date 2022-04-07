package Activity;

import static Helper.FirebaseUser.updateProfileImage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import Adapter.SelectedUserGroup;
import Config.FirebaseConfig;
import Helper.FirebaseUser;
import Model.Group;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class GroupRegister extends AppCompatActivity {

    private List<User> selectedMembersList = new ArrayList<>();
    private AppCompatEditText etNameGroup;
    private AppCompatTextView tvParticipants;
    private CircleImageView civGroup;

    private SelectedUserGroup selectedMembersAdapter;
    RecyclerView rvGroupMembers;

    private StorageReference storageReference;

    private static final int GALLERY_SELECTION = 200;

    private Group group;

    private FloatingActionButton fabGroupRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_register);

        Toolbar toolbar = findViewById(R.id.tbMain);
        toolbar.setTitle("Novo grupo");
        toolbar.setSubtitle("Defina o nome");
        setSupportActionBar(toolbar);

        etNameGroup = findViewById(R.id.etNameGroupRegister);
        tvParticipants = findViewById(R.id.tvParticipantsGroupRegister);
        civGroup = findViewById(R.id.civGroupRegister);
        rvGroupMembers = findViewById(R.id.rvGroupMembers);

        fabGroupRegister = findViewById(R.id.fabGroupRegisterCheck);

        storageReference = FirebaseConfig.getFirebaseStorage();
        group = new Group();

        if(getIntent().getExtras() != null){

            List<User> members = (List<User>) getIntent().getExtras().getSerializable("members");
            selectedMembersList.addAll(members);

            tvParticipants.setText("Participantes: " + selectedMembersList.size());
        }

        selectedMembersAdapter = new SelectedUserGroup(selectedMembersList, getApplicationContext());

        RecyclerView.LayoutManager layoutManagerHorizontal = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false);
        rvGroupMembers.setLayoutManager(layoutManagerHorizontal);
        rvGroupMembers.setHasFixedSize(true);
        rvGroupMembers.setAdapter(selectedMembersAdapter);

        civGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if(intent.resolveActivity(getPackageManager()) != null){

                    startActivityForResult(intent, GALLERY_SELECTION);

                }

            }
        });

        fabGroupRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String groupName = etNameGroup.getText().toString();

                selectedMembersList.add(FirebaseUser.getCurrentUserData());

                group.setMembers(selectedMembersList);
                group.setName(groupName);
                group.save();

                Intent intent = new Intent(GroupRegister.this, Chat.class);
                intent.putExtra("group", group);
                startActivity(intent);
                finish();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            Bitmap image = null;

            try{

                Uri localImage = data.getData();
                image = MediaStore.Images.Media.getBitmap(getContentResolver(), localImage);

            }catch (Exception e){

                e.printStackTrace();
            }

            if(image != null){

                civGroup.setImageBitmap(image);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] imageData = baos.toByteArray();

                final StorageReference imageRef = storageReference
                        .child("Images")
                        .child("Groups")
                        .child(group.getId() + ".jpeg");

                UploadTask uploadTask = imageRef.putBytes(imageData);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(GroupRegister.this, "Erro ao fazer upload da imagem.", Toast.LENGTH_SHORT).show();

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(GroupRegister.this, "Sucesso ao fazer upload da imagem.", Toast.LENGTH_SHORT).show();

                        imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                String url = task.getResult().toString();
                                group.setImage(url);
                            }
                        });

                    }
                });
            }
        }
    }
}