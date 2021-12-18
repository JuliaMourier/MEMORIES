package com.example.memories;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;

public class ImageFromStorage {
    public Bitmap imageBitmap;
    public String infoAboutImage;
    public ContentResolver contentResolver;
    public ImageFromStorage(Uri imageUri, String infoAboutImage_, ContentResolver contentResolver_){
        contentResolver = contentResolver_;
        setImageWithUri(imageUri, infoAboutImage_);
    }
    public void setImageWithUri(Uri imageUri, String infoAboutImage_){
        try {
            imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri);
            infoAboutImage = infoAboutImage_;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Bitmap getImage(){
        return imageBitmap;
    }
    public String getInfoAboutImage(){
        return infoAboutImage;
    }
}
