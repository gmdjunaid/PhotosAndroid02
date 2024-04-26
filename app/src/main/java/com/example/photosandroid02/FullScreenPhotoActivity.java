package com.example.photosandroid02;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GestureDetectorCompat;

import androidx.appcompat.app.AppCompatActivity;

import static com.example.photosandroid02.AlbumsView.currentAlbum;
import static com.example.photosandroid02.PhotoAdapter.currentPhoto;

import com.example.photosandroid02.models.Photo;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class FullScreenPhotoActivity extends AppCompatActivity {
    private GestureDetectorCompat gestureDetector;

    private ImageView imageView;

    private TextView tagTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_photo_activity);

        // Set up gesture detector
        gestureDetector = new GestureDetectorCompat(this, new MyGestureListener());

        Toolbar toolbar = findViewById(R.id.back_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Photo Sequence");
        }


        // Add tag button
        Button addTagButton = findViewById(R.id.addTagButton);
        Button deleteTagButton = findViewById(R.id.deleteTagButton);
        tagTextView = findViewById(R.id.tagTextView);

        if (currentPhoto.getTags() != null) {
            tagTextView.setText(currentPhoto.getFormattedTags());
        }
        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle adding tag functionality
                showDialogToAddTag();

            }
        });

        deleteTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open dialog with list view for selecting tag to delete
                showDeleteTagDialog();
            }
        });

        // ImageView for displaying the photo
        imageView = findViewById(R.id.fullScreenImageView);
        String photoUriString = getIntent().getStringExtra("photoUri");
        if (photoUriString != null) {
            Uri photoUri = Uri.parse(photoUriString);
            imageView.setImageURI(photoUri);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        // Swipe right, navigate to previous photo
                        refreshImage(false);
                    } else {
                        // Swipe left, navigate to next photo
                        refreshImage(true);
                    }
                    return true;
                }
            }
            return false;
        }
    }

    private int currentInd(Photo p) {
        for (Photo photo : currentAlbum.getPhotos()) {
            if (photo.equals(p)) {
                return currentAlbum.getPhotos().indexOf(p);
            }
        }
        return -1;
    }

    private void refreshImage(boolean swipedLeft) {
        int curr = currentInd(currentPhoto);
        if (!swipedLeft && curr > 0) {  // swiping right
            Photo prev_photo = currentAlbum.getPhotos().get(curr-1);
            currentPhoto = prev_photo;
            Uri photoUri = prev_photo.getImageUri();
            imageView.setImageURI(photoUri);
            tagTextView.setText(currentPhoto.getFormattedTags());
        } else if (swipedLeft && curr < currentAlbum.getPhotos().size() - 1) {
            Photo next_photo = currentAlbum.getPhotos().get(curr+1);
            currentPhoto = next_photo;
            Uri photoUri = next_photo.getImageUri();
            imageView.setImageURI(photoUri);
            tagTextView.setText(currentPhoto.getFormattedTags());
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showDialogToAddTag() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_tag, null);
        builder.setView(dialogView);

        RadioGroup tagTypeRadioGroup = dialogView.findViewById(R.id.tagTypeRadioGroup);
        EditText tagValueEditText = dialogView.findViewById(R.id.tagValueEditText);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get selected tag type
                String tagType = tagTypeRadioGroup.getCheckedRadioButtonId() == R.id.radioButtonPerson ?
                        "Person" : "Location";

                // Get tag value
                String tagValue = tagValueEditText.getText().toString().trim();

                // Perform necessary actions with tag type and value
                // For example, you can display a Toast with the selected tag type and value
                if (tagType.equalsIgnoreCase("location")) {
                    if (currentPhoto.getTags().containsKey("Location")) {
                        Toast.makeText(FullScreenPhotoActivity.this, "A location tag already exists.", Toast.LENGTH_LONG).show();
                    } else {
                        currentPhoto.addTag(tagType, tagValue);
                    }
                } else {
                    currentPhoto.addTag(tagType, tagValue);
                }
                tagTextView.setText(currentPhoto.getFormattedTags());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDeleteTagDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Tag to Delete");

        // Convert map keys (tag names) to array for ArrayAdapter
        final Set<String> tagNames = currentPhoto.getTags().keySet();
        ArrayList<String> tagValues = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : currentPhoto.getTags().entrySet()) {
            Set<String> values = entry.getValue();
            tagValues.addAll(values);
        }

        String[] tagArray = tagValues.toArray(new String[0]);

        builder.setItems(tagArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Remove selected tag from map
                String deletedTagValue = tagArray[which];
                String deletedTagType = currentPhoto.getTagType(tagArray[which]);
                currentPhoto.removeTag(deletedTagValue);
                tagTextView.setText(currentPhoto.getFormattedTags());
            }
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}