package au.com.pandamakes.www.pureblacktea;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by proto on 12/12/2016.
 */
public class menuSupportClass implements View.OnTouchListener{
    public Context mContext;
    public Button mBtnMenu,mBtnCamera,mBtnPicture,mBtnEdit, mBtnGear,mBtnQuestion;
    public int mMenuLayoutHeight;
    public LinearLayout mMenuLayout;
    public GridView mAdditionalBtnLayout;

    public menuSupportClass(Context context,LinearLayout rootView,ArrayList<String> additionalBtnNames){
        mContext = context;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.menu_layout,rootView,true);

        mBtnCamera = (Button) v.findViewById(R.id.btnCamera);
        mBtnPicture = (Button) v.findViewById(R.id.btnPicture);
        mBtnEdit = (Button) v.findViewById(R.id.btnEdit);
        mBtnGear = (Button) v.findViewById(R.id.btnGear);
        mBtnQuestion = (Button) v.findViewById(R.id.btnQuestion);

        mBtnMenu.setOnTouchListener(this);
        mBtnCamera.setOnTouchListener(this);
        mBtnPicture.setOnTouchListener(this);
        mBtnEdit.setOnTouchListener(this);
        mBtnGear.setOnTouchListener(this);
        mBtnQuestion.setOnTouchListener(this);

        Typeface fontawesome = Typeface.createFromAsset(mContext.getAssets(), "fonts/fontawesome.ttf");
        mBtnMenu.setTypeface(fontawesome);
        mBtnCamera.setTypeface(fontawesome);
        mBtnPicture.setTypeface(fontawesome);
        mBtnEdit.setTypeface(fontawesome);
        mBtnGear.setTypeface(fontawesome);
        mBtnQuestion.setTypeface(fontawesome);
        mBtnMenu.setOnTouchListener(this);

        /*
        if(additionalBtnNames!=null&&additionalBtnNames.size()>0){
            mAdditionalBtnLayout = (GridView) v.findViewById(R.id.linearLayoutAdditionalBtns);
            ArrayAdapter<String> additionalBtns = new ArrayAdapter<>(mContext,R.layout.borderless_button,additionalBtnNames);
            mAdditionalBtnLayout.setAdapter(additionalBtns);

            mAdditionalBtnLayout.post(new Runnable() {
                @Override
                public void run() {
                    Typeface fontawesome = Typeface.createFromAsset(mContext.getAssets(), "fonts/fontawesome.ttf");

                    for (int i = 0; i < mAdditionalBtnLayout.getChildCount(); i++) {
                        ((Button) mAdditionalBtnLayout.getChildAt(i)).setTypeface(fontawesome);
                    }
                }
            });
            mAdditionalBtnLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mBtnCallBack.onAdditionalButtonClick(((Button) view).getText().toString());
                }
            });
        }
        */

        mMenuLayout = (LinearLayout) v.findViewById(R.id.linearLayoutMenu);
        mMenuLayout.post(new Runnable() {
            @Override
            public void run() {
                mMenuLayoutHeight = mMenuLayout.getHeight();
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mMenuLayout.getLayoutParams();
                params.setMargins(0, -1 * mMenuLayoutHeight, 0, 0);
                mMenuLayout.setLayoutParams(params);
            }
        });
    }

    public void attachBtnCallback(Activity activity){
        try {
            mBtnCallBack = (btnCallBack) activity;
        } catch (ClassCastException e){
            throw new ClassCastException(activity.toString() +"must implement btnCallBack");
        }
    }

    public interface btnCallBack{
        void onAdditionalButtonClick(String buttonText);
    }

    btnCallBack mBtnCallBack;
    public boolean mFlagMenuShown = false;

    public boolean onTouch(View v, MotionEvent e) {
        switch(e.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP: {
                Intent intent;
                switch (v.getId()) {
                    /*
                    case R.id.btnMenu:{
                        if(mFlagMenuShown){
                        // flag is shown. hide menu

                            ObjectAnimator openMenu = ObjectAnimator
                                    .ofFloat(mBtnMenu, "rotation", 0)
                                    .setDuration(500);

                            ValueAnimator showMenu = ValueAnimator
                                    .ofInt(0, mMenuLayoutHeight)
                                    .setDuration(200);
                            showMenu.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    int val = (Integer) animation.getAnimatedValue();
                                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mMenuLayout.getLayoutParams();
                                    params.setMargins(0,-1*val,0,0);
                                    mMenuLayout.setLayoutParams(params);
                                }
                            });

                            AnimatorSet set = new AnimatorSet();
                            set.play(openMenu).with(showMenu);
                            set.start();

                        }else{
                        // flag is hidden, show menu

                            ObjectAnimator close = ObjectAnimator
                                    .ofFloat(mBtnMenu, "rotation", 180)
                                    .setDuration(500);

                            ValueAnimator hideMenu = ValueAnimator
                                    .ofInt(mMenuLayoutHeight, 0)
                                    .setDuration(200);
                            hideMenu.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    int val = (Integer) animation.getAnimatedValue();
                                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mMenuLayout.getLayoutParams();
                                    params.setMargins(0,-1*val,0,0);
                                    mMenuLayout.setLayoutParams(params);
                                }
                            });

                            AnimatorSet set = new AnimatorSet();
                            set.play(close).with(hideMenu);
                            set.start();

                        }
                        mFlagMenuShown = !mFlagMenuShown;
                    }break;
                    */
                    case R.id.btnCamera:{
                        //just close the slider
                        /*
                        intent = new Intent(mContext,mainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                        */
                    }break;
                    case R.id.btnPicture:{
                        intent = new Intent(mContext,viewCapture.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        mContext.startActivity(intent);
                    }break;
                    case R.id.btnEdit:{
                        intent = new Intent(mContext,editMolFile.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }break;
                    case R.id.btnGear:{
                        intent = new Intent(mContext,setting.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }break;
                    case R.id.btnQuestion:{
                        intent = new Intent(mContext,help.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }break;
                    default:{

                    }break;
                }
            }
        }
        return true;
    };
}
