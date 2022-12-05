package fr.favennec.mynewapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import fr.favennec.appsoft.Adapter.MyFotoAdapter;
import fr.favennec.appsoft.EditProfileActivity;
import fr.favennec.appsoft.FollowersActivity;
import fr.favennec.appsoft.MessageActivity;
import fr.favennec.appsoft.Model.Post;

import fr.favennec.appsoft.Model.User;
import fr.favennec.appsoft.NotificationActivity;
import fr.favennec.appsoft.OptionsActivity;
import fr.favennec.appsoft.R;
import fr.favennec.appsoft.StartActivity;


public class ProfileFragment extends Fragment {

    ImageView image_profile, options, more_acc, notification;
    TextView posts, followers, txt_following, fullname, bio, username;
    Button follow,custom, message_acc, shop;
    LinearLayout linear_edit_profile;




    private List<String> mySaves;
    RecyclerView recyclerView_saves;
    MyFotoAdapter myFotoAdapter_saves;
    List<Post> postList_saves;


    RecyclerView recyclerView;
    MyFotoAdapter myFotoAdapter;
    List<Post> postList;


    RelativeLayout Text_feed,Text_feed2;

    ViewFlipper reverse2, reverse;

    FirebaseUser firebaseUser;
    String profileid;

    ImageButton my_fotos, saved_fotos;

    Boolean isYourProfile=false, isPagePostSave=false, isPagePost=true;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid= prefs.getString("profileid", "none");

        image_profile = view.findViewById(R.id.image_profile);
        options = view.findViewById(R.id.options);
        posts = view.findViewById(R.id.posts);
        followers = view.findViewById(R.id.followers);
        txt_following = view.findViewById(R.id.txt_following);
        fullname = view.findViewById(R.id.fullname);
        bio = view.findViewById(R.id.bio);
        username = view.findViewById(R.id.username);
        my_fotos = view.findViewById(R.id.my_fotos);
        saved_fotos = view.findViewById(R.id.saved_fotos);
        notification = view.findViewById(R.id.notification);
        Text_feed = view.findViewById(R.id.Text_feed);
        Text_feed2 = view.findViewById(R.id.Text_feed2);

        reverse2 = view.findViewById(R.id.reverse2);
        shop = view.findViewById(R.id.shop);





        more_acc = view.findViewById(R.id.more_acc);
        follow = view.findViewById(R.id.follow);



        message_acc = view.findViewById(R.id.message_acc);
        custom = view.findViewById(R.id.ic_custom);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        //nombre de photo par ligne
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList= new ArrayList<>();

        myFotoAdapter = new MyFotoAdapter(getContext(), postList);
        /*myFotoAdapterVideo = new MyFotoAdapter(getContext(), postListVideo);*/
        recyclerView.setAdapter(myFotoAdapter);


        recyclerView_saves = view.findViewById(R.id.recycler_view_save);
        recyclerView_saves.setHasFixedSize(true);
        //nombre de photo par ligne
        LinearLayoutManager linearLayoutManager_saves = new GridLayoutManager(getContext(),3);
        recyclerView_saves.setLayoutManager(linearLayoutManager_saves);
        postList_saves= new ArrayList<>();
        myFotoAdapter_saves = new MyFotoAdapter(getContext(), postList_saves);
        recyclerView_saves.setAdapter(myFotoAdapter_saves);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_saves.setVisibility(View.GONE);

        //test pour savoir
        my_fotos.setImageResource(R.drawable.ic_grid);
        //////////




        userInfo();
        getFollowers();
        getNrPosts();
        myFotos();
        mysaves();

        if(profileid.equals(firebaseUser.getUid())) {

            //custom.setWidth(200);
            //custom.setTextSize(13);

            shop.setVisibility(View.GONE);
            follow.setVisibility(View.GONE);
            more_acc.setVisibility(View.GONE);
            message_acc.setVisibility(View.GONE);
            isYourProfile = true;


        } else {
            isYourProfile = false;
            checkFollow();
            shop.setVisibility(View.VISIBLE);
            message_acc.setVisibility(View.VISIBLE);
            saved_fotos.setVisibility(View.GONE);
            options.setVisibility(View.GONE);
            custom.setVisibility(View.GONE);

        }

