package au.com.pandamakes.www.pureblacktea;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by proto on 27/01/2017.
 */
public class splashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, mainActivity.class);
        startActivity(intent);
        finish();
    }
}
