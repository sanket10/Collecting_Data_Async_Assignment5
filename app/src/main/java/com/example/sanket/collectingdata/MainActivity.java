package com.example.sanket.collectingdata;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private TextView show_data_textview;
    private Button get_data_button;
    private String url = "https://www.iiitd.ac.in/about";
    private String desc = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"Enter onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.show_data_textview = (TextView)findViewById(R.id.about_page_view);
        this.get_data_button = (Button)findViewById(R.id.get_data_button);

        if(savedInstanceState != null){
            Log.d(TAG,"inside bundle checking onCreate()");
            this.desc = savedInstanceState.getString("desc");
            this.show_data_textview.setText(desc);
        }

        Log.d(TAG,"Return onCreate()");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG,"Enter onResume()");
        this.get_data_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Log.d(TAG,"Enter get_data_button clicked");
                getDataByInternetNetwork(view);
                Log.d(TAG,"Return get_data_button clicked");
            }
        });
        Log.d(TAG,"Return onResume()");
    }


    public void getDataByInternetNetwork(View view) {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            new DownloadData().execute();
        }else{
            if(this.desc != null){
                Toast.makeText(this,"No Internet Connect this is the saved data",Toast.LENGTH_SHORT).show();
            }else {
                show_data_textview.setText("NO Internet Connection Available");
            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle bundle){
        Log.d(TAG,"Enter onSaveInstanceState()");
        if(desc != null){
            Log.d(TAG,"Inside desc not null if loop of onSaveInstanceState()");
            bundle.putString("desc",desc);
            super.onSaveInstanceState(bundle);
        }
        Log.d(TAG,"Return onSaveInstanceState()");
    }

    private class DownloadData extends AsyncTask<Void,Void,Void>{
        private String TAG = "MainActivity.DownloadData";
        private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog.setMessage("\tLoading.....");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                Log.d(TAG,"Enter doInBackground()");
                Document document = Jsoup.connect(url).get();
                //document = Jsoup.parse(document.text());
                //Element elements =  document.body();
                desc = document.getElementsByTag("p").get(6).text()+"\n\n"+document.getElementsByTag("p").get(7).text();
                /*desc =  String.valueOf(Html
                        .fromHtml("<![CDATA[<body style=\"text-align:justify;color:#222222; \">"
                                +  desc + "</body>]]>"));

*/
                Log.d("TAG","Complete data - "+document.text());
                Log.d(TAG,"Return doInBackground()");
               // return downloadDataByUrl(strings[0]);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            Log.d(TAG,"Enter onPostExecute()");
            String htmltext = "%s";
            show_data_textview.setText(String.format(htmltext,desc));
            progressDialog.dismiss();
            Log.d(TAG,"Return onPostExecute()");
        }

    }

    private String downloadDataByUrl(String string) throws IOException{
        InputStream inputStream = null;
        int len = 10000;
        try{
            URL url = new URL(string);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            /*httpURLConnection.setReadTimeout(100000);
            httpURLConnection.setConnectTimeout(15000);*/
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            int response = httpURLConnection.getResponseCode();
            inputStream = httpURLConnection.getInputStream();
            String content_after_input_stream = readIt(inputStream,len);
            return content_after_input_stream;
        }finally {
            if(inputStream != null){
                inputStream.close();
            }
        }
    }
    public String readIt(InputStream inputStream,int length) throws IOException,UnsupportedEncodingException{
        BufferedReader reader = null;
        String forecastJsonStr = null;
        reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer buffer = new StringBuffer();
        if (inputStream == null) {
            // Nothing to do.
            return null;
        }
        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
            // But it does make debugging a *lot* easier if you print out the completed
            // buffer for debugging.
            buffer.append(line + "\n");
        }

        if (buffer.length() == 0) {
            // Stream was empty.  No point in parsing.
            return null;
        }
        forecastJsonStr = buffer.toString();
        return forecastJsonStr;
    }
}
