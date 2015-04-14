package test.AnDraw;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;


public class DrawPicActivity extends ActionBarActivity {
    private DrawView dv;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            // TODO Auto-generated method stub
            switch (status){
                case BaseLoaderCallback.SUCCESS:
                    Log.i("opencv", "成功加载");
                    break;
                default:
                    super.onManagerConnected(status);
                    Log.i("opencv", "加载失败");
                    break;
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_pic);

        dv = (DrawView)findViewById(R.id.drawView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        dv.paint.setXfermode(null);        //取消擦除效果
        //dv.paint.setStrokeWidth(10);        //初始化笔的宽度
        switch (item.getItemId()) {
            case R.id.color_red:
                dv.paint.setColor(Color.RED);        //设置画笔的颜色为红色
                item.setChecked(true);
                break;
            case R.id.color_green:
                dv.paint.setColor(Color.GREEN);        //设置画笔的颜色为绿色
                item.setChecked(true);
                break;
            case R.id.color_blue:
                dv.paint.setColor(Color.BLUE);        //设置画笔的颜色为蓝色
                item.setChecked(true);
                break;
            case R.id.width_1:
                dv.paint.setStrokeWidth(10);        //设置笔触的宽度为1像素
                break;
            case R.id.width_2:
                dv.paint.setStrokeWidth(20);        //设置笔触的宽度为2像素
                break;
            case R.id.width_3:
                dv.paint.setStrokeWidth(30);        //设置笔触的宽度为3像素
                break;
            case R.id.clear:
                dv.clear();                        //擦除绘图
                break;
            case R.id.fx_gray:
                dv.procSrc2Gray();        //将图片转为灰度
                break;
            case R.id.fx_gauss:
                dv.paint.setStrokeWidth(20);        //设置笔触的宽度为2像素
                break;
            case R.id.fx_bw:
                dv.paint.setStrokeWidth(30);        //设置笔触的宽度为3像素
                break;
            case R.id.reset:
                dv.reset();
                break;
            case R.id.save:
                String dir = dv.save();            //保存绘图
                showDialog(dir);
                break;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_draw_pic, menu);
        return true;
    }

    private void showDialog(final String dir){
        AlertDialog.Builder alert = new AlertDialog.Builder(DrawPicActivity.this);
        alert.setTitle("提示");
        alert.setMessage("图片已保存到" + dir);
        //Log.i("xxx","xxx");
        alert.setPositiveButton("打开",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + dir), "image/*");
                startActivity(intent);
            }
        });
        alert.setNegativeButton("关闭",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            BitmapProvider.destroy();
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }
}
