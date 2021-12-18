package com.example.memories;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
    boolean flippingCard = false;
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
                        if(easyFlipViewBooleanMap.keySet().contains(cardFlipView)){
                            if(!flippingCard){
                                flippingCard=true;
                                ArrayList<EasyFlipView> flippedCards = findFlippedCard();
                                int flippedCardsNumber = flippedCards.size();
                                if(flippedCardsNumber==0){
                                    easyFlipViewBooleanMap.put(cardFlipView,true);
                                    cardFlipView.flipTheView(true);
                                    flippedCards = findFlippedCard();
                                    Log.d("TAG",Integer.toString(flippedCards.size())+" cartes retournées");
                                }
                                else if(flippedCardsNumber==1){
                                    if(flippedCards.contains(cardFlipView)){
                                        easyFlipViewBooleanMap.put(cardFlipView,false);
                                        cardFlipView.flipTheView(true);
                                        flippedCards = findFlippedCard();
                                        Log.d("TAG",Integer.toString(flippedCards.size())+" cartes retournées");
                                    }
                                    else{
                                        easyFlipViewBooleanMap.put(cardFlipView,true);
                                        cardFlipView.flipTheView(true);
                                        flippedCards = findFlippedCard();
                                        ImageView imageView1 = (ImageView) flippedCards.get(0).getChildAt(0);
                                        ImageView imageView2 = (ImageView) flippedCards.get(1).getChildAt(0);
                                        if(imageViewMap.get(imageView1)==imageViewMap.get(imageView2)){
                                            Log.d("TAG","Paire trouvée !");
                                            easyFlipViewBooleanMap.remove(flippedCards.get(0));
                                            easyFlipViewBooleanMap.remove(flippedCards.get(1));
                                            flippedCards.get(0).setFlipEnabled(false);
                                            flippedCards.get(1).setFlipEnabled(false);
                                            Bitmap bm=((BitmapDrawable)imageView1.getDrawable()).getBitmap();
                                            ImageView imagePopup = new ImageView(GameActivity.this);
                                            imagePopup.setImageBitmap(bm);
                                            showImage(imagePopup);

                                        }
                                        Log.d("TAG",Integer.toString(flippedCards.size())+" cartes retournées");
                                    }
                                }
                                else if(flippedCardsNumber==2){
                                    setAllFlipDisabledExceptedTwoOnes(flippedCards.get(0),flippedCards.get(1));
                                    if(flippedCards.contains(cardFlipView)){
                                        easyFlipViewBooleanMap.put(cardFlipView,false);
                                        cardFlipView.flipTheView(true);
                                        flippedCards = findFlippedCard();
                                        Log.d("TAG",Integer.toString(flippedCards.size())+" cartes retournées");
                                        setAllFlipEnabled();
                                    }
                                    else{
                                        Log.d("TAG","Il faut retourner d'autre carte !");
                                    }
                                }
                            }
                            else{
                                Log.d("TAG","Patientez !");
                            }
                        }

                        flippingCard=false;
                    }
                    return true;
                }
            });
        }

    }
    public ArrayList<EasyFlipView> findFlippedCard(){
        ArrayList<EasyFlipView> flippedCards = new ArrayList<>();
        for(EasyFlipView cardFlipView : easyFlipViewBooleanMap.keySet()){
            if(easyFlipViewBooleanMap.get(cardFlipView))
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
    }
    public void setAllFlipEnabled(){
        for(EasyFlipView cardFlipView : easyFlipViewBooleanMap.keySet()){
            cardFlipView.setFlipEnabled(true);
        }

    }
    public void showImage(ImageView imageView) {
        Dialog builder = new Dialog(this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });
        builder.addContentView(imageView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.getWindow().getAttributes().windowAnimations = R.style.MyDialogAnimation;

        builder.show();
    }

}