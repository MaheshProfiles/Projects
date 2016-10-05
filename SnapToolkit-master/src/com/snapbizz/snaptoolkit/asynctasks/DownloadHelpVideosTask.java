package com.snapbizz.snaptoolkit.asynctasks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public class DownloadHelpVideosTask extends AsyncTask<String, String, String> {

    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;

    private String videoPath = "http://my_site.com/test_videos/";    
    private String[] fileNames = {"file1.mp4","file2.mp4"};
    private TextView tv;  
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
     //   showDialog(DIALOG_DOWNLOAD_PROGRESS);
    }

    @Override
    protected String doInBackground(String... aurl) {
        int count;

        try {
            URL url = new URL(aurl[0]);
            URLConnection conexion = url.openConnection();
            conexion.connect();

            File root = android.os.Environment.getExternalStorageDirectory();

            int lenghtOfFile = conexion.getContentLength();
            Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(root.getAbsolutePath() + "/videos/" + aurl[1]);

            byte data[] = new byte[102480];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress(""+(int)((total*100)/lenghtOfFile));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();          

        } catch (Exception e) {}
        return null;

    }
    protected void onProgressUpdate(String... progress) {
         Log.d("ANDRO_ASYNC",progress[0]);
       //  mProgressDialog.setProgress(Integer.parseInt(progress[0]));
    }

    @Override
    protected void onPostExecute(String unused) {
      //  dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
      //  sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));            
    }
}