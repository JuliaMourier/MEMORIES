package com.example.memories;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Map;

public class SelectedImages {
    public ArrayList<ImageFromStorage> imageList = new ArrayList<ImageFromStorage>();

    Context context;

    public SharedPreferences selectedImagesPreferences;
    public SharedPreferences selectedImagesDescriptionPreferences;
    public SharedPreferences.Editor selectedImagesEditor;
    public SharedPreferences.Editor selectedImagesDescriptionEditor;

    Map<String, String> selectedImagesURI;
    Map<String, String> selectedImagesDescription;

    public int getSelectedImagesNumber(){
        return imageList.size();
    }
    public SelectedImages(Context context){
        this.context=context;
        selectedImagesPreferences = context.getSharedPreferences("selectedImages", Context.MODE_PRIVATE);
        selectedImagesDescriptionPreferences = context.getSharedPreferences("selectedImagesDescription", Context.MODE_PRIVATE);
        selectedImagesEditor = selectedImagesPreferences.edit();
        selectedImagesDescriptionEditor = selectedImagesDescriptionPreferences.edit();

        loadSelectedImages();

    }
    public void loadSelectedImages(){
        selectedImagesURI = (Map<String, String>) selectedImagesPreferences.getAll();
        selectedImagesDescription = (Map<String, String>) selectedImagesDescriptionPreferences.getAll();
        ContentResolver contentResolver = context.getContentResolver();
        for(String imageId : selectedImagesURI.keySet()){
            Uri selectedImageUri = Uri.parse(selectedImagesURI.get(imageId));
            String selectedImageDescription;
            if(selectedImagesDescription.containsKey(imageId))
                selectedImageDescription = selectedImagesDescription.get(imageId);
            else
                selectedImageDescription = "";
            ImageFromStorage selectedImage = new ImageFromStorage(selectedImageUri, selectedImageDescription, contentResolver);
            imageList.add(selectedImage);
        }
    }

}
