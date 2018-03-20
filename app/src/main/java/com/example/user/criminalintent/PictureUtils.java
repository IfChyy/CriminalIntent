package com.example.user.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * Created by Ivo Georgiev (IfChyy)
 * class to compress and downscale bitmap images taken from camera intent
 * for storing in imageview
 */

public class PictureUtils {

    public static Bitmap getScaleBitmap(String path, int destWidth, int destHeight) {
        //read the dimensions of the image from file storage
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        //Figure out how  to downscale the image by
        int inSampleSize = 1;

        //check if src width and height is bigger than destination one
        if(srcHeight > destHeight || srcWidth > destWidth){
            if(srcWidth > srcHeight){
                inSampleSize = Math.round(srcHeight / destHeight);
            }else{
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        //get the path and its dimensions and create the downszed image
        return BitmapFactory.decodeFile(path, options);
    }

    //get the scaled bitmap with estimate of how big the image is
    // depending on screen dimensions
    // better usage is to wait for the layout to create itself and
    // get the layout params of the particuluar button

    public static Bitmap getScaledBitmap(String path, Activity activity){
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);

        return getScaleBitmap(path, size.x, size.y);
    }


}
