package fr.favennec.mynewapp.Fragment;

import android.content.Intent;
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
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.favennec.appsoft.Adapter.PostAdapter;

import fr.favennec.appsoft.Model.Post;

import fr.favennec.appsoft.NotificationActivity;
import fr.favennec.appsoft.R;
import fr.favennec.appsoft.searchActivity;


public class abonnementFragment extends Fragment {

    Boolean isScrolling = false;

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postLists;
    private List<String> followingList;
    RelativeLayout Text_feed;
    ProgressBar progressBar;
    ImageView GoTosearchActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_abonnement, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postLists= new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postLists);
        recyclerView.setAdapter(postAdapter);
        progressBar = view.findViewById(R.id.progress_circular);

        GoTosearchActivity = view.findViewById(R.id.GoTosearchActivity);
        Text_feed = view.findViewById(R.id.Text_feed);

        checkFollowing();

        GoTosearchActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), searchActivity.class));
            }
        });





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
                    if (post.getPublisher().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        postLists.add(post);
                    }
                    for (String id : followingList) {

                        if (post.getPublisher().equals(id) ) {
                            postLists.add(post);

                        }
                    }
                }

                postAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);


                if(postLists.size() == 0){
                    Text_feed.setVisibility(View.VISIBLE);

                } else {
                    Text_feed.setVisibility(View.GONE);

                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkFollowing() {
        followingList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child((Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())).getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren() ) {
                    followingList.add(snapshot.getKey());

                }

                readPosts();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}