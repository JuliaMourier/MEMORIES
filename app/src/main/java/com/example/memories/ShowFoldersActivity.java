package com.example.memories;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowFoldersActivity extends AppCompatActivity {
    GridLayout foldersGridLayout;
    ImageButton addButton;
    float density, dpHeight, dpWidth;
    int rowCount, columnCount,folderIconPxSize;
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
        for(int i =0;i<10;i++){


        }
        this.setOnClick(addButton,this);

    }
    protected void addFolder(){
        ViewGroup.LayoutParams paramsFrameLayout = new ActionBar.LayoutParams(folderIconPxSize,folderIconPxSize);
        ViewGroup.LayoutParams paramsIcon = new ViewGroup.LayoutParams((int)(folderIconPxSize*0.8), (int)(folderIconPxSize*0.8));
        FrameLayout.LayoutParams paramsText = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        FrameLayout cellFolderIcon = new FrameLayout(this);
        ImageView folderIcon = new ImageView(this);
        TextView folderName = new TextView(this);

        cellFolderIcon.setLayoutParams(paramsFrameLayout);
        folderName.setText("Nom");
        paramsText.gravity = Gravity.BOTTOM;
        folderIcon.setLayoutParams(paramsIcon);
        folderIcon.setBackgroundResource(R.drawable.folder_icon);
        folderName.setLayoutParams(paramsText);

        cellFolderIcon.addView(folderIcon);
        cellFolderIcon.addView(folderName);
        foldersGridLayout.addView(cellFolderIcon);
    }
    protected void setOnClick(ImageButton button, ShowFoldersActivity activity){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.addFolder();
            }
        });
    }
}