package test.AnDraw;

/**
 * Created by X on 2015/3/15.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Administrator
 *
 */
public class DrawView extends View {
    private int view_width = 0;		//屏幕的宽度
    private int view_height = 0;	//屏幕的高度
    private float preX;				//起始点的x坐标
    private float preY;				//起始点的y坐标
    private Path path;				//路径
    public Paint paint = null;		//画笔
    Bitmap cacheBitmap = null;		//定义一个内存中的图片，该图片将作为缓冲区
    Canvas cacheCanvas = null;		//定义cacheBitmap上的Canvas对象
    Bitmap fxBitmap = null;
    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }
    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //getDisplayMetrics()方法获取DisplayMetrics对象，用于获取屏幕信息
        view_width = context.getResources().getDisplayMetrics().widthPixels;	//获取屏幕宽度
        view_height = context.getResources().getDisplayMetrics().heightPixels;	//获取屏幕高度

        cacheBitmap = Bitmap.createBitmap(view_width, view_height, Config.ARGB_8888);
        cacheCanvas = new Canvas(cacheBitmap);		//创建第二层画布

        path = new Path();
        paint = new Paint(Paint.DITHER_FLAG);	//Paint.DITHER_FLAG 防抖动
        paint.setColor(Color.RED);				//设置默认的画笔颜色为红色
        //设置画笔风格
        paint.setStyle(Paint.Style.STROKE);		//设置填充方式为描边
        paint.setStrokeJoin(Paint.Join.ROUND);	//设置笔刷的图形样式
        paint.setStrokeCap(Paint.Cap.ROUND);	//设置画笔转弯处的连接风格
        paint.setStrokeWidth(10);				//设置默认笔触的宽度为1像素
        paint.setAntiAlias(true);				//设置抗锯齿功能
        paint.setDither(true);					//设置抖动效果
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(0xffffff);				//设置背景颜色
        Paint bmpPaint = new Paint();			//采用默认设置创建一个画笔

        if(BitmapProvider.isSet()) canvas.drawBitmap(BitmapProvider.getBitmap(), 0, 0, bmpPaint);
        if(fxBitmap != null) canvas.drawBitmap(fxBitmap, 0, 0, bmpPaint);
        canvas.drawBitmap(cacheBitmap, 0, 0, bmpPaint);        //绘制cacheBitmap

        cacheCanvas.drawPath(path, paint);            //绘制路径

        canvas.save(Canvas.ALL_SAVE_FLAG);		//保存canvas状态，最后所有的信息都会保存在第一个创建的Bitmap中
        canvas.restore();		//恢复canvas之前保存的状态，防止保存后对canvas执行的操作队后续的绘制有影响
        Log.i("jason","ondraw called");

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);			//将绘图的起点移动到（x,y）坐标的位置
                preX = x;
                preY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - preX);	//Math.abs 返回绝对值
                float dy = Math.abs(y - preY);
                if(dx >= 5 || dy >= 5){			//判断是否在允许的范围内
                    path.quadTo(preX, preY, (x + preX)/2, (y + preY)/2 );
                    preX = x;
                    preY = y;
                }
                break;
            case MotionEvent.ACTION_UP:
                cacheCanvas.drawPath(path, paint);		//绘制路径
                path.reset();
                break;
        }
        invalidate();		//更新视图
        return true;		//返回true表示处理方法已经处理该事件
    }

    /*
    设置背景
     */
    public void setBackground(int color){
        Bitmap bm = Bitmap.createBitmap(view_width, view_height, Config.ARGB_8888);
        for(int i = 0; i < bm.getHeight();i++){
            for(int j = 0; j <bm.getWidth(); j++){
                bm.setPixel(j,i,color);
            }
        }

        Paint bmpPaint = new Paint();
        cacheCanvas.drawBitmap(bm,0,0,bmpPaint);
    }

    /*
    重置
     */
    public void reset(){
        if (!cacheBitmap.isRecycled()) {
            cacheBitmap.recycle();
        }
        cacheBitmap = Bitmap.createBitmap(view_width, view_height, Config.ARGB_8888);
        cacheCanvas = new Canvas(cacheBitmap);		//创建第二层画布
        invalidate();		//更新视图
    }

    /*
     * 橡皮擦功能
     */
    public void clear(){
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));	//设置图形重叠时候的处理方式
        //paint.setStrokeWidth(50);
    }

    public void procSrc2Gray(){
        Mat rgbMat = new Mat();
        Mat grayMat = new Mat();
        Bitmap sourceBitmap = BitmapProvider.getBitmap();
        Bitmap grayBitmap =  Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(), Config.RGB_565);
        Utils.bitmapToMat(sourceBitmap, rgbMat);//convert original bitmap to Mat, R G B.
        Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGB2GRAY);//rgbMat to gray grayMat
        Utils.matToBitmap(grayMat, grayBitmap); //convert mat to bitmap
        fxBitmap = grayBitmap;
        invalidate();
        Log.i("jason", "procSrc2Gray sucess...");
        try {
            saveFxBitmap(grayBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //return grayBitmap;
    }
    /*
     * 保存功能
     */
    public String save(){
        try {
            return saveBitmap();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

    public String saveBitmap() throws IOException {
        boolean sdCardExist = android.os.Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);
        //Log.i("xxx", android.os.Environment.getExternalStorageState());
        if (sdCardExist) {
            SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
            String fileName = s.format(new Date());

            File sdDir = android.os.Environment.getExternalStorageDirectory();
            File file = new File(sdDir.toString() + "/pictures/" + fileName + ".jpg");    //创建文件对象
            file.createNewFile();    //创建一个新文件
            FileOutputStream fileOS = new FileOutputStream(file);    //创建一个文件输出流对象

            if(BitmapProvider.isSet()) {
                Paint bmpPaint = new Paint();            //采用默认设置创建一个画笔
                Bitmap newBitmap = Bitmap.createBitmap(view_width, view_height, Config.ARGB_8888);
                Canvas newCanvas = new Canvas(newBitmap);
                newCanvas.drawBitmap(BitmapProvider.getBitmap(), 0, 0, bmpPaint);
                newCanvas.drawBitmap(cacheBitmap, 0, 0, bmpPaint);
                newCanvas.save(Canvas.ALL_SAVE_FLAG);
                newCanvas.restore();
                newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOS);
            }else {
                cacheBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOS);
            }

            fileOS.flush();        //将缓冲区中的数据全部写出到输出流中
            fileOS.close();        //关闭文件输出流对象

            String dir = sdDir.toString() + "/pictures/" + fileName + ".jpg";
            Log.i("xzy",dir);
            return dir;
        }
        return "";
    }

    public String saveFxBitmap(Bitmap bitmap) throws IOException {
        boolean sdCardExist = android.os.Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);
        //Log.i("xxx", android.os.Environment.getExternalStorageState());
        if (sdCardExist) {
            SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
            String fileName = s.format(new Date());

            File sdDir = android.os.Environment.getExternalStorageDirectory();
            File file = new File(sdDir.toString() + "/pictures/" + fileName + "_fx.jpg");    //创建文件对象
            file.createNewFile();    //创建一个新文件
            FileOutputStream fileOS = new FileOutputStream(file);    //创建一个文件输出流对象


            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOS);


            fileOS.flush();        //将缓冲区中的数据全部写出到输出流中
            fileOS.close();        //关闭文件输出流对象

            String dir = sdDir.toString() + "/pictures/" + fileName + "_fx.jpg";
            Log.i("xzy",dir);
            return dir;
        }
        return "";
    }
}
