package com.example.photosandroid02;

import static com.example.photosandroid02.AlbumsView.currentAlbum;

import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.photosandroid02.models.Album;
import com.example.photosandroid02.models.Photo;

public class TagSearch extends AppCompatActivity {

    Button searchButton;

    RadioGroup firstTagSearch;
    RadioGroup conj;
    RadioGroup secondTagSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_search_view);
        searchButton = findViewById(R.id.search_button);
        firstTagSearch = findViewById(R.id.firstTagSearchGroup);
        conj = findViewById(R.id.conjunctionGroup);
        secondTagSearch = findViewById(R.id.secondTagTypeRadioGroup);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSearch();
            }
        });


    }

    private void handleSearch() {
        Album searchResults = new Album("Search Results");
        String firstTagType = firstTagSearch.getCheckedRadioButtonId() == R.id.radioButtonPerson ?
                "Person" : "Location";
        String conjunction = conj.getCheckedRadioButtonId() == R.id.radioButtonPerson ?
                "AND" : "OR";
        String secondTagType = secondTagSearch.getCheckedRadioButtonId() == R.id.radioButtonPerson ?
                "Person" : "Location";
    }
}
