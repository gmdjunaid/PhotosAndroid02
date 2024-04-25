package com.example.photosandroid02;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.photosandroid02.models.Photo;
import java.io.IOException;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private final List<Photo> photos;
    private LayoutInflater inflater;
    private final Context context;

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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Photo photo = photos.get(position);
        holder.imageView.setImageURI(photo.getImageUri());

        // Handle click event
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open full-screen photo activity
                Intent intent = new Intent(context, FullScreenPhotoActivity.class);
                intent.putExtra("photoUri", photo.getImageUri().toString());
                currentPhoto = photo;
                context.startActivity(intent);
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
        }
    }
}