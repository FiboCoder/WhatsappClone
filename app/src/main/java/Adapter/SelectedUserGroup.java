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

public class SelectedUserGroup extends RecyclerView.Adapter<SelectedUserGroup.MyViewHolder> {

    private List<User> selectedContacts;
    private Context context;

    public SelectedUserGroup(List<User> selectedContactsList, Context c) {
        this.selectedContacts = selectedContactsList;
        this.context = c;
    }

    @NonNull
    @Override
    public SelectedUserGroup.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_user_group, parent, false);
        return new SelectedUserGroup.MyViewHolder(listItem);

    }

    @Override
    public void onBindViewHolder(@NonNull SelectedUserGroup.MyViewHolder holder, int position) {

        User user = selectedContacts.get(position);

        holder.tvName.setText(user.getName());
        if(user.getPhoto() != null){

            Uri uri = Uri.parse(user.getPhoto());
            Glide.with(context).load(uri).into(holder.civProfile);

        }else{

            holder.civProfile.setImageResource(R.drawable.padrao);
        }
    }

    @Override
    public int getItemCount() {
        return selectedContacts.size();
    }

    public class MyViewHolder  extends RecyclerView.ViewHolder{

        private CircleImageView civProfile;
        private AppCompatTextView tvName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            civProfile = itemView.findViewById(R.id.civProfileSelectedGroup);
            tvName = itemView.findViewById(R.id.tvNameSelectedGroup);

        }
    }
}
