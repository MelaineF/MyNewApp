package fr.favennec.mynewapp.userStart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import fr.favennec.mynewapp.MainActivity;
import fr.favennec.mynewapp.R;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    //variables xml
    EditText email, password;
    TextView to_register, lost_password;
    Button login;
    ImageView close;


    //variables
    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //variables xml

        login = findViewById(R.id.login);
        email =  findViewById(R.id.email);
        password =  findViewById(R.id.password);
        lost_password =  findViewById(R.id.lost_password);
        close = findViewById(R.id.close);
        to_register = findViewById(R.id.to_register);

        //variables


        auth = FirebaseAuth.getInstance();


        //MAIN





        to_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, StartActivity.class));
                finish();
            }
        });


        lost_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
                finish();
            }
        });

        /*click to go on the home page*/
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO : changer progress dialog visuel
                ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                pd.setMessage("Please wait ..");
                pd.show();

                String str_email = email.getText().toString().trim();
                String str_password = password.getText().toString().trim();

                if (TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)) {
                    pd.dismiss();
                    Toast.makeText(LoginActivity.this,"All fileds are required"  ,Toast.LENGTH_SHORT).show();


                } else {

                    auth.signInWithEmailAndPassword(str_email, str_password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                                                .child(auth.getCurrentUser().getUid());

                                        reference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                pd.dismiss();
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                pd.dismiss();
                                            }
                                        });

                                    }else {
                                        pd.dismiss();
                                        Toast.makeText(LoginActivity.this,"Authentification failed!"  ,Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });
                }


            }
        });








    }
}