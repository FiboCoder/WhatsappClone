package Model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;

import Config.FirebaseConfig;

public class Conversation implements Serializable {

    private String senderID, recipientID;
    private String lastMessage;
    private User showedUser;
    private String isGroup;
    private Group group;

    public Conversation() {

        this.setIsGroup("false");
    }

    public void save(){

        DatabaseReference reference = FirebaseConfig.getReference();
        DatabaseReference conversationRef = reference.child("Conversations");
        conversationRef.child(senderID)
                .child(recipientID)
                .setValue(this);

    }

    public String getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(String isGroup) {
        this.isGroup = isGroup;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getRecipientID() {
        return recipientID;
    }

    public void setRecipientID(String recipientID) {
        this.recipientID = recipientID;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public User getShowedUser() {
        return showedUser;
    }

    public void setShowedUser(User showedUser) {
        this.showedUser = showedUser;
    }

}
