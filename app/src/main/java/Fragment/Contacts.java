package Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.whatsapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Activity.Chat;
import Activity.Group;
import Config.FirebaseConfig;
import Helper.FirebaseUser;
import Helper.RecyclerItemClickListener;
import Model.Conversation;
import Model.User;

public class Contacts extends Fragment {

    private RecyclerView rvContactsList;
    private Adapter.Contacts adapter;
    private ArrayList<User> contactsList = new ArrayList<>();

    private DatabaseReference usersRef;

    private ValueEventListener valueEventListenerContacts;

    private com.google.firebase.auth.FirebaseUser currentUser;

    public Contacts() {


    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        rvContactsList = view.findViewById(R.id.rvContactsList);
        usersRef = FirebaseConfig.getReference().child("Users");
        currentUser = FirebaseUser.getCurrentUser();

        //Adapter config
        adapter = new Adapter.Contacts(contactsList, getActivity());

        //Recyclerview config
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvContactsList.setLayoutManager(layoutManager);
        rvContactsList.setHasFixedSize(true);
        rvContactsList.setAdapter(adapter);

        rvContactsList.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        rvContactsList,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                List<User> updatedUsersList = adapter.usersList();

                                User selectedUser = updatedUsersList.get(position);

                                boolean header = selectedUser.getEmail().isEmpty();

                                if(header){

                                    Intent intent = new Intent(getActivity(), Group.class);
                                    startActivity(intent);

                                }else {

                                    Intent intent = new Intent(getActivity(), Chat.class);
                                    intent.putExtra("contact", selectedUser);
                                    startActivity(intent);

                                }
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                ));

        return view;
    }



    @Override
    public void onStart() {
        super.onStart();
        recoverContacts();
    }

    @Override
    public void onStop() {
        super.onStop();
        usersRef.removeEventListener(valueEventListenerContacts);
    }

    public void recoverContacts(){

        valueEventListenerContacts = usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                clearContactsList();

                for(DataSnapshot data : snapshot.getChildren()){

                    User user = data.getValue(User.class);

                    String currentUserEmail = currentUser.getEmail();
                    if(!currentUserEmail.equals(user.getEmail())){

                        contactsList.add(user);
                    }

                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void clearContactsList(){

        contactsList.clear();
        addMenuNewGroup();

    }

    public void addMenuNewGroup(){

        User itemGroup = new User();
        itemGroup.setName("Novo grupo");
        itemGroup.setEmail("");

        contactsList.add(itemGroup);
    }

    public void searchContacts(String text){

        List<User> usersListSearch = new ArrayList<>();

        for(User user : contactsList){

            String name = user.getName().toLowerCase();
            if(name.contains(text)){

                usersListSearch.add(user);

            }

        }

        adapter = new Adapter.Contacts(usersListSearch, getActivity());
        rvContactsList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    public void reloadContacts(){

        adapter = new Adapter.Contacts(contactsList, getActivity());
        rvContactsList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}