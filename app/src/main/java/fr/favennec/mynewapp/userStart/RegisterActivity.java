package fr.favennec.mynewapp.userStart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import fr.favennec.mynewapp.MainActivity;
import fr.favennec.mynewapp.R;

public class RegisterActivity extends AppCompatActivity {

    //variables xml
    EditText email, fullname, username, password;
    TextView toLogin;
    Button register;
    ImageView toStart;
    //variables
    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);





            //variables xml
            toLogin = findViewById(R.id.to_login);
            register = findViewById(R.id.register);

            username =  findViewById(R.id.username);
            email =  findViewById(R.id.email);
            fullname =  findViewById(R.id.fullname);
            password =  findViewById(R.id.password);
            toStart = findViewById(R.id.close);



            //variables
            auth = FirebaseAuth.getInstance();



            //MAIN

            toStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(RegisterActivity.this, StartActivity.class));
                    finish();
                }
            });


            toLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();

                }
            });


            /*click to go on the home page*/
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pd = new ProgressDialog(RegisterActivity.this);
                    pd.setMessage("Please wait ..");
                    pd.show();

                    String str_username = username.getText().toString().trim();
                    String str_fullname = fullname.getText().toString().trim();
                    String str_email = email.getText().toString().trim();
                    String str_password = password.getText().toString().trim();

                    if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname)
                            || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)) {
                        pd.dismiss();
                        Toast.makeText(RegisterActivity.this,"All fileds are required"  ,Toast.LENGTH_SHORT).show();

                    } else if (str_password.length() < 6) {
                        pd.dismiss();
                        Toast.makeText(RegisterActivity.this,"Password must have 6 characters"  ,Toast.LENGTH_SHORT).show();

                    } else {

                        //register(str_username,  str_fullname, str_email, str_password);
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("message");
                        pd.dismiss();
                        myRef.setValue("Hello, World!");

                    }





                }
            });
        }
        private void register(String username, String fullname, String email, String password) {
            Toast.makeText(getApplicationContext(), "passage 1 !", Toast.LENGTH_SHORT).show();
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Toast.makeText(getApplicationContext(), "passage 2 !", Toast.LENGTH_SHORT).show();
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "passage 3 !", Toast.LENGTH_SHORT).show();


                                //TODO : verif que le username n'exsite pas déjà

                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                assert firebaseUser != null;
                                String userid = firebaseUser.getUid();

                                reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("id", userid);
                                hashMap.put("username", username.toLowerCase());
                                hashMap.put("fullname", fullname);
                                hashMap.put("bio", "");
                                hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/mynewapp-65adc.appspot.com/o/profile.png?alt=media&token=f423dea7-918b-40a4-be85-d5be65290d5a");
                                hashMap.put("password", password);
                                hashMap.put("email", email);



                                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            pd.dismiss();
                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);





                                        }
                                    }
                                });

                            } else {
                                pd.dismiss();
                                Toast.makeText(RegisterActivity.this,"You can't register with this email or password"  ,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



    }
}