package Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;

import java.util.List;

import Helper.FirebaseUser;
import Model.Message;

public class Messages extends RecyclerView.Adapter<Messages.MyViewHolder> {

    private List<Message> messages;
    private Context context;
    private static final int SENDER_TYPE = 0;
    private static final int RECIPIENT_TYPE = 1;

    public Messages(List<Message> messageList, Context c) {

        this.messages = messageList;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item = null;
        if(viewType == SENDER_TYPE){

            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_sender, parent,false);

        }else if(viewType == RECIPIENT_TYPE){

            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_recipient, parent,false);

        }

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Message message = messages.get(position);
        String msg = message.getMessage();
        String image = message.getImage();
        String name = message.getName();

        if(image != null){

            Uri url = Uri.parse(image);
            Glide.with(context).load(url).into(holder.ivMessage);

            holder.tvMessage.setVisibility(View.GONE);

        }else{

            holder.tvMessage.setText(msg);

            holder.ivMessage.setVisibility(View.GONE);

        }
        if(name != null && !name.isEmpty()){

            holder.tvName.setText(message.getName());

        }else{

            holder.tvName.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {

        Message message = messages.get(position);
        String userID = FirebaseUser.getUserID();
        if(userID.equals(message.getUserID())){

            return SENDER_TYPE;

        }
        return RECIPIENT_TYPE;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView tvMessage, tvName;
        private AppCompatImageView ivMessage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvName = itemView.findViewById(R.id.tvNameShowedUserName);
            ivMessage = itemView.findViewById(R.id.ivMessage);
        }
    }
}
