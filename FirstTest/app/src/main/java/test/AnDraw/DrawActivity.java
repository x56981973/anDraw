package test.AnDraw;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class DrawActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

//        Button button1 = (Button)findViewById(R.id.button);
//        button1.setOnClickListener(new Button.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent,1);
//            }
//        });
    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK) {
//            Uri uri = data.getData();
//            ContentResolver cr = this.getContentResolver();
//            try{
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inJustDecodeBounds = true;
//                //Bitmap bm = BitmapFactory.decodeStream(cr.openInputStream(uri));
//                Bitmap bm = BitmapFactory.decodeStream(cr.openInputStream(uri),null,options);
//                options.inJustDecodeBounds = false;
//                //计算缩放比
//                int beh = (int)(options.outHeight / (float)1000);
//                if (beh <= 0)
//                    beh = 1;
//                int bew = (int)(options.outWidth / (float)1000);
//                if (bew <= 0)
//                    bew = 1;
//                options.inSampleSize = beh > bew? beh : bew;
//                bm = BitmapFactory.decodeStream(cr.openInputStream(uri),null,options);
//                //iv.setImageBitmap(bm);
//
//            }catch (FileNotFoundException e)
//            {
//                e.printStackTrace();
//            }
//        }
//
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DrawView dv = (DrawView)findViewById(R.id.drawView);
        dv.paint.setXfermode(null);        //取消擦除效果
        dv.paint.setStrokeWidth(1);        //初始化笔的宽度
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
                dv.paint.setStrokeWidth(1);        //设置笔触的宽度为1像素
                break;
            case R.id.width_2:
                dv.paint.setStrokeWidth(2);        //设置笔触的宽度为2像素
                break;
            case R.id.width_3:
                dv.paint.setStrokeWidth(3);        //设置笔触的宽度为3像素
                break;
            case R.id.clear:
                dv.clear();                        //擦除绘图
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private static boolean isExit = false;

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }

    private void showDialog(final String dir){
        AlertDialog.Builder alert = new AlertDialog.Builder(DrawActivity.this);
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
}
