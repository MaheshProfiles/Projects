package com.snapbizz.snaptoolkit.utils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.gson.GsonBuilder;
import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.domains.ProductCategory;

import android.content.Context;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ToolkitV2 {
	private static final int DEFAULT_CONNECTION_TIMEOUT = 120;			// Two minutes
	private static final int DEFAULT_READ_TIMEOUT = 120;
	
	public static Retrofit buildRetrofit(String baseUrl) {
		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
		okBuilder.readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS);
		okBuilder.connectTimeout(DEFAULT_CONNECTION_TIMEOUT, TimeUnit.SECONDS);
		okBuilder.addInterceptor(interceptor);
		Retrofit.Builder builder = new Retrofit.Builder().baseUrl(baseUrl)
				.addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
						.setDateFormat(SnapToolkitConstants.GDB_SNAPSHOT_TIMEFORMAT)
						.create()));
		return builder.client(okBuilder.build()).build();
	}

	public static Field findField(Field[] range, String search) {
		for(Field f : range) {
			if(f.getName().equalsIgnoreCase(search))
				return f;
		}
		return null;
	}

	public static void copyProperties(Object src, Object dest) {
		if(src == null || dest == null)
			return;
		Field[] destFields = dest.getClass().getDeclaredFields();
		for(Field f : src.getClass().getDeclaredFields()) {
			Field df = findField(destFields, f.getName());
			if(df != null && df.getType() == f.getType()) {
				try {
					f.setAccessible(true);
					df.setAccessible(true);
					df.set(dest, f.get(src));
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void hidingXtraProduct(List<ProductCategory> subproductCategoryList, Context context) {
		for (ProductCategory category : subproductCategoryList) {
			if (category.getCategoryName().equalsIgnoreCase(context.getString(R.string.xtra_products))) {
				subproductCategoryList.remove(category);
				break;
			}
		}
	}
}
