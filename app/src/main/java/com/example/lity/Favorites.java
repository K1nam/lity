package com.example.lity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Favorites extends AppCompatActivity {

    private ListView favoritesListView;
    private List<String> favoritesUrls;
    private boolean isActivityRunning = false;
    private FavoriteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorites);
        setupBottomNavigation(R.id.navfavoritos);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        favoritesUrls = new ArrayList<>();

        favoritesListView = findViewById(R.id.favorites_list_view);
        adapter = new FavoriteAdapter(this, new ArrayList<>());
        favoritesListView.setAdapter(adapter);

        displayFavorites();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActivityRunning = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityRunning = false;
    }

    private void displayFavorites() {
        SharedPreferences prefs = getSharedPreferences("favorites_prefs", MODE_PRIVATE);
        Set<String> favoritesSet = prefs.getStringSet("favorites", new HashSet<>());

        favoritesUrls.addAll(favoritesSet);

        for (String url : favoritesUrls) {
            fetchPromotionDetails(url);
        }
    }

    private void fetchPromotionDetails(String url) {
        new Thread(() -> {
            try {
                Document doc = Jsoup.connect(url).get();
                Element titleElement = doc.selectFirst("title");
                Element imgElement = doc.selectFirst("meta[property=og:image]");

                String title = titleElement != null ? titleElement.text() : "Promoção sem título";
                String imageUrl = imgElement != null ? imgElement.attr("content") : "";

                if (!title.isEmpty()) {
                    runOnUiThread(() -> {
                        adapter.add(new FavoriteItem(title, imageUrl, url));
                        adapter.notifyDataSetChanged();
                    });
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

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

    private class FavoriteItem {
        String title;
        String imageUrl;
        String url;

        FavoriteItem(String title, String imageUrl, String url) {
            this.title = title;
            this.imageUrl = imageUrl;
            this.url = url;
        }
    }

    private class FavoriteAdapter extends ArrayAdapter<FavoriteItem> {
        FavoriteAdapter(Favorites context, List<FavoriteItem> favorites) {
            super(context, 0, favorites);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_favorite, parent, false);
            }

            FavoriteItem favoriteItem = getItem(position);

            TextView titleTextView = convertView.findViewById(R.id.favorite_title);
            ImageView imageView = convertView.findViewById(R.id.favorite_image);
            ImageView removeFavorite = convertView.findViewById(R.id.remove_favorite);

            titleTextView.setText(favoriteItem.title);
            Glide.with(getContext()).load(favoriteItem.imageUrl).into(imageView);

//clique na leixeira
            removeFavorite.setOnClickListener(v -> {
                removeFavoriteItem(position);
            });

            return convertView;
        }

        private void removeFavoriteItem(int position) {
//remove o item da lista
            FavoriteItem itemToRemove = getItem(position);
            if (itemToRemove != null) {
                favoritesUrls.remove(itemToRemove.url);

//atualiza o sharedpreferences
                updateFavoritesPrefs();

                remove(itemToRemove);
                notifyDataSetChanged();
            }
        }

        private void updateFavoritesPrefs() {
            SharedPreferences prefs = getContext().getSharedPreferences("favorites_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            Set<String> favoritesSet = new HashSet<>(favoritesUrls);
            editor.putStringSet("favorites", favoritesSet);
            editor.apply();
        }
    }
}
