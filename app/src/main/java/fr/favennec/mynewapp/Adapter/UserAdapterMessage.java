package fr.favennec.mynewapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.favennec.appsoft.MessageActivity;
import fr.favennec.appsoft.Model.Chat;
import fr.favennec.appsoft.Model.User;
import fr.favennec.appsoft.R;


public class UserAdapterMessage extends RecyclerView.Adapter<UserAdapterMessage.ViewHolder> {

    private static Context mContext;
    private List<User> mUsers;
    private boolean ischat;

    String theLastMessage;
    String theLastHour;
    RelativeLayout back;




    private FirebaseUser firebaseUser;



    public static Context getContext() {
        return mContext;
    }


    public UserAdapterMessage(Context mContext, List<User> mUsers, Boolean ischat) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.ischat = ischat;

    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item_message, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewholder, int i) {




        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUsers.get(i);







        viewholder.username.setText(user.getUsername());

        Glide.with(mContext).load(user.getImageurl()).into(viewholder.image_profile);


        if (ischat) {
            lastMessage(user.getId(), viewholder.last_msg, viewholder.last_hour, viewholder.username, viewholder.have_message);

        } else {
            viewholder.last_msg.setVisibility(View.GONE);
        }

        if (ischat) {
            if (user.getStatus().equals("online")) {
                viewholder.img_on.setVisibility(View.VISIBLE);
                viewholder.img_off.setVisibility(View.GONE);
            } else {
                viewholder.img_on.setVisibility(View.GONE);
                viewholder.img_off.setVisibility(View.VISIBLE);

            }

        } else {
            viewholder.img_on.setVisibility(View.GONE);
            viewholder.img_off.setVisibility(View.GONE);

        }






        viewholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MessageActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //////
                intent.putExtra("userid", user.getId());
                //////
                getContext().startActivity(intent);







                }


        });





    }



    @Override
    public int getItemCount() {
        return mUsers.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;




        public TextView fullname;
        public ImageView image_profile,have_message;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg,last_hour;





        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            fullname = itemView.findViewById(R.id.fullname);
            image_profile = itemView.findViewById(R.id.image_profile);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
            last_hour =itemView.findViewById(R.id.last_hour);
            back = itemView.findViewById(R.id.back);
            have_message = itemView.findViewById(R.id.have_message);






        }
    }

    private void lastMessage(String userid, TextView last_msg, TextView last_hour, TextView username, ImageView have_message) {
        theLastMessage = "default";
        theLastHour = "default";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Chat chat = snapshot1.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                    chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                        theLastMessage = chat.getMessage();
                        theLastHour = chat.getHour();
                    }

                    if ( chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen()  && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && !chat.getSender().equals(firebaseUser.getUid())) {





                        username.setTypeface(Typeface.DEFAULT_BOLD);
                        last_msg.setTypeface(Typeface.DEFAULT_BOLD);
                        last_msg.setTextColor(R.color.black);
                        have_message.setVisibility(View.VISIBLE);

                        //Toast.makeText(mContext, "firstpart", Toast.LENGTH_SHORT).show();



                            //back.setBackgroundColor(R.color.black);





                    } else  {

                        last_msg.setTypeface(Typeface.DEFAULT);
                        username.setTypeface(Typeface.DEFAULT);
                        last_msg.setTextColor(Color.parseColor("#7E7E7E"));
                        have_message.setVisibility(View.INVISIBLE);


                        //Toast.makeText(mContext, "secondpoart", Toast.LENGTH_SHORT).show();



                        //back.setBackgroundColor(R.color.red);

                    }


                }

                switch (theLastMessage) {
                    case "default" :

                        ////TODO : ajouter "..." si le message n'apparait pas en entier

                        last_msg.setText("");
                        last_hour.setText("");

                        break;

                    default:

                        last_msg.setText(theLastMessage);
                        last_hour.setText(theLastHour + " : ");
                        break;

                }
                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

}
