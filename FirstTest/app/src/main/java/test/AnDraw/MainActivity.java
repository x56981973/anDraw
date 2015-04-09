package test.AnDraw;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.graphics.Matrix;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
    private ImageView iv;

    private Button.OnClickListener mainOnClickListener;

    private Bitmap bitmap, alteredBitmap;
    // 定義畫布
    private Canvas canvas;
    // 定義畫筆
    private Paint paint;
    // 定義輸入矩陣，該類使之在一幅圖像上應用空間轉換（比如旋轉，評議，縮放，裁剪等）
    private Matrix matrix;
    // 定義按下和停止的位置（x,y）座標
    private float downX = 0, downY = 0, upX = 0, upY = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = (Button)findViewById(R.id.button);
        iv = (ImageView)findViewById(R.id.imageView);

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
                }else{

                }

            }
        };
        button1.setOnClickListener(mainOnClickListener);
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
                int beh = (int)(options.outHeight / (float)1000);
                if (beh <= 0)
                    beh = 1;
                int bew = (int)(options.outWidth / (float)1000);
                if (bew <= 0)
                    bew = 1;
                options.inSampleSize = beh > bew? beh : bew;
                bm = BitmapFactory.decodeStream(cr.openInputStream(uri),null,options);
                iv.setImageBitmap(bm);
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
