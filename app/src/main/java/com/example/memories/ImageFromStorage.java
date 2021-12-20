package com.example.memories;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class ImageFromStorage {
    public Bitmap imageBitmapForIcon;
    public Bitmap imageBitmapRaw;
    public String infoAboutImage;
    public ContentResolver contentResolver;
    int imageIconPxSize;
    Context context;
    public ImageFromStorage(Uri imageUri, String infoAboutImage_, Context context_, int imageIconPxSize_){
        context =context_;
        contentResolver = context.getContentResolver();
        imageIconPxSize = imageIconPxSize_;
        setImageWithUri(imageUri, infoAboutImage_);
    }
    public void setImageWithUri(Uri imageUri, String infoAboutImage_){
        try {
            imageBitmapRaw = MediaStore.Images.Media.getBitmap(contentResolver, imageUri);
            imageBitmapForIcon = getCorrectlyOrientedImage(context,imageUri);
            infoAboutImage = infoAboutImage_;
            Log.d("TAG","JE SUIS LA"+Integer.toString(imageIconPxSize));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Bitmap getImage(){
        return imageBitmapForIcon;
    }
    public String getInfoAboutImage(){
        return infoAboutImage;
    }
    public static int getOrientation(Context context, Uri photoUri) {
        /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }
    public  Bitmap getCorrectlyOrientedImage(Context context, Uri photoUri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(context, photoUri);

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        int MAX_IMAGE_DIMENSION = imageIconPxSize;
        if (rotatedWidth > MAX_IMAGE_DIMENSION || rotatedHeight > MAX_IMAGE_DIMENSION) {
            float widthRatio = ((float) rotatedWidth) / ((float) MAX_IMAGE_DIMENSION);
            float heightRatio = ((float) rotatedHeight) / ((float) MAX_IMAGE_DIMENSION);
            float maxRatio = Math.max(widthRatio, heightRatio);

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        is.close();

        /*
         * if the orientation is not 0 (or -1, which means we don't know), we
         * have to do a rotation.
         */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }

        return srcBitmap;
    }
}
