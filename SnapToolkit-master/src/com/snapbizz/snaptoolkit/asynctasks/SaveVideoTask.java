package com.snapbizz.snaptoolkit.asynctasks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.os.AsyncTask;

import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class SaveVideoTask extends AsyncTask<InputStream  , Void, Void>{
    
    String fileName;
    String pathName;
    
    public SaveVideoTask(String fileName,String pathName){
        this.fileName= fileName;
        this.pathName = pathName;
    }

    @Override
    protected Void doInBackground(InputStream... params) {
        // TODO Auto-generated method stub
      //  Log.d("videoSize",SnapCommonUtils.convertStreamToString(params[0]));
        BufferedInputStream inStream = new BufferedInputStream(params[0], 8192);
        File file= null;
        if(pathName.contains("billing")){
         file=new File(SnapToolkitConstants.HELP_VIDEO_BILLING_PATH,fileName);
        }
        else if(pathName.contains("inventory")){
         file=new File(SnapToolkitConstants.HELP_VIDEO_INVENTORY_PATH,fileName);
        }
        else if(pathName.contains("push_offer")){
            file=new File(SnapToolkitConstants.HELP_VIDEO_PUSHOFFERS_PATH,fileName);
           }
        else{
            file=new File(SnapToolkitConstants.HELP_VIDEO_PATH,fileName);
        }
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } 
        FileOutputStream outStream;
        try {
            outStream = new FileOutputStream(file);
            byte[] buff = new byte[1024];
            int len;
            while ((len = inStream.read(buff)) != -1)   
            {
                outStream.write(buff,0,len);
            }
            //clean up
            outStream.flush();
            outStream.close();
            inStream.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
