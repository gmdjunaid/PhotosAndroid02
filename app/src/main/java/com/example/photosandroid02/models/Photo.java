package com.example.photosandroid02.models;

import android.net.Uri;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class represents a Photo in the Android application.
 */
public class Photo implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Uri imageUri; // URI to locate the image in storage
    private String caption; // Optional caption for the image
    private final Map<String, Set<String>> tags; // Tags associated with the photo

    public Photo(Uri imageUri) {
        this.imageUri = imageUri;
        this.caption = ""; // Initially, no caption
        this.tags = new HashMap<>(); // Initialize the tags map
    }

    // Getters and setters
    public Uri getImageUri() {
        return imageUri;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Map<String, Set<String>> getTags() {
        return tags;
    }

    // Add a tag to the photo
    public void addTag(String type, String value) {
        if (!tags.containsKey(type)) {
            tags.put(type, new HashSet<>());
        }
        tags.get(type).add(value);
    }

    // Remove a tag from the photo
    public void removeTag(String value) {
        for (Set<String> tagValues : tags.values()) {
            if (tagValues.contains(value)) {
                tagValues.remove(value);
                return; // Exit the loop once the tag value is removed
            }
        }
    }

    public String getTagType(String value) {
        for (Map.Entry<String, Set<String>> entry : tags.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    // Check if a photo has a specific tag
    public boolean hasTag(String type, String value) {
        return tags.containsKey(type) && tags.get(type).contains(value);
    }

    // Get a formatted string of all tags for display
    public String getFormattedTags() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Set<String>> entry : tags.entrySet()) {
            builder.append(entry.getKey()).append(": ");
            builder.append(String.join(", ", entry.getValue())).append("\n");
        }
        return builder.toString().trim();
    }
}

