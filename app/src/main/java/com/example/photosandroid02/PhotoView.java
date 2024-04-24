package com.example.photosandroid02;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photosandroid02.models.Photo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static com.example.photosandroid02.AlbumsView.currentAlbum;


public class PhotoView extends AppCompatActivity {
    private static final int REQUEST_GET_PHOTO = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private RecyclerView photosRecyclerView;
    private PhotoAdapter photosAdapter;
    private List<Photo> photosList;

    private ImageView selectedPhotoImageView;

    private FloatingActionButton addPhotoBtn;
    public Photo selectedPhoto;
    protected boolean isDeleteMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos_view);

        photosList = currentAlbum.getPhotos();
        photosRecyclerView = findViewById(R.id.photosRecyclerView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //setupRecyclerView();
        photosAdapter = new PhotoAdapter(this, photosList);
        photosRecyclerView.setLayoutManager(new GridLayoutManager(PhotoView.this, 4));
        photosRecyclerView.setAdapter(photosAdapter);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setToolbarTitle(currentAlbum.getAlbumName());

        addPhotoBtn = findViewById(R.id.addPhotoBtn);
        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhoto();
            }
        });

        FloatingActionButton deletePhotoBtn = findViewById(R.id.deletePhotoBtn);
        deletePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDeleteMode = !isDeleteMode;  // Toggle delete mode
                if (isDeleteMode) {
                    Toast.makeText(PhotoView.this, "Select a photo to delete", Toast.LENGTH_SHORT).show();
                } else {
                    selectedPhoto = null;  // Clear selection when exiting delete mode
                    photosAdapter.notifyDataSetChanged();  // Refresh to clear any selections visually
                }
            }
        });
    }

    private void addPhoto() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_GET_PHOTO);
    }

    /*public void deletePhoto(View view) {
        // Assuming there is a method to get the selected photo from the adapter
        if (selectedPhoto != null) {
            photosList.remove(selectedPhoto);
            photosAdapter.notifyDataSetChanged();
            selectedPhotoImageView.setImageDrawable(null); // Clear the preview
            Toast.makeText(this, "Photo deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No photo selected", Toast.LENGTH_SHORT).show();
        }
    }*/
    public void deletePhoto(Photo photo) {
        if (photo != null) {
            int index = photosList.indexOf(photo);
            if (index != -1) {
                photosList.remove(index);
                photosAdapter.notifyItemRemoved(index);
                selectedPhoto = null;  // Clear the selection
                Toast.makeText(this, "Photo deleted", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No photo selected", Toast.LENGTH_SHORT).show();
        }
    }

    public void captionPhoto(View view) {
        if (selectedPhoto != null) {
            final EditText input = new EditText(this);
            input.setText(selectedPhoto.getCaption());

            new AlertDialog.Builder(this)
                    .setTitle("Caption Photo")
                    .setView(input)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            selectedPhoto.setCaption(input.getText().toString());
                            photosAdapter.notifyDataSetChanged(); // Update the view
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Do nothing.
                        }
                    })
                    .show();
        } else {
            Toast.makeText(this, "No photo selected", Toast.LENGTH_SHORT).show();
        }
    }

    // Implement other methods based on your requirements and logic

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GET_PHOTO && resultCode == RESULT_OK) {
            if (data != null) {
                Uri photoUri = data.getData();
                if (photoUri != null) {
                    // Persist permission across reboots.
                    getContentResolver().takePersistableUriPermission(
                            photoUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    // Add the new photo to the list and notify the adapter.
                    Photo newPhoto = new Photo(photoUri);
                    photosList.add(newPhoto);
                    photosAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void setToolbarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
