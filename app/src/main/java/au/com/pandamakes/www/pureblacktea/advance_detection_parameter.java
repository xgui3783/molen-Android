package au.com.pandamakes.www.pureblacktea;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by proto on 13/12/2016.
 */
public class advance_detection_parameter extends DialogPreference {

    public SeekBar mSliderCanny1,mSliderCanny2,mSliderPoly,mSliderPattern;
    public TextView mTVCanny1, mTVCanny2, mTVPoly,mTVPattern;
    public String mPersistedValue;

    public advance_detection_parameter(Context context, AttributeSet attributeSet){
        super(context, attributeSet);

        setDialogLayoutResource(R.layout.advanced_detection_option);
        setPositiveButtonText("OK");
        setNegativeButtonText("Cancel");
    }

    @Override
    protected void onDialogClosed(boolean positiveResult){
        if(positiveResult){
            mPersistedValue = mTVCanny1.getText().toString() + ","+mTVCanny2.getText().toString()+","+mTVPoly.getText().toString()+","+mTVPattern.getText().toString();
            persistString(mPersistedValue);
        }else{
            String[] params = mPersistedValue.split(",");

            mSliderCanny1.setProgress((Integer.parseInt(params[0]) - 10) / 10);
            mSliderCanny2.setProgress((Integer.parseInt(params[1]) - 10) / 10);
            mSliderPoly.setProgress((Integer.parseInt(params[2]) - 10) / 10);
            mSliderPattern.setProgress((int) ((Double.parseDouble(params[3]) - 0.1) / 0.1));
        }
    }

    @Override
    protected void onBindDialogView(View view){
        mSliderCanny1 = (SeekBar) view.findViewById(R.id.idSeekbarCanny1);/* 10-250, stepsize of 10 */
        mSliderCanny2 = (SeekBar) view.findViewById(R.id.idSeekbarCanny2);/* 10-250 stepsize of 10 */
        mSliderPoly = (SeekBar) view.findViewById(R.id.idSeekbarPolygon);/* 10-100 stepsize of 10 */
        mSliderPattern = (SeekBar) view.findViewById(R.id.idSeekbarTemplate); /* 0.1-0.4, stepsize of 0.01 */

        mTVCanny1 = (TextView) view.findViewById(R.id.idCanny1Value);
        mTVCanny2 = (TextView) view.findViewById(R.id.idCanny2Value);
        mTVPoly = (TextView) view.findViewById(R.id.idPolygonValue);
        mTVPattern = (TextView) view.findViewById(R.id.idTemplateValue);

        mSliderCanny1.setMax(24);
        mSliderCanny2.setMax(24);
        mSliderPoly.setMax(9);
        mSliderPattern.setMax(30);

        mSliderCanny1.setOnSeekBarChangeListener(seekBarChangeListener);
        mSliderCanny2.setOnSeekBarChangeListener(seekBarChangeListener);
        mSliderPoly.setOnSeekBarChangeListener(seekBarChangeListener);
        mSliderPattern.setOnSeekBarChangeListener(seekBarChangeListener);

        String[] params = mPersistedValue.split(",");

        mSliderCanny1.setProgress(1);
        mSliderCanny1.setProgress(2);
        mSliderCanny2.setProgress(1);
        mSliderCanny2.setProgress(2);
        mSliderPattern.setProgress(1);
        mSliderPattern.setProgress(2);
        mSliderPoly.setProgress(1);
        mSliderPoly.setProgress(2);

        mSliderCanny1.setProgress((Integer.parseInt(params[0]) - 10) / 10);
        mSliderCanny2.setProgress((Integer.parseInt(params[1]) - 10) / 10);
        mSliderPoly.setProgress((Integer.parseInt(params[2]) - 10) / 10);
        mSliderPattern.setProgress((int) ((Double.parseDouble(params[3]) - 0.1) / 0.01));

        super.onBindDialogView(view);
    }

    public SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()){
                case R.id.idSeekbarCanny1:{
                    int value = progress * 10 + 10;
                    mTVCanny1.setText(String.valueOf(value));
                }break;
                case R.id.idSeekbarCanny2:{
                    int value = progress * 10 + 10;
                    mTVCanny2.setText(String.valueOf(value));
                }break;
                case R.id.idSeekbarPolygon:{
                    int value = progress * 10 + 10;
                    mTVPoly.setText(String.valueOf(value));
                }break;
                case R.id.idSeekbarTemplate:{
                    double value = ((double) progress) * 0.01 + 0.1;
                    mTVPattern.setText(String.format("%.2f", value));
                }break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue,Object defaultValue){
        if(restorePersistedValue){
            mPersistedValue = this.getPersistedString("multiple_detection_parameters");
        }else{
            mPersistedValue = (String) defaultValue;
            persistString(mPersistedValue);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index){
        return a.getString(index);
    }
}
