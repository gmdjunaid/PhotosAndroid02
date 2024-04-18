package com.example.photosandroid02;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photosandroid02.models.Photo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class PhotoView extends AppCompatActivity {
    private static final int REQUEST_GET_PHOTO = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private RecyclerView photosRecyclerView;
    private PhotoAdapter photosAdapter;
    private List<Photo> photosList;
    private ImageView selectedPhotoImageView;

    private FloatingActionButton addPhotoBtn;
    private Photo selectedPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos_view);

        photosList = new ArrayList<>();
        selectedPhotoImageView = findViewById(R.id.selectedPhotoImageView);
        photosRecyclerView = findViewById(R.id.photosRecyclerView);

        setupRecyclerView();

        addPhotoBtn = findViewById(R.id.addPhotoBtn);
        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhoto();
            }
        });
    }

    private void setupRecyclerView() {
        photosRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        photosAdapter = new PhotoAdapter(this, photosList);
        photosRecyclerView.setAdapter(photosAdapter);
    }

    /*public void addPhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_GET_PHOTO);
    }*/
    private void addPhoto() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_GET_PHOTO);
    }

    public void deletePhoto(View view) {
        // Assuming there is a method to get the selected photo from the adapter
        if (selectedPhoto != null) {
            photosList.remove(selectedPhoto);
            photosAdapter.notifyDataSetChanged();
            selectedPhotoImageView.setImageDrawable(null); // Clear the preview
            Toast.makeText(this, "Photo deleted", Toast.LENGTH_SHORT).show();
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

}
