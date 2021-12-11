package com.example.memories;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameActivity extends AppCompatActivity {
    SelectedImages selectedImagesLoader;
    ArrayList<ImageFromStorage> selectedImagesFromStorage;
    GridLayout cardGrid;
    int columnCount, cardIconPxSize;
    Map<ImageView,Integer> imageViewMap = new HashMap<>();
    Map<EasyFlipView, Boolean> easyFlipViewBooleanMap = new HashMap<EasyFlipView, Boolean>();
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

        int i =0;
        for(ImageFromStorage imageIter : selectedImagesFromStorage){
            ImageView imageCard1 = new ImageView(this);
            ImageView imageCard2 = new ImageView(this);
            imageCard1.setImageBitmap(imageIter.getImage());
            imageCard2.setImageBitmap(imageIter.getImage());
            imageViewMap.put(imageCard1, i);
            imageViewMap.put(imageCard2, i);
            i++;
        }
        for(ImageView imageIcon : imageViewMap.keySet()){
            EasyFlipView cardFlipView = new EasyFlipView(this);

            ImageView cardBackSide = new ImageView(this);
            cardBackSide.setImageResource(R.drawable.card_back_side2);

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
            easyFlipViewBooleanMap.put(cardFlipView,false);
            cardFlipView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        Toast.makeText(GameActivity.this,Integer.toString(countFlippedCard()), Toast.LENGTH_SHORT).show();
                        if(countFlippedCard()==0){
                            cardFlipView.flipTheView(true);
                            easyFlipViewBooleanMap.put(cardFlipView, true);
                        }
                        else if(countFlippedCard()==1){
                            cardFlipView.flipTheView(true);
                            easyFlipViewBooleanMap.put(cardFlipView, true);
                            ArrayList<EasyFlipView> flippedCards = findFlippedCard();
                            Log.d("TAG",Integer.toString(flippedCards.size()));
                            setAllFlipDisabledExceptedTwoOnes(flippedCards.get(0),flippedCards.get(1));
                        }
                        else if(countFlippedCard()==2){
                            ArrayList<EasyFlipView> flippedCards = findFlippedCard();
                            if(cardFlipView.equals(flippedCards.get(0)) || cardFlipView.equals(flippedCards.get(1))){
                                cardFlipView.flipTheView(true);
                                easyFlipViewBooleanMap.put(cardFlipView, false);
                            }

                        }

                    }
                    return true;
                }
            });
        }

    }
    public ArrayList<EasyFlipView> findFlippedCard(){
        ArrayList<EasyFlipView> flippedCards = new ArrayList<>();
        for(EasyFlipView cardFlipView : easyFlipViewBooleanMap.keySet()){
            if(cardFlipView.isBackSide())
                flippedCards.add(cardFlipView);
        }
        return flippedCards;
    }
    public int countFlippedCard(){
        int i =0;
        for(EasyFlipView cardFlipView : easyFlipViewBooleanMap.keySet()){
            if(easyFlipViewBooleanMap.get(cardFlipView))
                i++;
        }
        return i;
    }
    public void setAllFlipDisabledExceptedTwoOnes(EasyFlipView cardFlipView1, EasyFlipView cardFlipView2){
        int i=0;
        for(EasyFlipView cardFlipView : easyFlipViewBooleanMap.keySet()){
            if(cardFlipView!=cardFlipView1 && cardFlipView!=cardFlipView2){
                cardFlipView.setFlipEnabled(false);
                i++;
            }
        }
        Toast.makeText(this, Integer.toString(i), Toast.LENGTH_SHORT).show();
    }
    public void setAllFlipEnabled(){
        for(EasyFlipView cardFlipView : easyFlipViewBooleanMap.keySet()){
            cardFlipView.setFlipEnabled(true);
        }

    }

}