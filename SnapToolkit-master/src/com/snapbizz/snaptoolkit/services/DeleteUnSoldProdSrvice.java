package com.snapbizz.snaptoolkit.services;

import java.util.Timer;
import java.util.TimerTask;
import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;



public class DeleteUnSoldProdSrvice extends IntentService
{
    private Timer mBackGroundTimer;
    public DeleteUnSoldProdSrvice()
{
    super("myservice");
    this.mBackGroundTimer=new Timer();
}
    @Override
    protected void onHandleIntent(Intent intent)
{
    mBackGroundTimer.schedule(new TimerTask()
    {
        public void run()
        {
            try
            {
             }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    },1000, 2000);
}
    private void mStopTimer()
{
   
    mBackGroundTimer.cancel();
}


    
    @SuppressLint("NewApi")
	@Override
    public void onTaskRemoved(Intent rootIntent){
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());


        
        mBackGroundTimer.schedule(new TimerTask()
        {
            public void run()
            {
                try
                {
                	
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        },1000, 2000);

        super.onTaskRemoved(rootIntent);
     }
    
    
    public void onDestroy() {
		
    	Intent visibilityServiceIntent = new Intent(this,
    			DeleteUnSoldProdSrvice.class);
	this.startService(visibilityServiceIntent);
	}
}

