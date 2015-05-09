package test.AnDraw;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import test.AnDraw.R;

public class AboutActivity extends ActionBarActivity {


    private Button contactBtn, returnBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getSupportActionBar().hide();

        init();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about, menu);
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

    private void init(){
        contactBtn = (Button) findViewById(R.id.btn_contact);
        returnBtn = (Button) findViewById(R.id.btn_return);

        View.OnClickListener btnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v == contactBtn){

                }else if(v == returnBtn) {
//                    Intent intent = new Intent(AboutActivity.this, MainActivity.class);
//                    startActivity(intent);
                    finish();
                }
            }
        };
        contactBtn.setOnClickListener(btnListener);
        returnBtn.setOnClickListener(btnListener);
    }
}
