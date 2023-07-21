package com.example.musicstreaming;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    FrameLayout frameMain;
    BottomNavigationView bottomNavMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadFragment(new fragment_home());
        frameMain = (FrameLayout) findViewById(R.id.frameMain);
        bottomNavMain = (BottomNavigationView) findViewById(R.id.bottomNavMain);
        bottomNavMain.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.home_nav){
                    loadFragment(new fragment_home());
                }
                if(id == R.id.search_nav){
                    loadFragment(new fragment_search());
                }
                if(id == R.id.playlist_nav){
                    loadFragment(new fragment_playlist());
                }
                return true;
            }
        });
    }
    public void loadFragment(Fragment fragment)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameMain,fragment);
        ft.commit();
    }
}