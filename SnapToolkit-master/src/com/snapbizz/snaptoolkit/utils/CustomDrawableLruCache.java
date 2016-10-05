package com.snapbizz.snaptoolkit.utils;

import java.util.ArrayList;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.LruCache;

// CustomLRUCache for managing the Drawables that has to be recycled.
public class CustomDrawableLruCache extends LruCache<String, Drawable> {

    private ArrayList<Drawable> m_drawableObjList;

    public CustomDrawableLruCache(int maxSize) {
        super(maxSize);
        m_drawableObjList = new ArrayList<Drawable>();
    }

    @Override
    protected void entryRemoved(boolean evicted, String key, Drawable oldValue, Drawable newValue) {
    	
        if (oldValue != null && 
        	oldValue instanceof BitmapDrawable &&  
        	!((BitmapDrawable)oldValue).getBitmap().isRecycled()) {
        	
        	m_drawableObjList.add(oldValue);
        	
        }
        super.entryRemoved(evicted, key, oldValue, newValue);
    }

    public void clearList() {
    	
        for (Drawable drawable : m_drawableObjList) {
        	
            if (drawable instanceof BitmapDrawable) {           	            	
                ((BitmapDrawable) drawable).getBitmap().recycle();
                drawable = null;                
            }
        }
        m_drawableObjList.clear();
    }

}
