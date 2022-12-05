package fr.favennec.mynewapp.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.favennec.appsoft.MessageActivity;
import fr.favennec.appsoft.Model.Chat;
import fr.favennec.appsoft.Model.Chatlist;
import fr.favennec.appsoft.Model.User;
import fr.favennec.appsoft.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;



    FirebaseUser fuser;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageurl ) {
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MSG_TYPE_RIGHT) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);

        }
    }





    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Chat chat = mChat.get(position);

        holder.show_message.setText(chat.getMessage());
        holder.text_heure.setText(chat.getHour());

        Glide.with(mContext).load(imageurl).into(holder.image_profile);







        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (chat.getSender().equals(fuser.getUid()) ){

                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    //test personnalisation dialog
                    View view = LayoutInflater.from(mContext).inflate(R.layout.custom_layout_dialog_box_comments, null);
                    //élément du alertdialog
                    TextView comments_text = (TextView) view.findViewById(R.id.comments_text);
                    ImageView close = (ImageView) view.findViewById(R.id.close);
                    ImageView delete = (ImageView) view.findViewById(R.id.delete);
                    TextView username2 =(TextView) view.findViewById(R.id.username2);
                    CircleImageView circleImageView = (CircleImageView) view.findViewById(R.id.image_profile2);


                    comments_text.setText(chat.getMessage());
                    //TODO : faire apparaitre pseudo et photo du publisher


                    /*getUserInfo2(holder.image_profile, holder.username, comment.getPublisher());

                    User user = getValue(User.class);
                    Glide.with(mContext).load(user.getImageurl()).into(image_profile2);
                    username2.setText(user.getUsername());*/





                    /*
                    fonctionne pour tous les messages sauf quand il n'en reste qu'un
                    TODO : réfléchir à quoi faire si on supprime le last message

                     */
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase.getInstance().getReference("Chats")
                                    //TODO : retirer un message lorsqu'il est supprimé
                                    .child(chat.getChatid())
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(mContext, "Deleted msg!", Toast.LENGTH_SHORT).show();



                                    }
                                }
                            });

                            //TODO : si chatlist = 0 supprimer list
                            /*
                            if(mChat.size() == 0) {
                                FirebaseDatabase.getInstance().getReference("Chatlist")

                                        .child(Chatlist.get)
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(mContext, "Deleted msg!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });*/


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


                }
                return true;
            }
        });

        //TODO : vérifier si on veut garder ou pas les messages vu
        if (position == mChat.size()-1) {  //-1 --> check for last message
            if  (chat.isIsseen()) {
                holder.txt_seen.setText("Seen");


            } else {
                holder.txt_seen.setText("Delivered");

            }
        } else {
            //si c'est pas le dernier message on fait disparaitre le texte
            holder.txt_seen.setVisibility(View.GONE);
        }




    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message, text_heure;
        public ImageView image_profile;




        public TextView txt_seen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            image_profile = itemView.findViewById(R.id.image_profile);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            text_heure = itemView.findViewById(R.id.text_heure);

        }
    }


    @Override
    public int getItemViewType(int position) {
       fuser = FirebaseAuth.getInstance().getCurrentUser();
       if (mChat.get(position).getSender().equals(fuser.getUid())){
           return MSG_TYPE_RIGHT;

       }else {
           return MSG_TYPE_LEFT;
       }
    }


}
