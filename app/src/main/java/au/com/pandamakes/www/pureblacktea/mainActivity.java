package au.com.pandamakes.www.pureblacktea;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.view.MotionEvent.ACTION_MASK;

public class mainActivity extends AppCompatActivity implements View.OnTouchListener,CameraBridgeViewBase.CvCameraViewListener2{

    private Mat mRgba,mGray,mEdges,mTemplate,mTemplate90,mTemplate180,mTemplate270,mTemplateGray,mDrawingLayer,mMixed,mDescriptor,mGaussianMask,mCameraCaliberation,mRVec,mTVec,mOutput;
    private final String TAG = "PureBlackTeaLog";
    private MatOfPoint2f mTemplateTargetPoints,mFrameKP,mFrameBox,mCaptureAreaAdjusted;
    private ArrayList<Point> mArrayListTemplateKP;
    private MatOfPoint3f mRefCoord,mRefBox;
    private FeatureDetector mDetector;
    private DescriptorExtractor mExtractor;
    private DescriptorMatcher mMatcher;
    private MatOfKeyPoint mKeyPoints;
    private Point mScanCenter,mBoxCenter;
    private double mScanWidth;
    private int mScanLeft, mScanRight, mScanTop, mScanBottom,mMostRight,mMostBottom;
    private MatOfKeyPoint mKpMat;
    private Scalar mRectBorder;
    private double mLostTrackCounter;
    private visualisation mXAxis, mYAxis, mZAxis;
    private List<visualisation> mMolecule;
    private boolean mFlagShowAxis,mFlagShowCaptureArea,mFlagShowFPS,mFlagTempShowCaptureArea;
    private double mCanny1,mCanny2,mPoly, mTemplateThreshold,mDetectionBoxSize,mCaptureBoxSize;

