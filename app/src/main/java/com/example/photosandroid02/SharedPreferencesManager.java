package com.example.photosandroid02;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.example.photosandroid02.models.Album;
import com.example.photosandroid02.models.Photo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.photosandroid02.TagSearch.locationSuggestions;
import static com.example.photosandroid02.TagSearch.personSuggestions;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SharedPreferencesManager {
    private static final String PREF_NAME = "MyAlbums";
    private static final String KEY_ALBUMS = "albums";

    public static void saveAlbums(Context context, List<Album> albums) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Convert albums to JSON strings and save to SharedPreferences
        JSONArray jsonArray = new JSONArray();
        for (Album album : albums) {
            JSONObject jsonAlbum = new JSONObject();
            try {
                jsonAlbum.put("name", album.getAlbumName());

                JSONArray jsonPhotos = new JSONArray();
                for (Photo photo : album.getPhotos()) {
                    JSONObject jsonPhoto = new JSONObject();
                    jsonPhoto.put("uri", photo.getImageUri().toString());
                    jsonPhoto.put("caption", photo.getCaption());

                    // Convert tags to JSON array
                    JSONArray jsonTags = new JSONArray();
                    for (Map.Entry<String, Set<String>> entry : photo.getTags().entrySet()) {
                        JSONObject jsonTag = new JSONObject();
                        jsonTag.put("type", entry.getKey());
                        JSONArray jsonValues = new JSONArray(entry.getValue());
                        jsonTag.put("values", jsonValues);
                        jsonTags.put(jsonTag);
                    }
                    jsonPhoto.put("tags", jsonTags);

                    jsonPhotos.put(jsonPhoto);
                }
                jsonAlbum.put("photos", jsonPhotos);

                jsonArray.put(jsonAlbum);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        editor.putString(KEY_ALBUMS, jsonArray.toString());
        editor.apply();
    }

    public static List<Album> loadAlbums(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String albumsJson = sharedPreferences.getString(KEY_ALBUMS, "");
        List<Album> albums = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(albumsJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonAlbum = jsonArray.getJSONObject(i);
                String albumName = jsonAlbum.getString("name");
                Album album = new Album(albumName);

                JSONArray jsonPhotos = jsonAlbum.getJSONArray("photos");
                for (int j = 0; j < jsonPhotos.length(); j++) {
                    JSONObject jsonPhoto = jsonPhotos.getJSONObject(j);
                    Uri imageUri = Uri.parse(jsonPhoto.getString("uri"));
                    String caption = jsonPhoto.getString("caption");

                    // Deserialize tags
                    Map<String, Set<String>> tags = new HashMap<>();
                    JSONArray jsonTags = jsonPhoto.getJSONArray("tags");
                    for (int k = 0; k < jsonTags.length(); k++) {
                        JSONObject jsonTag = jsonTags.getJSONObject(k);
                        String type = jsonTag.getString("type");
                        JSONArray jsonValues = jsonTag.getJSONArray("values");
                        Set<String> values = new HashSet<>();
                        for (int l = 0; l < jsonValues.length(); l++) {
                            values.add(jsonValues.getString(l));
                            locationSuggestions.add(jsonValues.getString(l));
                        }
                        tags.put(type, values);
                    }

                    Photo photo = new Photo(imageUri);
                    photo.setCaption(caption);
                    photo.getTags().putAll(tags);
                    album.getPhotos().add(photo);
                }
                albums.add(album);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return albums;
    }
}