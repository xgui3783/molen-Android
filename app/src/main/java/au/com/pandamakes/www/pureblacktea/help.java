package au.com.pandamakes.www.pureblacktea;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by proto on 15/12/2016.
 */
public class help extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_screen);

        TextView textView = (TextView) findViewById(R.id.tvHeart);
        Typeface fontawesome = Typeface.createFromAsset(getAssets(), "fonts/fontawesome.ttf");
        textView.setTypeface(fontawesome);

        Button btnWWW = (Button) findViewById(R.id.btnWebsite);
        Button btnEmail = (Button) findViewById(R.id.btnEmailSupport);
        Button btnAR = (Button) findViewById(R.id.btnARURL);
        btnWWW.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:{
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.pandamakes.com.au"));
                        startActivity(intent);
                    }
                }
                return false;
            }
        });
        btnEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:{
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:panda@pandamakes.com.au"));
                        intent.putExtra(Intent.EXTRA_EMAIL, "panda@pandamakes.com.au");
                        intent.putExtra(Intent.EXTRA_SUBJECT,"Feedback on molens");
                        startActivity(intent);
                    }
                }
                return false;
            }
        });
        btnAR.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP: {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.pandamakes.com.au/molens/ar.png"));
                        startActivity(intent);
                    }
                }
                return false;
            }
        });

        /*
        LinearLayout rootView = (LinearLayout) findViewById(R.id.idMenuPlaceHolder);
        menuSupportClass menusupport = new menuSupportClass(getApplicationContext(),rootView,null);
        */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //really, there's only 1 back button here. so just call back?
        this.finish();
        return true;
    }
}
