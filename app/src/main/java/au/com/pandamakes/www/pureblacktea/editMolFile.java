package au.com.pandamakes.www.pureblacktea;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ListPopupWindowCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by proto on 10/12/2016.
 */
public class editMolFile extends AppCompatActivity implements asyncDataToActivity,View.OnTouchListener,fragmentSaveMolFile.NoticeDialogListener {

    public static final String TAG = "PureBlackTeaLog";
    public EditText mEditTextMolFile;
    public Button mBtnFileMolFile, mBtnRenderMolFile, mBtnMolTo3D;
    private ImageView ivIndicator;
    private TextView tvServerStatus,tvServerRefresh;
    private int duration = 200;

    public void osrRes(String string){

    }
    public void mol2Dto3D(String string){

        View loadingScreen = findViewById(R.id.idLoadingScreen);
        loadingScreen.animate().alpha(0f).setDuration(100);
        loadingScreen.setVisibility(View.GONE);

        if(string.substring(0,5).equals("Error")){
            Toast.makeText(this,string,Toast.LENGTH_SHORT).show();
        }else{
            mEditTextMolFile.setText(string);
            Toast.makeText(getApplicationContext(),"Successfully translated to 3D mol file.",Toast.LENGTH_SHORT).show();
        }
    }
    public void pingServer(String string){

        ivIndicator = (ImageView) findViewById(R.id.ivOSRStatus);
        tvServerStatus = (TextView) findViewById(R.id.tvOSRServerStatus);
        final String pingString = string;

        tvServerRefresh.animate().alpha(0f).setDuration(duration);
        ivIndicator.animate().alpha(0f).setDuration(duration).withEndAction(new Runnable() {
            @Override
            public void run() {
                if(pingString.equals("OK")){
                    ivIndicator.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.server_status_green));
                }else{
                    ivIndicator.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.server_status_red));
                }
                ivIndicator.animate().alpha(1f).setDuration(duration);
            }
        });

        tvServerStatus.animate().alpha(0f).setDuration(duration).withEndAction(new Runnable() {
            @Override
            public void run() {
                if (pingString.equals("OK")) {
                    tvServerStatus.setText("Server OK!");
                    tvServerRefresh.setVisibility(View.GONE);
                } else {
                    tvServerStatus.setText("Server not OK!");
                    tvServerRefresh.setVisibility(View.VISIBLE);
                }
                tvServerRefresh.animate().alpha(1f).setDuration(duration);
                tvServerStatus.animate().alpha(1f).setDuration(duration);
            }
        });
    }

    @Override
    public void onDialogPositiveClick(String newFilename){
        /* save molfile here */
        if(mEditTextMolFile.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(),"Empty edit text field. Not saved.",Toast.LENGTH_LONG).show();
            return;
        }

        try{
            String[] ss = mEditTextMolFile.getText().toString().split(System.getProperty("line.separator"));
            PrintWriter writer = new PrintWriter(getFilesDir()+"/"+newFilename+".mol");
            for(int i = 0; i<ss.length; i++){
                writer.println(ss[i]);
            }
            writer.close();
            if(!(newFilename.equals("mol")||newFilename.equals("mol2d"))){
                Toast.makeText(getApplicationContext(), "mol file " + newFilename + " saved!", Toast.LENGTH_LONG).show();
            }
        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Saving failed!",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDialogLoadMolFile(String filename){
        File molFile = new File(getFilesDir()+"/"+filename+".mol");
        try{
            BufferedReader reader = new BufferedReader(new FileReader(molFile));
            String line;
            String toBeShown = "";
            while((line=reader.readLine())!=null){
                toBeShown+=line;
                toBeShown+=(System.getProperty("line.separator"));
            }
            mEditTextMolFile.setText(toBeShown);
        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Loading file failed. Perhaps the file is corrupted, or it does not exist?",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDialogOverwriteMolFile(String filename){
        try{
            String[] ss = mEditTextMolFile.getText().toString().split(System.getProperty("line.separator"));
            PrintWriter writer = new PrintWriter(getFilesDir()+"/"+filename+".mol");
            for(int i = 0; i<ss.length; i++){
                writer.println(ss[i]);
            }
            writer.close();
            if(!(filename.equals("mol")||filename.equals("mol2d"))){
                Toast.makeText(getApplicationContext(), "mol file " + filename + " saved!", Toast.LENGTH_LONG).show();
            }
        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Saving failed!",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_proper, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.idMenuCamera:{
                Intent intent = new Intent(this,mainActivity.class);
                startActivity(intent);
            }break;
            case R.id.idMenuPicture:{
                Intent intent = new Intent(this,viewCapture.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }break;
            case R.id.idMenuEdit:{
                //menu is dismissed
                Intent intent = new Intent(this,editMolFile.class);
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

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.molfile_control);

        pingServer pingserver = new pingServer(this);
        pingserver.execute();

        mBtnFileMolFile = (Button) findViewById(R.id.btnFileMolFile);
        mBtnRenderMolFile = (Button) findViewById(R.id.btnRenderMolFile);
        mBtnMolTo3D = (Button) findViewById(R.id.btnMolto3D);
        //TextView textViewTitle = (TextView) findViewById(R.id.textViewTitleBar);

        mBtnFileMolFile.setOnTouchListener(this);
        mBtnRenderMolFile.setOnTouchListener(this);
        mBtnMolTo3D.setOnTouchListener(this);

        Typeface fontawesome = Typeface.createFromAsset(getAssets(), "fonts/fontawesome.ttf");
        mBtnFileMolFile.setTypeface(fontawesome);
        mBtnRenderMolFile.setTypeface(fontawesome);
        mBtnMolTo3D.setTypeface(fontawesome);
        //textViewTitle.setTypeface(fontawesome);

        tvServerRefresh = (TextView) findViewById(R.id.tvOSRStatusRefresh);
        tvServerRefresh.setTypeface(fontawesome);

        mEditTextMolFile = (EditText) findViewById(R.id.editTextMolFile);
        mEditTextMolFile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mEditTextMolFile.getText().toString().matches("")) {
                    mBtnRenderMolFile.setEnabled(false);
                    mBtnMolTo3D.setEnabled(false);
                } else {
                    mBtnRenderMolFile.setEnabled(true);
                    mBtnMolTo3D.setEnabled(true);
                }
            }
        });

        try{
            Intent intent = getIntent();
            String string = intent.getExtras().getString("molstring");
            mEditTextMolFile.setText(string);
        }catch (NullPointerException e){
            /* no string passed. so setting mEditTextMolFile as empty */
            mEditTextMolFile.setText("");
        }

        try{
            Intent intent = getIntent();
            String name = intent.getData().getPath();
            File molFile = new File(name);
            try{
                BufferedReader reader = new BufferedReader(new FileReader(molFile));
                String line;
                String toBeShown = "";
                while((line=reader.readLine())!=null){
                    toBeShown+=line;
                    toBeShown+=(System.getProperty("line.separator"));
                }
                mEditTextMolFile.setText(toBeShown);
            }catch (IOException e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Loading file failed. Perhaps the file is corrupted, or it does not exist?",Toast.LENGTH_LONG).show();
            }
        }catch (NullPointerException e){
            Log.i(TAG,"nothing to show");
        }
        /*

        LinearLayout rootView = (LinearLayout) findViewById(R.id.idMenuPlaceHolder);
        menuSupportClass menusupport = new menuSupportClass(getApplicationContext(),rootView,null);
        */
    }

    public boolean onTouch(View v, MotionEvent e){
        switch(e.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP: {
                switch (v.getId()){
                    case R.id.btnFileMolFile:{
                        DialogFragment dialog = new fragmentSaveMolFile();
                        dialog.show(getFragmentManager(), "Notice");
                    }break;
                    case R.id.btnRenderMolFile:{
                        /* this should also be where the edit text gets written into mol.mol, rather than on async complete */

                        onDialogPositiveClick("mol");

                        Intent intent = new Intent(this,mainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                    }break;
                    case R.id.btnMolto3D:{
                        onDialogPositiveClick("mol2d");
                        mol2Dto3D async = new mol2Dto3D(this);
                        async.execute(getFilesDir().toString());

                        View loadingScreen = findViewById(R.id.idLoadingScreen);
                        loadingScreen.setAlpha(0f);
                        loadingScreen.setVisibility(View.VISIBLE);
                        loadingScreen.animate().alpha(1f).setDuration(200);
                        //add loading screen);
                    }break;
                }
            }
            break;
        }
        return true;
    }
}
