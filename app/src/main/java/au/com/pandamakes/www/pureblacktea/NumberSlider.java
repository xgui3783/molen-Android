package au.com.pandamakes.www.pureblacktea;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by proto on 13/12/2016.
 */
public class NumberSlider extends DialogPreference {
    public NumberSlider(Context context,AttributeSet attributeSet){
        super(context,attributeSet);

        mKey = getKey();
        switch (mKey){
            case "int_detection_box_size": {
                /* range from 50 - 200, stepsize = 10, default 100 */
                mMax = 15;
                mMin = 50;
                mStep = 10;
                mFlagDecimal = false;
            }break;
            case "double_capture_box_size":{
                /* range from 2.0 to 4.0, stepsize = 0.1 default to 2.0 */
                mMax = 20;
                mMin = 2.0;
                mStep = 0.1;
                mFlagDecimal = true;
            }break;
            default:{
                Toast.makeText(context,"Slider Unrecognised. Please contact developer.",Toast.LENGTH_LONG).show();
                mMax = 1;
                mMin = 0;
                mStep = 1;
                mFlagDecimal = false;
            }break;
        }

        setDialogLayoutResource(R.layout.numberslider);
        setPositiveButtonText("OK");
        setNegativeButtonText("Cancel");
    }

    public SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Log.i(staticConfig.TAG,String.valueOf(mStep));
            Log.i(staticConfig.TAG, String.valueOf(mMin));
            if(mFlagDecimal){
                mTV.setText(String.valueOf(mStep*progress+mMin).substring(0,3));
            }else{
                mTV.setText(String.valueOf((int)(mStep*progress+mMin)));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    public float mPersistedValue;
    public String mKey;
    public SeekBar mSlider;
    public TextView mTV;
    public int mMax;
    public double mMin,mStep;
    public boolean mFlagDecimal;

    @Override
    protected void onDialogClosed(boolean positiveResult){
        if(positiveResult){
            mPersistedValue = Float.parseFloat(mTV.getText().toString());
            persistFloat(mPersistedValue);
        }else{
            mSlider.setProgress((int)((mPersistedValue-mMin)/mStep));
        }
    }

    @Override
    protected void onBindDialogView(View view){
        mSlider = (SeekBar) view.findViewById(R.id.seekBar);
        mTV = (TextView) view.findViewById(R.id.idTVNumber);

        TextView textviewNotice = (TextView) view.findViewById(R.id.idTVNotes);
        switch(mKey){
            case "int_detection_box_size":{
                textviewNotice.setText("Note: increasing the size of detection box can severely slow down the performance.");
            }break;
        }

        mSlider.setOnSeekBarChangeListener(onSeekBarChangeListener);
        mSlider.setMax(mMax);

        mSlider.setProgress(0);
        mSlider.setProgress(1);
        mSlider.setProgress((int)((mPersistedValue-mMin)/mStep));

        super.onBindDialogView(view);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue,Object defaultValue){
        if(restorePersistedValue){
            mPersistedValue = this.getPersistedFloat(0f);
        }else{
            mPersistedValue = (Float) defaultValue;
            persistFloat(mPersistedValue);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index){
        return a.getFloat(index,0f);
    }
}
