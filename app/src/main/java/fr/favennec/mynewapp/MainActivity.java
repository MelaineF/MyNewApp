package fr.favennec.mynewapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import fr.favennec.mynewapp.Fragment.HomeFragment;
import fr.favennec.mynewapp.Fragment.tsFragment;
import fr.favennec.mynewapp.Fragment.ProfileFragment;
import fr.favennec.mynewapp.Fragment.abonnementFragment;

import com.google.firebase.auth.FirebaseAuth;

import fr.favennec.mynewapp.userStart.StartActivity;

public class MainActivity extends AppCompatActivity {


    //variables xml
    BottomNavigationView bottomNavigationView;
    BottomNavigationView addNavigationView;

    float x1,x2,y1,y2;

    Fragment selectedFragment = null;

    DatabaseReference reference;

    private FirebaseUser firebaseUser;

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private FrameLayout fragment_container;
    private Fragment PageMessageFragment;
    private Fragment HomeFragment;
    private AppBarLayout bottom;
    private View view_add_navigation;

    private Shape round_corner_event;

    private AppBarLayout appBarLayout;

    /*
     * Work on this page :
     * TODO : better design for toolbar of camera
     * TODO : ...
     *
     * */


    //variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment_container =findViewById(R.id.fragment_container);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        addNavigationView = findViewById(R.id.add_navigation);



        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        view_add_navigation = findViewById(R.id.view_add_navigation);


        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navig_home:
                        /////////////





                        bottomNavigationView.setBackgroundResource(R.color.white);
                        addNavigationView.setVisibility(View.GONE);
                        view_add_navigation.setVisibility(View.GONE);



                        /////////////
                        selectedFragment = new HomeFragment();



                        break;

                    case R.id.navig_search:



                        selectedFragment = new tsFragment();

                        bottomNavigationView.setBackgroundResource(R.color.white);

                        addNavigationView.setVisibility(View.GONE);
                        view_add_navigation.setVisibility(View.GONE);



                        //////////////


                        //////////////

                        break;


                    case R.id.navig_add:
                        //////////////

                        bottomNavigationView.setBackgroundResource(R.color.white);
                        //////////////
                        selectedFragment = null;

                        //startActivity(new Intent(MainActivity.this, CameraActivity.class));
                        ///startActivity(new Intent(MainActivity.this, PostActivity.class));
                        addNavigationView.setVisibility(View.VISIBLE);
                        view_add_navigation.setVisibility(View.VISIBLE);
                        break;

                    case R.id.navig_abonnement:
                        //////////////

                        bottomNavigationView.setBackgroundResource(R.color.white);
                        addNavigationView.setVisibility(View.GONE);
                        view_add_navigation.setVisibility(View.GONE);
                        //////////////


                        selectedFragment = new abonnementFragment();



                        break;

                    case R.id.navig_account:
                        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                        editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        editor.apply();
                        //////////////

                        bottomNavigationView.setBackgroundResource(R.color.white);
                        addNavigationView.setVisibility(View.GONE);
                        view_add_navigation.setVisibility(View.GONE);

                        //////////////
                        selectedFragment = new ProfileFragment();
                        break;

                    case R.id.navig_add_galerie:
                        //////////////

                        bottomNavigationView.setBackgroundResource(R.color.white);
                        //////////////
                        selectedFragment = null;

                        //startActivity(new Intent(MainActivity.this, CameraActivity.class));
                        startActivity(new Intent(MainActivity.this, PostActivity.class));
                        break;


                    case R.id.navig_photo:
                        //////////////

                        bottomNavigationView.setBackgroundResource(R.color.white);
                        //////////////
                        selectedFragment = null;

                        //startActivity(new Intent(MainActivity.this, CameraActivity.class));
                        startActivity(new Intent(MainActivity.this, CameraActivity.class));
                        break;








                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                }





                return true;
            }
        };



    }
        });





}