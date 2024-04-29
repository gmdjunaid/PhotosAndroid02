package com.example.photosandroid02;

import static com.example.photosandroid02.AlbumsView.adapter;
import static com.example.photosandroid02.AlbumsView.albums;
import static com.example.photosandroid02.AlbumsView.currentAlbum;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.photosandroid02.models.Album;
import com.example.photosandroid02.models.Photo;

import java.util.ArrayList;
import java.util.List;

public class TagSearch extends AppCompatActivity {

    Button searchButton;

    RadioGroup firstTagSearch;
    RadioGroup conj;
    RadioGroup secondTagSearch;

    AutoCompleteTextView firstTextView;

    AutoCompleteTextView secondTextView;

    public static List<String> locationSuggestions = new ArrayList<>();

    public static List<String> personSuggestions = new ArrayList<>();
    ArrayAdapter<String> locationAdapter;
    ArrayAdapter<String> personAdapter;
    public static ArrayAdapter<String> suggestionsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_search_view);
        searchButton = findViewById(R.id.search_button);
        firstTagSearch = findViewById(R.id.firstTagSearchGroup);
        conj = findViewById(R.id.conjunctionGroup);
        secondTagSearch = findViewById(R.id.secondTagTypeRadioGroup);
        firstTextView = findViewById(R.id.firstTagValue);
        secondTextView = findViewById(R.id.secondTagValue);

        if (locationSuggestions == null) {
            locationSuggestions = new ArrayList<>();
        }
        if (personSuggestions == null) {
            personSuggestions = new ArrayList<>();
        }


        List<String> allSuggestions = new ArrayList<>();
        allSuggestions.addAll(locationSuggestions);
        allSuggestions.addAll(personSuggestions);

        // Create ArrayAdapter with combined suggestions
        suggestionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, allSuggestions);

        // Set adapter for both AutoCompleteTextViews
        firstTextView.setAdapter(suggestionsAdapter);
        secondTextView.setAdapter(suggestionsAdapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSearch();
            }
        });


    }

    private void handleSearch() {
        Album searchResults = new Album("Search Results");

        String firstTagType = getSelectedTagType(firstTagSearch);
        String conjunction = getSelectedConjunction(conj);
        String secondTagType = getSelectedTagType(secondTagSearch);

        // Retrieve the text entered by the user in the AutoCompleteTextView fields
        String firstTagValue = firstTextView.getText().toString().trim();
        String secondTagValue = secondTextView.getText().toString().trim();

        if (conjunction != null || secondTagType != null || secondTagValue != null) {
            if (conjunction.equalsIgnoreCase("and")) {
                for (Album a : albums) {
                    for (Photo p : a.getPhotos()) {
                        if (p.hasTag(firstTagType ,firstTagValue) && p.hasTag(secondTagType, secondTagValue)) {
                            searchResults.getPhotos().add(p);
                        }
                    }
                }
            }
            if (conjunction.equalsIgnoreCase("or")) {
                for (Album a : albums) {
                    for (Photo p : a.getPhotos()) {
                        if (p.hasTag(firstTagType ,firstTagValue) || p.hasTag(secondTagType, secondTagValue)) {
                            searchResults.getPhotos().add(p);
                        }
                    }
                }
            }
        } else if (firstTagType != null && firstTagValue != null) {
            for (Album a : albums) {
                for (Photo p : a.getPhotos()) {
                    if (p.hasTag(firstTagType ,firstTagValue)) {
                        searchResults.getPhotos().add(p);
                    }
                }
            }
        }

        if (!searchResults.getPhotos().isEmpty()) {
            Intent intent = new Intent(TagSearch.this, PhotoView.class);
            currentAlbum = searchResults;
            intent.putExtra("searchResults", searchResults);
            startActivity(intent);
        } else {
            Toast.makeText(TagSearch.this, "No results found for this search.", Toast.LENGTH_LONG).show();
        }
    }

    private String getSelectedTagType(RadioGroup radioGroup) {
        int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        if (checkedRadioButtonId == R.id.firstTagTypeP || checkedRadioButtonId == R.id.secondTagTypeP) {
            return "Person";
        } else if (checkedRadioButtonId == R.id.firstTagTypeL || checkedRadioButtonId == R.id.secondTagTypeL) {
            return "Location";
        }
        return null;
    }

    private String getSelectedConjunction(RadioGroup radioGroup) {
        int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        if (checkedRadioButtonId == R.id.andConj) {
            return "AND";
        } else {
            return "OR";
        }
    }

    /*private void handleSearch() {
        Album searchResults = new Album("Search Results");
        String firstTagType = firstTagSearch.getCheckedRadioButtonId() == R.id.radioButtonPerson ?
                "Person" : "Location";
        String conjunction = conj.getCheckedRadioButtonId() == R.id.radioButtonPerson ?
                "AND" : "OR";
        String secondTagType = secondTagSearch.getCheckedRadioButtonId() == R.id.radioButtonPerson ?
                "Person" : "Location";
    }*/
}
