package com.example.memories;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ShowImagesFromFolderActivity extends AppCompatActivity {
    String folderName;
    TextView folderNameTextView;
    ImageButton addButton, deleteButton, infoButton;
    GridLayout foldersGridLayout;
    float density, dpHeight, dpWidth;
    int rowCount, columnCount, imageIconPxSize;
    private static final int PICK_IMAGE = 100;
    private static final int LOAD_IMAGE = 99;

    public ArrayList<Uri> imageList = new ArrayList<Uri>();
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ArrayList<ImageButton> deleteButtonList = new ArrayList<>();
    ArrayList<ImageButton> infoButtonList = new ArrayList<>();
    boolean deleteButtonVisible = false;
    boolean infoButtonVisible = false;
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
        infoButton = findViewById(R.id.info_button);
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
                ShowImagesFromFolderActivity.this.setAllDeleteButtonVisibility(!deleteButtonVisible);
            }
        });
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowImagesFromFolderActivity.this.setAllInfoButtonVisibility(!infoButtonVisible);
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
        ImageButton infoImageButton = new ImageButton(this);
        ImageButton deleteImageButton = new ImageButton(this);

        infoButtonList.add(infoImageButton);
        deleteButtonList.add(deleteImageButton);

        infoImageButton.setVisibility(View.INVISIBLE);
        deleteImageButton.setVisibility(View.INVISIBLE);
        deleteImageButton.setBackgroundResource(R.drawable.minus);
        infoImageButton.setBackgroundResource(R.drawable.info);
        FrameLayout imageFrameLayout = new FrameLayout(activity);
        FrameLayout.LayoutParams paramsFrame = new FrameLayout.LayoutParams(activity.imageIconPxSize, activity.imageIconPxSize);
        FrameLayout.LayoutParams paramsDelete = new FrameLayout.LayoutParams((int)(activity.imageIconPxSize*0.2),(int)(activity.imageIconPxSize*0.2));
        FrameLayout.LayoutParams paramsInfo = new FrameLayout.LayoutParams((int)(activity.imageIconPxSize*0.2),(int)(activity.imageIconPxSize*0.2));
        FrameLayout.LayoutParams paramsImage = new FrameLayout.LayoutParams((int)(activity.imageIconPxSize*0.8),(int)(activity.imageIconPxSize*0.8));
        paramsDelete.gravity = Gravity.TOP|Gravity.RIGHT;
        paramsInfo.gravity = Gravity.TOP|Gravity.LEFT;
        paramsImage.gravity = Gravity.CENTER_HORIZONTAL;

        infoImageButton.setLayoutParams(paramsInfo);
        imageFrameLayout.setLayoutParams(paramsFrame);
        deleteImageButton.setLayoutParams(paramsDelete);
        imageView.setLayoutParams(paramsImage);

        imageFrameLayout.addView(infoImageButton);
        imageFrameLayout.addView(deleteImageButton);
        imageFrameLayout.addView(imageView);

        activity.foldersGridLayout.addView(imageFrameLayout);
        infoImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowImagesFromFolderActivity.this);
                String defaultText = preferences.getString(Integer.toString(imageId)+"_comment",null);
                EditText commentEditText = new EditText(ShowImagesFromFolderActivity.this);
                commentEditText.setGravity(Gravity.START);
                commentEditText.setMaxLines(10);
                commentEditText.setMinLines(10);
                if(defaultText!=null){
                    commentEditText.setText(defaultText);
                }
                builder.setView(commentEditText)
                        .setCancelable(false)
                        .setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String commentText = commentEditText.getText().toString();
                                if(!commentText.equals("")){
                                    editor.putString(Integer.toString(imageId)+"_comment",commentText);
                                    editor.apply();
                                }

                            }
                        })
                        .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                ShowImagesFromFolderActivity.this.setAllDeleteButtonVisibility(false);
                            }
                        });
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Modifier la description");
                alert.getWindow().getAttributes().windowAnimations=R.style.MyDialogAnimation;
                alert.show();
            }
        });
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
                                editor.remove(Integer.toString(imageId)+"_comment");
                                editor.apply();
                            }
                        })
                        .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                ShowImagesFromFolderActivity.this.setAllDeleteButtonVisibility(false);
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
    public void setAllDeleteButtonVisibility(boolean visibilityBool){
        int n = deleteButtonList.size();
        int visibility;
        if(visibilityBool)
            visibility = View.VISIBLE;
        else
            visibility = View.INVISIBLE;
        for(int i=0;i<n;i++){
            deleteButtonList.get(i).setVisibility(visibility);
        }
        deleteButtonVisible = visibilityBool;
    }
    public void setAllInfoButtonVisibility(boolean visibilityBool){
        int n = infoButtonList.size();
        int visibility;
        if(visibilityBool)
            visibility = View.VISIBLE;
        else
            visibility = View.INVISIBLE;
        for(int i=0;i<n;i++){
            infoButtonList.get(i).setVisibility(visibility);
        }
        infoButtonVisible = visibilityBool;
    }




}