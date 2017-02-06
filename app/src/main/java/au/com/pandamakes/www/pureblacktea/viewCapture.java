package au.com.pandamakes.www.pureblacktea;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;


/**
 * Created by proto on 19/11/2016.
 */
public class viewCapture extends AppCompatActivity implements asyncDataToActivity,View.OnTouchListener{

    public static final String TAG = "PureBlackTeaLog";
    private Button btnAsyncStart,btnClear;
    private ImageView iv,ivIndicator;
    private LinearLayout ll;
    private TextView tvOSRServerRefresh,tvOSRStatus;
    private int duration = 100;

    public void mol2Dto3D(String string){

    }

    public void osrRes(String string){
        if(string.substring(0,5).equals("Error")){
            Toast.makeText(this,string,Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(this,editMolFile.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("molstring",string);
            startActivity(intent);
        }

        View loadingScreen = findViewById(R.id.idLoadingScreen);
        loadingScreen.animate().alpha(0f).setDuration(100);
        loadingScreen.setVisibility(View.GONE);
        Button btn = (Button) findViewById(R.id.btnAsyncStart);
        btn.setEnabled(true);
    }

    public void pingServer(String string){
        ivIndicator = (ImageView) findViewById(R.id.ivOSRStatus);
        tvOSRStatus = (TextView) findViewById(R.id.tvOSRServerStatus);
        final String pingString = string;

        tvOSRServerRefresh.animate().alpha(0f).setDuration(duration);
        ivIndicator.animate().alpha(0f).setDuration(duration).withEndAction(new Runnable() {
            @Override
            public void run() {
                if(pingString.equals("OK")){
                    ivIndicator.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.server_status_green));
                }else{
                    ivIndicator.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.server_status_red));
                }
                ivIndicator.animate().alpha(1f).setDuration(duration);
            }
        });

        tvOSRStatus.animate().alpha(0f).setDuration(duration).withEndAction(new Runnable() {
            @Override
            public void run() {
                if (pingString.equals("OK")) {
                    tvOSRStatus.setText("OSR Server is up and running");
                    tvOSRServerRefresh.setVisibility(View.GONE);
                } else {
                    tvOSRStatus.setText("OSR Server might be down");
                    tvOSRServerRefresh.setVisibility(View.VISIBLE);
                }
                tvOSRServerRefresh.animate().alpha(1f).setDuration(duration);
                tvOSRStatus.animate().alpha(1f).setDuration(duration);
            }
        });
        /*
        ObjectAnimator animatorIndicator = ObjectAnimator.ofFloat(iv,"alpha",1f,0f).setDuration(200);
        ObjectAnimator animatorText = ObjectAnimator.ofFloat(tv,"alpha",1f,0f).setDuration(210);
        AnimatorSet set = new AnimatorSet();
        set.play(animatorIndicator).with(animatorText);
        set.start();

        if(string.equals("OK")){
            iv.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.server_status_green));
            tv.setText("OSR Server is up and running");
        }else{
            iv.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.server_status_red));
            tv.setText("OSR Server might be down");
        }

        ObjectAnimator animatorIndicator2 = ObjectAnimator.ofFloat(iv,"alpha",0f,1f).setDuration(200);
        ObjectAnimator animatorText2 = ObjectAnimator.ofFloat(tv,"alpha",0f,1f).setDuration(200);
        AnimatorSet set2 = new AnimatorSet();
        set.play(animatorIndicator2).with(animatorText2);
        set2.start();
        */
    }

    private void rePingOSRServer(){

        tvOSRServerRefresh.animate().alpha(0f).setDuration(duration);
        ivIndicator.animate().alpha(0f).setDuration(duration).withEndAction(new Runnable() {
            @Override
            public void run() {

                ivIndicator.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.server_status_yellow));
                ivIndicator.animate().alpha(1f).setDuration(duration);
            }
        });

        tvOSRStatus.animate().alpha(0f).setDuration(duration).withEndAction(new Runnable() {
            @Override
            public void run() {
                tvOSRStatus.setText("Requesting OSR Server status ...");
                tvOSRStatus.animate().alpha(1f).setDuration(duration);
            }
        });

        pingServer pingserver = new pingServer(this);
        pingserver.execute();
    }

    @Override
    protected void onActivityResult(int requestedCode, int resultCode, Intent data){
        /* needed to pick image/hopefully also .mol file */
        super.onActivityResult(requestedCode, resultCode, data);
        if(requestedCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data){
            Uri uri = data.getData();
            InputStream stream = null;
            try{
                stream = getContentResolver().openInputStream(uri);
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }

            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
            bmpFactoryOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bmp = BitmapFactory.decodeStream(stream,null,bmpFactoryOptions);

            iv.setImageBitmap(bmp);

            iv.setAlpha(1f);
            iv.setVisibility(View.VISIBLE);
            ll.setVisibility(View.GONE);

            btnClear.setEnabled(true);
            btnAsyncStart.setEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_proper,menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewcapture);

        pingServer pingserver = new pingServer(this);
        pingserver.execute();

        iv = (ImageView) findViewById(R.id.imageViewCroppedImage);
        ll = (LinearLayout) findViewById(R.id.llViewCaptureControl);

        btnClear = (Button) findViewById(R.id.btnViewCaptureClear);
        btnAsyncStart = (Button) findViewById(R.id.btnAsyncStart);

        Intent intent = getIntent();
        try{
            /* something is captured */
            intent.getExtras().getBoolean("flagToCaptureImage");
            ll.setVisibility(View.GONE);
            iv.setVisibility(View.VISIBLE);

            /* nb if file does not exist, it will throw an error and app will crash */
            Bitmap bmp = BitmapFactory.decodeFile(getFilesDir().toString().concat("/croppedImg.png"));
            iv.setImageBitmap(bmp);
        }catch (NullPointerException e){
            /* not captured */
            iv.setVisibility(View.GONE);
            ll.setVisibility(View.VISIBLE);
            btnClear.setEnabled(false);
            btnAsyncStart.setEnabled(false);
        }


        TextView btnScanAgain = (TextView) findViewById(R.id.tvViewCaptureScanAgain);
        TextView btnFromGallery = (TextView) findViewById(R.id.tvViewCaptureFromGallery);
        tvOSRServerRefresh = (TextView) findViewById(R.id.tvOSRStatusRefresh);

        //TextView textviewTitle = (TextView) findViewById(R.id.textViewTitleBar);

        Typeface fontawesome = Typeface.createFromAsset(getAssets(), "fonts/fontawesome.ttf");
        btnClear.setOnTouchListener(this);
        btnAsyncStart.setOnTouchListener(this);
        btnScanAgain.setOnTouchListener(this);
        btnFromGallery.setOnTouchListener(this);

        btnClear.setTypeface(fontawesome);
        btnAsyncStart.setTypeface(fontawesome);
        btnScanAgain.setTypeface(fontawesome);
        btnFromGallery.setTypeface(fontawesome);
        tvOSRServerRefresh.setTypeface(fontawesome);

        tvOSRServerRefresh.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_DOWN:{
                        rePingOSRServer();
                    }break;
                }
                return true;
            }
        });
        //textviewTitle.setTypeface(fontawesome);

        /*
        LinearLayout rootView = (LinearLayout) findViewById(R.id.idMenuPlaceHolder);
        menuSupportClass menusupport = new menuSupportClass(getApplicationContext(),rootView,null);
        */
    }

    public boolean onTouch(View view, MotionEvent event){
        switch(event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_UP:{
                switch(view.getId()){
                    case R.id.btnAsyncStart:{
                        async upload = new async(this);
                        upload.execute(getFilesDir().toString());
                        View loadingScreen = findViewById(R.id.idLoadingScreen);
                        loadingScreen.setAlpha(0f);
                        loadingScreen.setVisibility(View.VISIBLE);
                        loadingScreen.animate().alpha(1f).setDuration(200);
                        view.setEnabled(false);
                    }break;
                    case R.id.btnViewCaptureClear:{
                        iv.animate().alpha(0f).setDuration(200).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                iv.setVisibility(View.GONE);
                                ll.setVisibility(View.VISIBLE);
                                ll.setAlpha(0f);
                                ll.animate().alpha(1f).setDuration(200);
                                btnClear.setEnabled(false);
                                btnAsyncStart.setEnabled(false);
                            }
                        });
                    }break;
                    case R.id.tvViewCaptureFromGallery:{
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent,RESULT_LOAD_IMAGE);
                        //choose image from gallery
                    }break;
                    case R.id.tvViewCaptureScanAgain:{
                        Intent intent = new Intent(this,mainActivity.class);
                        intent.putExtra("flagCaptureImage",true);
                        startActivity(intent);
                    }break;
                }
            }break;
        }
        return true;
    }

    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.idMenuCamera:{
                Intent intent = new Intent(this,mainActivity.class);
                startActivity(intent);
            }break;
            case R.id.idMenuPicture:{
                //menu is dismissed
                Intent intent = new Intent(this,viewCapture.class);
                startActivity(intent);
            }break;
            case R.id.idMenuEdit:{
                Intent intent = new Intent(this,editMolFile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);

            }break;
            case R.id.idMenuGear:{
                Intent intent = new Intent(this,setting.class);
                startActivity(intent);

            }break;
            case R.id.idMenuQuestion:{
                Intent intent = new Intent(this,help.class);
                startActivity(intent);

            }break;
        }
        return true;
    }
}
