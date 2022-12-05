package fr.favennec.mynewapp.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchUIUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.List;

import javax.security.auth.DestroyFailedException;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.favennec.appsoft.MainActivity;
import fr.favennec.appsoft.Model.Comment;
import fr.favennec.appsoft.Model.User;
import fr.favennec.appsoft.R;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;
    private List<Comment> mComment;
    private String postid;

    private FirebaseUser firebaseUser;


    public CommentAdapter(Context mContext, List<Comment> mComment, String postid) {
        this.mContext = mContext;
        this.mComment = mComment;
        this.postid = postid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Comment comment = mComment.get(position);

        holder.comment.setText(comment.getComment());
        getUserInfo(holder.image_profile, holder.username, comment.getPublisher());

        /*holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherid", comment.getPublisher());
                mContext.startActivity(intent);

            }
        });*/

        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherid", comment.getPublisher());
                mContext.startActivity(intent);

            }
        });


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //TODO : si c'est sur un de nos posts on peut supprimer
                if (comment.getPublisher().equals(firebaseUser.getUid()) ){

                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    //test personnalisation dialog
                    View view = LayoutInflater.from(mContext).inflate(R.layout.custom_layout_dialog_box_comments, null);
                    //élément du alertdialog
                    TextView comments_text = (TextView) view.findViewById(R.id.comments_text);
                    ImageView close = (ImageView) view.findViewById(R.id.close);
                    ImageView delete = (ImageView) view.findViewById(R.id.delete);
                    TextView username2 =(TextView) view.findViewById(R.id.username2);
                    CircleImageView circleImageView = (CircleImageView) view.findViewById(R.id.image_profile2);


                    comments_text.setText(comment.getComment());
                    //TODO : faire apparaitre pseudo et photo du publisher


                    /*getUserInfo2(holder.image_profile, holder.username, comment.getPublisher());

                    User user = getValue(User.class);
                    Glide.with(mContext).load(user.getImageurl()).into(image_profile2);
                    username2.setText(user.getUsername());*/





                    //alertDialog.setTitle("Choose");
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase.getInstance().getReference("Comments")
                                    .child(postid).child(comment.getCommentid())
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            alertDialog.dismiss();
                        }
                    });
                    //cacher clavier lorsque on selectionne un commentaire
                     UIUtil.hideKeyboard((Activity) mContext);

                    alertDialog.setView(view);
                    alertDialog.show();

                    //alertDialog.getWindow().setLayout(1000, 400);
                    alertDialog.getWindow().setLayout( RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.getWindow().setGravity(Gravity.BOTTOM);


                } else {

                    //signaler

                    AlertDialog alertDialog2 = new AlertDialog.Builder(mContext).create();
                    //test personnalisation dialog
                    View view = LayoutInflater.from(mContext).inflate(R.layout.custom_layout_dialog_box_comments_report, null);
                    //élément du alertdialog
                    TextView comments_text2 = (TextView) view.findViewById(R.id.comments_text2);
                    ImageView close2 = (ImageView) view.findViewById(R.id.close2);
                    ImageView report = (ImageView) view.findViewById(R.id.report);
                    TextView username3 =(TextView) view.findViewById(R.id.username3);
                    CircleImageView circleImageView3 = (CircleImageView) view.findViewById(R.id.image_profile3);


                    comments_text2.setText(comment.getComment());
                    //TODO : faire apparaitre pseudo et photo du publisher
                    /*getUserInfo2(holder.image_profile, holder.username, comment.getPublisher());

                    User user = getValue(User.class);
                    Glide.with(mContext).load(user.getImageurl()).into(image_profile2);
                    username2.setText(user.getUsername());*/





                    //alertDialog.setTitle("Choose");
                    report.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(mContext, "Report!", Toast.LENGTH_SHORT).show();
                            alertDialog2.dismiss();
                        }
                    });
                    //cacher clavier lorsque on selectionne un commentaire
                    UIUtil.hideKeyboard((Activity) mContext);

                    alertDialog2.setView(view);
                    alertDialog2.show();

                    //alertDialog.getWindow().setLayout(1000, 400);
                    alertDialog2.getWindow().setLayout( RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    alertDialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog2.getWindow().setGravity(Gravity.BOTTOM);

                }
                return true;
            }
        });


    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile;
        public TextView username, comment;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);


        }
    }

    private void getUserInfo(ImageView imageView, TextView username, String publisherid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherid);

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





}
