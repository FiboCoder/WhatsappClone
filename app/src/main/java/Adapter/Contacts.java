package Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;

import java.util.List;

import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class Contacts extends RecyclerView.Adapter<Contacts.MyViewHolder> {

    private List<User> contacts;
    private Context context;

    public Contacts(List<User> contactsList, Context c) {

        this.contacts = contactsList;
        this.context = c;
    }

    public List<User> usersList(){

        return this.contacts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts, parent, false);
        return new Contacts.MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        User user = contacts.get(position);

        boolean header = user.getEmail().isEmpty();

        holder.tvName.setText(user.getName());
        holder.tvEmail.setText(user.getEmail());
        if(user.getPhoto() != null){

            Uri uri = Uri.parse(user.getPhoto());
            Glide.with(context).load(uri).into(holder.civProfile);
        }else{

            if(header){

                holder.civProfile.setImageResource(R.drawable.icone_grupo);
                holder.tvEmail.setVisibility(View.GONE);

            }else{

                holder.civProfile.setImageResource(R.drawable.padrao);

            }
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView civProfile;
        private AppCompatTextView tvName, tvEmail;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            civProfile = itemView.findViewById(R.id.civProfileContact);
            tvName = itemView.findViewById(R.id.tvNameContact);
            tvEmail = itemView.findViewById(R.id.tvEmailContact);
        }

    }

}
