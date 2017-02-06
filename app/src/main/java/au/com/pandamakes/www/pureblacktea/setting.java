package au.com.pandamakes.www.pureblacktea;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by proto on 13/12/2016.
 */
public class setting extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        /*
        LinearLayout rootView = (LinearLayout) findViewById(R.id.idMenuPlaceHolder);
        menuSupportClass menusupport = new menuSupportClass(getApplicationContext(),rootView,null);


        Typeface fontawesome = Typeface.createFromAsset(getAssets(), "fonts/fontawesome.ttf");

        TextView textViewTitle = (TextView) findViewById(R.id.textViewTitleBar);
        textViewTitle.setTypeface(fontawesome);
        */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction().replace(R.id.idPreferenceLayout,new SettingFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //really, there's only 1 back button here. so just call back?
        this.finish();
        return true;
    }

    public static class SettingFragment extends PreferenceFragment{
        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preference);
        }
    }
}
