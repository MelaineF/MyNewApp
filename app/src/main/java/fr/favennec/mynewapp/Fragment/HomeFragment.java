package fr.favennec.mynewapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import fr.favennec.appsoft.Adapter.PostAdapter;
import fr.favennec.appsoft.Adapter.StoryAdapter;

import fr.favennec.appsoft.Adapter.UserAdapter;
import fr.favennec.appsoft.ContentCreator.MainActivityContentCreator;

import fr.favennec.appsoft.MainActivity;
import fr.favennec.appsoft.MessageActivity;
import fr.favennec.appsoft.Model.Chat;
import fr.favennec.appsoft.Model.Post;
import fr.favennec.appsoft.Model.Story;
import fr.favennec.appsoft.Model.User;
import fr.favennec.appsoft.NotificationActivity;
import fr.favennec.appsoft.PageMessageActivity;
import fr.favennec.appsoft.R;


public class HomeFragment extends Fragment {


    DatabaseReference reference;
    FirebaseUser firebaseUser;

    private PostAdapter postAdapter;
    private List<Post> postLists;
    private RecyclerView recyclerView;
    ProgressBar progressBar;


    //ImageView like2;
    ImageView img_notif, message, img_border_notif, event,community;
    TextView text_notif, text_notif_9;


    String profileid;


    ////////////
    BottomNavigationView bottomNavigationView;

    Fragment selectedFragment = null;
    ////////////


    /////

    int currentItems, totalItems, scrollOutItems;
    /////


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        /*
         * Work on this page :
         * TODO : slide to open pageMessage
         * TODO : ...
         *
         * */


        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postLists= new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postLists);
        recyclerView.setAdapter(postAdapter);
        /*progressBar = view.findViewById(R.id.progress_circular);*/










        ////////////////////////////////////////////////////////////
        ///////////////// Scrolling more for posts /////////////////
        ////////////////////////////////////////////////////////////

         /*

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if( newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = linearLayoutManager.getChildCount();
                totalItems = linearLayoutManager.getItemCount();
                scrollOutItems = linearLayoutManager.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {

                    //data fetch
                    isScrolling = false;
                    fetchData();
                }
            }
        });*/

        ////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////


        message = view.findViewById(R.id.message);
        event = view.findViewById(R.id.event);
        community = view.findViewById(R.id.community);


        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid= prefs.getString("profileid", "none");



        /////////
        /*bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
*/




        ////////





        img_notif = view.findViewById(R.id.img_notif);
        text_notif = view.findViewById(R.id.text_notif);
        text_notif_9 = view.findViewById(R.id.text_notif_9);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();








        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int unread = 0;
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    Chat chat = snapshot1.getValue(Chat.class);

                    if (chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen()) {
                        //ajouter si c'es tle même user on fait rien
                        /*if (chat.getReceiver().equals(firebaseUser.getUid()) != chat.getReceiver().equals(firebaseUser.getUid())) {*/ //marche pas


                        unread++;

                    }
                }

                if (unread == 0) {
                    img_notif.setVisibility(View.GONE);

                    text_notif.setVisibility(View.GONE);
                    text_notif_9.setVisibility(View.GONE);

                } else {
                    img_notif.setVisibility(View.VISIBLE);



                    if (unread<10) {
                        text_notif.setVisibility(View.VISIBLE);
                        text_notif_9.setVisibility(View.GONE);
                        text_notif.setText(Integer.toString(unread));
                    } else {
                        text_notif.setVisibility(View.GONE);
                        text_notif_9.setVisibility(View.VISIBLE);

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        community.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick (View v){
                startActivity(new Intent(getContext(), MainActivityContentCreator.class));

            }
        });




        message.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick (View v){
                startActivity(new Intent(getContext(), PageMessageActivity.class));

            }
        });








        readPosts();

        return view;
    }

    private void readPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                postLists.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post!=null){

                    if (post.getIsPublic()) {
                        postLists.add(post);
                    }
                }






                }

                postAdapter.notifyDataSetChanged();
                /*progressBar.setVisibility(View.GONE);*/






            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



   /* private void fetchData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i=0; i<5; i++)
                {

                    postAdapter.notifyDataSetChanged();
                }

            }
        }, 5000);
    }*/

















}