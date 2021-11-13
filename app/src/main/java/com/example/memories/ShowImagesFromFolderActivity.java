package com.example.memories;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ShowImagesFromFolderActivity extends AppCompatActivity {
    String folderName;
    ImageButton addButton, deleteButton;
    GridLayout foldersGridLayout;
    float density, dpHeight, dpWidth;
    int rowCount, columnCount, imageIconPxSize;
    private static final int PICK_IMAGE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_images_from_folder);
        addButton = findViewById(R.id.add_button);
        deleteButton = findViewById(R.id.delete_button);
        foldersGridLayout = findViewById(R.id.folder_grid);

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        density  = getResources().getDisplayMetrics().density;
        dpHeight = outMetrics.heightPixels / density;
        dpWidth  = outMetrics.widthPixels / density;
        rowCount =4;
        columnCount=4;
        imageIconPxSize = (int)(dpWidth/columnCount*density);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowImagesFromFolderActivity.this.openGallery();
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
            ShowImagesFromFolderActivity.this.addImagetoGrid(imageUri);
        }
    }
    public void addImagetoGrid(Uri imageUri){
        ShowImagesFromFolderActivity activity = ShowImagesFromFolderActivity.this;
        ImageView imageView = new ImageView(activity);
        imageView.setImageURI(imageUri);

        FrameLayout imageFrameLayout = new FrameLayout(activity);
        FrameLayout.LayoutParams paramsFrame = new FrameLayout.LayoutParams(activity.imageIconPxSize, activity.imageIconPxSize);
        FrameLayout.LayoutParams paramsImage = new FrameLayout.LayoutParams((int)(activity.imageIconPxSize*0.8),(int)(activity.imageIconPxSize*0.8));
        imageFrameLayout.setLayoutParams(paramsFrame);
        imageView.setLayoutParams(paramsImage);

        imageFrameLayout.addView(imageView);
        activity.foldersGridLayout.addView(imageFrameLayout);
    }
}