package fr.favennec.mynewapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import fr.favennec.appsoft.CommentsActivity;
import fr.favennec.appsoft.Fragment.PostDetailFragment;
import fr.favennec.appsoft.Fragment.ProfileFragment;
import fr.favennec.appsoft.MainActivity;
import fr.favennec.appsoft.Model.Notification;
import fr.favennec.appsoft.Model.Post;
import fr.favennec.appsoft.Model.User;
import fr.favennec.appsoft.NotificationActivity;
import fr.favennec.appsoft.R;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>  {

    private Context mContext;
    private List<fr.favennec.appsoft.Model.Notification> mNotification;
    private boolean isfragment;

    public NotificationAdapter(Context mContext, List<fr.favennec.appsoft.Model.Notification> mNotification, Boolean isfragment ) {
        this.mContext = mContext;
        this.mNotification = mNotification;
        this.isfragment = isfragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = mNotification.get(position);

        if (notification != null) {

            holder.text.setText(notification.getText());

            getUserInfo(holder.image_profile, holder.username, notification.getUserid());

            if (notification.getIspost()) {
                holder.post_image.setVisibility(View.VISIBLE);
                getPostImage(holder.post_image, notification.getPostid());
            } else {
                holder.post_image.setVisibility(View.GONE);
            }
        } else {
            Intent intent = new Intent(mContext, NotificationActivity.class);
            mContext.startActivity(intent);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert notification != null;
                if(notification.getIspost() == true) {
                   ///test /////
                    //TODO : bug notification aller à post et account

                    if (isfragment) {
                        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                        editor.putString("profileid", notification.getPostid());
                        editor.apply();

                        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new PostDetailFragment()).commit();
                    } else {
                        Intent intent = new Intent(mContext, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("publisherid", notification.getPostid());
                        mContext.startActivity(intent);
                    }



                } else {
                    if (isfragment) {
                        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                        editor.putString("profileid", notification.getUserid());
                        editor.apply();

                        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new ProfileFragment()).commit();
                    } else {
                        Intent intent = new Intent(mContext, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("publisherid", notification.getUserid());
                        mContext.startActivity(intent);
                    }

                }

                /////////////////////////////////////////////

                //////voir commentAdapter : utliser publisherid

            }
        });

    }



    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile, post_image;
        public TextView username, text;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            username = itemView.findViewById(R.id.username);
            text = itemView.findViewById(R.id.comment);


        }
    }

    private void getUserInfo (ImageView imageView, TextView username, String publisherid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(publisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getPostImage (ImageView imageView, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                if (post != null) {
                    //TODO
                    /*Glide.with(mContext).load(post.getPostimage()).into(imageView);*/
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
