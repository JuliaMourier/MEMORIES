package com.example.memories;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ShowFoldersActivity extends AppCompatActivity {
    private static final String FILE_NAME = "foldersName.txt";
    GridLayout foldersGridLayout;
    ImageButton addButton;
    float density, dpHeight, dpWidth;
    int rowCount, columnCount,folderIconPxSize;
    ArrayList<String> foldersNameListOnStorage=new ArrayList<String>();
    ArrayList<String> foldersNameList=new ArrayList<String>();
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_folders);
        this.loadFoldersNameFromStorage();
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        density  = getResources().getDisplayMetrics().density;
        dpHeight = outMetrics.heightPixels / density;
        dpWidth  = outMetrics.widthPixels / density;
        rowCount =4;
        columnCount=4;

        folderIconPxSize = (int)(outMetrics.widthPixels/columnCount);
        this.foldersGridLayout= (GridLayout)findViewById(R.id.folder_grid);
        this.addButton = findViewById(R.id.add_button);
        builder = new AlertDialog.Builder(this);
        int n = foldersNameListOnStorage.size();
        for(int i =0;i<n;i++){
            String folderNameText = foldersNameListOnStorage.get(i);
            ViewGroup.LayoutParams paramsFrameLayout = new ActionBar.LayoutParams(folderIconPxSize,folderIconPxSize);
            FrameLayout.LayoutParams paramsIcon = new FrameLayout.LayoutParams((int)(folderIconPxSize*0.8), (int)(folderIconPxSize*0.8));
            FrameLayout.LayoutParams paramsText = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            FrameLayout cellFolderIcon = new FrameLayout(this);
            ImageView folderIcon = new ImageView(this);
            TextView folderName = new TextView(this);

            cellFolderIcon.setLayoutParams(paramsFrameLayout);
            folderName.setText(folderNameText);
            paramsText.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
            folderIcon.setLayoutParams(paramsIcon);
            paramsIcon.gravity = Gravity.CENTER_HORIZONTAL;
            folderIcon.setBackgroundResource(R.drawable.folder_icon);

            folderName.setLayoutParams(paramsText);

            cellFolderIcon.addView(folderIcon);
            cellFolderIcon.addView(folderName);
            this.foldersNameList.add(folderNameText);
            foldersGridLayout.addView(cellFolderIcon);
        }
        this.setOnClick(addButton,this);

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
                activity.addFolder();
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
                if(!folderNameText.equals("")){
                    ViewGroup.LayoutParams paramsFrameLayout = new ActionBar.LayoutParams(folderIconPxSize,folderIconPxSize);
                    FrameLayout.LayoutParams paramsIcon = new FrameLayout.LayoutParams((int)(folderIconPxSize*0.8), (int)(folderIconPxSize*0.8));
                    FrameLayout.LayoutParams paramsText = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    FrameLayout cellFolderIcon = new FrameLayout(activity);
                    ImageView folderIcon = new ImageView(activity);
                    TextView folderName = new TextView(activity);

                    cellFolderIcon.setLayoutParams(paramsFrameLayout);
                    folderName.setText(folderNameText);
                    paramsText.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
                    folderIcon.setLayoutParams(paramsIcon);
                    paramsIcon.gravity = Gravity.CENTER_HORIZONTAL;
                    folderIcon.setBackgroundResource(R.drawable.folder_icon);

                    folderName.setLayoutParams(paramsText);

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
    public void saveFoldersNameOnStorage() {
        String text = this.textInFileFoldersName();
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(text.getBytes());
            Toast.makeText(this, "Saved to " + getFilesDir() + "/" + FILE_NAME,
                    Toast.LENGTH_LONG).show();
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
    public void loadFoldersNameFromStorage() {
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
}