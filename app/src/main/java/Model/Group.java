package Model;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

import Config.FirebaseConfig;
import Helper.Base64Custom;

public class Group implements Serializable {

    private String id;
    private String name;
    private String image;
    private List<User> members;

    public Group() {

        DatabaseReference reference = FirebaseConfig.getReference();
        DatabaseReference groupRef = reference.child("Groups");

        String groupID = groupRef.push().getKey();
        setId(groupID);
    }

    public void save(){

        DatabaseReference reference = FirebaseConfig.getReference();
        DatabaseReference groupRef = reference.child("Groups");

        groupRef.child(getId())
                .setValue(this);

        for(User member : getMembers()){

            String senderID = Base64Custom.encode(member.getEmail());
            String recipientID = getId();

            Conversation conversation = new Conversation();
            conversation.setSenderID(senderID);
            conversation.setRecipientID(recipientID);
            conversation.setLastMessage("");
            conversation.setIsGroup("true");
            conversation.setGroup(this);

            DatabaseReference conversationRef = reference.child("Conversations");
            conversationRef.child(senderID)
                    .child(recipientID)
                    .setValue(conversation);

        }

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }
}