    private CameraBridgeViewBase mOpenCameraView;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch(status){
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG,"opencv loaded successfully");
                    mOpenCameraView.enableView();
                }break;
                default:
                {
                    super.onManagerConnected(status);
                }break;
            }
        }
    };

    private PopupWindow popupWindow;
    private TextView tvShutter;
    private Button btnCamera;
    /*
    public void onAdditionalButtonClick(String btnString){
        // only additional button is crop... so when this function is called, crop
        if(mTVec==null||mTVec.cols()==0){
            Toast.makeText(getApplicationContext(),"Please align the detection box with the AR marker. For more information, please check the tutorial.",Toast.LENGTH_LONG).show();
        }else{
            cropMoleculeRecognition();
        }
    }
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mOpenCameraView = (CameraBridgeViewBase) findViewById(R.id.idMainCamera);
        mOpenCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCameraView.setCvCameraViewListener(this);

        Typeface fontawesome = Typeface.createFromAsset(getAssets(), "fonts/fontawesome.ttf");
        final TextView tv = (TextView) findViewById(R.id.tvEllipses);
        tv.setTypeface(fontawesome);
        tv.setShadowLayer(4, 2, 0, ContextCompat.getColor(this, R.color.colorLightGrey));

        tvShutter = (TextView) findViewById(R.id.tvShutter);
        tvShutter.setTypeface(fontawesome);
        tvShutter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:{
                        if(mTVec==null||mTVec.cols()==0){
                            Toast.makeText(getApplicationContext(),"Please align the detection box with the AR marker. For more information, please check the tutorial.",Toast.LENGTH_LONG).show();
                        }else{
                            cropMoleculeRecognition();
                        }
                    }break;
                }
                return true;
            }
        });

        tvShutter.setVisibility(View.GONE);

        /*
        final PopupMenu popup = new PopupMenu(getApplicationContext(),tv);

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_proper, popup.getMenu());

        tv.setRotation(90);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.idMenuCamera: {
                        Intent intent = new Intent(getApplicationContext(), mainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                    }
                    break;
                    case R.id.idMenuPicture: {
                        Intent intent = new Intent(getApplicationContext(), viewCapture.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                    }
                    break;
                    case R.id.idMenuEdit: {
                        Intent intent = new Intent(getApplicationContext(), editMolFile.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);

                    }
                    break;
                    case R.id.idMenuGear: {
                        Intent intent = new Intent(getApplicationContext(), setting.class);
                        startActivity(intent);

                    }
                    break;
                    case R.id.idMenuQuestion: {
                        Intent intent = new Intent(getApplicationContext(), help.class);
                        startActivity(intent);

                    }
                    break;
                }
                return true;
            }
        });
        */

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.menu_layout, null);

        btnCamera = (Button)content.findViewById(R.id.btnCamera);
        Button btnPicture = (Button)content.findViewById(R.id.btnPicture);
        Button btnEdit = (Button)content.findViewById(R.id.btnEdit);
        Button btnGear = (Button)content.findViewById(R.id.btnGear);
        Button btnQuestion = ((Button)content.findViewById(R.id.btnQuestion));

        btnCamera.setPressed(true);

        btnCamera.setOnTouchListener(this);
        btnPicture.setOnTouchListener(this);
        btnEdit.setOnTouchListener(this);
        btnGear.setOnTouchListener(this);
        btnQuestion.setOnTouchListener(this);

        btnCamera.setShadowLayer(4, 2, 0, ContextCompat.getColor(this, R.color.colorLightGrey));
        btnPicture.setShadowLayer(4, 2, 0, ContextCompat.getColor(this, R.color.colorLightGrey));
        btnEdit.setShadowLayer(4, 2, 0, ContextCompat.getColor(this, R.color.colorLightGrey));
        btnGear.setShadowLayer(4, 2, 0, ContextCompat.getColor(this, R.color.colorLightGrey));
        btnQuestion.setShadowLayer(4, 2, 0, ContextCompat.getColor(this, R.color.colorLightGrey));

        btnCamera.setTypeface(fontawesome);
        btnPicture.setTypeface(fontawesome);
        btnEdit.setTypeface(fontawesome);
        btnGear.setTypeface(fontawesome);
        btnQuestion.setTypeface(fontawesome);

        popupWindow = new PopupWindow(this);
        popupWindow.setContentView(content);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));

        tv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //switch(event.getAction() & )
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    popupWindow.showAsDropDown(tv);
                }
                return false;
            }
        });

        /*
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();

                return true;
            }
        });
        */

        /*
        ArrayList<String> additionalBtns = new ArrayList<>();
        additionalBtns.add("\uF030");

        LinearLayout rootView = (LinearLayout) findViewById(R.id.idMenuPlaceHolder);
        menuSupportClass menusupport = new menuSupportClass(getApplicationContext(),rootView,additionalBtns);
        menusupport.attachBtnCallback(this);
        */
    }

    @Override
    public void onPause(){
        super.onPause();
        if(mOpenCameraView != null){
            mOpenCameraView.disableView();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!OpenCVLoader.initDebug()){
            Log.i(TAG,"internal opencv library not found. need to install opencv manager");
        }else{
            Log.i(TAG,"using internal opencv library");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        mFlagShowAxis = pref.getBoolean("flag_render_axis", staticConfig.showAxis);
        mFlagShowCaptureArea = pref.getBoolean("flag_render_capture_area",staticConfig.showCaptureArea);
        mFlagShowFPS = pref.getBoolean("flag_show_fps",staticConfig.showFPS);

        mDetectionBoxSize = (int) pref.getFloat("int_detection_box_size",staticConfig.detectionBoxSize);
        mCaptureBoxSize = (double) pref.getFloat("double_capture_box_size",staticConfig.captureBoxSize);

        String multipleVar = pref.getString("multiple_detection_parameters","10,50,64,0.2");
        mCanny1 = Double.parseDouble(multipleVar.split(",")[0]);
        mCanny2 = Double.parseDouble(multipleVar.split(",")[1]);
        mPoly = Double.parseDouble(multipleVar.split(",")[2]);
        mTemplateThreshold = Double.parseDouble(multipleVar.split(",")[3]);

        if(mFlagShowFPS){
            mOpenCameraView.enableFpsMeter();
        }else{
            mOpenCameraView.disableFpsMeter();
        }

        if(mFlagShowCaptureArea){
            tvShutter.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);

        Intent getintent = getIntent();
        try{
            getintent.getExtras().getBoolean("flagToCaptureImage");
            Log.i(staticConfig.TAG,"get boolean");
            mFlagTempShowCaptureArea = true;
            tvShutter.setVisibility(View.VISIBLE);
        }catch (NullPointerException e){
            Log.i(staticConfig.TAG,"no boolean");
            tvShutter.setVisibility(View.GONE);
        }
    }

    private long firstTouch;
    public boolean onTouch(View v, MotionEvent e){
        switch(e.getAction() & ACTION_MASK){
            case MotionEvent.ACTION_UP:{
                Log.i(TAG,"ontouch motion up");
                long dt = SystemClock.elapsedRealtime() - firstTouch;
                if(dt<300){
                    //click
                    switch (v.getId()){
                        case R.id.btnCamera:{
                            popupWindow.dismiss();
                            btnCamera.setPressed(true);
                        }break;
                        case R.id.btnPicture:{
                            popupWindow.dismiss();
                            Intent intent = new Intent(this,viewCapture.class);
                            startActivity(intent);
                        }break;
                        case R.id.btnEdit:{
                            popupWindow.dismiss();
                            Intent intent = new Intent(this,editMolFile.class);
                            startActivity(intent);
                        }break;
                        case R.id.btnGear:{
                            popupWindow.dismiss();
                            Intent intent = new Intent(this,setting.class);
                            startActivity(intent);
                        }break;
                        case R.id.btnQuestion:{
                            popupWindow.dismiss();
                            Intent intent = new Intent(this,help.class);
                            startActivity(intent);
                        }break;
                    }
                }
                //else drag
            }break;
            case MotionEvent.ACTION_DOWN:{
                firstTouch = SystemClock.elapsedRealtime();
            }break;
        }
        /*
        */

        return false;
    }

    @Override
    public void onCameraViewStarted(int width, int height){

        /* initialise mRgba, mGray and mDrawinglayer */
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
        mDrawingLayer = new Mat(height,width,CvType.CV_8UC4);
        mMixed = new Mat(height,width,CvType.CV_8UC4);
        mOutput = new Mat(width,height,CvType.CV_8UC4);
        mGaussianMask = new Mat();

        Mat horizontal = Imgproc.getGaussianKernel(width * 2, staticConfig.vignetteLevel,CvType.CV_32F);
        Mat vertical = Imgproc.getGaussianKernel(height * 2, staticConfig.vignetteLevel, CvType.CV_32F);
        Mat matrix = new Mat();
        Core.gemm(vertical,horizontal,1,new Mat(),0,matrix,Core.GEMM_2_T);
        Core.MinMaxLocResult mmr = Core.minMaxLoc(matrix);
        Core.divide(matrix,new Scalar(mmr.maxVal),mGaussianMask);

        /* initialising mats that are necessary for frame processing */
        mEdges = new Mat(height, width, CvType.CV_8UC1);
        mTemplateTargetPoints = new MatOfPoint2f(new Point(0, 0), new Point(0, staticConfig.templateSize),new Point(staticConfig.templateSize,staticConfig.templateSize),new Point(staticConfig.templateSize,0));
        mFrameBox = new MatOfPoint2f();
        /* mTemplateTargetPoints.fromArray(new Point(0, 0), new Point(0, staticConfig.templateSize),new Point(staticConfig.templateSize,staticConfig.templateSize),new Point(staticConfig.templateSize,0)); */

        /* loading template */
        /* the template is a 200 x 50 png image */
        /* containing the template in 4 different orientations */

        mTemplate = new Mat();
        InputStream stream = null;
        Uri uri = Uri.parse("android.resource://au.com.pandamakes.www.pureblacktea/drawable/templatelg7");
        try{
            stream = getContentResolver().openInputStream(uri);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bmp = BitmapFactory.decodeStream(stream,null,bmpFactoryOptions);
        Utils.bitmapToMat(bmp, mTemplate);
        mTemplateGray = new Mat();
        Imgproc.cvtColor(mTemplate, mTemplateGray, Imgproc.COLOR_RGB2GRAY);

        /* setting up feature detectors */
        mDetector = FeatureDetector.create(FeatureDetector.ORB);
        mExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        mMatcher = DescriptorMatcher.create(DescriptorExtractor.ORB);

        /* detect key points */
        mKeyPoints = new MatOfKeyPoint();
        mDescriptor = new Mat();
        mDetector.detect(mTemplateGray, mKeyPoints);
        mExtractor.compute(mTemplateGray, mKeyPoints, mDescriptor);
        List<KeyPoint> listKeyPoint = mKeyPoints.toList();
        mArrayListTemplateKP = new ArrayList<>();
        for(int i = 0; i<listKeyPoint.size(); i++){
            mArrayListTemplateKP.add(listKeyPoint.get(i).pt);
        }
        mFrameKP = new MatOfPoint2f();
        mKpMat = new MatOfKeyPoint();

        /* setting up templates */
        Imgproc.resize(mTemplateGray, mTemplateGray, new Size(staticConfig.templateSize, staticConfig.templateSize));

        mTemplate90 = new Mat();
        Core.transpose(mTemplateGray, mTemplate90);
        Core.flip(mTemplate90, mTemplate90, 0);

        mTemplate180 = new Mat();
        Core.flip(mTemplateGray,mTemplate180,-1);

        mTemplate270 = new Mat();
        Core.flip(mTemplate90,mTemplate270,-1);

        /* options associated with rendering */
        mCameraCaliberation = new Mat(3,3,CvType.CV_32F);
        float data[] = {3284,0,(float) width/2,0,3284,(float) height/2,0,0,1};
        mCameraCaliberation.put(0,0,data);
        mRefCoord = new MatOfPoint3f();
        mRefBox = new MatOfPoint3f(new Point3(-50,-50,0),new Point3(-50,50,0),new Point3(50,50,0),new Point3(50,-50,0));
        mRVec = new Mat();
        mTVec = new Mat();
        mCaptureAreaAdjusted = new MatOfPoint2f();

        /* helper class */
        mXAxis = new visualisation();
        mXAxis.start = new Point3(0,0,0);
        mXAxis.end = new Point3(30,0,0);
        mXAxis.width = 3;
        mXAxis.color = new Scalar(155,55,55);

        mYAxis = new visualisation();
        mYAxis.start = new Point3(0,0,0);
        mYAxis.end = new Point3(0,30,0);
        mYAxis.width = 3;
        mYAxis.color = new Scalar(55,155,55);

        mZAxis = new visualisation();
        mZAxis.start = new Point3(0,0,0);
        mZAxis.end = new Point3(0,0,-30);
        mZAxis.width = 3;
        mZAxis.color = new Scalar(55,55,155);

        /* other misc settings */
        mScanCenter = new Point(width/2,height/2);
        mBoxCenter = new Point(width/2,height/2);
        mScanWidth = staticConfig.scanWidth;
        mRectBorder = new Scalar(200,200,50);
        mLostTrackCounter = 0;

        mMostRight = width;
        mMostBottom = height;

        adjustROI(mScanCenter,mDetectionBoxSize);

        /* init visualisation parameters */
        if(staticConfig.showMolecule){
            mMolecule = new ArrayList<>();
            Boolean flag1 = false,
                    flag2 = false;
            File molFile = new File(getFilesDir()+"/mol.mol");
            try{
                BufferedReader reader = new BufferedReader(new FileReader(molFile));
                String line;
                while((line=reader.readLine())!=null){
                    if(line.contains("END")){
                        break;
                    }else if(flag2){

                    }else if(flag1){
                        if(line.replace(" ","").contains("000000000000")) {
                            visualisation newAtom = new visualisation();
                            try{
                                newAtom.initAtom(line);
                            }catch (NumberFormatException e){
                                /* if a double parsing error occurred */
                                mMolecule = new ArrayList<>();
                                boolean deleted = molFile.delete();
                                if(deleted){
                                    Toast.makeText(getApplicationContext(),"Coordinates NaN. For the healthiness of the app, the mol file was deleted.",Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(getApplicationContext(),"Coordinates NaN. The file was not able to be deleted somehow.",Toast.LENGTH_LONG).show();
                                }
                                break;
                            }
                            mMolecule.add(newAtom);
                        }else{
                            flag2 = true;
                        }
                    }else if(line.contains("V2000")){
                        flag1 = true;
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCameraViewStopped(){

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();
        mDrawingLayer.setTo(new Scalar(0));

        findKeyPoints();

        //Core.multiply(mGray, mGaussianMask.submat(cropTop, cropBottom, cropLeft, cropRight), mGray, 1, 0);

        if(staticConfig.showKeyPoints) {
            Imgproc.cvtColor(mRgba,mRgba,Imgproc.COLOR_RGBA2RGB);
            Features2d.drawKeypoints(mRgba, mKpMat, mMixed);
        }else{
            mMixed = mRgba;
        }

        if(staticConfig.showTrackingBox){
            Imgproc.rectangle(mMixed, new Point(mScanLeft, mScanTop), new Point(mScanRight, mScanBottom), new Scalar(200,200,200,0.5), 5);
            Imgproc.rectangle(mMixed, new Point(mScanLeft, mScanTop), new Point(mScanRight, mScanBottom), new Scalar(150,150,150,0.8), 3);
            Imgproc.rectangle(mMixed, new Point(mScanLeft, mScanTop), new Point(mScanRight, mScanBottom), mRectBorder, 2);
        }

        findPattern();

        /* take pattern matched box into consideration, and readjust ROI */
        adjustROI(new Point((mScanCenter.x / 2 + mBoxCenter.x / 2), (mScanCenter.y / 2 + mBoxCenter.y / 2)), mDetectionBoxSize);

        if(mFrameBox.cols()!=0){
            Calib3d.solvePnP(mRefBox, mFrameBox, mCameraCaliberation, new MatOfDouble(), mRVec, mTVec);

            if(mFlagShowCaptureArea||mFlagTempShowCaptureArea){
                MatOfPoint2f captureArea = new MatOfPoint2f();
                Calib3d.projectPoints(new MatOfPoint3f(new Point3(-50, -50, 0), new Point3(-50, 150, 0), new Point3(150, 150, 0), new Point3(150, -50, 0)), mRVec, mTVec, mCameraCaliberation, new MatOfDouble(), captureArea);

                List<Point> listCaptureArea = captureArea.toList();
                List<Point> listCaptureArea2 = new ArrayList<>();
                for(int i = 0; i<listCaptureArea.size();i++){
                    listCaptureArea2.add(new Point(listCaptureArea.get(i).x + mScanLeft, listCaptureArea.get(i).y + mScanTop));
                }

                mCaptureAreaAdjusted.fromList(listCaptureArea2);

                for(int i = 0; i<4;i++){
                    Imgproc.line(mMixed,new Point(listCaptureArea2.get(i%4).x,listCaptureArea2.get(i%4).y),new Point(listCaptureArea2.get((i+1)%4).x,listCaptureArea2.get((i+1)%4).y),new Scalar(100,12,50,0.5),6,Imgproc.LINE_4,0);
                    Imgproc.line(mMixed,new Point(listCaptureArea2.get(i%4).x,listCaptureArea2.get(i%4).y),new Point(listCaptureArea2.get((i+1)%4).x,listCaptureArea2.get((i+1)%4).y),new Scalar(100,25,100,0.8),2,Imgproc.LINE_4,0);
                    Imgproc.line(mMixed,new Point(listCaptureArea2.get(i%4).x,listCaptureArea2.get(i%4).y),new Point(listCaptureArea2.get((i+1)%4).x,listCaptureArea2.get((i+1)%4).y),new Scalar(200,50,200,1),1,Imgproc.LINE_4,0);
                }
            }

            if(mFlagShowAxis){
                MatOfPoint2f XAxis = new MatOfPoint2f(),
                        YAxis = new MatOfPoint2f(),
                        ZAxis = new MatOfPoint2f();
                Calib3d.projectPoints(new MatOfPoint3f(mXAxis.start, mXAxis.end), mRVec, mTVec, mCameraCaliberation, new MatOfDouble(), XAxis);
                Imgproc.line(mMixed, new Point(XAxis.toList().get(0).x+mScanLeft,XAxis.toList().get(0).y+mScanTop), new Point(XAxis.toList().get(1).x+mScanLeft,XAxis.toList().get(1).y+mScanTop), mXAxis.color, (int) mXAxis.width);

                Calib3d.projectPoints(new MatOfPoint3f(mYAxis.start, mYAxis.end), mRVec, mTVec, mCameraCaliberation, new MatOfDouble(), YAxis);
                Imgproc.line(mMixed, new Point(YAxis.toList().get(0).x+mScanLeft,YAxis.toList().get(0).y+mScanTop), new Point(YAxis.toList().get(1).x+mScanLeft,YAxis.toList().get(1).y+mScanTop), mYAxis.color, (int) mYAxis.width);

                Calib3d.projectPoints(new MatOfPoint3f(mZAxis.start, mZAxis.end), mRVec, mTVec, mCameraCaliberation, new MatOfDouble(), ZAxis);
                Imgproc.line(mMixed, new Point(ZAxis.toList().get(0).x+mScanLeft,ZAxis.toList().get(0).y+mScanTop), new Point(ZAxis.toList().get(1).x+mScanLeft,ZAxis.toList().get(1).y+mScanTop), mZAxis.color, (int) mZAxis.width);
            }


            if(staticConfig.showMolecule){
                for(int i = 0; i<mMolecule.size();i++){
                    mMolecule.get(i).calcXYZ(mRVec, mTVec, mCameraCaliberation);
                }

                Collections.sort(mMolecule);
                double baseDistance=0.0;
                for(int i = 0; i<mMolecule.size(); i++){
                    double ratio;
                    if(baseDistance==0.0){
                        baseDistance = mMolecule.get(i).Z * mMolecule.get(i).Z;
                        ratio = 1.0;
                    }else{
                        ratio = baseDistance/(mMolecule.get(i).Z * mMolecule.get(i).Z);
                    }
                    ratio = Math.pow(ratio,-8);

                    Point center = new Point(mMolecule.get(i).XY.x+mScanLeft,mMolecule.get(i).XY.y+mScanTop);
                    Scalar staticcolor = mMolecule.get(i).color;
                    Scalar color = new Scalar(staticcolor.val[0]*ratio,staticcolor.val[1]*ratio,staticcolor.val[2]*ratio);

                    double power1 = 0.8;
                    double power2 = 0.7;

                    Scalar color1 = new Scalar(Math.pow((color.val[0] / 255), power1)*255,Math.pow((color.val[1]/255),power1)*255,Math.pow((color.val[2]/255),power1)*255);
                    Scalar color2 = new Scalar(Math.pow((color.val[0] / 255), power2)*255,Math.pow((color.val[1]/255),power2)*255,Math.pow((color.val[2]/255),power2)*255);

                    double ratio1 = 0.8;
                    double ratio2 = 0.5;

                    Imgproc.circle(mMixed,center,(int)(mMolecule.get(i).width*ratio),color,-1);
                    Imgproc.circle(mMixed,center,(int)(mMolecule.get(i).width*ratio*ratio1),color1,-1);
                    Imgproc.circle(mMixed,center,(int)(mMolecule.get(i).width*ratio*ratio2),color2,-1);

                    Imgproc.circle(mMixed,new Point(mMolecule.get(i).XY.x+mScanLeft,mMolecule.get(i).XY.y+mScanTop),(int)(mMolecule.get(i).width*ratio),new Scalar(0,0,0),1);
                }
            }
        }

        return mMixed;
    }

    /* nb this function will only be triggered: 1. option to extract optical is checked 2. pattern recognised 3. a trigger (button) is activated */
    public void cropMoleculeRecognition(){
        mOpenCameraView.disableView();

        Mat transformMat = Imgproc.getPerspectiveTransform(mCaptureAreaAdjusted,new MatOfPoint2f(new Point(0,0),new Point(0,400),new Point(400,400), new Point(400,0)));
        Mat croppedImage = new Mat();
        Imgproc.warpPerspective(mGray, croppedImage, transformMat, new Size(400, 400));

        /* cleanup the AR marker */
        int cropout = 240;

        croppedImage.submat(0,cropout,0,cropout).setTo(new Scalar(0));
        Core.MinMaxLocResult mmr;

        for(int i = 0; i<cropout; i++){
            mmr = Core.minMaxLoc(croppedImage.row(i).colRange(cropout,400));
            Core.add(croppedImage.row(i).colRange(0,cropout),new Scalar(mmr.maxVal/2),croppedImage.row(i).colRange(0,cropout));

            mmr = Core.minMaxLoc(croppedImage.col(i).rowRange(cropout, 400));
            Core.add(croppedImage.col(i).rowRange(0, cropout),new Scalar(mmr.maxVal/2),croppedImage.col(i).rowRange(0, cropout));
        }

        /*for now, just save the cropedImage*/
        /* in the live version, will need to sort out the ar marker on the top left */
        Imgcodecs.imwrite(getFilesDir().toString().concat("/croppedImg.png"),croppedImage);
        Intent intent = new Intent(this,viewCapture.class);
        intent.putExtra("flagCaptureImage",true);
        startActivity(intent);
    }

    public void findKeyPoints(){
        /* compute key points of the incoming frame */
        Mat descriptorFrame = new Mat();
        MatOfKeyPoint keyPointFrame = new MatOfKeyPoint();

        mDetector.detect(mGray.submat(mScanTop,mScanBottom,mScanLeft,mScanRight), keyPointFrame);
        mExtractor.compute(mGray.submat(mScanTop,mScanBottom,mScanLeft,mScanRight), keyPointFrame, descriptorFrame);

        /* matches the keypoints of incoming frame with the key points of template */
        MatOfDMatch matches = new MatOfDMatch();

        /* if descriptorFrame.cols()==0, mMatcher.match will cause exception */
        if(descriptorFrame.cols()==0){
            lostTrack();
            return;
        }else{
            mMatcher.match(mDescriptor, descriptorFrame, matches);
            mLostTrackCounter = 0;
            mRectBorder = new Scalar(150,200,150);
        }

        /* check if pattern exist by checking the sume of DMatch.distance */
        DMatch dm[] = matches.toArray();

        /* pattern found. go through matofdmatch again, find all the key points whose distance is lower than threshold */
        /* which, in theory, should mean that these points belong to the pattern */
        KeyPoint kp[] = keyPointFrame.toArray();
        List<Point> frameKp = new ArrayList<>();
        List<KeyPoint> kpNew = new ArrayList<>();
        List<Point3> kpTemplate = new ArrayList<>();

        /* form new points to monitor */
        double xTally = 0,
                yTally = 0,
                counter = 0;

        /* find the average point to monitor */

        for(int i = 0; i<dm.length;i++){
            if(dm[i].distance<800&&dm[i].distance>200){
                kpNew.add(new KeyPoint((float) kp[dm[i].trainIdx].pt.x+mScanLeft,(float) kp[dm[i].trainIdx].pt.y+mScanTop, kp[dm[i].trainIdx].size));
                frameKp.add(new Point((float) kp[dm[i].trainIdx].pt.x+mScanLeft,(float) kp[dm[i].trainIdx].pt.y+mScanTop));
                Point3 newPoint3 = new Point3(mArrayListTemplateKP.get(dm[i].queryIdx).x-90,mArrayListTemplateKP.get(dm[i].queryIdx).y-70,0);
                kpTemplate.add(newPoint3);

                xTally += kp[dm[i].trainIdx].pt.x + (double)mScanLeft;
                yTally += kp[dm[i].trainIdx].pt.y + (double)mScanTop;
                counter += 1;
            }
        }

        if(counter>2){
            mKpMat.fromList(kpNew);
            mRefCoord.fromList(kpTemplate);
            mFrameKP.fromList(frameKp);

            mScanCenter = new Point(xTally/counter,yTally/counter);
        }else{
            lostTrack();
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvShutter.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.shutter_active));
            }
        });

        adjustROI(mScanCenter, mDetectionBoxSize);
    }

    /* this function finds the pattern that matches with the template */
    /* this function will then populate a few mat objects */
    public void findPattern(){
        List<MatOfPoint> arrContours = new ArrayList<>();
        Mat hierachy = new Mat();
        MatOfPoint2f contour2f = new MatOfPoint2f();
        MatOfPoint2f approxPoly = new MatOfPoint2f();
        Mat transformMatrix;
        Mat warped = new Mat();
        Mat compareTemplateResult = new Mat(4,1,CvType.CV_32FC1);

        Imgproc.Canny(mGray.submat(mScanTop,mScanBottom,mScanLeft,mScanRight), mEdges, mCanny1, mCanny2);
        Imgproc.findContours(mEdges,arrContours,hierachy,Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
        for(int i = 0; i<arrContours.size(); i++){
            arrContours.get(i).convertTo(contour2f, CvType.CV_32FC2);
            Imgproc.approxPolyDP(contour2f,approxPoly,mPoly,true);
            if(approxPoly.height()==4){
                transformMatrix = Imgproc.getPerspectiveTransform(approxPoly,mTemplateTargetPoints);
                Imgproc.warpPerspective(mGray.submat(mScanTop, mScanBottom, mScanLeft, mScanRight), warped, transformMatrix, new Size(staticConfig.templateSize, staticConfig.templateSize));

                Imgproc.matchTemplate(warped, mTemplateGray, compareTemplateResult.rowRange(0, 1), Imgproc.TM_SQDIFF_NORMED);
                Imgproc.matchTemplate(warped,mTemplate90,compareTemplateResult.rowRange(1, 2),Imgproc.TM_SQDIFF_NORMED);
                Imgproc.matchTemplate(warped,mTemplate180,compareTemplateResult.rowRange(2, 3),Imgproc.TM_SQDIFF_NORMED);
                Imgproc.matchTemplate(warped, mTemplate270, compareTemplateResult.rowRange(3, 4), Imgproc.TM_SQDIFF_NORMED);

                Core.MinMaxLocResult mmr = Core.minMaxLoc(compareTemplateResult);

                if(Double.compare(mmr.minVal,mTemplateThreshold)<0){

                    /* used to solvepnp */
                    List<Point> approxPolyList = approxPoly.toList();
                    switch (mmr.minLoc.toString()){
                        case "{0.0, 0.0}":
                        {
                            mFrameBox.fromList(approxPolyList);
                        }break;
                        case "{0.0, 1.0}":
                        {
                            Collections.rotate(approxPolyList,3);
                            mFrameBox.fromList(approxPolyList);
                        }break;
                        case "{0.0, 2.0}":
                        {
                            Collections.rotate(approxPolyList,2);
                            mFrameBox.fromList(approxPolyList);
                        }break;
                        case "{0.0, 3.0}":
                        {
                            Collections.rotate(approxPolyList,1);
                            mFrameBox.fromList(approxPolyList);
                        }break;
                        default:{
                            Log.i(TAG,"mmr.minLoc out of bound error.");
                            mFrameBox.fromList(approxPolyList);
                        }break;
                    }
                    /* calculate the average center of the box. used to readjust mscancenter later */
                    List<Point> approxPolyPts = approxPoly.toList();
                    double totalX = 0,
                            totalY = 0,
                            count = 0;
                    for(int j = 0; j<approxPolyPts.size(); j++){
                        count++;
                        totalX += approxPolyPts.get(j).x+mScanLeft;
                        totalY += approxPolyPts.get(j).y+mScanTop;
                    }
                    mBoxCenter = new Point(totalX/count,totalY/count);


                    /* check if show fill poly */
                    if(staticConfig.showPatternMatch){
                        Imgproc.fillConvexPoly(mMixed.submat(mScanTop,mScanBottom,mScanLeft,mScanRight),arrContours.get(i),new Scalar(255,155,155,255));
                    }

                    return;
                }
            }
        }
    }

    public void adjustROI(Point center,double width){
        // center is nullable. if that's the case, increase width by a specificed or default amount
        if(center==null){
            Log.i(TAG,"extending");
            mScanWidth += width;
        }else{
            mScanWidth = width;
            mScanCenter = center;
        }

        mScanLeft = (int)Math.max(0,mScanCenter.x - mScanWidth);
        mScanTop = (int)Math.max(0,mScanCenter.y-mScanWidth);
        mScanRight = (int)Math.min(mMostRight, mScanCenter.x + mScanWidth);
        mScanBottom = (int)Math.min(mMostBottom, mScanCenter.y + mScanWidth);
    }

    public void lostTrack(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvShutter.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.shutter));
            }
        });

        mRectBorder = new Scalar(200,150,150);

        //necessary, so that after lostTrack, does not show molecule or capture box
        mFrameBox = new MatOfPoint2f();

        mLostTrackCounter ++;
        if(mLostTrackCounter>=20){
            mRectBorder = new Scalar(200,200,150);
            mScanCenter = new Point(mMostRight/2,mMostBottom/2);

            //necessary, so that after lostTrack reset, the box does not drift
            mBoxCenter = new Point(mMostRight/2,mMostBottom/2);


            adjustROI(mScanCenter,mDetectionBoxSize);
            mKpMat = new MatOfKeyPoint();
            mRefCoord = new MatOfPoint3f();
            mFrameKP = new MatOfPoint2f();
        }
    }
}
