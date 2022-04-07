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
import Config.FirebaseConfig;
import Helper.FirebaseUser;
import Helper.RecyclerItemClickListener;
import Model.Conversation;
import Model.User;

public class Conversations extends Fragment {

    private RecyclerView rvConversationsList;
    private Adapter.Conversations adapter;
    private ArrayList<Conversation> conversationsList = new ArrayList<>();

    private DatabaseReference conversationsRef;

    private ValueEventListener valueEventListenerConversations;

    private com.google.firebase.auth.FirebaseUser currentUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversations, container, false);

        rvConversationsList = view.findViewById(R.id.rvConversations);

        currentUser = FirebaseUser.getCurrentUser();

        //Adapter Conversations Config
        adapter = new Adapter.Conversations(conversationsList, getActivity());


        //Recycler View Conversations Config
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvConversationsList.setLayoutManager(layoutManager);
        rvConversationsList.setHasFixedSize(true);
        rvConversationsList.setAdapter(adapter);

        rvConversationsList.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        rvConversationsList,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                List<Conversation> updatedConversationList = adapter.conversationsList();
                                Conversation selectedConversation = updatedConversationList.get(position);

                                if(selectedConversation.getIsGroup().equals("true")){

                                    Intent intent = new Intent(getActivity(), Chat.class);
                                    intent.putExtra("group", selectedConversation.getGroup());
                                    startActivity(intent);

                                }else{

                                    Intent intent = new Intent(getActivity(), Chat.class);
                                    intent.putExtra("contact", selectedConversation.getShowedUser());
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
        recoverConversation();
        reloadConversations();
    }

    @Override
    public void onStop() {
        super.onStop();
        conversationsRef.removeEventListener(valueEventListenerConversations);
    }

    public void recoverConversation(){
        conversationsList.clear();

        String userID = FirebaseUser.getUserID();
        conversationsRef = FirebaseConfig.getReference().child("Conversations").child(userID);
        valueEventListenerConversations = conversationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot data : snapshot.getChildren()){

                    Conversation conversation = data.getValue(Conversation.class);

                    conversationsList.add(conversation);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void searchConversations(String text){

        List<Conversation> conversationListSearch = new ArrayList<>();

        for(Conversation conversation : conversationsList){

            if(conversation.getShowedUser() != null){

                String name = conversation.getShowedUser().getName().toLowerCase();
                String lastMessage = conversation.getLastMessage().toLowerCase();
                if(name.contains(text) || lastMessage.contains(text)){

                    conversationListSearch.add(conversation);

                }

            }else{

                String name = conversation.getGroup().getName().toLowerCase();
                String lastMessage = conversation.getLastMessage().toLowerCase();
                if(name.contains(text) || lastMessage.contains(text)){

                    conversationListSearch.add(conversation);

                }

            }


        }

        adapter = new Adapter.Conversations(conversationListSearch, getActivity());
        rvConversationsList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    public void reloadConversations(){

        adapter = new Adapter.Conversations(conversationsList, getActivity());
        rvConversationsList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}