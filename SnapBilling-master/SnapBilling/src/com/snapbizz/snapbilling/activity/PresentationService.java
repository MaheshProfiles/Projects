/***
  Copyright (c) 2014 CommonsWare, LLC
  
  Licensed under the Apache License, Version 2.0 (the "License"); you may
  not use this file except in compliance with the License. You may obtain
  a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package com.snapbizz.snapbilling.activity;

import java.util.Random;

import com.snapbizz.snapbilling.R;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

public abstract class PresentationService extends Service implements
    PresentationHelper.Listener {
  protected abstract int getThemeId();

  protected abstract View buildPresoView(Context ctxt,
                                         LayoutInflater inflater);
  private Random randomGenerator;
  private AnimatorSet animatorSet;
  private WindowManager wm = null;
  private View presoView = null;
  private PresentationHelper helper=null;
  protected int m_slideShowDuration = 1000;

  @Override
  public IBinder onBind(Intent intent) {
    return(null);
  }

  @Override
  public void onCreate() {
    super.onCreate();

    helper = new PresentationHelper(this, this);
    helper.onResume();
    randomGenerator = new Random();
	animatorSet = new AnimatorSet();

  }

  @Override
  public void onDestroy() {
    helper.onPause();
    randomGenerator = null;
    animatorSet = null;
    helper = null;
    super.onDestroy();
  }

  @Override
  public void showPreso(Display display) {
    try {
    	
    	
		Context presoContext = createPresoContext(display);
		LayoutInflater inflater = LayoutInflater.from(presoContext);
		wm = (WindowManager)presoContext.getSystemService(Context.WINDOW_SERVICE);
		presoView = buildPresoView(presoContext, inflater);
		wm.addView(presoView, buildLayoutParams());
		
		
	} catch (IllegalStateException e) {
		
	}
  }

  @Override
  public void clearPreso(boolean switchToInline) {
    if (presoView != null) {
      try {
        wm.removeView(presoView);
      }
      catch (Exception e) {
        // probably the window is gone, don't worry, be
        // happy
      }
    }

    presoView=null;
  }

  protected WindowManager.LayoutParams buildLayoutParams() {
    return(new WindowManager.LayoutParams(
                                          WindowManager.LayoutParams.MATCH_PARENT,
                                          WindowManager.LayoutParams.MATCH_PARENT,
                                          0,
                                          0,
                                          0,
                                          0, PixelFormat.OPAQUE));
  }

  private Context createPresoContext(Display display) {
    final Context displayContext=createDisplayContext(display);    
    return(new ContextThemeWrapper(displayContext, getThemeId()) {
      @Override
      public Object getSystemService(String name) {
        if (Context.WINDOW_SERVICE.equals(name)) {
        	final WindowManager wm=
        	(WindowManager)displayContext.getSystemService(WINDOW_SERVICE);
        	return(wm);
        }

        return(super.getSystemService(name));
      }
    });
  }
  
  public void randomAnimateViewLeft(View view) {
		String translation = getString(R.string.translation_x);
		int rand = randomGenerator.nextInt(2);
		if (rand == 1)
			translation = getString(R.string.translation_y);
		ObjectAnimator mover = ObjectAnimator.ofFloat(view, translation, -500f,
				0f);
		mover.setDuration(m_slideShowDuration);
		ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, getString(R.string.alpha), 0f, 1f);
		fadeIn.setDuration(m_slideShowDuration);
		animatorSet.play(mover).with(fadeIn).after(0);
		animatorSet.start();
	}

	public void randomAnimateViewRight(View view) {
		String translation = getString(R.string.translation_x);
		int rand = randomGenerator.nextInt(2);
		if (rand == 1)
			translation = getString(R.string.translation_y);
		ObjectAnimator mover = ObjectAnimator.ofFloat(view, translation, 500f,
				0f);
		mover.setDuration(m_slideShowDuration);
		ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, getString(R.string.alpha), 0f, 1f);
		fadeIn.setDuration(m_slideShowDuration);
		animatorSet.play(mover).with(fadeIn).after(0);
		animatorSet.start();
	}

}
