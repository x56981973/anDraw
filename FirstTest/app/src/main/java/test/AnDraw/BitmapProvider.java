package test.AnDraw;

import android.graphics.Bitmap;

/**
 * Created by X on 2015/4/10.
 */
public class BitmapProvider {
    private static Bitmap bitmap;
    public static int flag = 0;

    public static Bitmap getBitmap(){ return bitmap;}
    public static void setBitmap(Bitmap bm) {bitmap = bm;}
    public static boolean isSet() {return flag == 1;}
    public static void destroy() {
        if(!bitmap.isRecycled())
            bitmap.recycle();
        flag = 0;
    }
}
