package au.com.pandamakes.www.pureblacktea;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by proto on 16/12/2016.
 */
public class confirmationPreference extends DialogPreference {
    public confirmationPreference(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        setDialogLayoutResource(R.layout.confirmation_preference);
        setPositiveButtonText("OK");
        setNegativeButtonText("Cancel");
        mContext = context;
    }

    public Context mContext;

    @Override
    protected void onDialogClosed(boolean positiveResult){
        if(positiveResult){
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.commit();
        }
    }
}
