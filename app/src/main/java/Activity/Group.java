package Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import Adapter.Contacts;
import Adapter.SelectedUserGroup;
import Config.FirebaseConfig;
import Helper.RecyclerItemClickListener;
import Model.User;

public class Group extends AppCompatActivity {

    private RecyclerView rvSelectedMembers, rvMembers;
    private Contacts membersAdapter;
    private SelectedUserGroup selectedMembersAdapter;
    private List<User> membersList = new ArrayList<>();
    private List<User> selectedMembersList = new ArrayList<>();
    private ValueEventListener valueEventListenerMembers;
    private ValueEventListener valueEventListenerSelectedMembers;
    private DatabaseReference usersRef;
    private FirebaseUser currentUser;
    private Toolbar toolbar;
    private FloatingActionButton fabGroupRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        toolbar = findViewById(R.id.tbMain);
        toolbar.setTitle("Novo Grupo");


        rvSelectedMembers = findViewById(R.id.rvSelectedMembers);
        rvMembers = findViewById(R.id.rvMembers);
        fabGroupRegister = findViewById(R.id.fabGroupRegister);
        currentUser = Helper.FirebaseUser.getCurrentUser();

        usersRef = FirebaseConfig.getReference().child("Users");

        membersAdapter = new Contacts(membersList, getApplicationContext());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvMembers.setLayoutManager(layoutManager);
        rvMembers.setHasFixedSize(true);
        rvMembers.setAdapter(membersAdapter);

        rvMembers.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        rvMembers, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        User selectedUser = membersList.get(position);

                        membersList.remove(selectedUser);
                        membersAdapter.notifyDataSetChanged();
                        selectedMembersList.add(selectedUser);
                        selectedMembersAdapter.notifyDataSetChanged();
                        updateMembersToolbar();

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
            )
        );

        selectedMembersAdapter = new SelectedUserGroup(selectedMembersList, getApplicationContext());

        RecyclerView.LayoutManager layoutManagerHorizontal = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false);
        rvSelectedMembers.setLayoutManager(layoutManagerHorizontal);
        rvSelectedMembers.setHasFixedSize(true);
        rvSelectedMembers.setAdapter(selectedMembersAdapter);

        rvSelectedMembers.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        rvSelectedMembers, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        User selectedUser = selectedMembersList.get(position);

                        selectedMembersList.remove(selectedUser);
                        selectedMembersAdapter.notifyDataSetChanged();
                        membersList.add(selectedUser);
                        membersAdapter.notifyDataSetChanged();
                        updateMembersToolbar();

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
                )
        );

        fabGroupRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Group.this, GroupRegister.class);
                intent.putExtra("members", (Serializable) selectedMembersList);
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        recoverContacts();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usersRef.removeEventListener(valueEventListenerMembers);
    }

    public void recoverContacts(){

        valueEventListenerMembers = usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot data : snapshot.getChildren()){

                    User user = data.getValue(User.class);

                    String currentUserEmail = currentUser.getEmail();
                    if(!currentUserEmail.equals(user.getEmail())){

                        membersList.add(user);
                    }

                }

                membersAdapter.notifyDataSetChanged();
                updateMembersToolbar();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateMembersToolbar(){

        int totalSelected = selectedMembersList.size();
        int total = membersList.size() + totalSelected;
        toolbar.setSubtitle(totalSelected + " de " + total + " selecionados.");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}