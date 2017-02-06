package au.com.pandamakes.www.pureblacktea;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by proto on 23/11/2016.
 */

public class async extends AsyncTask<String,String,String> {

    public String mFilesDir;
    private final String TAG = "PureBlackTeaLog";
    asyncDataToActivity asyncDTA;

    public async (Activity activity){
        asyncDTA = (asyncDataToActivity)activity;
    }

    protected String doInBackground(String ... params){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS)
                .writeTimeout(30,TimeUnit.SECONDS)
                .build();
        File file = new File(params[0]+"/croppedImg.png");
        mFilesDir = params[0];

        MediaType mediaType = MediaType.parse("multipart/form-data; boundary=---011000010111000001101001");
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("returnType","string")
                .addFormDataPart("openbabel","true")
                .addFormDataPart("openbabelOutFormat","mol")
                .addFormDataPart("openbabelArgs"," --gen3d")
                .addFormDataPart("photo", "temp.png", RequestBody.create(MediaType.parse("image/png"), file))
                .build();
        Request request = new Request.Builder()
                .url("http://molens-pandamakes.rhcloud.com/osr")
                .post(body)
                .addHeader("content-type", "multipart/form-data; boundary=---011000010111000001101001")
                .addHeader("cache-control", "no-cache")
                .build();

        try {
            Response response = client.newCall(request).execute();

            return response.body().string();
        }catch(IOException error){
            error.printStackTrace();
            return null;
        }
    }

    protected void onPostExecute(String stringResponse){

        /* when the server responds */
        try {

            /* check if it is in the correct json format */
            JSONObject json = new JSONObject(stringResponse.replace("\\","\\\\"));
            try{

                /* check if the error field is populated. if it is, parsing is incorrect */
                String error = json.getString("error");
                asyncDTA.osrRes("Error! Parsing error! "+error);
                Log.i(TAG,error);

            }catch(JSONException e2){

                /* when async call is successful, and error field is not populated */
                String mol = json.getString("mol");

                String toBeDisplayed = "";
                String[] toBeWritten = mol.split("\\\\n");

                for(int i = 0; i<toBeWritten.length; i++){
                    toBeDisplayed = toBeDisplayed.concat(toBeWritten[i]).concat(System.getProperty("line.separator"));
                }

                asyncDTA.osrRes(toBeDisplayed);
            }
        }catch (JSONException e){

            /* async call error */

            asyncDTA.osrRes("Error! Server error! "+e.toString());
        }

    }
}
