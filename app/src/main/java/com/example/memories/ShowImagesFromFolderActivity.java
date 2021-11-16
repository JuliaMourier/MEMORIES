package com.example.memories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ShowImagesFromFolderActivity extends AppCompatActivity {
    String folderName;
    TextView folderNameTextView;
    ImageButton addButton, deleteButton;
    GridLayout foldersGridLayout;
    float density, dpHeight, dpWidth;
    int rowCount, columnCount, imageIconPxSize;
    private static final int PICK_IMAGE = 100;
    private static final int LOAD_IMAGE = 99;

    public ArrayList<Uri> imageList = new ArrayList<Uri>();
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ArrayList<ImageButton> deleteButtonList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        folderName = this.getIntent().getStringExtra("folderName");

        preferences = getSharedPreferences(folderName, MODE_PRIVATE);
        editor = preferences.edit();
        setContentView(R.layout.activity_show_images_from_folder);

        folderNameTextView = findViewById(R.id.folder_name);
        addButton = findViewById(R.id.add_button);
        deleteButton = findViewById(R.id.delete_button);
        foldersGridLayout = findViewById(R.id.folder_grid);

        folderNameTextView.setText(folderName);
        this.loadImagesFromStorage();
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        density  = getResources().getDisplayMetrics().density;
        dpHeight = outMetrics.heightPixels / density;
        dpWidth  = outMetrics.widthPixels / density;
        rowCount =4;
        columnCount=4;
        imageIconPxSize = (int)(dpWidth/columnCount*density);
        this.initGallery();
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowImagesFromFolderActivity.this.openGallery();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowImagesFromFolderActivity.this.setAllDeleteButtonVisible();
            }
        });



    }
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

        startActivityForResult(gallery, PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            Uri imageUri = data.getData();
            ShowImagesFromFolderActivity.this.addImagetoGrid(imageUri, true, imageList.size());
        }
    }
    public void initGallery(){
        int n = imageList.size();
        for(int i=0;i<n;i++){
            Uri imageUri = imageList.get(i);
            this.addImagetoGrid(imageUri,false, i);
        }
    }
    public void addImagetoGrid(Uri imageUri, boolean saveOnStorage, int imageId){
        ShowImagesFromFolderActivity activity = ShowImagesFromFolderActivity.this;

        ImageView imageView = new ImageView(activity);
        imageView.setImageURI(imageUri);
        ImageButton deleteImageButton = new ImageButton(this);
        deleteButtonList.add(deleteImageButton);
        deleteImageButton.setVisibility(View.INVISIBLE);
        deleteImageButton.setBackgroundResource(R.drawable.minus);
        FrameLayout imageFrameLayout = new FrameLayout(activity);
        FrameLayout.LayoutParams paramsFrame = new FrameLayout.LayoutParams(activity.imageIconPxSize, activity.imageIconPxSize);
        FrameLayout.LayoutParams paramsDelete = new FrameLayout.LayoutParams((int)(activity.imageIconPxSize*0.2),(int)(activity.imageIconPxSize*0.2));
        FrameLayout.LayoutParams paramsImage = new FrameLayout.LayoutParams((int)(activity.imageIconPxSize*0.8),(int)(activity.imageIconPxSize*0.8));
        paramsDelete.gravity = Gravity.TOP|Gravity.RIGHT;

        imageFrameLayout.setLayoutParams(paramsFrame);
        deleteImageButton.setLayoutParams(paramsDelete);
        imageView.setLayoutParams(paramsImage);

        imageFrameLayout.addView(deleteImageButton);
        imageFrameLayout.addView(imageView);

        activity.foldersGridLayout.addView(imageFrameLayout);
        deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowImagesFromFolderActivity.this);
                builder.setMessage("Confirmer la suppression ?")
                        .setCancelable(false)
                        .setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ShowImagesFromFolderActivity.this.foldersGridLayout.removeView(imageFrameLayout);
                                imageList.remove(imageUri);
                                editor.remove(Integer.toString(imageId));
                                editor.apply();
                            }
                        })
                        .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                ShowImagesFromFolderActivity.this.setAllDeleteButtonInvisible();
                            }
                        });
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Suppression d'une photo");
                alert.getWindow().getAttributes().windowAnimations=R.style.MyDialogAnimation;
                alert.show();
            }
        });
        if(saveOnStorage){
            activity.imageList.add(imageUri);
            activity.saveImagesOnStorage();
        }

    }
    public void saveImagesOnStorage(){
        int n = imageList.size();
        for(int i=0;i<n;i++){
            editor.putString(Integer.toString(i),imageList.get(i).toString());
        }
        editor.apply();
    }
    public void loadImagesFromStorage(){
        String uriText="a";
        int i = 0;
        while(uriText!=null){
            uriText = preferences.getString(Integer.toString(i),null);
            if(uriText!=null){
                imageList.add(Uri.parse(uriText));
                i++;
            }
        }
    }
    public void setAllDeleteButtonInvisible(){
        int n = deleteButtonList.size();
        for(int i=0;i<n;i++){
            deleteButtonList.get(i).setVisibility(View.INVISIBLE);
        }
    }
    public void setAllDeleteButtonVisible(){
        int n = deleteButtonList.size();
        for(int i=0;i<n;i++){
            deleteButtonList.get(i).setVisibility(View.VISIBLE);
        }
    }




}