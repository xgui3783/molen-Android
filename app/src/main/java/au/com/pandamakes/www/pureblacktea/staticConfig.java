package au.com.pandamakes.www.pureblacktea;

import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by proto on 10/11/2016.
 */
public class staticConfig {
    public static final String TAG = "PureBlackTeaLog";
    public static int templateSize = 50;
    public static int scanWidth = 100;
    public static double vignetteLevel = 600;

    public static float captureBoxSize = 2.0f;
    public static float detectionBoxSize = 100f;
    public static boolean showFPS = false;
    public static boolean showCaptureArea = false;
    public static boolean showAxis = false;
    public static boolean showKeyPoints = false;
    public static boolean showTrackingBox = true;
    public static boolean showCoord = true;
    public static boolean showPatternMatch = false;

    public static boolean showMolecule = true;

    public static double defaultScale = 24;

}
