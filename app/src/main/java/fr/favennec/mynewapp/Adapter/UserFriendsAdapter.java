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
import fr.favennec.appsoft.FriendsListActivity;
import fr.favennec.appsoft.MainActivity;
import fr.favennec.appsoft.MessageActivity;
import fr.favennec.appsoft.Model.User;
import fr.favennec.appsoft.R;


public class UserFriendsAdapter extends RecyclerView.Adapter<UserFriendsAdapter.ViewHolder> {

    private static Context mContext;
    private List<User> mUsers;
    private boolean isfragment;
    private String profileid;


    private FirebaseUser firebaseUser;



    public static Context getContext() {
        return mContext;
    }


    public UserFriendsAdapter(Context mContext, List<User> mUsers, Boolean isfragment) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isfragment = isfragment;

    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item_friends, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewholder, int i) {


        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid= prefs.getString("profileid", "none");



        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUsers.get(i);


        viewholder.linear_btn_follow.setVisibility(View.VISIBLE);






        viewholder.username.setText(user.getUsername());
        viewholder.fullname.setText(user.getFullname());
        Glide.with(mContext).load(user.getImageurl()).into(viewholder.image_profile);



        if(user.getId().equals(firebaseUser.getUid())) {


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
                if (isfragment) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileid", user.getId());
                    editor.apply();

                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new ProfileFragment()).commit();
                } else {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("publisherid", user.getId());
                    mContext.startActivity(intent);
                }

            }
        });

        viewholder.ic_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(getContext(), MessageActivity.class);
                    intent.putExtra("userid", user.getId());
                    getContext().startActivity(intent);


            }
        });






    }



    @Override
    public int getItemCount() {
        return mUsers.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public TextView fullname;
        public ImageView image_profile;
        public Button ic_message;
        public LinearLayout linear_btn_follow;

        //
        public ImageView follow, following;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            fullname = itemView.findViewById(R.id.fullname);
            image_profile = itemView.findViewById(R.id.image_profile);



            linear_btn_follow = itemView.findViewById(R.id.linear_btn_follow);
            //
            follow = itemView.findViewById(R.id.follow);
            following = itemView.findViewById(R.id.following);
            ic_message = itemView.findViewById(R.id.ic_message);
        }
    }





}
