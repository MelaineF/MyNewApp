package fr.favennec.mynewapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.favennec.appsoft.Fragment.ProfileFragment;
import fr.favennec.appsoft.MainActivity;
import fr.favennec.appsoft.Model.User;
import fr.favennec.appsoft.R;


public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.ViewHolder> {

    private static Context mContext;
    private List<User> mUsers;
    private boolean isfragment;


    private FirebaseUser firebaseUser;



    public static Context getContext() {
        return mContext;
    }


    public UserSearchAdapter(Context mContext, List<User> mUsers, Boolean isfragment) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isfragment = isfragment;

    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item_search, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewholder, int i) {




        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUsers.get(i);


        viewholder.linear_btn_follow.setVisibility(View.VISIBLE);
        viewholder.btn_follow.setVisibility(View.VISIBLE);





        viewholder.username.setText(user.getUsername());
        viewholder.fullname.setText(user.getFullname());
        Glide.with(mContext).load(user.getImageurl()).into(viewholder.image_profile);

        isFollowing(user.getId(),viewholder.btn_follow,viewholder.follow,viewholder.following, viewholder.linear_btn_follow);

        if(user.getId().equals(firebaseUser.getUid())) {
            viewholder.btn_follow.setVisibility(View.GONE);
            viewholder.btn_follow_2.setVisibility(View.GONE);
            viewholder.linear_btn_follow.setVisibility(View.GONE);

        }

        /*
        if(viewholder.btn_follow.getText().toString().equals("follow") ) {

            viewholder.follow.setVisibility(View.GONE);
            viewholder.following.setVisibility(View.VISIBLE);
        }
       */


        viewholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //FIXME : pas bon pour isfragment, fonctionnel mais pas correct
                if (!isfragment) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileid", user.getId());
                    editor.apply();

                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new ProfileFragment()).commit();
                } else {
                    Intent intent = new Intent(mContext, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("publisherid", user.getId());
                    mContext.startActivity(intent);
                }

            }
        });

        viewholder.linear_btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewholder.btn_follow.getText().toString().equals("follow") ) {

                    viewholder.follow.setVisibility(View.GONE);
                    viewholder.following.setVisibility(View.VISIBLE);



                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).setValue(true);



                    addNotifications(user.getId());





                }  else {

                    viewholder.follow.setVisibility(View.VISIBLE);
                    viewholder.following.setVisibility(View.GONE);

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).removeValue();


                }

            }
        });



    }

    private void addNotifications(String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "started following you");
        hashMap.put("postid", "");
        hashMap.put("ispost", false);

        reference.push().setValue(hashMap);

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public TextView fullname;
        public ImageView image_profile;
        public Button btn_follow, btn_follow_2;
        public LinearLayout linear_btn_follow;
        //
        public ImageView follow ;
        public CircleImageView following;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            fullname = itemView.findViewById(R.id.fullname);
            image_profile = itemView.findViewById(R.id.image_profile);
            btn_follow = itemView.findViewById(R.id.btn_follow);
            btn_follow_2 = itemView.findViewById(R.id.btn_follow_2);
            linear_btn_follow = itemView.findViewById(R.id.linear_btn_follow);
            //
            follow = itemView.findViewById(R.id.follow);
            following = itemView.findViewById(R.id.following);
        }
    }


    private void isFollowing(String userid, Button button,ImageView follow, ImageView following, LinearLayout linearLayout){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userid).exists()) {
                    button.setText("following");
                    //button color
                    follow.setVisibility(View.GONE);
                    following.setVisibility(View.VISIBLE);
                    //////////

                } else {

                    button.setText("follow");
                    //button color
                    follow.setVisibility(View.VISIBLE);
                    following.setVisibility(View.GONE);
                    //////////
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
