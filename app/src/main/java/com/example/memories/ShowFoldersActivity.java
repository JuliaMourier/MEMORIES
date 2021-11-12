package com.example.memories;

import androidx.appcompat.app.AppCompatActivity;

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

public class ShowFoldersActivity extends AppCompatActivity {
    GridLayout foldersGridLayout;
    ImageButton addButton;
    float density, dpHeight, dpWidth;
    int rowCount, columnCount,folderIconPxSize;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_folders);
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
                if(folderNameText!=""){
                    ViewGroup.LayoutParams paramsFrameLayout = new ActionBar.LayoutParams(folderIconPxSize,folderIconPxSize);
                    ViewGroup.LayoutParams paramsIcon = new ViewGroup.LayoutParams((int)(folderIconPxSize*0.8), (int)(folderIconPxSize*0.8));
                    FrameLayout.LayoutParams paramsText = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    FrameLayout cellFolderIcon = new FrameLayout(activity);
                    ImageView folderIcon = new ImageView(activity);
                    TextView folderName = new TextView(activity);

                    cellFolderIcon.setLayoutParams(paramsFrameLayout);
                    folderName.setText(folderNameText);
                    paramsText.gravity = Gravity.BOTTOM;
                    folderIcon.setLayoutParams(paramsIcon);
                    folderIcon.setBackgroundResource(R.drawable.folder_icon);
                    folderName.setLayoutParams(paramsText);

                    cellFolderIcon.addView(folderIcon);
                    cellFolderIcon.addView(folderName);
                    foldersGridLayout.addView(cellFolderIcon);
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
}