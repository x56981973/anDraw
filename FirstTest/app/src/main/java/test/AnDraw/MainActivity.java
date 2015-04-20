package test.AnDraw;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileNotFoundException;


public class MainActivity extends ActionBarActivity {
    private static int flag = 0;
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

    private Button button1;
    private Button button2;
    private Button.OnClickListener mainOnClickListener;
    private String  background[] = new String[] {"白色","黑色","红色","蓝色"};
    private  int view_width;
    private  int view_height;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics  dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        view_width = dm.widthPixels;
        view_height = dm.heightPixels;

        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);

        mainOnClickListener = new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(v == button1){
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent,1);
                }else if(v == button2){
                    new AlertDialog.Builder(MainActivity.this)
                    .setTitle("请选择背景颜色")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setItems(background,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i("xxxxxxx",String.valueOf(which));
                            makeBackground(which);

                            Intent intent = new Intent(MainActivity.this,DrawBlankActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();

                }

            }
        };
        button1.setOnClickListener(mainOnClickListener);
        button2.setOnClickListener(mainOnClickListener);
    }

    private void makeBackground(int color){
        Resources res = getResources();
        Bitmap temp;
        switch (color)
        {
            case 0: temp = BitmapFactory.decodeResource(res, R.drawable.white);break;
            case 1: temp = BitmapFactory.decodeResource(res, R.drawable.black);break;
            case 2: temp = BitmapFactory.decodeResource(res, R.drawable.red);break;
            case 3: temp = BitmapFactory.decodeResource(res, R.drawable.blue);break;
            default: temp = BitmapFactory.decodeResource(res, R.drawable.white);break;
        }
        Bitmap bm;
        bm = Bitmap.createBitmap(temp,0,0,view_width,view_height);
        BitmapProvider bitmapProvider = new BitmapProvider();
        bitmapProvider.setBitmap(bm);
        temp.recycle();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();
            try{
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                //Bitmap bm = BitmapFactory.decodeStream(cr.openInputStream(uri));
                Bitmap bm = BitmapFactory.decodeStream(cr.openInputStream(uri),null,options);
                options.inJustDecodeBounds = false;
                //计算缩放比
                int beh = (int)(options.outHeight / (float)view_height);
                if (beh <= 0)
                    beh = 1;
                int bew = (int)(options.outWidth / (float) view_width);
                if (bew <= 0)
                    bew = 1;
                options.inSampleSize = beh > bew? beh : bew;
                bm = BitmapFactory.decodeStream(cr.openInputStream(uri),null,options);

                BitmapProvider bitmapProvider = new BitmapProvider();
                bitmapProvider.setBitmap(bm);
                Intent intent = new Intent(MainActivity.this,DrawPicActivity.class);
                startActivity(intent);
            }catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
