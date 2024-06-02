package com.iri5.medicine_management;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.iri5.medicine_management.Fragment.ListFragment;
import com.iri5.medicine_management.Fragment.MapsFragment;
import com.iri5.medicine_management.Utils.Util;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bn_main_view);



        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_main_frame, new MapsFragment())
                    .commit();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.nav_menu_map){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fl_main_frame, new MapsFragment()).commit();

                }else if(menuItem.getItemId() == R.id.nav_menu_list){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fl_main_frame, new ListFragment()).commit();
                }
                return true;
            }
        });
        Util.printSHA1Key(this);
    }

    private void setBottomNavigationView(){

    }
}