        shop.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick (View v){
                Toast.makeText(getContext(), "COMING SOON : VOTRE PROPRE SHOP", Toast.LENGTH_SHORT).show();

            }
        });


        notification.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick (View v){
                startActivity(new Intent(getContext(), NotificationActivity.class));
            }
        });

        custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), EditProfileActivity.class));
            }
        });

        ///////a verif

        message_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MessageActivity.class);
                intent.putExtra("userid", profileid);
                getContext().startActivity(intent);
            }
        });

        //////////////





        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = follow.getText().toString();


                 if (btn.equals("follow")) {

                     follow.setVisibility(View.GONE);


                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).setValue(true);

                    addNotifications();

                } else if (btn.equals("following")) {

                     follow.setVisibility(View.VISIBLE);


                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).removeValue();

                }
            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OptionsActivity.class);
                startActivity(intent);

            }
        });

        my_fotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saved_fotos.setImageResource(R.drawable.ic_saved);
                my_fotos.setImageResource(R.drawable.ic_grid);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_saves.setVisibility(View.GONE);

                Text_feed2.setVisibility(View.GONE);

                if (isYourProfile) {


                    if(postList.size() == 0 ){
                        Text_feed.setVisibility(View.VISIBLE);


                    } else {
                        Text_feed.setVisibility(View.GONE);

                    }
                } else {
                    Text_feed.setVisibility(View.GONE);
                }

            }
        });


        saved_fotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                my_fotos.setImageResource(R.drawable.ic_grid_2);
                saved_fotos.setImageResource(R.drawable.ic_save);

                recyclerView.setVisibility(View.GONE);
                recyclerView_saves.setVisibility(View.VISIBLE);

                Text_feed.setVisibility(View.GONE);

                if(postList_saves.size() == 0){
                    Text_feed2.setVisibility(View.VISIBLE);


                } else {
                    Text_feed2.setVisibility(View.GONE);

                }


            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title", "followers");
                startActivity(intent);

            }
        });

        txt_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title", "following");
                startActivity(intent);
            }
        });



        return view;
    }

    private void addNotifications() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "started following you");
        hashMap.put("postid", "");
        hashMap.put("ispost", false);

        reference.push().setValue(hashMap);
    }

    private void userInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null ) {
                    return;
                }

                User user = snapshot.getValue(User.class);

                if ( user!= null) {

                    Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
                    username.setText(user.getUsername());
                    fullname.setText(user.getFullname());
                    bio.setText(user.getBio());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFollow() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(firebaseUser.getUid()).child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileid).exists()) {
                    follow.setText("following");
                    follow.setVisibility(View.GONE);


                } else {
                    follow.setText("follow");
                    follow.setVisibility(View.VISIBLE);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("followers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(""+snapshot.getChildrenCount());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("following");

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txt_following.setText(""+snapshot.getChildrenCount());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getNrPosts () {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Post post = snapshot1.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)){
                        i++;
                    }
                }
                posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("postsVideo");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Post post = snapshot1.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)){
                        i++;
                    }
                }
                posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void myFotos() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Post post = snapshot1.getValue(Post.class);
                    if(post.getPublisher().equals(profileid)) {
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                myFotoAdapter.notifyDataSetChanged();

                if(postList.size() == 0 && isPagePost && isYourProfile){
                    Text_feed.setVisibility(View.VISIBLE);

                } else {
                    Text_feed.setVisibility(View.GONE);

                }
                /*Toast.makeText(getContext(), "size of list : "+postList.size(), Toast.LENGTH_SHORT).show();*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        /*DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("postsVideo");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    PostVideo postVideo = snapshot1.getValue(PostVideo.class);
                    if(postVideo.getPublisher().equals(profileid)) {
                        postList.add(postVideo);
                    }
                }
                Collections.reverse(postList);
                myFotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

    }
    private void mysaves() {
        mySaves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    mySaves.add(snapshot1.getKey());
                }

                readSaves();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readSaves() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList_saves.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Post post = snapshot1.getValue(Post.class);

                    for ( String id : mySaves) {
                        if (post.getPostid().equals(id)) {
                            postList_saves.add(post);
                        }
                    }
                }
                myFotoAdapter_saves.notifyDataSetChanged();


                if(postList_saves.size() == 0 && isPagePostSave && isYourProfile){
                    Text_feed2.setVisibility(View.VISIBLE);

                } else {
                    Text_feed2.setVisibility(View.GONE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}