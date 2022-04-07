package Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import Adapter.Messages;
import Config.FirebaseConfig;
import Helper.Base64Custom;
import Helper.FirebaseUser;
import Helper.Permission;
import Model.Conversation;
import Model.Group;
import Model.Message;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends AppCompatActivity {

    private String[] permissions = new String[]{

            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private CircleImageView civProfile;
    private AppCompatTextView tvName;
    private EditText etMessage;
    private AppCompatImageView ivPhoto;

    private RecyclerView rvChat;
    private Messages adapter;
    private List<Message> messagesList = new ArrayList<>();

    private static final int CAMERA_SELECTION = 100;

    private User recipientUser;
    private Group group;
    private DatabaseReference reference;
    private StorageReference storage;
    private DatabaseReference messagesRef;
    private ChildEventListener childEventListenerMessages;
    private User senderUser;

    private String senderUserID;
    private String recipientUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.tbChat);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Permission.validatePermissions(permissions, this, 1);

        civProfile = findViewById(R.id.civChat);
        tvName = findViewById(R.id.tvNameChat);
        etMessage = findViewById(R.id.etMessageChat);
        ivPhoto = findViewById(R.id.ivPhotoChat);

        rvChat = findViewById(R.id.rvChat);

        senderUserID = FirebaseUser.getUserID();
        senderUser = FirebaseUser.getCurrentUserData();

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            if(bundle.containsKey("group")){

                group = (Model.Group) bundle.getSerializable("group");
                recipientUserID = group.getId();
                tvName.setText(group.getName());

                String image = group.getImage();
                if(image != null){

                    Uri url = Uri.parse(image);
                    Glide.with(Chat.this)
                            .load(url)
                            .into(civProfile);

                }else{

                    civProfile.setImageResource(R.drawable.padrao);
                }

            }else{

                recipientUser = (User) bundle.getSerializable("contact");

                tvName.setText(recipientUser.getName());

                String photo = recipientUser.getPhoto();
                if(photo != null){

                    Uri url = Uri.parse(recipientUser.getPhoto());
                    Glide.with(Chat.this)
                            .load(url)
                            .into(civProfile);

                }else{

                    civProfile.setImageResource(R.drawable.padrao);
                }

                recipientUserID = Base64Custom.encode(recipientUser.getEmail());

            }
        }


        adapter = new Messages(messagesList, getApplicationContext());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Chat.this);
        rvChat.setLayoutManager(layoutManager);
        rvChat.setHasFixedSize(true);
        rvChat.setAdapter(adapter);

        reference = FirebaseConfig.getReference();
        storage = FirebaseConfig.getFirebaseStorage();
        messagesRef = reference.child("Messages")
                .child(senderUserID)
                .child(recipientUserID);

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(getPackageManager()) != null){

                    startActivityForResult(intent, CAMERA_SELECTION);
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

                switch (requestCode) {

                    case CAMERA_SELECTION:
                        image = (Bitmap) data.getExtras().get("data");
                        break;
                }

                if(image != null){

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] imageData = baos.toByteArray();

                    //Image name generator
                    String imageName = UUID.randomUUID().toString();

                    //Reference Configuration
                    final StorageReference imageRef = storage.child("Images")
                            .child("Photo_Messages")
                            .child(senderUserID)
                            .child(imageName);


                    UploadTask uploadTask = imageRef.putBytes(imageData);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(Chat.this, "Erro ao fazer o upload da imagem!", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    String url = task.getResult().toString();

                                    if(recipientUser != null){

                                        Message message = new Message();
                                        message.setUserID(senderUserID);
                                        message.setMessage("image.jpeg");
                                        message.setName("");
                                        message.setImage(url);
                                        saveMessage(senderUserID, recipientUserID, message);
                                        saveMessage(recipientUserID, senderUserID, message);

                                        etMessage.setText("");

                                    }else{

                                        for(User member : group.getMembers()){

                                            String senderGroupId = Base64Custom.encode(member.getEmail());
                                            String loggedUserIdGroup = FirebaseUser.getUserID();

                                            Message message = new Message();
                                            message.setUserID(loggedUserIdGroup);
                                            message.setMessage("image.jpeg");
                                            message.setName(senderUser.getName());
                                            message.setImage(url);
                                            saveMessage(senderGroupId, recipientUserID, message);
                                            setDataToSaveConversation(senderGroupId, recipientUserID, recipientUser, message, true);

                                            etMessage.setText("");
                                        }

                                    }

                                    Toast.makeText(Chat.this, "Sucesso ao enviar a foto!", Toast.LENGTH_SHORT).show();

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

    public void sendMessage(View view){
        
        String message = etMessage.getText().toString();
        
        if(!message.isEmpty()){

            if(recipientUser != null){

                Message msg = new Message();
                msg.setUserID(senderUserID);
                msg.setMessage(message);
                msg.setName("");
                saveMessage(senderUserID, recipientUserID, msg);
                saveMessage(recipientUserID, senderUserID, msg);
                setDataToSaveConversation(senderUserID, recipientUserID, recipientUser, msg, false);
                setDataToSaveConversation(recipientUserID, senderUserID, senderUser,  msg, false);

                etMessage.setText("");

            }else{

                for(User member : group.getMembers()){

                    String senderGroupId = Base64Custom.encode(member.getEmail());
                    String loggedUserIdGroup = FirebaseUser.getUserID();

                    Message msg = new Message();
                    msg.setUserID(loggedUserIdGroup);
                    msg.setMessage(message);
                    msg.setName(senderUser.getName());
                    saveMessage(senderGroupId, recipientUserID, msg);
                    setDataToSaveConversation(senderGroupId, recipientUserID, recipientUser, msg, true);

                    etMessage.setText("");
                }

            }

        }else{

            Toast.makeText(Chat.this, "Digite uma menssagem para enviar!", Toast.LENGTH_LONG).show();
        }
    }

    private void saveMessage(String senderID, String recipientID, Message message){

        DatabaseReference reference = FirebaseConfig.getReference();
        DatabaseReference messagesRef = reference.child("Messages");

        messagesRef.child(senderID)
                .child(recipientID)
                .push()
                .setValue(message);

    }

    private void setDataToSaveConversation(String userID1, String userID2, User showedUser, Message msg, boolean isGroup){

        final Conversation conversation = new Conversation();

        if(isGroup){

            conversation.setSenderID(userID1);
            conversation.setRecipientID(userID2);
            conversation.setLastMessage(msg.getMessage());
            conversation.setGroup(group);
            conversation.setIsGroup("true");
            conversation.save();

        }else{

            conversation.setSenderID(userID1);
            conversation.setRecipientID(userID2);
            conversation.setLastMessage(msg.getMessage());
            conversation.setShowedUser(showedUser);
            conversation.setIsGroup("false");
            conversation.save();


        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        recoverMessages();
    }

    @Override
    protected void onStop() {
        super.onStop();

        messagesRef.removeEventListener(childEventListenerMessages);
    }

    private void recoverMessages(){

        messagesList.clear();

        childEventListenerMessages = messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Message message = snapshot.getValue(Message.class);
                messagesList.add(message);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
}