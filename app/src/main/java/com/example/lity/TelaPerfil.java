package com.example.lity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TelaPerfil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_perfil);
        setupBottomNavigation(R.id.navperfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
//bottomnav troca telas
    protected void setupBottomNavigation(int currentActivity) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navinicio) {
                if (currentActivity != R.id.navinicio) {
                    startActivity(new Intent(this, InicioPromos.class));
                    finish();
                }
                return true;
            } else if (itemId == R.id.navfavoritos) {
                if (currentActivity != R.id.navfavoritos) {
                    startActivity(new Intent(this, Favorites.class));
                    finish();
                }
                return true;
            } else if (itemId == R.id.navperfil) {
                if (currentActivity != R.id.navperfil) {
                    startActivity(new Intent(this, TelaPerfil.class));
                    finish();
                }
                return true;
            } else {
                return false;
            }
        });
        bottomNavigationView.setSelectedItemId(currentActivity);
    }
}