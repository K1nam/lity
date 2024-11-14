package com.example.lity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InicioPromos extends AppCompatActivity {

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio_promos);
        setupBottomNavigation(R.id.navinicio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupPromotionLinks();
    }
//oromocoes de exemplo
    private void setupPromotionLinks() {

        TextView promo1 = findViewById(R.id.promo1);
        ImageButton heart1 = findViewById(R.id.heart1);
        ImageView promoImage1 = findViewById(R.id.promoImage1);
        setupPromotion(promo1, heart1, promoImage1, "https://www.kabum.com.br/produto/403847/playstation-vr2-oculos-de-realidade-virtual-para-ps5-branco-e-preto-1000032476");


        TextView promo2 = findViewById(R.id.promo2);
        ImageButton heart2 = findViewById(R.id.heart2);
        ImageView promoImage2 = findViewById(R.id.promoImage2);
        setupPromotion(promo2, heart2, promoImage2, "https://www.kabum.com.br/produto/444038/monitor-gamer-lg-ultragear-27-144hz-full-hd-1ms-ips-displayport-e-hdmi-hdr-10-99-srgb-freesync-premium-vesa-27gn65r");


        TextView promo3 = findViewById(R.id.promo3);
        ImageButton heart3 = findViewById(R.id.heart3);
        ImageView promoImage3 = findViewById(R.id.promoImage3);
        setupPromotion(promo3, heart3, promoImage3, "https://www.kabum.com.br/produto/633191/camera-gopro-hero-4k-12mp-a-prova-d-agua-ate-5m-preto-chdhf-131-at");
    }
//click listener para redirecionar a promocao e config do botao de salvar
    private void setupPromotion(TextView promoTextView, ImageButton heartButton, ImageView promoImageView, String url) {

        promoTextView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        fetchPromotionDetails(url, promoImageView, promoTextView);

        SharedPreferences prefs = getSharedPreferences("favorites_prefs", MODE_PRIVATE);
        Set<String> favorites = prefs.getStringSet("favorites", new HashSet<>());
        if (favorites.contains(url)) {
            heartButton.setImageResource(R.drawable.ic_heart_filled);
        } else {
            heartButton.setImageResource(R.drawable.ic_heart_empty);
        }

        heartButton.setOnClickListener(v -> {
            if (favorites.contains(url)) {
                removeFromFavorites(url, heartButton);
            } else {
                addToFavorites(url, heartButton);
            }
        });
    }
//metodo para pegar imagens/titulos dos links
    private void fetchPromotionDetails(String url, ImageView promoImageView, TextView promoTextView) {
        executorService.execute(() -> {
            try {
                Document doc = Jsoup.connect(url).get();
                Element imgElement = doc.selectFirst("meta[property=og:image]");
                Element titleElement = doc.selectFirst("title");
                String imageUrl = imgElement != null ? imgElement.attr("content") : null;
                String title = titleElement != null ? titleElement.text() : null;
                mainHandler.post(() -> {
                    if (imageUrl != null) {
                        Glide.with(InicioPromos.this).load(imageUrl).into(promoImageView);
                    }
                    if (title != null) {
                        promoTextView.setText(title);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
//salvar nos favoritos usando shared preferences
    private void addToFavorites(String promoUrl, ImageButton heartButton) {
        SharedPreferences prefs = getSharedPreferences("favorites_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Set<String> favorites = prefs.getStringSet("favorites", new HashSet<>());
        favorites.add(promoUrl);

        editor.putStringSet("favorites", favorites);
        editor.apply();

        heartButton.setImageResource(R.drawable.ic_heart_filled);
        Toast.makeText(this, "Promoção salva nos favoritos!", Toast.LENGTH_SHORT).show();
    }

    private void removeFromFavorites(String promoUrl, ImageButton heartButton) {
        SharedPreferences prefs = getSharedPreferences("favorites_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Set<String> favorites = prefs.getStringSet("favorites", new HashSet<>());
        favorites.remove(promoUrl);

        editor.putStringSet("favorites", favorites);
        editor.apply();

        heartButton.setImageResource(R.drawable.ic_heart_empty);
        Toast.makeText(this, "Promoção removida dos favoritos.", Toast.LENGTH_SHORT).show();
    }
//config bottomnav
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
