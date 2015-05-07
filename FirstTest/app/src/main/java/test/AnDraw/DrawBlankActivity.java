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
import android.view.ext.SatelliteMenu;
import android.view.ext.SatelliteMenuItem;

import java.util.ArrayList;
import java.util.List;


public class DrawBlankActivity extends ActionBarActivity {
    private DrawView dv;
    private int menuLevel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_blank);
        getSupportActionBar().hide();
        dv = (DrawView)findViewById(R.id.drawView_blank);

        initMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        dv.paint.setXfermode(null);        //取消擦除效果
        dv.paint.setStrokeWidth(10);        //初始化笔的宽度
        switch (item.getItemId()) {
            case R.id.color_red:
                dv.paint.setColor(Color.RED);        //设置画笔的颜色为红色
                item.setChecked(true);
                break;
            case R.id.color_black:
                dv.paint.setColor(Color.BLACK);        //设置画笔的颜色为黑色
                item.setChecked(true);
                break;
            case R.id.color_white:
                dv.paint.setColor(Color.WHITE);        //设置画笔的颜色为蓝色
                item.setChecked(true);
                break;
            case R.id.width_1:
                dv.paint.setStrokeWidth(10);        //设置笔触的宽度为10像素
                item.setChecked(true);
                break;
            case R.id.width_2:
                dv.paint.setStrokeWidth(20);        //设置笔触的宽度为20像素
                item.setChecked(true);
                break;
            case R.id.width_3:
                dv.paint.setStrokeWidth(30);        //设置笔触的宽度为30像素
                item.setChecked(true);
                break;
            case R.id.clear:
                dv.clear();                        //擦除绘图
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
        getMenuInflater().inflate(R.menu.menu_draw_blank, menu);
        return true;
    }

    private void showDialog(final String dir){
        AlertDialog.Builder alert = new AlertDialog.Builder(DrawBlankActivity.this);
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

    void initMenu(){
        menuLevel = 0;
        final SatelliteMenu menu = (SatelliteMenu) findViewById(R.id.menu);
        final List<SatelliteMenuItem> itemsLevelOne = new ArrayList<SatelliteMenuItem>();
        final List<SatelliteMenuItem> itemsLevelTwoColor = new ArrayList<SatelliteMenuItem>();
        final List<SatelliteMenuItem> itemsLevelTwoThickness = new ArrayList<SatelliteMenuItem>();

        itemsLevelOne.add(new SatelliteMenuItem(5, R.drawable.ic_save));
        itemsLevelOne.add(new SatelliteMenuItem(4, R.drawable.ic_reset));
        itemsLevelOne.add(new SatelliteMenuItem(3, R.drawable.ic_eraser));
        itemsLevelOne.add(new SatelliteMenuItem(2, R.drawable.ic_thickness));
        itemsLevelOne.add(new SatelliteMenuItem(1, R.drawable.ic_color));

        itemsLevelTwoColor.add(new SatelliteMenuItem(1,R.drawable.ic_color_red));
        itemsLevelTwoColor.add(new SatelliteMenuItem(2,R.drawable.ic_color_green));
        itemsLevelTwoColor.add(new SatelliteMenuItem(3,R.drawable.ic_color_blue));
        itemsLevelTwoColor.add(new SatelliteMenuItem(4,R.drawable.ic_color_black));

        itemsLevelTwoThickness.add(new SatelliteMenuItem(1,R.drawable.ic_thickness_1));
        itemsLevelTwoThickness.add(new SatelliteMenuItem(2,R.drawable.ic_thickness_2));
        itemsLevelTwoThickness.add(new SatelliteMenuItem(3, R.drawable.ic_thickness_3));

        menu.setSatelliteDistance(400);
        menu.addItems(itemsLevelOne);
        menu.setOnItemClickedListener(new SatelliteMenu.SateliteClickedListener(){

            @Override
            public void eventOccured(int id) {
                if(menuLevel == 0){
                    dv.paint.setXfermode(null);        //取消擦除效果

                    switch (id){
                        case 1:

                            menu.clearMenu();
                            menu.addItems(itemsLevelTwoColor);
                            menuLevel = 1;
                            break;
                        case 2:

                            menu.clearMenu();
                            menu.addItems(itemsLevelTwoThickness);
                            menuLevel = 2;
                            break;
                        case 3:
                            dv.clear();
                            break;
                        case 4:
                            dv.reset();
                            break;
                        case 5:
                            String dir = dv.save();            //保存绘图
                            showDialog(dir);
                            break;
                        default:break;
                    }
                }else if(menuLevel == 1){
                    switch (id){
                        case 1:
                            dv.paint.setColor(Color.RED);        //设置画笔的颜色为红色
                            menu.clearMenu();
                            menu.addItems(itemsLevelOne);
                            menuLevel = 0;
                            break;
                        case 2:
                            dv.paint.setColor(Color.GREEN);        //设置画笔的颜色为红色
                            menu.clearMenu();
                            menu.addItems(itemsLevelOne);
                            menuLevel = 0;
                            break;
                        case 3:
                            dv.paint.setColor(Color.BLUE);        //设置画笔的颜色为红色
                            menu.clearMenu();
                            menu.addItems(itemsLevelOne);
                            menuLevel = 0;
                            break;
                        case 4:
                            dv.paint.setColor(Color.BLACK);        //设置画笔的颜色为红色
                            menu.clearMenu();
                            menu.addItems(itemsLevelOne);
                            menuLevel = 0;
                            break;
                        default:
                            menu.clearMenu();
                            menu.addItems(itemsLevelOne);
                            menuLevel = 0;
                            break;
                    }
                }else{
                    dv.paint.setStrokeWidth(10);        //初始化笔的宽度
                    switch (id){
                        case 1:

                            dv.paint.setStrokeWidth(10);        //设置笔触的宽度为10像素
                            menu.clearMenu();
                            menu.addItems(itemsLevelOne);
                            menuLevel = 0;
                            break;
                        case 2:
                            dv.paint.setStrokeWidth(20);        //设置笔触的宽度为20像素
                            menu.clearMenu();
                            menu.addItems(itemsLevelOne);
                            menuLevel = 0;
                            break;
                        case 3:
                            dv.paint.setStrokeWidth(30);        //设置笔触的宽度为30像素
                            menu.clearMenu();
                            menu.addItems(itemsLevelOne);
                            menuLevel = 0;

                            break;

                        default:
                            menu.clearMenu();
                            menu.addItems(itemsLevelOne);
                            menuLevel = 0;
                            break;
                    }
                }

            }
        });
    }
}
