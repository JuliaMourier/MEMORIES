package com.example.memories;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    SelectedImages selectedImagesLoader;;
    ArrayList<ImageFromStorage> selectedImagesFromStorage;
    GridLayout cardGrid;
    int columnCount, cardIconPxSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);
        columnCount=3;
        cardIconPxSize = (int)(outMetrics.widthPixels/columnCount);

        selectedImagesLoader = new SelectedImages(this);
        selectedImagesFromStorage = selectedImagesLoader.imageList;
        cardGrid = findViewById(R.id.card_grid);

        for(ImageFromStorage imageIter : selectedImagesFromStorage){
            EasyFlipView cardFlipView = new EasyFlipView(this);

            ImageView imageIcon = new ImageView(this);
            ImageView cardBackSide = new ImageView(this);
            cardBackSide.setImageResource(R.drawable.card_back_side);
            imageIcon.setImageBitmap(imageIter.imageBitmap);

            FrameLayout imageIconFrameLayout = new FrameLayout(this);
            FrameLayout.LayoutParams paramsFrameLayout = new FrameLayout.LayoutParams((int)(cardIconPxSize), (int)(cardIconPxSize));
            FrameLayout.LayoutParams paramsIcon = new FrameLayout.LayoutParams((int)(cardIconPxSize*0.95), (int)(cardIconPxSize*0.95));
            paramsIcon.gravity = Gravity.CENTER_HORIZONTAL;
            imageIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
            cardBackSide.setScaleType(ImageView.ScaleType.CENTER_CROP);

            imageIcon.setLayoutParams(paramsIcon);
            cardBackSide.setLayoutParams(paramsIcon);
            cardFlipView.setLayoutParams(paramsIcon);
            imageIconFrameLayout.setLayoutParams(paramsFrameLayout);

            cardFlipView.addView(imageIcon);
            cardFlipView.addView(cardBackSide);
            imageIconFrameLayout.addView(cardFlipView);
            cardGrid.addView(imageIconFrameLayout);
        }

    }

}