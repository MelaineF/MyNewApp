package fr.favennec.mynewapp.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.internal.IAccountAccessor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;


import fr.favennec.mynewapp.Fragment.HomeFragment;
import fr.favennec.mynewapp.Fragment.PostDetailFragment;
import fr.favennec.mynewapp.Fragment.ProfileFragment;
import fr.favennec.mynewapp.MainActivity;
import fr.favennec.mynewapp.Model.Post;
import fr.favennec.mynewapp.Model.User;

import fr.favennec.mynewapp.R;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public Context mContext;
    public List<Post> mPost;









    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPost  ) {
        this.mContext = mContext;
        this.mPost = mPost;

    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent,false);
        return new ViewHolder(view);


    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        final Post post = mPost.get(position);







        if (post != null) {

            holder.text_heure.setText(post.getHour());

           /* Toast.makeText(mContext, "post : "+post, Toast.LENGTH_SHORT).show();
            Toast.makeText(mContext, "getPost : "+post.getPost(), Toast.LENGTH_SHORT).show();*/

            if (post.getPost() == null) {



                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            } else {

                //TODO changer pour video
                if (post.getExtension().equals("jpg") || post.getExtension().equals("jpeg") ){
                    Glide.with(mContext).load(post.getPost())
                            .apply(new RequestOptions().placeholder(R.drawable.postload))
                            .into(holder.post_image);

                } else if (post.getExtension().equals("3gp") || post.getExtension().equals("mp4") ){
                    Glide.with(mContext).load(post.getPost())
                            .apply(new RequestOptions().placeholder(R.drawable.postload))
                            .into(holder.post_image);

                    holder.post_video.setVideoPath(post.getPost());

                }



            }

            if ( post.getDescription().equals("")) {
                holder.description.setVisibility(View.GONE);
            } else {
                holder.description.setVisibility(View.VISIBLE);
                holder.description.setText("Title : "+post.getDescription());

            }

            publisherinfo(holder.image_profile, holder.username, holder.publisher, post.getPublisher() );

            isLikes(post.getPostid(), holder.like);
            nrLikes(holder.likes, post.getPostid());
            getComments(post.getPostid(), holder.comments);
            isSaved(post.getPostid(), holder.save);
        } else {



            ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();



        }

        if (post != null) {
        if (post.getExtension().equals("3gp") || post.getExtension().equals("mp4") ){
            holder.post_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    holder.post_image.setVisibility(View.GONE);
                    holder.post_video.setVisibility(View.VISIBLE);
                    holder.post_video.start();

                }
            });

        }
        } else {

            ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();
        }




        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();

            }
        });


        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();

            }
        });

        holder.publisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();

            }
        });






        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.save.getTag().equals("save")) {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(post.getPostid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(post.getPostid()).removeValue();
                }
            }
        });




        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO : animation of logo like
                if (holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                            .child(firebaseUser.getUid()).setValue(true);
                    addNotifications(post.getPublisher(), post.getPostid());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });
        //cliquer sur le logo
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postid", post.getPostid());
                intent.putExtra("publisherid", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        //cliquer sur le texte
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postid", post.getPostid());
                intent.putExtra("publisherid", post.getPublisher());
                mContext.startActivity(intent);
            }
        });


        /*holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FollowersActivity.class);
                intent.putExtra("id", post.getPostid());
                intent.putExtra("title", "likes");
                mContext.startActivity(intent);
            }
        });*/

        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                            .child(firebaseUser.getUid()).setValue(true);
                    addNotifications(post.getPublisher(), post.getPostid());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        //test

                        ////
                        switch (item.getItemId()){
                            case R.id.edit:
                                editPost(post.getPostid());
                                return true;
                            case R.id.delete:
                                final String id = post.getPostid();

                                FirebaseDatabase.getInstance().getReference("posts")
                                        .child(post.getPostid()).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    deleteNotifications(id, firebaseUser.getUid());
                                                    Toast.makeText(mContext, "delete", Toast.LENGTH_SHORT).show();









                                                }
                                            }
                                        });
                                return true;
                            case R.id.report:
                                Toast.makeText(mContext, "Report clicked!", Toast.LENGTH_SHORT).show();
                                //mettre en place un système de report
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.post_menu);
                if (!post.getPublisher().equals(firebaseUser.getUid())){
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                }
                popupMenu.show();
            }
        });





    }



    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile, post_image, like, comment, save, more;
        public TextView username, likes, publisher, description, comments,text_heure;
        public VideoView post_video;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            username = itemView.findViewById(R.id.username);
            likes = itemView.findViewById(R.id.likes);
            publisher = itemView.findViewById(R.id.publisher);
            description = itemView.findViewById(R.id.description);
            comments = itemView.findViewById(R.id.comments);
            more = itemView.findViewById(R.id.more);
            text_heure = itemView.findViewById(R.id.text_heure);
            post_video = itemView.findViewById(R.id.post_video);

            description.setPaintFlags(description.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
    }

     private void getComments(String postid, TextView comments) {
         DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postid);

         reference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.setText("View All "+snapshot.getChildrenCount() + " Comments");
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });


     }





    private void isLikes(String postid, ImageView imageView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.like_ic_2);
                    imageView.setTag("liked");

                } else {
                    imageView.setImageResource(R.drawable.like_ic);
                    imageView.setTag("like");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addNotifications(String userid, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text","liked your post");
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);

        reference.push().setValue(hashMap);


    }
    ////////new////
    private void deleteNotifications(final String postid, String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (snapshot.child("postid").getValue().equals(postid)){
                        snapshot.getRef().removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

        ///////////





    private void nrLikes (TextView likes, String postid)  {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount() + "");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    private void publisherinfo(ImageView image_profile, TextView username, TextView publisher, String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);


                if (isValidContextForGlide(mContext)){
                    Glide.with(mContext).load(user.getImageurl()).into(image_profile);

                }


                username.setText(user.getUsername());
                publisher.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isSaved(String postid, ImageView imageView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postid).exists()) {
                    imageView.setImageResource(R.drawable.ic_saved_2);
                    imageView.setTag("saved");

                } else {
                    imageView.setImageResource(R.drawable.ic_save_2);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void editPost (String postid) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Edit Post");

        //EditText editText = new EditText(mContext);


        //MAX LENGH EDIT TEXT
        EditText editText = new EditText(mContext); int maxLength = 35;
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
        editText.setFilters(FilterArray);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        editText.setLayoutParams(lp);
        alertDialog.setView(editText);
        getText(postid, editText);

        alertDialog.setPositiveButton("Edit",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("description", editText.getText().toString());

                        FirebaseDatabase.getInstance().getReference("posts")
                                .child(postid).updateChildren(hashMap);

                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }
    private void getText(String postid, EditText editText){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts")
                .child(postid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                

                    editText.setText( snapshot.getValue(Post.class).getDescription());







            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }




}
