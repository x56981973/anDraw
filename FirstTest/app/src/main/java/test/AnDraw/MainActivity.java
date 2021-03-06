package test.AnDraw;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static java.lang.Thread.sleep;


public class MainActivity extends ActionBarActivity {
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

    private Button btnPic, btnBlank, btnAbout, btnUpdate;
    private Button.OnClickListener mainOnClickListener;
    private String bgColor[] = new String[] {"白色","黑色","红色","蓝色"};
    private  int view_width;
    private  int view_height;
    private ImageView iconView;
    private LinearLayout containerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        DisplayMetrics  dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        view_width = dm.widthPixels;
        view_height = dm.heightPixels;

        //int actionBarHeight = getActionBar().getHeight();
        //Log.i("xxx",String.valueOf(actionBarHeight));

        BitmapProvider bitmapProvider = new BitmapProvider();
        bitmapProvider.width = view_width;
        bitmapProvider.height = view_height;

        btnPic = (Button)findViewById(R.id.btn_pic);
        btnBlank = (Button)findViewById(R.id.btn_blank);
        btnAbout = (Button)findViewById(R.id.btn_about);
        btnUpdate = (Button)findViewById(R.id.btn_update);

        mainOnClickListener = new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(v == btnPic){
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent,1);
                }else if(v == btnBlank){
                    new AlertDialog.Builder(MainActivity.this)
                    .setTitle("请选择背景颜色")
                    .setItems(bgColor, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i("xxx", String.valueOf(which));
                            makeBackground(which);

                            Intent intent = new Intent(MainActivity.this, DrawBlankActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
                }else if(v == btnAbout){
                    Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                    startActivity(intent);
                }else if(v == btnUpdate){
                    UpdateProvider updateProvider = new UpdateProvider(getApplicationContext());
                    updateProvider.checkUpdateInfo();
                }

            }
        };
        btnPic.setOnClickListener(mainOnClickListener);
        btnBlank.setOnClickListener(mainOnClickListener);
        btnAbout.setOnClickListener(mainOnClickListener);
        btnUpdate.setOnClickListener(mainOnClickListener);
//        blankView = (LinearLayout) findViewById(R.id.blank_main);
        containerView = (LinearLayout) findViewById(R.id.container_main);
        containerView.setVisibility(View.INVISIBLE);

        animProcess();

    }

    public Bitmap readBitmap(int id){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig=Bitmap.Config.RGB_565;//表示16位位图 565代表对应三原色占的位数
        InputStream is = getResources().openRawResource(id);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    private void animProcess(){

        final AlphaAnimation loadAnim1 = new AlphaAnimation(0,1);
        loadAnim1.setDuration(1200);
        TranslateAnimation loadAnim2 = new TranslateAnimation(0,0,100,0);
        loadAnim2.setDuration(800);
        iconView = (ImageView) findViewById(R.id.icon_main);

        final TranslateAnimation mHiddenAction = new TranslateAnimation(0,0,100,0);
        mHiddenAction.setDuration(2500);

//        loadAnimSet.addAnimation(loadAnim2);




//        blankView.setAnimation(mHiddenAction);
//        blankView.setVisibility(View.GONE);
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                AnimationSet loadAnimSet = new AnimationSet(MainActivity.this, null);
                loadAnimSet.addAnimation(loadAnim1);
                iconView.startAnimation(loadAnimSet);
                try {
                    sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                containerView.startAnimation(mHiddenAction);
                containerView.setVisibility(View.VISIBLE);
            }
        }.execute();
    }

    private void makeBackground(int color){
        Bitmap temp;
        switch (color)
        {
            case 0: temp = readBitmap( R.drawable.white);break;
            case 1: temp = readBitmap( R.drawable.black);break;
            case 2: temp = readBitmap( R.drawable.red);break;
            case 3: temp = readBitmap( R.drawable.blue);break;
            default: temp = readBitmap( R.drawable.white);break;
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
