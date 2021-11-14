package com.example.memories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
    public ArrayList<Uri> imageList = new ArrayList<Uri>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        folderName = this.getIntent().getStringExtra("folderName");
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
            ShowImagesFromFolderActivity.this.addImagetoGrid(imageUri, true);
        }
    }
    public void initGallery(){
        int n = imageList.size();
        for(int i=0;i<n;i++){
            this.addImagetoGrid(imageList.get(i),false);
        }
    }
    public void addImagetoGrid(Uri imageUri, boolean saveOnStorage){
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
        if(saveOnStorage){
            activity.imageList.add(imageUri);
            activity.saveImagesOnStorage();
        }

    }
    public String textInFileImagePath()  {
        String pathList = "";
        int n = imageList.size();
        for(int i=0;i<n;i++){
            pathList+=imageList.get(i).getPath()+"\n";
        }
        return pathList;
    }
    public void saveImagesOnStorage(){
        String text = this.textInFileImagePath();
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(folderName+".txt", MODE_PRIVATE);
            fos.write(text.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void loadImagesFromStorage(){
        FileInputStream fis = null;
        try {
            fis = openFileInput(folderName+".txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            while ((text = br.readLine()) != null) {
                imageList.add(Uri.fromFile(new File(text)));

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



}