package com.snapbizz.snaptoolkit.utils;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.util.LruCache;

// Custom LRU Cache for managing the Bitmaps that are to be recycled.
public class CustomBitmapLruCache extends LruCache<String, Bitmap>{
    
    private ArrayList<Bitmap> m_bitmapObjList;

    public CustomBitmapLruCache(int maxSize) {
        super(maxSize);
        m_bitmapObjList = new ArrayList<Bitmap>();
    }
    
    @Override
    protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
        if (oldValue != null && !oldValue.isRecycled()){
        	m_bitmapObjList.add(oldValue);
        }
        super.entryRemoved(evicted, key, oldValue, newValue);
    }
    
    @Override
    protected int sizeOf(String key, Bitmap value) {
        return (value != null) ? value.getByteCount() : 0;
    }
    
    public void clearList(){

    	for (Bitmap bitmap : m_bitmapObjList) {
            bitmap.recycle();
            bitmap=null;
        }
    	
    	m_bitmapObjList.clear();
    }
    

}
