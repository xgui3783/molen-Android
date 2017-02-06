package au.com.pandamakes.www.pureblacktea;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by proto on 22/01/2017.
 */
public class pingServer extends AsyncTask<Void,Void,String> {

    public pingServer (Activity activity){
        asyncDTA = (asyncDataToActivity)activity;
    }

    asyncDataToActivity asyncDTA;

    protected String doInBackground(Void ... params){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url("http://molens-pandamakes.rhcloud.com/testget")
                .get()
                .build();

        try{
            Log.i(staticConfig.TAG, "ping server successful");
            Response response = client.newCall(request).execute();
            return response.body().string();
        }catch(IOException error){
            Log.i(staticConfig.TAG, "ping server io exception");
            return "Server seems to be unresponsive.";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        asyncDTA.pingServer(s);
    }
}
