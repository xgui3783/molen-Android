package au.com.pandamakes.www.pureblacktea;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by proto on 31/01/2017.
 */
public class mol2Dto3D extends AsyncTask<String, String, String> {

    asyncDataToActivity asyncDTA;

    public mol2Dto3D(Activity activity){
        asyncDTA = (asyncDataToActivity) activity;
    }

    protected String doInBackground(String ... params){
        File mol2d = new File(params[0]+"/mol2d.mol");
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS)
                .writeTimeout(30,TimeUnit.SECONDS)
                .build();

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("returnType","string")
                .addFormDataPart("openbabel","true")
                .addFormDataPart("openbabelOutFormat","mol")
                .addFormDataPart("openbabelArgs"," --gen3d")
                .addFormDataPart("file", "mol2d.mol", RequestBody.create(MediaType.parse("text/plain; charset=utf-8"), mol2d))
                .build();
        Request request = new Request.Builder()
                .url("http://molens-pandamakes.rhcloud.com/openbabel")
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

    protected void onPostExecute(String response){

        /* when the server responds */
        try {

            /* check if it is in the correct json format */
            JSONObject json = new JSONObject(response.replace("\\","\\\\"));
            try{

                /* check if the error field is populated. if it is, parsing is incorrect */
                String error = json.getString("error");
                asyncDTA.mol2Dto3D("Error! Parsing error! "+error);
                Log.i(staticConfig.TAG, error);

            }catch(JSONException e2){

                /* when async call is successful, and error field is not populated */
                String mol = json.getString("mol");

                String toBeDisplayed = "";
                String[] toBeWritten = mol.split("\\\\n");

                for(int i = 0; i<toBeWritten.length; i++){
                    toBeDisplayed = toBeDisplayed.concat(toBeWritten[i]).concat(System.getProperty("line.separator"));
                }

                asyncDTA.mol2Dto3D(toBeDisplayed);
            }
        }catch (JSONException e){

            /* async call error */

            asyncDTA.osrRes("Error! Server error! "+e.toString());
        }
    }
}
