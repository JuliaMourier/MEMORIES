package com.example.memories;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShowImagesFromFolderActivity extends AppCompatActivity {
    String folderName;
    TextView folderNameTextView;
    ImageButton addButton, deleteButton, infoButton, playButton;
    GridLayout foldersGridLayout;
    LinearLayout principalLayout;
    float density, dpHeight, dpWidth;
    int rowCount, columnCount, imageIconPxSize;
    private static final int PICK_IMAGE = 100;
    private static final int LOAD_IMAGE = 99;

    public Map<Integer, Uri> imageList = new HashMap<Integer, Uri>();
    SharedPreferences preferences, selectedImagesPreferences, selectedImagesDescriptionPreferences;
    SharedPreferences.Editor editor, selectedImagesEditor, selectedImagesDescriptionEditor;
    ArrayList<ImageButton> deleteButtonList = new ArrayList<>();
    ArrayList<ImageButton> infoButtonList = new ArrayList<>();
    ArrayList<ImageView> selectedImageView = new ArrayList<>();
    ArrayList<Integer> selectedImageID = new ArrayList<>();

    boolean deleteButtonVisible = false;
    boolean infoButtonVisible = false;
    boolean selectionMode;
    int nbCards, selectedCards;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        folderName = this.getIntent().getStringExtra("folderName");
        selectionMode = this.getIntent().getBooleanExtra("selectionMode",false);
        nbCards = this.getIntent().getIntExtra("nbCards",0);
        preferences = getSharedPreferences(folderName, MODE_PRIVATE);
        selectedImagesPreferences = getSharedPreferences("selectedImages", MODE_PRIVATE);
        selectedImagesDescriptionPreferences = getSharedPreferences("selectedImagesDescription", MODE_PRIVATE);
        editor = preferences.edit();
        selectedImagesEditor = selectedImagesPreferences.edit();
        selectedImagesDescriptionEditor = selectedImagesDescriptionPreferences.edit();
        setContentView(R.layout.activity_show_images_from_folder);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(folderName);
        selectedCards=selectedImagesPreferences.getAll().size();
        if(selectionMode)
            toolbar.setTitle("Images sélectionnées "+selectedCards+"/"+nbCards+" - "+folderName);
        principalLayout = findViewById(R.id.principal_layout);
        addButton = findViewById(R.id.add_button);
        deleteButton = findViewById(R.id.delete_button);
        infoButton = findViewById(R.id.info_button);
        playButton = findViewById(R.id.play_button);
        foldersGridLayout = findViewById(R.id.folder_grid);

        this.loadSelectedImages();
        this.loadImagesFromStorage();
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        density  = getResources().getDisplayMetrics().density;
        dpHeight = outMetrics.heightPixels / density;
        dpWidth  = outMetrics.widthPixels / density;
        rowCount =4;
        columnCount=3;
        imageIconPxSize = (int)(dpWidth/columnCount*density);
        this.initGallery();
        if(selectionMode){
            addButton.setVisibility(View.INVISIBLE);
            deleteButton.setVisibility(View.INVISIBLE);
            infoButton.setVisibility(View.INVISIBLE);
            playButton.setVisibility(View.VISIBLE);

        }
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedCards==nbCards){
                    Intent memoriesGame = new Intent(ShowImagesFromFolderActivity.this, GameActivity.class);
                    memoriesGame.putExtra("nbCards", nbCards);
                    ShowImagesFromFolderActivity.this.startActivity(memoriesGame);
                }
            }
        });
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
            int i =0;
            while(imageList.keySet().contains(i)){
                i++;
            }
            ShowImagesFromFolderActivity.this.addImagetoGrid(imageUri, true, i);
        }
    }
    public void initGallery(){
        for(int i : imageList.keySet()){
            Uri imageUri = imageList.get(i);
            this.addImagetoGrid(imageUri,false, i);
        }
    }
    public static int getOrientation(Context context, Uri photoUri) {
        /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }
    public  Bitmap getCorrectlyOrientedImage(Context context, Uri photoUri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(context, photoUri);

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        int MAX_IMAGE_DIMENSION = imageIconPxSize;
        if (rotatedWidth > MAX_IMAGE_DIMENSION || rotatedHeight > MAX_IMAGE_DIMENSION) {
            float widthRatio = ((float) rotatedWidth) / ((float) MAX_IMAGE_DIMENSION);
            float heightRatio = ((float) rotatedHeight) / ((float) MAX_IMAGE_DIMENSION);
            float maxRatio = Math.max(widthRatio, heightRatio);

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        is.close();

        /*
         * if the orientation is not 0 (or -1, which means we don't know), we
         * have to do a rotation.
         */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }

        return srcBitmap;
    }
    public int addImagetoGrid(Uri imageUri, boolean saveOnStorage, int imageId){
        ShowImagesFromFolderActivity activity = ShowImagesFromFolderActivity.this;
        Bitmap bitmap = null;
        ImageView imageView = new ImageView(activity);
        try {
            //bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            //Bitmap bitmapScaled = scaleDown(bitmap, this.imageIconPxSize, true);
            bitmap = getCorrectlyOrientedImage(this, imageUri);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            editor.remove(Integer.toString(imageId));
            editor.remove(Integer.toString(imageId)+"_comment");
            editor.remove(Integer.toString(imageId)+"_selected");
            selectedImagesEditor.remove(folderName+"_"+Integer.toString(imageId));
            selectedImagesDescriptionEditor.remove(folderName+"_"+Integer.toString(imageId));
            editor.apply();
            selectedImagesEditor.apply();
            selectedImagesDescriptionEditor.apply();
            return -1;

        }
        //imageView.setImageURI(imageUri);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
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
        FrameLayout.LayoutParams paramsImage = new FrameLayout.LayoutParams((int)(activity.imageIconPxSize*0.95),(int)(activity.imageIconPxSize*0.95));
        paramsDelete.gravity = Gravity.TOP|Gravity.RIGHT;
        paramsInfo.gravity = Gravity.TOP|Gravity.LEFT;
        paramsImage.gravity = Gravity.CENTER_HORIZONTAL;
        if(selectionMode){
            if(selectedImageID.contains(imageId)){
                imageView.setColorFilter(Color.argb(90, 0, 0, 255));
                selectedImageView.add(imageView);
            }
            imageView.setOnTouchListener(new View.OnTouchListener() {
                private Rect rect;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        if(!selectedImageView.contains(imageView) & selectedCards<nbCards){
                            imageView.setColorFilter(Color.argb(90, 0, 0, 255));
                            rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                            selectedImageView.add(imageView);
                            editor.putString(Integer.toString(imageId)+"_selected","image selected");
                            editor.apply();
                            selectedImagesEditor.putString(folderName+"_"+Integer.toString(imageId),imageUri.toString());
                            selectedImagesEditor.apply();
                            String selectedImageDescription = preferences.getString(Integer.toString(imageId)+"_comment",null);
                            if(selectedImageDescription!=null){
                                selectedImagesDescriptionEditor.putString(folderName+"_"+Integer.toString(imageId),selectedImageDescription);
                                selectedImagesDescriptionEditor.apply();
                            }
                            getCurrentSelectedImagesNumber();
                        }
                        else{
                            selectedImageView.remove(imageView);
                            imageView.clearColorFilter();
                            editor.remove(Integer.toString(imageId)+"_selected");
                            editor.apply();
                            selectedImagesEditor.remove(folderName+"_"+Integer.toString(imageId));
                            selectedImagesEditor.apply();
                            String selectedImageDescription = preferences.getString(Integer.toString(imageId),null);
                            if(selectedImageDescription!=null){
                                selectedImagesDescriptionEditor.remove(folderName+"_"+Integer.toString(imageId));
                                selectedImagesDescriptionEditor.apply();
                            }
                            getCurrentSelectedImagesNumber();
                        }
                        return false;
                    }
                    return false;
                }
            });
        }
        infoImageButton.setLayoutParams(paramsInfo);
        imageFrameLayout.setLayoutParams(paramsFrame);
        deleteImageButton.setLayoutParams(paramsDelete);
        imageView.setLayoutParams(paramsImage);
        imageFrameLayout.addView(imageView);
        imageFrameLayout.addView(infoImageButton);
        imageFrameLayout.addView(deleteImageButton);


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
                                imageList.remove(imageId);
                                Log.d("TAG","Id supprimé "+Integer.toString(imageId));
                                editor.remove(Integer.toString(imageId));
                                editor.remove(Integer.toString(imageId)+"_comment");
                                editor.remove(Integer.toString(imageId)+"_selected");
                                selectedImagesEditor.remove(folderName+"_"+Integer.toString(imageId));
                                selectedImagesDescriptionEditor.remove(folderName+"_"+Integer.toString(imageId));
                                editor.apply();
                                selectedImagesEditor.apply();
                                selectedImagesDescriptionEditor.apply();
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
            activity.imageList.put(imageId,imageUri);
            activity.saveImagesOnStorage();
        }
    return 1;
    }
    public void saveImagesOnStorage(){
        for(int i : imageList.keySet()){
            editor.putString(Integer.toString(i),imageList.get(i).toString());
        }
        editor.apply();
    }
    public void loadImagesFromStorage(){
        String uriText="a";
        int i = 0;
        for(String keyMap : preferences.getAll().keySet()){
            if(isNumeric(keyMap)){
                uriText = preferences.getString(keyMap,null);
                imageList.put(Integer.parseInt(keyMap),Uri.parse(uriText));
            }
        }
    }
    public void loadSelectedImages(){
        String imageViewId="";
        int i = 0;
        int n = preferences.getAll().size();
        while(i<n){
            imageViewId = preferences.getString(Integer.toString(i)+"_selected",null);
            if(imageViewId!=null){
                selectedImageID.add(i);
            }
            i++;
        }
    }
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
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
    public void getCurrentSelectedImagesNumber(){
        selectedCards = this.selectedImagesPreferences.getAll().size();
        toolbar.setTitle("Images sélectionnées "+selectedCards+"/"+nbCards);
    }
    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }



}