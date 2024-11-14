package com.example.lity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TelaPerfil extends AppCompatActivity {

    private AppCompatButton bt_deslogar;
    private ImageView settings;

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
        iniciarcomponentes();

        bt_deslogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(TelaPerfil.this, FormLogin.class);
                startActivity(intent);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(TelaPerfil.this, Settings.class);
                startActivity(intent);
            }
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
    private void iniciarcomponentes(){
        settings = findViewById(R.id.settings);
        bt_deslogar = findViewById(R.id.bt_deslogar);
    }
}