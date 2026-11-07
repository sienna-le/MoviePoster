package com.example.poster1019;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The one place that owns the app's movie data: all posters, the selection, and the favorites.
 */
public class PosterRepository {

    // Notified whenever favorites are added or removed (Favorite List, badge).
    public interface FavoritesListener{
        void onFavoritesChanged();
    }

    private static PosterRepository instance;

    private final List<Posters> allPosters;
    private final FavoritesStore favoritesStore;
    private final List<FavoritesListener> favoritesListeners = new ArrayList<>();

    // Creates the single instance on first use. It keeps the application context,
    // never an Activity: holding an Activity in a singleton would leak it.
    public static synchronized PosterRepository getInstance(Context context) {
        if (instance == null) {
            instance = new PosterRepository(context.getApplicationContext());
        }
        return instance;
    }

    private PosterRepository(Context appContext) {
        allPosters = Collections.unmodifiableList(createPosters());
        favoritesStore = new FavoritesStore(appContext);

        // Forget saved favorites whose poster no longer exists in the app.
        List<String> validIds = new ArrayList<>();
        for (Posters poster : allPosters) {
            validIds.add(poster.id);
        }
        favoritesStore.retainOnly(validIds);
    }

    public List<Posters> getAllPosters(){
        return allPosters;
    }

    public Posters findById(String id){
        for(Posters posters:allPosters){
            if(posters.id.equals(id)){
                return posters;
            }
        }
        return null;
    }

    // deselects every poster
    public void clearSelection(){
        for(Posters poster : allPosters){
            poster.isSelected = false;
        }
    }

    //get favorite posters, in the order they were added
    public List<Posters> getFavorites(){
        List<Posters> favorites = new ArrayList<>();
        for (String id : favoritesStore.getIds()) {
            Posters poster = findById(id);
            if (poster != null) {
                favorites.add(poster);
            }
        }
        return favorites;
    }

    public int getFavoriteCount(){
        return favoritesStore.getIds().size();
    }

    // Adds posters to favorites, skipping ones already there.
    // Returns how many were new, so the page can show the right message.
    public int addFavorites(List<Posters> posters) {
        List<String> ids = new ArrayList<>();
        for (Posters poster : posters) {
            ids.add(poster.id);
        }
        int added = favoritesStore.addAll(ids);
        if (added > 0) {
            notifyFavoritesChanged();
        }
        return added;
    }

    // Removes one favorite. Returns true if it was a favorite.
    public boolean removeFavorite(String id) {
        boolean removed = favoritesStore.remove(id);
        if (removed) {
            notifyFavoritesChanged();
        }
        return removed;
    }

    // The listener (observer) pattern: pages register to hear about changes.
    // Whoever adds a listener must remove it again (onDestroyView / onDestroy).
    public void addFavoritesListener(FavoritesListener listener) {
        if (!favoritesListeners.contains(listener)) {
            favoritesListeners.add(listener);
        }
    }

    public void removeFavoritesListener(FavoritesListener listener) {
        favoritesListeners.remove(listener);
    }

    private void notifyFavoritesChanged() {
        for(FavoritesListener listener : new ArrayList<>(favoritesListeners)){
            listener.onFavoritesChanged();
        }
    }

    //set up movie posters and descriptions as a array of Posters
    //with 5 new movies to be implemented into poster fragment
    private static List<Posters> createPosters() {
        List<Posters> posters = new ArrayList<>();

        Posters godzilla = new Posters();
        godzilla.id = "godzilla";
        godzilla.image = R.drawable.poster1;
        godzilla.name = "Godzilla Minus Zero";
        godzilla.rating = 4f;
        godzilla.createdBy = "Takashi Yamazaki";
        godzilla.story = "2026 Japanese monster film.";
        posters.add(godzilla);

        Posters wildwood = new Posters();
        wildwood.id = "wildwood";
        wildwood.image = R.drawable.poster2;
        wildwood.name = "Wildwood";
        wildwood.rating = 5f;
        wildwood.createdBy = "Travis Knight";
        wildwood.story = "2026 American stop-motion animated dark fantasy film.";
        posters.add(wildwood);

        Posters cat = new Posters();
        cat.id = "cat";
        cat.image = R.drawable.poster3;
        cat.name = "The Cat in the Hat";
        cat.rating = 3f;
        cat.createdBy = "Erica Rivinoja, Alessandro Carloni";
        cat.story = "2026 animated theatrical feature film.";
        posters.add(cat);

        Posters drummer = new Posters();
        drummer.id = "drummer";
        drummer.image = R.drawable.poster4;
        drummer.name = "Drummer Boy";
        drummer.rating = 4f;
        drummer.createdBy = "Ben Smallbone, Joel David Smallbone";
        drummer.story = "2026 American historical musical drama film.";
        posters.add(drummer);

        Posters barbie = new Posters();
        barbie.id = "american";
        barbie.image = R.drawable.poster5;
        barbie.name = "Barbie as the Princess and the Pauper";
        barbie.rating = 5f;
        barbie.createdBy = "William Lau";
        barbie.story = "2004 computer-animated musical film.";
        posters.add(barbie);

        return posters;

    }


}
