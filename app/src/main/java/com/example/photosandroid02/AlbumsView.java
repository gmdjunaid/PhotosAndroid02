package com.example.photosandroid02;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.photosandroid02.models.Album;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class AlbumsView extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSION = 101;
    ArrayList<Album> albums = new ArrayList<>();
    ArrayAdapter<Album> adapter;
    ListView listView;
    ImageButton searchButton;

    public static Album currentAlbum;

    private int longPressedItemPosition;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_list);
        fab = findViewById(R.id.addAlbumBtn);
        listView = (ListView) findViewById(R.id.album_list_view);
        searchButton = findViewById(R.id.search_button);
        adapter = new ArrayAdapter<Album>(this, R.layout.activity_albumview, R.id.album, albums);
        listView.setAdapter(adapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestStoragePermission();
            } else {
                // Permission already granted
                // You can proceed with your logic here
            }
        } else {
            // For SDK versions below 23, permission is granted at installation time
            // You can proceed with your logic here
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAlbumWithDialog();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item from the adapter
                Album selectedAlbum = (Album) parent.getItemAtPosition(position);
                currentAlbum = selectedAlbum;

                // Launch a new activity with details about the selected album
                Intent intent = new Intent(AlbumsView.this, PhotoView.class);
                intent.putExtra("album", selectedAlbum); // Pass the selected album to the new activity
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                registerForContextMenu(listView);
                longPressedItemPosition = position;
                return false;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AlbumsView.this, "Clicked Search", Toast.LENGTH_LONG).show();
            }
        });

    }

    // Handles the context menu when long pressing an album.
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.album_context_menu, menu);
    }

    // Adds an album to the list
    private void addAlbumWithDialog() {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Album Name");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String albumName = input.getText().toString();
                if (!albumName.isEmpty()) {
                    addAlbum(albumName);
                } else {
                    Toast.makeText(AlbumsView.this, "Album name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Shows the menu when long pressing an album.
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_rename) {
            renameAlbum();
            return true;
        } else if (itemId == R.id.menu_delete) {
            deleteAlbum();
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }

    // Handles renaming the album.
    private void renameAlbum() {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(albums.get(longPressedItemPosition).getAlbumName());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rename Album");
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newAlbumName = input.getText().toString();
                albums.get(longPressedItemPosition).setAlbumName(newAlbumName);
                if (!newAlbumName.isEmpty()) {
                    albums.set(longPressedItemPosition, albums.get(longPressedItemPosition));
                    adapter.notifyDataSetChanged();
                    Toast.makeText(AlbumsView.this, "Album renamed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AlbumsView.this, "Album name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Handles deleting the album.
    private void deleteAlbum() {
        albums.remove(longPressedItemPosition);
        adapter.notifyDataSetChanged();
        Toast.makeText(AlbumsView.this, "Album deleted", Toast.LENGTH_SHORT).show();
    }


    // Handles Adding an album.
    private void addAlbum(String newAlbum) {
        Album album = new Album(newAlbum);
        albums.add(album);
        adapter.notifyDataSetChanged();
        listView.setSelection(adapter.getCount() - 1);

        Toast.makeText(AlbumsView.this, "New album added: " + album.getAlbumName() + ". To rename or delete the album, simple long press it.", Toast.LENGTH_LONG).show();
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CODE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                // You can proceed with your logic here
            } else {
                // Permission denied
                // You may inform the user or show a rationale for needing the permission
                showPermissionDeniedDialog();
            }
        }
    }

    private void showPermissionDeniedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This app needs permission to access your photos. Please grant permission in settings.")
                .setCancelable(false)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        openAppSettings();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        // Handle the denial as per your app's logic
                        Toast.makeText(AlbumsView.this, "Permission denied. Cannot access photos.", Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
}