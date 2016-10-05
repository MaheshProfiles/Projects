package com.snapbizz.snaptoolkit.utils;

import java.lang.reflect.Modifier;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.snapbizz.snaptoolkit.interfaces.Transformable;

public class TransformableFactory {
	
	public enum GsonType { DEFAULT, EXCLUDE_TRANSIENT_FIELDS, EXCLUDE_FIELDS_WITHOUT_EXPOSE }

	public static Gson newGson(GsonType gsonType) {
		if(gsonType == GsonType.EXCLUDE_TRANSIENT_FIELDS)
			return new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).create();
		else if(gsonType == GsonType.EXCLUDE_FIELDS_WITHOUT_EXPOSE)
			return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		else
			return new Gson();
	}

	public static String tranformObjectToJson(Transformable obj) throws Exception {
		return tranformObjectToJson(obj, GsonType.DEFAULT);
	}

	public static String tranformObjectToJson(Transformable obj, GsonType gsonType) throws Exception {
		String jsonString="";
		try {
			Gson gson = newGson(gsonType);  
			jsonString = gson.toJson(obj); 
			Log.e("JSON", jsonString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonString;
	}

}
