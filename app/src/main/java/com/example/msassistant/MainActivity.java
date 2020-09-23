package com.example.msassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.msassistant.FragmentController.Fragment_About;
import com.example.msassistant.FragmentController.Fragment_Emergency;
import com.example.msassistant.FragmentController.Fragment_Home;
import com.example.msassistant.FragmentController.Fragment_Profile;
import com.example.msassistant.FragmentController.Fragment_Report;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        onNavigationItemSelected(navigationView.getMenu().getItem(0));
        navigationView.getMenu().getItem(0).setChecked(true);
        View headerView = navigationView.getHeaderView(0);
        TextView riderName = headerView.findViewById(R.id.txt_riderName);
        ImageView riderPhoto = headerView.findViewById(R.id.profile_image);
        riderName.setText(currentUser.getDisplayName());
        Glide.with(this).load(String.valueOf(currentUser.getPhotoUrl())).into(riderPhoto);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        switch (id) {
            case R.id.nav_home:
                ft.replace(R.id.content_frame, new Fragment_Home());
                ft.commit();
                break;

            case R.id.nav_emergency:
                ft.replace(R.id.content_frame, new Fragment_Emergency());
                ft.commit();
                break;

            case R.id.nav_profile:
                ft.replace(R.id.content_frame, new Fragment_Profile());
                ft.commit();
                break;

            case R.id.nav_about:
                ft.replace(R.id.content_frame, new Fragment_About());
                ft.commit();
                break;

            case R.id.nav_report:
                ft.replace(R.id.content_frame, new Fragment_Report());
                ft.commit();
                break;

            case R.id.nav_logout:

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));

                break;
        }



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}