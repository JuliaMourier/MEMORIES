package com.example.memories;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memories.Database.FirebaseActivity;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;


public class GameActivity extends AppCompatActivity {
    //This has been realized with :
    //https://github.com/DanielMartinus/Konfetti
    SelectedImages selectedImagesLoader;
    ArrayList<ImageFromStorage> selectedImagesFromStorage;
    GridLayout cardGrid;
    int columnCount, cardIconPxSize;
    Map<ImageView,Integer> imageViewMap = new HashMap<>();
    Map<EasyFlipView, Boolean> easyFlipViewBooleanMap = new HashMap<EasyFlipView, Boolean>();
    Map<Integer, Bitmap> imageRawBitmapMap = new HashMap<Integer, Bitmap>();
    public MediaPlayer mp;
    //Chronometer and nb of tries
    int nbTries = 0;
    private int seconds = 0; // number of seconds since launched
    private boolean running = false;//is it running or not?
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        int nbCards = this.getIntent().getIntExtra("nbCards",8);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        cardGrid = findViewById(R.id.card_grid);


        //Get height of the screen
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int supposedcardIconPxSize = (int)(outMetrics.widthPixels/3);
        Double nbRowSupposed = Math.floor(2*nbCards/3);

        int heightOfAllCardsFor3Columns = (int)(nbRowSupposed*supposedcardIconPxSize); //Height of all cards if 3 columns
        if(heightOfAllCardsFor3Columns > screenHeight){//If the height is going to be too much
            columnCount = 4; //Set a fourth column
            cardGrid.setColumnCount(4); //Set 4 columns the GridLayout
        }
        else {
            columnCount=3; //Set 3 columns
            cardGrid.setColumnCount(3);
        }
        cardIconPxSize = (int)(outMetrics.widthPixels/columnCount);

        selectedImagesLoader = new SelectedImages(this, cardIconPxSize, outMetrics.widthPixels);
        selectedImagesFromStorage = selectedImagesLoader.imageList;

        int i =0;
        for(ImageFromStorage imageIter : selectedImagesFromStorage){
            ImageView imageCard1 = new ImageView(this);
            ImageView imageCard2 = new ImageView(this);
            imageCard1.setImageBitmap(imageIter.getImage());
            imageCard2.setImageBitmap(imageIter.getImage());
            imageViewMap.put(imageCard1, i);
            imageViewMap.put(imageCard2, i);
            imageRawBitmapMap.put(i, imageIter.imageBitmapRaw);
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
                            ArrayList<EasyFlipView> flippedCards = findFlippedCard();
                            int flippedCardsNumber = flippedCards.size();
                            if(flippedCardsNumber==0){
                                easyFlipViewBooleanMap.put(cardFlipView,true);
                                cardFlipView.flipTheView(true);
                                flippedCards = findFlippedCard();
                            }
                            else if(flippedCardsNumber==1){
                                if(flippedCards.contains(cardFlipView)){
                                    easyFlipViewBooleanMap.put(cardFlipView,false);
                                    cardFlipView.flipTheView(true);
                                    flippedCards = findFlippedCard();
                                }
                                else{
                                    nbTries++;
                                    easyFlipViewBooleanMap.put(cardFlipView,true);
                                    cardFlipView.flipTheView(true);
                                    flippedCards = findFlippedCard();
                                    EasyFlipView flippedCard1 = flippedCards.get(0);
                                    EasyFlipView flippedCard2 = flippedCards.get(1);
                                    ImageView imageView1 = (ImageView) flippedCard1.getChildAt(0);
                                    ImageView imageView2 = (ImageView) flippedCard2.getChildAt(0);
                                    if(imageViewMap.get(imageView1)==imageViewMap.get(imageView2)){
                                        easyFlipViewBooleanMap.remove(flippedCard1);
                                        easyFlipViewBooleanMap.remove(flippedCard2);
                                        flippedCard1.setFlipEnabled(false);
                                        flippedCard2.setFlipEnabled(false);
                                        ImageView imagePopup = new ImageView(GameActivity.this);
                                        imagePopup.setImageBitmap(imageRawBitmapMap.get(imageViewMap.get(imageView1)));
                                        imagePopup.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                        showImage(imagePopup);

                                    }
                                }
                            }
                            else if(flippedCardsNumber==2){
                                setAllFlipDisabledExceptedTwoOnes(flippedCards.get(0),flippedCards.get(1));
                                if(flippedCards.contains(cardFlipView)){
                                    easyFlipViewBooleanMap.put(cardFlipView,false);
                                    cardFlipView.flipTheView(true);
                                    flippedCards = findFlippedCard();
                                    setAllFlipEnabled();
                                }
                            }
                        }
                    }
                    return true;
                }
            });
        }
        //Launch chronometer
        running = true;
        runTimer();
        //Gives the sound to mediaplayer
        mp = MediaPlayer.create(this, R.raw.winsound);
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
                //Test WIN;
                if(easyFlipViewBooleanMap.isEmpty()){
                    //WIN
                    onGameWin();
                }
            }
        });
        //Dismiss the dialog image on click
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                builder.dismiss();
            }
        });
        builder.addContentView(imageView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.getWindow().getAttributes().windowAnimations = R.style.MyDialogAnimation;

        builder.show();
    }

    public void showRestartMenu() {

        Dialog builder = new Dialog(this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //Do nothing
            }
        });
        Button restartView = new Button(getApplicationContext());
        restartView.setText("RECOMMENCER");
        restartView.setTextSize(36);
        restartView.setPadding(100, 100,100,100);

        restartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                builder.dismiss();
                onRestartGame();

            }
        });
        Button menuView = new Button(getApplicationContext());
        menuView.setText("MENU");
        menuView.setTextSize(36);
        menuView.setPadding(100, 100,100,100);
        menuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                builder.dismiss();
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout lv = new LinearLayout(getApplicationContext());
        lv.setOrientation(LinearLayout.VERTICAL);
        lv.addView(restartView);
        lv.addView(menuView);
        builder.addContentView(lv, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        builder.show();
    }

    private void runTimer() { //timer
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run () {

                double t = SystemClock.elapsedRealtime();
                if (running) {
                    seconds++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void onGameWin(){
        //TextView text = findViewById(R.id.findPairsText);
        //text.setText("Bravo !!!");
        mp.start();
        //Stop Chronometer
        running = false;
        //Animation of confettis
        final KonfettiView konfettiView = findViewById(R.id.konfettiView);
        konfettiView.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.BLUE, Color.RED)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                .addSizes(new Size(12, 5f))
                .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                .streamFor(300, 5000L);
        konfettiView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) { //When click => Konfettis
                showRestartMenu();
                konfettiView.build()
                        .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.BLUE, Color.RED)
                        .setDirection(0.0, 359.0)
                        .setSpeed(1f, 5f)
                        .setFadeOutEnabled(true)
                        .setTimeToLive(2000L)
                        .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                        .addSizes(new Size(12, 5f))
                        .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                        .streamFor(300, 5000L);
            }

        });

        //Send data to database
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FirebaseActivity firebaseActivity = new FirebaseActivity();
                firebaseActivity.writeNewGameFromGameActivity(getApplicationContext(),Integer.toString(seconds), Integer.toString(nbTries));
            }
        });
    }

    public void onRestartGame(){
        //Restart the chrono
        seconds = 0;
        running = true;
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

}