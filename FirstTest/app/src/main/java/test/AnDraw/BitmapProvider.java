package test.AnDraw;

import android.graphics.Bitmap;

/**
 * Created by X on 2015/4/10.
 */
public class BitmapProvider {
    private static Bitmap bitmap;
    public static int width;
    public static int height;
    public static int flag = 0;

    public static Bitmap getBitmap(){ return bitmap;}
    public static void setBitmap(Bitmap bm) {
        int bmWidth = bm.getWidth();
        int bmHeight = bm.getHeight();

        int cutPointX;
        int cutPointY;
        int cutWidth;
        int cutHeight;

        if( bmHeight > height){
            cutPointY = (bmHeight - height)/2;
            cutHeight = height;
        }
        else{
            cutPointY = 0;
            cutHeight = bmHeight;
        }

        if( bmWidth > width) {
            cutPointX = (bmWidth - width)/2;
            cutWidth = width;
        }
        else {
            cutPointX = 0;
            cutWidth = bmWidth;
        }

        Bitmap t = Bitmap.createBitmap(bm,cutPointX,cutPointY,cutWidth,cutHeight);
        bitmap = t;
        flag = 1;
    }
    public static boolean isSet() {return flag == 1;}
    public static void destroy() {
        if(!bitmap.isRecycled())
            bitmap.recycle();
        flag = 0;
    }
}
