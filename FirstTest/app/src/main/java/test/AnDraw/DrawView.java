package test.AnDraw;

/**
 * Created by X on 2015/3/15.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
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

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //getDisplayMetrics()方法获取DisplayMetrics对象，用于获取屏幕信息
        view_width = context.getResources().getDisplayMetrics().widthPixels;	//获取屏幕宽度
        view_height = context.getResources().getDisplayMetrics().heightPixels;	//获取屏幕高度

        //创建一个与该view相同大小的缓存区,Config.ARGB_8888 --> 一种32位的位图,意味着有四个参数,即A,R,G,B,每一个参数由8bit来表示.
        cacheBitmap = Bitmap.createBitmap(view_width, view_height, Config.ARGB_8888);

        cacheCanvas = new Canvas(cacheBitmap);		//创建一个新画布
        path = new Path();
        paint = new Paint(Paint.DITHER_FLAG);	//Paint.DITHER_FLAG 防抖动
        paint.setColor(Color.RED);				//设置默认的画笔颜色为红色
        //设置画笔风格
        paint.setStyle(Paint.Style.STROKE);		//设置填充方式为描边
        paint.setStrokeJoin(Paint.Join.ROUND);	//设置笔刷的图形样式
        paint.setStrokeCap(Paint.Cap.ROUND);	//设置画笔转弯处的连接风格
        paint.setStrokeWidth(1);				//设置默认笔触的宽度为1像素
        paint.setAntiAlias(true);				//设置抗锯齿功能
        paint.setDither(true);					//设置抖动效果
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(0xffffff);				//设置背景颜色
        Paint bmpPaint = new Paint();			//采用默认设置创建一个画笔
        canvas.drawBitmap(cacheBitmap, 0, 0, bmpPaint);		//绘制cacheBitmap
        canvas.drawPath(path, paint);			//绘制路径
        canvas.save(Canvas.ALL_SAVE_FLAG);		//保存canvas状态，最后所有的信息都会保存在第一个创建的Bitmap中
        canvas.restore();		//恢复canvas之前保存的状态，防止保存后对canvas执行的操作队后续的绘制有影响


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
    public void setBitmap(Bitmap bm){
        //int bHeight = bm.getHeight();
        //int bWidth = bm.getWidth();
        cacheCanvas.drawBitmap(bm,0,0,null);
    }

    /*
     * 橡皮擦功能
     */
    public void clear(){
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));	//设置图形重叠时候的处理方式
        paint.setStrokeWidth(50);		//设置笔触的宽度
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
            //将绘图内容压缩为png格式输出到输出流对象中,其中100代表品质
            cacheBitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOS);
            fileOS.flush();        //将缓冲区中的数据全部写出到输出流中
            fileOS.close();        //关闭文件输出流对象

            String dir = sdDir.toString() + "/pictures/" + fileName + ".jpg";
            Log.i("xzy",dir);
            return dir;
        }
        return "";
    }
}
