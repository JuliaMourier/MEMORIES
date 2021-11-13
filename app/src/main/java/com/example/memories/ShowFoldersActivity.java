package com.example.memories;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
    ImageButton addButton, generalDeleteButton;
    float density, dpHeight, dpWidth;
    int rowCount, columnCount,folderIconPxSize;
    ArrayList<String> foldersNameListOnStorage=new ArrayList<String>();
    ArrayList<String> foldersNameList=new ArrayList<String>();
    AlertDialog.Builder builder, builder1;
    ArrayList<ImageButton> deleteButtonList = new ArrayList<ImageButton>();
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
        this.generalDeleteButton = findViewById(R.id.delete_button);
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
            ImageView folderIcon = new ImageView(this);
            TextView folderName = new TextView(this);

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
                                }
                            })
                            .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
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
                    FrameLayout.LayoutParams paramsDelete = new FrameLayout.LayoutParams((int)(folderIconPxSize*0.2), (int)(folderIconPxSize*0.2));
                    FrameLayout.LayoutParams paramsIcon = new FrameLayout.LayoutParams((int)(folderIconPxSize*0.8), (int)(folderIconPxSize*0.8));
                    FrameLayout.LayoutParams paramsText = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    FrameLayout cellFolderIcon = new FrameLayout(activity);
                    ImageButton deleteIcon = new ImageButton(activity);
                    deleteIcon.setVisibility(View.INVISIBLE);
                    activity.deleteButtonList.add(deleteIcon);
                    ImageView folderIcon = new ImageView(activity);
                    TextView folderName = new TextView(activity);

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
                                        }
                                    })
                                    .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
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

}