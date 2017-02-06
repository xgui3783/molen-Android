package au.com.pandamakes.www.pureblacktea;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by proto on 06/12/2016.
 */
public class fragmentSaveMolFile extends DialogFragment {

    public ArrayList<String> mMolFilenames;
    public EditText mFilenameInput;
    public ListPopupWindow mMolFileMenu;
    public String mFocusMolFile = "";
    public Handler mHandler;
    public Runnable mRunnable;

    public interface NoticeDialogListener{
        void onDialogPositiveClick(String filename);
        void onDialogLoadMolFile(String filename);
        void onDialogOverwriteMolFile(String filename);
    }

    @Override
    public void onCancel(DialogInterface dialog){
        if(mFocusMolFile.equals("")){
            super.onCancel(dialog);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        /* Title */
        builder.setTitle("File");

        /* Layout of the message */
        LinearLayout filenameLayout = new LinearLayout(getActivity().getApplicationContext());
        filenameLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(32, 0, 32, 16);

        /* text field, for filename */
        mFilenameInput = new EditText(getActivity());
        mFilenameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        mFilenameInput.setLayoutParams(params);
        filenameLayout.addView(mFilenameInput);
        
        /* already saved file */
        final TextView textView = new TextView(getActivity());
        textView.setText("Existing files:");
        textView.setLayoutParams(params);
        filenameLayout.addView(textView);

        GridView gridViewOldFiles = new GridView(getActivity());
        gridViewOldFiles.setLayoutParams(params);
        gridViewOldFiles.setVerticalSpacing(10);
        gridViewOldFiles.setHorizontalSpacing(5);
        gridViewOldFiles.setNumColumns(3);

        File[] arrfiles = getActivity().getFilesDir().listFiles();
        mMolFilenames = new ArrayList<>();
        for(int i = 0; i<arrfiles.length; i++){

            /* not even showing mol.mol */
            if(arrfiles[i].getName().endsWith(".mol")){
                if(!(arrfiles[i].getName().equals("mol.mol")||arrfiles[i].getName().equals("mol2d.mol"))){
                    mMolFilenames.add(arrfiles[i].getName().replace(".mol",""));
                }
            }
        }

        final ArrayAdapter<String> arrayadapter = new ArrayAdapter<String>(getActivity(),R.layout.singletextview,mMolFilenames);
        gridViewOldFiles.setAdapter(arrayadapter);
        /* existing files onclicklistener */
        gridViewOldFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //TextView textview = (TextView) view;
                Log.i(staticConfig.TAG,mFocusMolFile);

                TextView textview = (TextView) view;
                if(mFocusMolFile.equals(textview.getText().toString())){
                    mHandler.removeCallbacks(mRunnable);
                    mMolFileMenu.dismiss();
                    mFocusMolFile = "";
                    Log.i(staticConfig.TAG, "on item click true");
                }else{
                    Log.i(staticConfig.TAG,"on item click false");
                    mFocusMolFile = textview.getText().toString();

                    Log.i(staticConfig.TAG,textview.getText().toString());
                    Log.i(staticConfig.TAG, mFocusMolFile);

                    mHandler.removeCallbacks(mRunnable);
                    mHandler.postDelayed(mRunnable, 3 * 1000);

                    mMolFileMenu.setAnchorView(view);
                    mMolFileMenu.show();
                }
            }
        });


        filenameLayout.addView(gridViewOldFiles);
        
        /* appending the masterview to alertdialog */
        builder.setView(filenameLayout);

        /* configuring the save/cancel buttons */
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Positive
                //Do nothing here, as it will be overwritten later
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Negative
            }
        });

        /* handles the anchored list */
        mMolFileMenu = new ListPopupWindow(getActivity());
        final String[] molfilemenuitems = {"Overwrite","Load","Delete"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),R.layout.molfilemenu,molfilemenuitems);
        mMolFileMenu.setAdapter(adapter);
        mMolFileMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textview = (TextView) view;
                switch (textview.getText().toString()) {
                    case "Overwrite": {
                        mListener.onDialogOverwriteMolFile(mFocusMolFile);
                        getDialog().dismiss();
                    }
                    break;
                    case "Load": {
                        mListener.onDialogLoadMolFile(mFocusMolFile);
                        getDialog().dismiss();
                    }
                    break;
                    case "Delete": {
                        File deletefile = new File(mContext.getFilesDir() + "/" + mFocusMolFile + ".mol");
                        if (deletefile.delete()) {
                            Toast.makeText(mContext, "File deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "File not deleted. Perhaps it does not exist?", Toast.LENGTH_SHORT).show();
                        }
                        for (int i = 0; i < mMolFilenames.size(); i++) {
                            if (mMolFilenames.get(i).equals(mFocusMolFile)) {
                                mMolFilenames.remove(i);
                                arrayadapter.notifyDataSetChanged();
                                getDialog().dismiss();
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        });



        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                mMolFileMenu.dismiss();
                mFocusMolFile = "";
            }
        };

        return builder.create();
    }

    NoticeDialogListener mListener;
    public Context mContext;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        try{
            mListener = (NoticeDialogListener) activity;
            mContext = activity.getApplicationContext();
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() +"must implement NoticeDialogListner");
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        final AlertDialog dialog = (AlertDialog) getDialog();
        if(dialog!=null){

            /* overwrites the OK button, if duplicate filename */
            Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(mFilenameInput.getText().toString().toLowerCase().equals("mol")||mFilenameInput.getText().toString().toLowerCase().equals("mol2d")){
                        Toast.makeText(getActivity().getApplicationContext(),"Sorry, mol & mol2d is reserved for rendering use.",Toast.LENGTH_SHORT).show();
                    }

                    Pattern p = Pattern.compile("^[a-zA-Z0-9_]*$");
                    Matcher m = p.matcher(mFilenameInput.getText().toString());
                    if (mFilenameInput.getText().toString().equals("")) {
                        /* filename is empty */
                        Toast.makeText(mContext, "Please enter a non-empty filename.", Toast.LENGTH_LONG).show();
                    } else if (!m.find()) {
                        /* not all characters are alphanumeric */
                        Toast.makeText(mContext, "Filename can only contain letters, numbers and/or underscores.", Toast.LENGTH_LONG).show();
                    } else if (mFilenameInput.getText().toString().length() > 64) {
                        /* long file name warning */
                        Toast.makeText(mContext, "Do you _really_ need a filename _this_ long?", Toast.LENGTH_LONG).show();
                    } else if (mMolFilenames.contains(mFilenameInput.getText().toString())) {
                        /* filename already exists */
                        Toast.makeText(mContext, "This file already exists!", Toast.LENGTH_LONG).show();
                    } else {
                        mListener.onDialogPositiveClick(mFilenameInput.getText().toString());
                        dialog.dismiss();
                    }
                }
            });
        }
    }
}
