package au.com.pandamakes.www.pureblacktea;

import android.util.Log;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by proto on 17/11/2016.
 */
public class visualisation implements  Comparable<visualisation>{

    public int compareTo(visualisation other){
        return Double.compare(this.Z,other.Z);
    }

    public Point3 start;
    public Point3 end;
    public double width;
    public Scalar color;
    public double defaultWidth = 16;
    public double Z;
    public MatOfPoint2f mopXY;
    public Point XY;
    private MatOfPoint3f mopXYZ;
    public double scale = staticConfig.defaultScale;

    public void initAtom(String line){

        /* hopefully future proof. remove empty space at the beginning */
        int i = 0;
        String[] splitLine = line.split("\\s+");
        while(splitLine[i].isEmpty()){
            i++;
        }

        try {
            start = new Point3(Double.parseDouble(splitLine[i]) * scale, Double.parseDouble(splitLine[i + 1]) * scale, Double.parseDouble(splitLine[i + 2]) * scale);
        } catch (NumberFormatException e){
            throw e;
        }

        mopXYZ = new MatOfPoint3f(start);
        mopXY = new MatOfPoint2f();

        switch (splitLine[i+3]){
            case "C":{
                this.color = new Scalar(50,50,50);
                this.width = defaultWidth;
            }break;
            case "H":{
                this.color = new Scalar(200,200,200);
                this.width = defaultWidth/2;
            }break;
            case "O":{
                this.color = new Scalar(200,20,20);
                this.width = defaultWidth;
            }break;
            case "N":{
                this.color = new Scalar(20,20,200);
                this.width = defaultWidth;
            }break;
            case "S":{
                this.color = new Scalar(200,200,20);
                this.width = defaultWidth*1.2;
            }break;
            default:{
                this.color = new Scalar(200,20,200);
                this.width = defaultWidth;
            }
        }
    }

    public void calcXYZ(Mat mRVec, Mat mTVec, Mat mCameraCalibration){
        Calib3d.projectPoints(mopXYZ,mRVec,mTVec,mCameraCalibration,new MatOfDouble(),mopXY);
        XY = mopXY.toArray()[0];
        Z =     (mTVec.get(0,0)[0] - start.x) * (mTVec.get(0,0)[0] - start.x) +
                (mTVec.get(1,0)[0] - start.y) * (mTVec.get(1,0)[0] - start.y) +
                (mTVec.get(2,0)[0] - start.z) * (mTVec.get(2,0)[0] - start.z);
    }
}
