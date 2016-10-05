package com.snapbizz.snaptoolkit.utils;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.util.Log;

public class ImageUtil {

	public static void saveBitmapToPath(String path, Bitmap bitmap, int quality) {
		saveBitmapToPath(new File(path), bitmap, quality);
	}

	public static void saveBitmapToPath(File file, Bitmap bitmap, int quality) {
		if (file == null || bitmap == null)
			return;
		if (file.exists()) {
			file.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, quality, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			Log.e("file", "Failed to save file:" + file.getName(), e);
		}
	}
	
}
