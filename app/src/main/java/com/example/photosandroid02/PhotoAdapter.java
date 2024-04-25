package com.example.photosandroid02;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photosandroid02.models.Album;
import com.example.photosandroid02.models.Photo;
import java.io.IOException;
import java.util.List;

import com.example.photosandroid02.PhotoView;

import static com.example.photosandroid02.AlbumsView.albums;
import static com.example.photosandroid02.AlbumsView.currentAlbum;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    public interface OnPhotoLongClickListener {
        void onPhotoLongClicked(int position);
    }

    private final List<Photo> photos;
    private LayoutInflater inflater;
    private final Context context;
    private static OnPhotoLongClickListener listener;

    private int longPressedItemPosition;

    public static Photo currentPhoto;
    public PhotoAdapter(Context context, List<Photo> photos) {
        this.photos = photos;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        //ImageView imageView = new ImageView(context);
        //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        View view = inflater.inflate(R.layout.photo_item, parent, false);
        // Set layout parameters as needed
        return new ViewHolder(view);
    }

    private int selectedPos = RecyclerView.NO_POSITION;
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Photo photo = photos.get(position);
        holder.imageView.setImageURI(photo.getImageUri());
        holder.itemView.setSelected(selectedPos == position);

        // Handle click event
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((PhotoView) context).isDeleteMode) {
                    // In delete mode, select the photo and show deletion option
                    selectedPos = position;
                    notifyDataSetChanged();  // Highlight the selected item
                    ((PhotoView) context).selectedPhoto = photo;  // Update the selected photo
                    if(context instanceof PhotoView) {
                        ((PhotoView) context).deletePhoto(photo);
                    }
                } else {
                    // Normal mode, open photo view or perform other actions
                    // Open full-screen photo activity
                    Intent intent = new Intent(context, FullScreenPhotoActivity.class);
                    intent.putExtra("photoUri", photo.getImageUri().toString());
                    context.startActivity(intent);
                }
            }
        });

        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    listener.onPhotoLongClicked(holder.getAdapterPosition()); // Notify listener
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.photoImageView);

            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onPhotoLongClicked(position); // Notify listener
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    public void setOnPhotoLongClickListener(OnPhotoLongClickListener listener) {
        PhotoAdapter.listener = listener;
    }

    public void movePhotoToAlbum(int album) {
        if (currentAlbum != null && currentAlbum.getPhotos() != null && !currentAlbum.getPhotos().isEmpty()) {
            if (longPressedItemPosition >= 0 && longPressedItemPosition < currentAlbum.getPhotos().size()) {
                Photo p = currentAlbum.getPhotos().get(longPressedItemPosition);
                if (albums != null && albums.size() > album && albums.get(album) != null) {
                    albums.get(album).getPhotos().add(p);
                    currentAlbum.getPhotos().remove(p); // Remove the photo from the current album
                    notifyDataSetChanged();
                }
            }
        }
    }
}