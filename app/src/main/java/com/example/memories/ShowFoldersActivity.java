package com.example.memories;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ShowFoldersActivity extends AppCompatActivity {
    private static final String FILE_NAME = "foldersName.txt";
    LinearLayout principalLayout;
    GridLayout foldersGridLayout;
    ImageButton addButton, generalDeleteButton, playButton;
    float density, dpHeight, dpWidth;
    int rowCount, columnCount,folderIconPxSize;
    ArrayList<String> foldersNameListOnStorage=new ArrayList<String>();
    ArrayList<String> foldersNameList=new ArrayList<String>();
    AlertDialog.Builder builder, builder1;
    ArrayList<ImageButton> deleteButtonList = new ArrayList<ImageButton>();
    SharedPreferences preferences, selectedImagesPreferences;
    SharedPreferences.Editor editor;
    private int STORAGE_PERMISSION_CODE = 1;
    private static final String SHARED_PREF_USER_INFO = "folderNames";
    boolean selectionMode;
    int nbCards, selectedCards;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_folders);
        preferences = getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE);
        editor = preferences.edit();
        selectedImagesPreferences = getSharedPreferences("selectedImages", MODE_PRIVATE);

        selectionMode = this.getIntent().getBooleanExtra("selectionMode",false);
        nbCards = this.getIntent().getIntExtra("nbCards",0);
        this.loadFoldersNameFromStorage();
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        density  = getResources().getDisplayMetrics().density;
        dpHeight = outMetrics.heightPixels / density;
        dpWidth  = outMetrics.widthPixels / density;
        rowCount =4;
        columnCount=4;

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Dossier photos");
        selectedCards=selectedImagesPreferences.getAll().size();

        folderIconPxSize = (int)(outMetrics.widthPixels/columnCount);
        this.principalLayout = findViewById(R.id.principal_layout);
        this.foldersGridLayout= findViewById(R.id.folder_grid);
        this.addButton = findViewById(R.id.add_button);
        this.playButton = findViewById(R.id.play_button);
        this.generalDeleteButton = findViewById(R.id.delete_button);
        if(selectionMode){
            toolbar.setTitle("Images sélectionnées "+selectedCards+"/"+nbCards);
            addButton.setVisibility(View.INVISIBLE);
            generalDeleteButton.setVisibility(View.INVISIBLE);
            playButton.setVisibility(View.VISIBLE);
        }
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent memoriesGame = new Intent(ShowFoldersActivity.this, GameActivity.class);
                ShowFoldersActivity.this.startActivity(memoriesGame);
            }
        });
        generalDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowFoldersActivity.this.setAllDeleteButtonsVisible();
            }
        });
        builder = new AlertDialog.Builder(this);
        builder1 = new AlertDialog.Builder(this);
        int n = foldersNameListOnStorage.size();
        for(int i =0;i<n;i++){
            String folderNameText = foldersNameListOnStorage.get(i);
            ViewGroup.LayoutParams paramsFrameLayout = new ActionBar.LayoutParams(folderIconPxSize,folderIconPxSize);
            FrameLayout.LayoutParams paramsDelete = new FrameLayout.LayoutParams((int)(folderIconPxSize*0.2), (int)(folderIconPxSize*0.2));
            FrameLayout.LayoutParams paramsIcon = new FrameLayout.LayoutParams((int)(folderIconPxSize*0.8), (int)(folderIconPxSize*0.8));
            FrameLayout.LayoutParams paramsText = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            FrameLayout cellFolderIcon = new FrameLayout(this);
            ImageButton deleteIcon = new ImageButton(this);
            deleteIcon.setVisibility(View.INVISIBLE);
            deleteButtonList.add(deleteIcon);
            ImageButton folderIcon = new ImageButton(this);
            TextView folderName = new TextView(this);
            folderIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent ShowImagesFromFolderIntent = new Intent(ShowFoldersActivity.this, ShowImagesFromFolderActivity.class);
                    ShowImagesFromFolderIntent.putExtra("folderName",folderNameText);
                    ShowImagesFromFolderIntent.putExtra("selectionMode",selectionMode);
                    ShowImagesFromFolderIntent.putExtra("nbCards",nbCards);
                    ShowFoldersActivity.this.startActivity(ShowImagesFromFolderIntent);
                }
            });
            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    builder1.setMessage("Confirmer la suppression ?")
                            .setCancelable(false)
                            .setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    foldersGridLayout.removeView(cellFolderIcon);
                                    foldersNameList.remove(folderNameText);
                                    ShowFoldersActivity.this.saveFoldersNameOnStorage();
                                    ShowFoldersActivity.this.setAllDeleteButtonsInvisible();
                                    File file = new File(ShowFoldersActivity.this.getFilesDir().getParent() + File.separator + "shared_prefs"+"/"+folderNameText+".xml");
                                    file.delete();
                                }
                            })
                            .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ShowFoldersActivity.this.setAllDeleteButtonsInvisible();
                                    dialog.cancel();
                                }
                            });
                    //Creating dialog box
                    AlertDialog alert1 = builder1.create();
                    //Setting the title manually
                    alert1.setTitle("Suppression Dossier");
                    alert1.getWindow().getAttributes().windowAnimations=R.style.MyDialogAnimation;
                    alert1.show();
                }
            });
            cellFolderIcon.setLayoutParams(paramsFrameLayout);
            folderName.setText(folderNameText);
            paramsDelete.gravity = Gravity.TOP|Gravity.RIGHT;
            deleteIcon.setLayoutParams(paramsDelete);
            paramsText.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
            folderIcon.setLayoutParams(paramsIcon);
            paramsIcon.gravity = Gravity.CENTER_HORIZONTAL;
            folderIcon.setBackgroundResource(R.drawable.folder_icon);
            deleteIcon.setBackgroundResource(R.drawable.minus);

            folderName.setLayoutParams(paramsText);

            cellFolderIcon.addView(deleteIcon);
            cellFolderIcon.addView(folderIcon);
            cellFolderIcon.addView(folderName);
            this.foldersNameList.add(folderNameText);
            foldersGridLayout.addView(cellFolderIcon);
        }
        this.setOnClick(addButton,this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(selectionMode)
            this.getCurrentSelectedImagesNumber();
    }

    protected void addFolder(){
        builder.setMessage("Nom de dossier")
                .setCancelable(false)
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        this.setOnClick(builder,this);
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Création dossier");
        alert.getWindow().getAttributes().windowAnimations=R.style.MyDialogAnimation;
        alert.show();
    }
    protected void setOnClick(ImageButton button, ShowFoldersActivity activity){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ShowFoldersActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(ShowFoldersActivity.this, "You have already granted this permission!",
                      //      Toast.LENGTH_SHORT).show();
                    activity.addFolder();
                } else {
                    requestStoragePermission();
                }


            }
        });
    }
    protected void setOnClick(AlertDialog.Builder builder, ShowFoldersActivity activity){
        EditText editText = new EditText(this);
        this.setFiltersEditText(editText);
        builder.setView(editText);
        builder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String folderNameText = editText.getText().toString();
                if(!folderNameText.equals("") & !activity.foldersNameList.contains(folderNameText)){
                    ViewGroup.LayoutParams paramsFrameLayout = new ActionBar.LayoutParams(folderIconPxSize,folderIconPxSize);
                    FrameLayout.LayoutParams paramsDelete = new FrameLayout.LayoutParams((int)(folderIconPxSize*0.2), (int)(folderIconPxSize*0.2));
                    FrameLayout.LayoutParams paramsIcon = new FrameLayout.LayoutParams((int)(folderIconPxSize*0.8), (int)(folderIconPxSize*0.8));
                    FrameLayout.LayoutParams paramsText = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    FrameLayout cellFolderIcon = new FrameLayout(activity);
                    ImageButton deleteIcon = new ImageButton(activity);
                    deleteIcon.setVisibility(View.INVISIBLE);
                    activity.deleteButtonList.add(deleteIcon);
                    ImageButton folderIcon = new ImageButton(activity);
                    TextView folderName = new TextView(activity);
                    folderIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent ShowImagesFromFolderIntent = new Intent(activity, ShowImagesFromFolderActivity.class);
                            ShowImagesFromFolderIntent.putExtra("folderName",folderNameText);
                            ShowImagesFromFolderIntent.putExtra("selectionMode",selectionMode);
                            ShowImagesFromFolderIntent.putExtra("nbCards",nbCards);
                            activity.startActivity(ShowImagesFromFolderIntent);
                        }
                    });

                    deleteIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            builder1.setMessage("Confirmer la suppression ?")
                                    .setCancelable(false)
                                    .setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            foldersGridLayout.removeView(cellFolderIcon);
                                            foldersNameList.remove(folderNameText);
                                            File file = new File(ShowFoldersActivity.this.getFilesDir().getParent() + File.separator + "shared_prefs"+"/"+folderNameText+".xml");
                                            file.delete();
                                            ShowFoldersActivity.this.saveFoldersNameOnStorage();
                                            ShowFoldersActivity.this.setAllDeleteButtonsInvisible();
                                        }
                                    })
                                    .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            ShowFoldersActivity.this.setAllDeleteButtonsInvisible();
                                            dialog.cancel();
                                        }
                                    });
                            //Creating dialog box
                            AlertDialog alert1 = builder1.create();
                            //Setting the title manually
                            alert1.setTitle("Suppression Dossier");
                            alert1.getWindow().getAttributes().windowAnimations=R.style.MyDialogAnimation;
                            alert1.show();
                        }
                    });

                    cellFolderIcon.setLayoutParams(paramsFrameLayout);
                    folderName.setText(folderNameText);
                    paramsDelete.gravity = Gravity.TOP|Gravity.RIGHT;
                    deleteIcon.setLayoutParams(paramsDelete);
                    paramsText.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
                    folderIcon.setLayoutParams(paramsIcon);
                    paramsIcon.gravity = Gravity.CENTER_HORIZONTAL;
                    folderIcon.setBackgroundResource(R.drawable.folder_icon);
                    deleteIcon.setBackgroundResource(R.drawable.minus);

                    folderName.setLayoutParams(paramsText);

                    cellFolderIcon.addView(deleteIcon);
                    cellFolderIcon.addView(folderIcon);
                    cellFolderIcon.addView(folderName);
                    activity.foldersNameList.add(folderNameText);
                    foldersGridLayout.addView(cellFolderIcon);
                    activity.saveFoldersNameOnStorage();
                }
                else{
                    builder.setMessage("Erreur : le nom doit au moins contenir 1 caractère");

                }
            }
        });
    }
    private void setFiltersEditText(EditText editText){
        editText.setSingleLine(true);
        editText.setFilters(new InputFilter[] {
                new InputFilter() {
                    public CharSequence filter(CharSequence src, int start,
                                               int end, Spanned dst, int dstart, int dend) {
                        if(src.equals("")){ // for backspace
                            return src;
                        }
                        if(src.toString().matches("[a-zA-Z ]+")){
                            return src;
                        }
                        return editText.getText().toString();
                    }
                }

        , new InputFilter.LengthFilter(10)});
        editText.setMaxLines(1);
    }
    protected String textInFileFoldersName(){
        int n = foldersNameList.size();
        String textInFileFoldersNameString ="";
        for(int i=0;i<n;i++){
            textInFileFoldersNameString+=foldersNameList.get(i)+"\n";
        }
        return textInFileFoldersNameString;
    }
    protected void saveFoldersNameOnStorage() {
        String text = this.textInFileFoldersName();
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(text.getBytes());
            //Toast.makeText(this, "Saved to " + getFilesDir() + "/" + FILE_NAME,
              //      Toast.LENGTH_LONG).show();
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
    protected void saveFoldersNameOnStorage2(){
        int n = foldersNameList.size();
        for(int i=0;i<n;i++){
            editor.putString(Integer.toString(i),foldersNameList.get(i).toString());

        }
        editor.commit();
    }
    protected void loadFoldersNameFromStorage() {
        FileInputStream fis = null;
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            while ((text = br.readLine()) != null) {
                foldersNameListOnStorage.add(text);
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
    protected void loadFoldersNameFromStorage2(){
        String uriText="a";
        int i = 0;
        while(uriText!=null){
            uriText = preferences.getString(Integer.toString(i),null);
            if(uriText!=null){
                foldersNameList.add(uriText);
                i++;
            }
        }

    }
    public void setAllDeleteButtonsVisible(){
        int n = deleteButtonList.size();
        for (int i=0;i<n;i++){
            deleteButtonList.get(i).setVisibility(View.VISIBLE);
        }
    }
    public void setAllDeleteButtonsInvisible(){
        int n = deleteButtonList.size();
        for (int i=0;i<n;i++){
            deleteButtonList.get(i).setVisibility(View.INVISIBLE);
        }
    }
    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission demandée")
                    .setMessage("La permission suivante est demandée afin d'accéder à la gallerie photo du téléphone, permettant dans le cas échéant d'ajouter du contenu n'importe quelle carte au jeu ")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(ShowFoldersActivity.this,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void getCurrentSelectedImagesNumber(){
        selectedCards = this.selectedImagesPreferences.getAll().size();
        toolbar.setTitle("Images sélectionnées "+selectedCards+"/"+nbCards);
    }
}
