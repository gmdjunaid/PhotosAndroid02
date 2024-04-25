package com.example.photosandroid02;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ContextMenu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photosandroid02.models.Photo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static com.example.photosandroid02.AlbumsView.currentAlbum;
import static com.example.photosandroid02.AlbumsView.albums;


public class PhotoView extends AppCompatActivity implements PhotoAdapter.OnPhotoLongClickListener {
    private static final int REQUEST_GET_PHOTO = 1;
    private RecyclerView photosRecyclerView;
    private PhotoAdapter photosAdapter;
    private List<Photo> photosList;
    private FloatingActionButton addPhotoBtn;
    private int longPressedItemPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos_view);

        if (currentAlbum != null) {
            photosList = currentAlbum.getPhotos();
        }
        photosRecyclerView = findViewById(R.id.photosRecyclerView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //setupRecyclerView();
        photosAdapter = new PhotoAdapter(this, currentAlbum.getPhotos());
        photosRecyclerView.setLayoutManager(new GridLayoutManager(PhotoView.this, 4));
        photosRecyclerView.setAdapter(photosAdapter);

        photosAdapter.setOnPhotoLongClickListener(this);

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
    }

    @Override
    public void onPhotoLongClicked(int position) {
        // Handle the long click event on the photo at the specified position
        registerForContextMenu(photosRecyclerView);
        longPressedItemPosition = position;
        openContextMenu(photosRecyclerView);
    }

    private void addPhoto() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_GET_PHOTO);
    }

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


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.photo_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        Photo p = currentAlbum.getPhotos().get(longPressedItemPosition);
        if (itemId == R.id.menu_move) {
            // Handle move photo option
            showAlbumSelectionDialog();
            return true;
        } else if (itemId == R.id.menu_delete) {
            // Handle delete photo option
            currentAlbum.getPhotos().remove(p);
            photosAdapter.notifyDataSetChanged();
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }

    private void showAlbumSelectionDialog() {
        CharSequence[] albumNames = new CharSequence[albums.size()];
        for (int i = 0; i < albums.size(); i++) {
            albumNames[i] = albums.get(i).getAlbumName();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Move to");
        builder.setItems(albumNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Photo p = currentAlbum.getPhotos().get(longPressedItemPosition);
                photosAdapter.movePhotoToAlbum(which); // Move photo to the selected album
                currentAlbum.getPhotos().remove(p);
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
