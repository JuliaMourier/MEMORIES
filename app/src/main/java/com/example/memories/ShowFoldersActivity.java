package com.example.memories;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

public class ShowFoldersActivity extends AppCompatActivity {
    GridLayout foldersGridLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_folders);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        float density  = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth  = outMetrics.widthPixels / density;
        int rowCount =4;
        int columnCount=4;
        int folderIconDpSize = (int)(outMetrics.widthPixels/columnCount);
        this.foldersGridLayout= (GridLayout)findViewById(R.id.folder_grid);
        for(int i =0;i<10;i++){
            ImageView folderIcon = new ImageView(this);
            ViewGroup.LayoutParams params = new ActionBar.LayoutParams(folderIconDpSize,folderIconDpSize);
            folderIcon.setLayoutParams(params);
            folderIcon.setBackgroundResource(R.drawable.folder_icon);
            foldersGridLayout.addView(folderIcon);

        }
    }
}