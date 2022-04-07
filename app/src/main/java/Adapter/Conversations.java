package Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import java.util.List;
import Model.Conversation;
import Model.Group;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class Conversations extends RecyclerView.Adapter<Conversations.MyViewHolder> {

    private List<Conversation> conversations;
    private Context context;

    public Conversations(List<Conversation> conversationList, Context c) {

        this.conversations = conversationList;
        this.context = c;
    }

    public List<Conversation> conversationsList(){

        return this.conversations;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View listItem = LayoutInflater.from(context).inflate(R.layout.conversations, parent, false);
        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Conversation conversation = conversations.get(position);

        if(conversation.getIsGroup().equals("true")){

            Group group = conversation.getGroup();
            holder.tvName.setText(group.getName());

            if(group.getImage() != null){

                Uri url = Uri.parse(group.getImage());
                Glide.with(context).load(url).into(holder.civProfile);

            }else{

                holder.civProfile.setImageResource(R.drawable.padrao);
            }
            holder.tvLastMessage.setText(conversation.getLastMessage());

        }else{

            User user = conversation.getShowedUser();

            if(user != null){

                holder.tvName.setText(user.getName());
                if(user.getPhoto() != null){

                    Uri url = Uri.parse(user.getPhoto());
                    Glide.with(context).load(url).into(holder.civProfile);

                }else{

                    holder.civProfile.setImageResource(R.drawable.padrao);
                }

                holder.tvLastMessage.setText(conversation.getLastMessage());

            }
        }
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView civProfile;
        private TextView tvName, tvLastMessage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            civProfile = itemView.findViewById(R.id.civProfileConversation);
            tvName = itemView.findViewById(R.id.tvNameConversation);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessageConversation);
        }
    }
}
