package com.snapbizz.snaptoolkit.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.Campaigns;
import com.snapbizz.snaptoolkit.domains.Company;
import com.snapbizz.snaptoolkit.domains.Order;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.domains.ProductSku;

import fr.maxcom.http.LocalSingleHttpServer;

public class SnapCommonUtils {

	private static String ID = null;
	private static SnapBizzDatabaseHelper snapDatabaseHelper;
	private static SnapBizzSyncDatabaseHelper snapSyncDatabaseHelper;
	private static Context snapdataContext;
	private static Context commonContext;
	private static Dialog alertDialog;
	private static LocalSingleHttpServer mServer;
	private static CustomDrawableLruCache mMemoryDrawableCache;
	public static Hashtable<String, List<ProductSku>> resultTable;
	public static HashMap<String, Integer> resultMap;
	private static NumberFormat formatter;
	
	public static HashMap<String, Integer> getResultMap() {
		return resultMap;
	}

	public static void setResultMap(HashMap<String, Integer> resultMap) {
		Log.d("TAG", "Testing --resultMap-- > " + resultMap);
		SnapCommonUtils.resultMap = resultMap;
	}
	
	

	public static Hashtable<String, List<ProductSku>> getResultTable() {
		return resultTable;
	}

	public static void setResultTable(
			Hashtable<String, List<ProductSku>> resultTable) {
		SnapCommonUtils.resultTable = resultTable;
	}

	public static String StripPercentage(String input) {
		int index = input.indexOf('%');
		String result = input;
		if (index != -1) {
			result = input.substring(0, index - 1);
		}
		return result;
	}

	public static Dialog buildAlertListViewDialog(String title,
			String[] arrayList, Context context,
			OnItemClickListener onItemClickListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		ListView mListView = new ListView(context);
		mListView.setOnItemClickListener(onItemClickListener);
		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				arrayList);
		mListView.setAdapter(modeAdapter);
		builder.setView(mListView);
		return builder.create();
	}

	public static Drawable getBrandDrawable(Brand brand, Context context) {
		try {
			if (brand.isGDB())
				return Drawable.createFromStream(getSnapDataContext(context)
						.getAssets().open(brand.getBrandImageUrl()), "");
			else {
				Bitmap bitmap = getImageBitmap(brand);
				if (bitmap != null)
					return new BitmapDrawable(bitmap);
				else
					return context.getResources().getDrawable(
							R.drawable.icon_no_brand);
			}
		} catch (Exception e) {
			return context.getResources().getDrawable(R.drawable.icon_no_brand);
		}
	}

	public static Drawable getBrandDrawable(String imageUrl, Context context) {
		if (imageUrl == null)
			return context.getResources().getDrawable(R.drawable.icon_no_brand);
		try {
			InputStream is = getSnapDataContext(context).getAssets().open(
					imageUrl);
			return Drawable.createFromStream(is, "");
		} catch (Exception e) {
			Bitmap bitmap = getImageBitmap(imageUrl);
			if (bitmap != null)
				return new BitmapDrawable(bitmap);
			else
				return context.getResources().getDrawable(
						R.drawable.icon_no_brand);
		}
	}

	public static Context getSnapDataContext(Context context) {
		if (snapdataContext == null)
			try {
				snapdataContext = context.createPackageContext(
						"com.snapbizz.snapbizzdata",
						Context.CONTEXT_IGNORE_SECURITY);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		return snapdataContext;
	}

	public static void closeDatabaseHelper() {
		OpenHelperManager.releaseHelper();
		snapDatabaseHelper = null;
	}

	public static boolean hasProductDrawable(ProductSku productSku,
			Context context) {
		try {
			String imageUrl = "products/"
					+ getHash("snapbizz_product_"
							+ productSku.getProductSkuCode().replaceAll(" ",
									"_") + "_160x150") + ".jpg";
			getSnapDataContext(context).getAssets().open(imageUrl).close();
			return true;
			/*return Drawable.createFromStream(getSnapDataContext(context)
					.getAssets().open(imageUrl), "") == null ? false : true;*/
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return false;
	}

	public static boolean hasBrandDrawable(Brand brand, Context context) {
		try {
			String imageUrl = "brands/" + brand.getBrandName().toLowerCase()
					+ ".jpg";
			return Drawable.createFromStream(getSnapDataContext(context)
					.getAssets().open(imageUrl), "") == null ? false : true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static Drawable getForgottenProdDrawable(String forgotProdName,
			Context context) {
		try {

			forgotProdName = SnapToolkitConstants.FORGOTTEN_IMAGE_PREFIX
					+ forgotProdName.replaceAll(" ", "_").toLowerCase()
					+ ".png";
			return Drawable.createFromStream(getSnapDataContext(context)
					.getAssets().open(forgotProdName), "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return context.getResources().getDrawable(R.drawable.icon_no_product);
	}

	public static Drawable getProductCategoryDrawableFromBilling(
			ProductCategory productCategory, Context context) {
		Drawable drawable = null;
		try {
			drawable = Drawable.createFromStream(context.getAssets().open(
							"categories/" + productCategory.getCategoryId() + ".png"), "");
			getDrawableCache();
			addDrawableToCache(productCategory.getCategoryId() + "", drawable);
		}
		catch (IOException e1) {
			drawable = null;
			e1.printStackTrace();
		}
		catch (NullPointerException e1) {
			e1.printStackTrace();
			drawable = null;
		}
		if(drawable != null)
			return drawable;
		return context.getResources().getDrawable(R.drawable.icon_no_product);
	}

	public static Drawable getProductCategoryDrawable(
			ProductCategory productCategory, Context context) {
		Drawable drawable = null;
		try {
			Drawable cachedDrawable = getDrawableFromMemCache(productCategory.getCategoryId() + "");
			if (cachedDrawable != null)
				return cachedDrawable;
			drawable = Drawable.createFromStream(
					getSnapDataContext(context).getAssets().open(
							"categories/" + productCategory.getCategoryId() + ".png"), "");
			addDrawableToCache(productCategory.getCategoryId() + "", drawable);
			return drawable;
		} catch (Exception e) {
			try {
				drawable = Drawable.createFromStream(
						getSnapDataContext(context.getApplicationContext()).getAssets().open(
								"categories/" + productCategory.getCategoryId() + ".png"), "");
				getDrawableCache();
				addDrawableToCache(productCategory.getCategoryId() + "", drawable);
				e.printStackTrace();
			}
			catch (IOException e1) {
				drawable = null;
				e1.printStackTrace();
			}
			catch (NullPointerException e1) {
				e1.printStackTrace();
				drawable = null;
			}
		}
		if(drawable != null)
			return drawable;
		return getProductCategoryDrawableFromBilling(productCategory, context);
		
	}
	
	public static Drawable getProductDrawable(ProductSku productSku,
			Context context) {
		return getProductDrawable(productSku, context, true);
	}

	public static Drawable getProductDrawable(ProductSku productSku,
			Context context, boolean fAddToCache) {
		return getDrawable(productSku.getImageUrl(), context,
				productSku.isGDB(), fAddToCache);
	}

	public static Drawable getProductDrawable(String imageUrl, Context context) {
		return getProductDrawable(imageUrl, context, true);
	}

	public static Drawable getProductDrawable(String imageUrl, Context context,
			boolean fAddToCache) {
		return getDrawable(imageUrl, context, true, fAddToCache);
	}

	public static boolean storeCampaignBitmap(Bitmap bitmap, String imageUrl) {
		File camPath;
		camPath = new File(SnapToolkitConstants.VISIBILITY_IMAGE_PATH,
				"/campaign/");
		if (!camPath.exists()) {
			if (!camPath.mkdirs()) {
			}
		}
		return saveCampImg(bitmap, camPath, imageUrl);
	}

	private static boolean saveCampImg(Bitmap bitmapImage, File path,
			String imgUrl) {
		File camPath = new File(path, imgUrl);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(camPath);
			bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
			return true;
		} catch (Exception e) {
			Log.e("TAG","Error in image store---->",e);
			return false;
		}
	}
	
	public static Drawable checkProductDrawable(String imageUrl, Context context) {
		return checkProductDrawable(imageUrl, context, true);
	}

	public static Drawable checkProductDrawable(String imageUrl, Context context,
			boolean fAddToCache) {
		return checkDrawable(imageUrl, context, true, fAddToCache);
	}
	
	
	public static Drawable getDrawable(Context context, String imageUrl) {
		BitmapDrawable bmpDrawable = null;
		if (imageUrl == null)
			return null;
		try {
			Bitmap bitmap = getSampledBitmap(imageUrl, context);
			if (bitmap != null) {
				bmpDrawable = new BitmapDrawable(context.getResources(), bitmap);
				addDrawableToCache(imageUrl, bmpDrawable);
			}
		} catch (Exception e) {
			e.printStackTrace();
			bmpDrawable = null;
		}
		return bmpDrawable;
	}
	
	private static Drawable checkDrawable(String imageUrl, Context context,
			boolean fIsGdb, boolean fAddToCache) {
		Drawable drawable = null;
		BitmapDrawable bmpDrawable = null;
		boolean fCreateFromBitmap = false;
		try {
			if (imageUrl == null) {
				return null;
			}

			Drawable cachedDrawable = getDrawableFromMemCache(imageUrl);
			if (cachedDrawable != null) {
				return cachedDrawable;
			}

			if (fIsGdb) {
				 drawable = Drawable.createFromStream(
						getSnapDataContext(context).getAssets().open(imageUrl),
						"");
				if (fAddToCache) {
					addDrawableToCache(imageUrl, drawable);
				}
				return drawable;
			} else {
				fCreateFromBitmap = true;
			}
		} catch (Exception e) {
			fCreateFromBitmap = true;
			e.printStackTrace();
		}

		if (fCreateFromBitmap) {
			try {
				Bitmap bitmap = getSampledBitmap(imageUrl, context);
				Log.d("Drawable", "bitmap----->" + bitmap);
				Log.d("Drawable", "bitmap----ByteCount---->" + bitmap.getByteCount());
				if(bitmap.getByteCount() == 0){
					return null;
				}
				
				if (bitmap != null) {
				 bmpDrawable = new BitmapDrawable(bitmap);
					if (fAddToCache) {
						addDrawableToCache(imageUrl, bmpDrawable);
					}
					return bmpDrawable;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Log.d("Drawable", "bmpDrawable----->" + bmpDrawable);
		return bmpDrawable;
		
	}

	private static Drawable getDrawable(String imageUrl, Context context,
			boolean fIsGdb, boolean fAddToCache) {
		Drawable drawable = null;
		BitmapDrawable bmpDrawable = null;
		boolean fCreateFromBitmap = false;
		try {
			if (imageUrl == null) {
				return context.getResources().getDrawable(
						R.drawable.icon_no_product);
			}

			Drawable cachedDrawable = getDrawableFromMemCache(imageUrl);
			if (cachedDrawable != null) {
				return cachedDrawable;
			}

			if (fIsGdb) {
				 drawable = Drawable.createFromStream(
						getSnapDataContext(context).getAssets().open(imageUrl), "");
				if (fAddToCache) {
					addDrawableToCache(imageUrl, drawable);
				}
				return drawable;
			} else {
				fCreateFromBitmap = true;
			}
		} catch (Exception e) {
			fCreateFromBitmap = true;
		}

		if (fCreateFromBitmap) {
			try {
				Bitmap bitmap = getSampledBitmap(imageUrl, context);
				Log.d("Drawable", "bitmap----->" + bitmap);
				if (bitmap != null) {
				 bmpDrawable = new BitmapDrawable(bitmap);
					if (fAddToCache) {
						addDrawableToCache(imageUrl, bmpDrawable);
					}
					return bmpDrawable;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(bmpDrawable != null) {
			return bmpDrawable;
		}
		else{
			return context.getResources().getDrawable(R.drawable.icon_no_product);
		}

	}

	private static Bitmap getSampledBitmap(String imageUrl, Context context) {
		Bitmap newBitmap = null;
		try {
			Bitmap originalBmp = getImageBitmap(imageUrl);
			if (originalBmp != null) {
				newBitmap = Bitmap.createScaledBitmap(
						originalBmp,
						(int) context.getResources().getDimension(
								R.dimen.new_prod_image_width),
						(int) context.getResources().getDimension(
								R.dimen.new_prod_image_height), false);

				originalBmp.recycle();
				originalBmp = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newBitmap;
	}

	public static Drawable getQuickaddDrawable(String productCode,
			Context context) {
		try {

			productCode = SnapToolkitConstants.QUICKADD_IMAGE_PREFIX
					+ productCode.replaceAll("-", "_") + "_160x150";

			Drawable cachedDrawable = getDrawableFromMemCache(productCode);
			if (cachedDrawable != null) {
				return cachedDrawable;
			}

			Drawable drawable = context.getResources().getDrawable(
					context.getResources().getIdentifier(
							productCode.toLowerCase(), "drawable",
							context.getPackageName()));
			addDrawableToCache(productCode, drawable);
			return drawable;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return context.getResources().getDrawable(R.drawable.icon_no_product);
	}

	public static Drawable getProductLargeDrawable(String imageUrl,
			Context context) {
		try {
			String prodImageUrl = imageUrl.replace("products/",
					"products_large/");
			InputStream is = getSnapDataContext(context).getAssets().open(
					prodImageUrl);
			return Drawable.createFromStream(is, "");
		} catch (Exception e) {
			Bitmap bitmap = getImageBitmap(imageUrl);
			if (bitmap != null)
				return new BitmapDrawable(bitmap);
			else
				return context.getResources().getDrawable(
						R.drawable.icon_no_product);
		}
	}

	public static Drawable checkProductLargeDrawable(String imageUrl,
			Context context) {
		try {
			String prodImageUrl = imageUrl.replace("products/",
					"products_large/");
			InputStream is = getSnapDataContext(context).getAssets().open(
					prodImageUrl);
			return Drawable.createFromStream(is, "");
		} catch (Exception e) {
			Bitmap bitmap = getImageBitmap(imageUrl);
			if (bitmap != null)
				return new BitmapDrawable(bitmap);
			else
				return null;
		}
	}

	public static SnapBizzDatabaseHelper getDatabaseHelper(Context context) {
		try {
			if (snapDatabaseHelper == null || !snapDatabaseHelper.isOpen()) {
				snapDatabaseHelper = new SnapBizzDatabaseHelper(
						context.createPackageContext(
								SnapToolkitConstants.SNAP_PACKAGENAME,
								Context.CONTEXT_INCLUDE_CODE));
				snapDatabaseHelper.getReadableDatabase(context.getString(R.string.passkey));
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return snapDatabaseHelper;
	}

	public static SnapBizzDatabaseHelper getWritableDatabaseHelper(Context context) {
		try {
			if (snapDatabaseHelper == null || !snapDatabaseHelper.isOpen()) {
				snapDatabaseHelper = new SnapBizzDatabaseHelper(
						context.createPackageContext(
								SnapToolkitConstants.SNAP_PACKAGENAME,
								Context.CONTEXT_INCLUDE_CODE));
				snapDatabaseHelper.getWritableDatabase(context
						.getString(R.string.passkey));
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return snapDatabaseHelper;
	}

	public static SnapBizzSyncDatabaseHelper getSyncDatabaseHelper(Context context) {
		if (snapSyncDatabaseHelper == null || !snapSyncDatabaseHelper.isOpen())
			snapSyncDatabaseHelper = new SnapBizzSyncDatabaseHelper(
					getSnapContext(context));
		return snapSyncDatabaseHelper;
	}

	public static Context getSnapContext(Context context) {
		try {
			if (commonContext == null)
				commonContext = context.createPackageContext(
						SnapToolkitConstants.SNAP_PACKAGENAME,
						Context.CONTEXT_INCLUDE_CODE);
			return commonContext;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String convertStreamToString(InputStream inputStream) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		StringBuilder stringBuilder = new StringBuilder();

		String streamLine = null;
		try {
			while ((streamLine = reader.readLine()) != null) {
				stringBuilder.append(streamLine + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringBuilder.toString();
	}

	public static String buildEncodedQueryString(
			HashMap<String, String> requestParams) {
		String queryString = "?";
		if (requestParams == null) {
			return "";
		}
		Iterator<Entry<String, String>> it = requestParams.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			try {
				queryString += URLEncoder.encode(pairs.getKey().toString(),
						"UTF-8")
						+ "="
						+ URLEncoder.encode(pairs.getValue().toString(),
								"UTF-8") + "&";
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		if (queryString.length() > 0)
			queryString = queryString.substring(0, queryString.length() - 1);
		return queryString;
	}

	public static String getBarCode(String barCode) {
		return barCode.substring(5);
	}

	public static String getScannerId(String barCode) {
		if(barCode.length() < 5)
			return "";
		return barCode.substring(0, 5);
	}

	public static int getScannerDefaultShoppingCartId(String scannerId) {
		try {
			return Integer.parseInt(scannerId.charAt(4) + "") - 1;
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static String getMonthName(int month) {
		return new DateFormatSymbols().getMonths()[month - 1];
	}

	public static int getRandomColor() {
		Random rnd = new Random();
		return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256),
				rnd.nextInt(256));
	}

	// return a cached unique ID for each device
	public static String getDeviceID(Context context) {

		// if the ID isn't cached inside the class itself
		if (ID == null) {
			// get it from database / settings table (implement your own method
			// here)
			// ID = Settings.get("DeviceID");
			ID = SnapSharedUtils.getDeviceId(SnapCommonUtils
					.getSnapContext(context));
		}

		// if the saved value was incorrect
		if (ID.isEmpty()) {
			// generate a new ID
			ID = generateID(context);

			if (ID != null) {
				// save it to database / setting (implement your own method
				// here)
				// Settings.set("DeviceID", ID);
				SnapSharedUtils.setDeviceId(
						SnapCommonUtils.getSnapContext(context), ID);
			}
		}

		return ID;
	}

	// generate a unique ID for each device
	// use available schemes if possible / generate a random signature instead
	private static String generateID(Context context) {

		// use the ANDROID_ID constant, generated at the first device boot
		String deviceId = null;// Secure.getString(context.getContentResolver(),Secure.ANDROID_ID);

		// in case known problems are occured
		if (deviceId == null) {

			// get a unique deviceID like IMEI for GSM or ESN for CDMA phones
			// don't forget:
			// <uses-permission
			// android:name="android.permission.READ_PHONE_STATE" />
			deviceId = ((TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();

			// if nothing else works, generate a random number
			if (deviceId == null) {

				Random tmpRand = new Random();
				deviceId = String.valueOf(tmpRand.nextLong());
			}

		}
		return deviceId;

		// any value is hashed to have consistent format
		// return getHash(deviceId);
	}

	// generates a SHA-1 hash for any string
	public static String getHash(String stringToHash) {

		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		byte[] result = null;

		try {
			result = digest.digest(stringToHash.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		StringBuilder sb = new StringBuilder();

		for (byte b : result) {
			sb.append(String.format("%02X", b));
		}

		String messageDigest = sb.toString();
		return messageDigest;
	}

	public static String storeImageBitmap(Bitmap bitmap, String imageUrl) {
		System.out.println("storing " + imageUrl);
		File mypath = new File(imageUrl);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(mypath);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imageUrl;
	}

	public static String storeImageBitmap(Bitmap bitmap, Company company) {
		File mypath = new File(SnapToolkitConstants.COMPANY_FOLDER
				+ SnapCommonUtils.getHash(company.getCompanyName()));
		return storeBitmapToFile(bitmap, mypath);
	}

	public static String storeImageBitmap(Bitmap bitmap, Brand brand) {
		File mypath = new File(SnapToolkitConstants.BRANDS_FOLDER
				+ SnapCommonUtils.getHash(brand.getBrandName()));
		return storeBitmapToFile(bitmap, mypath);
	}

	public static String storeImageBitmap(Bitmap bitmap, ProductSku productSku) {
		// return
		// MediaStore.Images.Media.insertImage(getSnapDataContext(context).getContentResolver(),
		// bitmap, SnapToolkitConstants.PRODUCT_IMAGE_PREFIX+
		// productSku.getProductSkuCode(),
		// SnapToolkitConstants.PRODUCT_IMAGE_PREFIX+
		// productSku.getProductSkuCode());
		File mypath = new File(SnapToolkitConstants.PRODUCTS_FOLDER
				+ SnapCommonUtils.getHash(productSku.getProductSkuCode()));
		return storeBitmapToFile(bitmap, mypath);
	}

	private static String storeBitmapToFile(Bitmap bitmap, File file) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file.getAbsolutePath();
	}

	public static String storeImageBitmap(Bitmap bitmap, Order order) {
		File mypath = new File(SnapToolkitConstants.PO_FOLDER
				+ SnapCommonUtils.getHash(order.getOrderNumber() + ""));
		return storeBitmapToFile(bitmap, mypath);
	}

	public static Bitmap getImageBitmap(ProductSku productSku) {
		return getImageBitmap(productSku.getImageUrl());
	}

	public static Bitmap getImageBitmap(Brand brand) {
		return getImageBitmap(brand.getBrandImageUrl());
	}

	public static Bitmap getImageBitmap(Order order) {
		return getImageBitmap(order.getImage());
	}

	public static Bitmap getImageBitmap(String imageUrl) {
		try {
			if (imageUrl == null)
				return null;

			File f = new File(imageUrl);
			Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(f));
			return bmp;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Bitmap scaleBitmap(Bitmap bm,int maxWidth,int maxHeight) {
	    int width = bm.getWidth();
	    int height = bm.getHeight();
	    if (width > height) {
	        // landscape
	        float ratio = (float) width / maxWidth;
	        width = maxWidth;
	        height = (int)(height / ratio);
	    } else if (height > width) {
	        // portrait
	        float ratio = (float) height / maxHeight;
	        height = maxHeight;
	        width = (int)(width / ratio);
	    } else {
	        // square
	        height = maxHeight;
	        width = maxWidth;
	    }
	    bm = Bitmap.createScaledBitmap(bm, width, height, true);
	    return bm;
	}

	public static void showAlert(Context context, String title, String message,
			android.view.View.OnClickListener positiveClickListener,
			android.view.View.OnClickListener negativeClickListener,
			boolean isNotCancelable) {
		// Builder builder = new AlertDialog.Builder(context);
		if (alertDialog != null && alertDialog.isShowing()) {
			alertDialog.dismiss();
		}
		View view = ((Activity) context).getLayoutInflater().inflate(
				R.layout.alertdialog_layout, null);
		// builder.setView(view);
		((TextView) view.findViewById(R.id.alert_message_textview))
				.setText(message);
		((TextView) view.findViewById(R.id.alert_message_textview))
				.setTextColor(Color.parseColor("#000000"));
		// builder.setCancelable(!isNotCancelable);
		if (positiveClickListener == null) {
			((ImageButton) view.findViewById(R.id.positiveButton))
					.setOnClickListener(new android.view.View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if (alertDialog != null)
								alertDialog.dismiss();
						}
					});
		} else {
			((ImageButton) view.findViewById(R.id.positiveButton))
					.setOnClickListener(positiveClickListener);
		}
		if (negativeClickListener != null) {
			((ImageButton) view.findViewById(R.id.negativeButton))
					.setOnClickListener(negativeClickListener);
		}
		if (!((Activity) context).isFinishing()) {
			alertDialog = new Dialog(context);
			alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			alertDialog.setContentView(view);
			alertDialog.getWindow().setBackgroundDrawableResource(
					R.drawable.confirmation_pop_up_bg);
			alertDialog.setCancelable(!isNotCancelable);
			alertDialog.show();
		}
	}

	public static View showPinAlert(Context context,
			android.view.View.OnClickListener positiveClickListener,
			android.view.View.OnClickListener negativeClickListener,
			boolean isNotCancelable) {
		if (alertDialog != null && alertDialog.isShowing()) {
			alertDialog.dismiss();
		}
		View view = ((Activity) context).getLayoutInflater().inflate(
				R.layout.pin_dialog_layout, null);
		if (positiveClickListener == null) {
			((ImageButton) view.findViewById(R.id.positiveButton))
					.setOnClickListener(new android.view.View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if (alertDialog != null)
								alertDialog.dismiss();
						}
					});
		} else {
			((ImageButton) view.findViewById(R.id.positiveButton))
					.setOnClickListener(positiveClickListener);
		}

		if (negativeClickListener != null) {
			((ImageButton) view.findViewById(R.id.negativeButton))
					.setOnClickListener(negativeClickListener);
		}

		if (!((Activity) context).isFinishing()) {
			alertDialog = new Dialog(context);
			alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			alertDialog.setContentView(view);
			alertDialog.getWindow().setBackgroundDrawableResource(
					R.drawable.confirmation_pop_up_bg);
			alertDialog.setCancelable(!isNotCancelable);
			alertDialog.show();
		}
		return view;
	}
	public static View showMessageAlert(Context context,
			android.view.View.OnClickListener positiveClickListener,
			android.view.View.OnClickListener negativeClickListener,
			boolean isNotCancelable) {
		if (alertDialog != null && alertDialog.isShowing()) {
			alertDialog.dismiss();
		}
		View view = ((Activity) context).getLayoutInflater().inflate(
				R.layout.sku_alret_dialog, null);
		if (positiveClickListener == null) {
			((ImageButton) view.findViewById(R.id.positiveButton))
					.setOnClickListener(new android.view.View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if (alertDialog != null)
								alertDialog.dismiss();
						}
					});
		} else {
			((ImageButton) view.findViewById(R.id.positiveButton))
					.setOnClickListener(positiveClickListener);
		}

		if (negativeClickListener != null) {
			((ImageButton) view.findViewById(R.id.negativeButton))
					.setOnClickListener(negativeClickListener);
		}

		if (!((Activity) context).isFinishing()) {
			alertDialog = new Dialog(context);
			alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			alertDialog.setContentView(view);
			alertDialog.getWindow().setBackgroundDrawableResource(
					R.drawable.confirmation_pop_up_bg);
			alertDialog.setCancelable(!isNotCancelable);
			alertDialog.show();
		}
		return view;
	}
	
	
	public static boolean isDialogShowing() {
		return alertDialog != null && alertDialog.isShowing();
	}

	public static void showDeleteAlert(Context context, String title,
			String message,
			android.view.View.OnClickListener positiveClickListener,
			android.view.View.OnClickListener negativeClickListener,
			boolean isNotCancelable) {
		// Builder builder = new AlertDialog.Builder(context);
		View view = ((Activity) context).getLayoutInflater().inflate(
				R.layout.alertdialog_layout, null);
		// builder.setView(view);
		((TextView) view.findViewById(R.id.alert_message_textview))
				.setText(message);
		// builder.setCancelable(!isNotCancelable);
		((ImageButton) view.findViewById(R.id.positiveButton))
				.setImageResource(R.drawable.alertdialog_delete_selector);
		if (positiveClickListener == null) {
			((ImageButton) view.findViewById(R.id.positiveButton))
					.setOnClickListener(new android.view.View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if (alertDialog != null)
								alertDialog.dismiss();
						}
					});
		} else {
			((ImageButton) view.findViewById(R.id.positiveButton))
					.setOnClickListener(positiveClickListener);
		}
		if (negativeClickListener != null) {
			((ImageButton) view.findViewById(R.id.negativeButton))
					.setOnClickListener(negativeClickListener);
		}
		if (!((Activity) context).isFinishing()) {
			if (alertDialog != null && alertDialog.isShowing()) {
				alertDialog.dismiss();
			}
			alertDialog = new Dialog(context);
			alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			alertDialog.setContentView(view);
			alertDialog.getWindow().setBackgroundDrawableResource(
					R.drawable.confirmation_pop_up_bg);
			alertDialog.setCancelable(!isNotCancelable);
			alertDialog.show();
		}
	}

	public static void dismissAlert() {
		if (alertDialog != null) {
			alertDialog.dismiss();
			alertDialog = null;
		}
	}

	public static int daysBetween(Date d1, Date d2) {
		return (int) (Math.abs((d2.getTime() - d1.getTime())) / (1000 * 60 * 60 * 24));
	}

	public static void hideSoftKeyboard(Context context, IBinder iBinder) {
		if(context == null) // Probably check iBinder == null as well...
			return;
		InputMethodManager inputMethodManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if(inputMethodManager != null)
			inputMethodManager.hideSoftInputFromWindow(iBinder, 0);
	}

	public static void storeVideo(InputStream inputStream, String fileName) {
		BufferedInputStream inStream = new BufferedInputStream(inputStream,
				1024 * 5);
		File file = new File(SnapToolkitConstants.HELP_VIDEO_PATH, fileName);
		FileOutputStream outStream;
		try {
			outStream = new FileOutputStream(file);
			byte[] buff = new byte[5 * 1024];
			int len;
			while ((len = inStream.read(buff)) != -1) {
				outStream.write(buff, 0, len);
			}

			// clean up
			outStream.flush();
			outStream.close();
			inStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static SecretKey encryptVideo() {
		FileInputStream fis;
		SecretKey skey = null;
		ArrayList<String> videoNameList = new ArrayList<String>();
		videoNameList.add("add new product");
		videoNameList.add("adding a product to mystore");
		videoNameList.add("adding loose item and billing");
		videoNameList.add("adding quick add products");
		videoNameList.add("bills history");
		videoNameList.add("cancel bill");
		videoNameList.add("complete bill transaction");
		videoNameList.add("create distributor,receive");
		videoNameList.add("Create distributor");
		videoNameList.add("Creating new customer");
		videoNameList.add("Daily sales summary");
		videoNameList.add("Delivery vs paid");
		videoNameList.add("edit product details");
		videoNameList.add("edit qty and price");
		videoNameList.add("Excess and shortage stock");
		videoNameList.add("find a product by scanning");
		videoNameList.add("find a product by search");
		videoNameList.add("find aproduct by cat,sub cat and brands filter");
		videoNameList.add("Hot products");
		videoNameList.add("offers");
		videoNameList.add("order by searching");
		videoNameList.add("product return");
		videoNameList.add("Push offer Buy get offer");
		videoNameList.add("Push offer store wide offer");
		videoNameList.add("Sales alert");
		videoNameList.add("scan a product not in DB");
		videoNameList.add("scan a product to cart");
		videoNameList.add("Scan order and scan receive");
		videoNameList.add("search and add");
		videoNameList.add("Search and receive products");
		videoNameList.add("Select a distributor and order");
		videoNameList.add("Select existing customer and receive");
		videoNameList.add("Show case product as display products");
		videoNameList.add("showcase product as offers");
		videoNameList.add("simultaneous billing");
		videoNameList.add("Snap money");
		videoNameList.add("Stock charts");
		videoNameList.add("Stock money");
		videoNameList.add("Stock Summary");
		videoNameList.add("switch between carts");
		videoNameList.add("tag existing cutomer");
		videoNameList.add("Update or change the SP,MRP etc");
		videoNameList.add("Weekly monthly sales");

		try {
			for (int i = 0; i < videoNameList.size(); i++) {
				fis = new FileInputStream(new File(Environment
						.getExternalStorageDirectory().getPath()
						+ "/SnapVideos/" + videoNameList.get(i) + ".mp4"));

				File outfile = new File(Environment
						.getExternalStorageDirectory().getPath()
						+ "/SnapEncVideos/" + videoNameList.get(i) + "_enc.mp4");
				int read = 0;
				if (!outfile.exists())
					outfile.createNewFile();
				FileOutputStream fos = new FileOutputStream(outfile);

				FileInputStream encfis = new FileInputStream(outfile);
				Cipher encipher = Cipher.getInstance("AES"); // Cipher decipher
																// =
				// KeyGenerator kgen = KeyGenerator.getInstance("AES");
				skey = generateKey();
				encipher.init(Cipher.ENCRYPT_MODE, skey);
				CipherInputStream cis = new CipherInputStream(fis, encipher);

				// decipher.init(Cipher.DECRYPT_MODE, skey);
				// CipherOutputStream cos = new CipherOutputStream(decfos,
				// decipher);
				// long start = System.nanoTime();
				// Log.d("security", String.valueOf(start));
				while ((read = cis.read()) != -1) {
					fos.write((char) read);
					fos.flush();
				}

				/*
				 * byte[] buff=new byte[1024]; while((read =
				 * cis.read(buff))!=-1) { fos.write(buff,0,read); fos.flush(); }
				 * fos.close();
				 */
				// long stop = System.nanoTime();
				// Log.d("security", String.valueOf(stop));
				// long seconds = (stop - start) / 1000000000;// for seconds
				// Log.d("security", String.valueOf(seconds));
				fos.close();
				// mTimeTook = seconds;
				// mDoneEncryption = true;
			}
			return skey;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return skey;
	}

	public static SecretKey generateKey() throws NoSuchAlgorithmException,
			InvalidKeySpecException {
		// Number of PBKDF2 hardening rounds to use. Larger values increase
		// computation time. You should select a value that causes computation
		// to take >100ms.
		final int iterations = 1000;
		String saltString = "rand";
		// Generate a 256-bit key
		final int outputKeyLength = 256;
		String password = "pass";
		char[] passphraseOrPin = password.toCharArray();
		byte[] salt = saltString.getBytes();
		SecretKeyFactory secretKeyFactory = SecretKeyFactory
				.getInstance("PBKDF2WithHmacSHA1");
		KeySpec keySpec = new PBEKeySpec(passphraseOrPin, salt, iterations,
				outputKeyLength);
		SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
		return secretKey;
	}

	public static String decryptAndPlayVideo(String filename, String pathName) {
		try {
			Cipher decipher = null;
			decipher = Cipher.getInstance("AES");
			SecretKey skey = generateKey();
			decipher.init(Cipher.DECRYPT_MODE, skey);
			mServer = new LocalSingleHttpServer();
			mServer.setCipher(decipher);
			mServer.start();
			String path = mServer.getURL(pathName + filename);
			return path;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// view.setVideoPath();
		catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void appendLog(String logToWrite) {
		File logFile = new File(SnapToolkitConstants.EXTERNAL_LOG_FILE_PATH
				+ "/log.file");
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				SnapCommonUtils.appendLog(logToWrite);
				e.printStackTrace();
			}
		}
		try {
			// BufferedWriter for performance, true to set append to file flag
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile,
					true));
			buf.append(logToWrite);
			buf.newLine();
			buf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static CustomDrawableLruCache getDrawableCache() {
		if (mMemoryDrawableCache == null) {
			final int cacheSize = 100;
			mMemoryDrawableCache = new CustomDrawableLruCache(cacheSize);
		}
		return mMemoryDrawableCache;
	}

	public static void addDrawableToCache(String key, Drawable drawable) {
		if (getDrawableFromMemCache(key) == null) {
			mMemoryDrawableCache.put(key, drawable);
		}
	}

	public static Drawable getDrawableFromMemCache(String key) {
		if(mMemoryDrawableCache == null)
			getDrawableCache();
		return mMemoryDrawableCache.get(key);
	}

	public static String roundOffDecimalPoints(Float number) {
		return (String.format("%.2f", number));
	}
	
	public static List<Campaigns> removeCurrentCampaign(
			List<Campaigns> campaignsList, Campaigns currentCampaigns) {
		for (Iterator<Campaigns> iter = campaignsList.listIterator(); iter
				.hasNext();) {
			Campaigns campaign = iter.next();
			if (campaign.getId() == currentCampaigns.getId()) {
				iter.remove();
			}
		}
		return campaignsList;
	}
	
	public static boolean isNetworkAvailable(Context context) {
		boolean outcome = false;

		if (context != null) {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			// For 3G check
			if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null &&
					cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting()) {
				outcome = true;
			}
			// For WiFi Check
			if (cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null &&
					cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting()) {
				outcome = true;
			}

		}

		return outcome;
	}

	public static void showErrorDialogAndFinish(Context context, String message) {
		final Context mContext = context;
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
		alertDialogBuilder.setMessage(message);
		alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int id) {
		        if(alertDialog != null)
                    alertDialog.dismiss();
		        alertDialog = null;
		        ((Activity) mContext).finish();
		    }
		});
		alertDialog = alertDialogBuilder.create();
		alertDialog.show();
    }

	public static int getVatAmount(double vatRate, int sp, double qty) {
		return getVatAmount(vatRate, sp, qty, true);
	}

	public static int getVatAmount(double vatRate, int sp, double qty, boolean bAbs) {
		if(bAbs)
			qty = Math.abs(qty);
		return (int) (((sp) / (100 + vatRate)) * vatRate * qty);
	}

	public static String[] getDownloadSyncKeys() {
		String[] downloadSyncKeys = { SnapToolkitConstants.SYNC_DISTRIBUTOR_PRODUCT_MAP_KEY, 
				SnapToolkitConstants.SYNC_INVOICE_ITEMS_KEY, SnapToolkitConstants.SYNC_RECEIVE_ITEMS_KEY,
				SnapToolkitConstants.SYNC_COMPANY_RETRIEVAL_KEY, SnapToolkitConstants.SYNC_BRAND_RETRIEVAL_KEY,
				SnapToolkitConstants.SYNC_PRODUCTSKU_RETRIEVAL_KEY, SnapToolkitConstants.SYNC_ORDERSDETAILS_KEY,
				SnapToolkitConstants.SYNC_ORDERS_KEY, SnapToolkitConstants.SYNC_PAYMENTS_KEY,
				SnapToolkitConstants.SYNC_DISTRIBUTOR_BRAND_MAP_KEY, SnapToolkitConstants.SYNC_DISTRIBUTOR_KEY,
				SnapToolkitConstants.SYNC_BRAND_KEY, SnapToolkitConstants.SYNC_CAMPAIGN_KEY,
				SnapToolkitConstants.SYNC_COMPANY_KEY, SnapToolkitConstants.SYNC_INVENTORYBATCH_KEY,
				SnapToolkitConstants.SYNC_TRANSACTION_KEY, SnapToolkitConstants.SYNC_BILLITEM_KEY,
				SnapToolkitConstants.SYNC_INVENTORY_KEY, SnapToolkitConstants.SYNC_CUSTOMER_PAYMENT_KEY,
				SnapToolkitConstants.SYNC_CUSTOMER_KEY, SnapToolkitConstants.SYNC_CUSTOMERSUGGESTIONS_KEY,
				SnapToolkitConstants.SYNC_PRODUCTSKU_KEY, SnapToolkitConstants.SYNC_CATEGORY_KEY,
			};
		return downloadSyncKeys;
		
	}
	
    public static boolean isInteger(String s) {
        try {
           Integer.parseInt(s);
           return true;
        }
        catch (NumberFormatException ex) { }
        return false;
    }

    public static int getCurrentFY() {
		return getCurrentFY(null);
    }

    public static int getCurrentFY(Date date) {
    	Calendar calendar = Calendar.getInstance();
    	if(date != null)
    		calendar.setTime(date);
    	int year = calendar.get(Calendar.YEAR) % 100;
		if(calendar.get(Calendar.MONTH) <= Calendar.MARCH)
			year = year - 1;
		return year;
    }

    public static long getInvoiceOrMemoYear(long id, boolean bIsInvoice) {
        if(id < SnapToolkitConstants.YEAR_MULTIPLIER)
            return -1;
        return (id / SnapToolkitConstants.YEAR_MULTIPLIER) % 100;
    }

	public static String getCurrentVersion(Context context){
		String currentVersion = "";
		try {
			currentVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (Exception e) {
			currentVersion = context.getString(R.string.app_build_version);
		}
		if(SnapToolkitConstants.PRODUCTION_BUILD)
			currentVersion = currentVersion + "( Prod )";
		else
			currentVersion = currentVersion + "( Test )";
		return currentVersion;
	}

	public static void forceCloseApp(Activity activity) {
		activity.finishAffinity();
		System.exit(0);
	}

	public static int getCurrentVersionCode(Context context, String packageName){
		int currentVersion = 0;;
		try {
			currentVersion = context.getPackageManager().getPackageInfo(packageName, 0).versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return currentVersion;
	}

	public static Date setTimeToEOD(Date date) {
		Calendar calendar = Calendar.getInstance();
		if(date != null)
			calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}

	public static Date setTimeToSOD(Date date) {
		Calendar calendar = Calendar.getInstance();
		if(date != null)
			calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	public static double formatDecimalValue(double decimalNumber) {
		formatter = new DecimalFormat(SnapToolkitTextFormatter.PRINT_PRICE_FORMAT);
		return Double.parseDouble(formatter.format(decimalNumber));
	}

	public static boolean isScanner(String deviceName) {
		if(deviceName != null && (deviceName.contains(SnapToolkitConstants.SCANNER_KEY) ||
								  deviceName.contains(SnapToolkitConstants.SCANNER_KEY2) ||
								  deviceName.contains(SnapToolkitConstants.SCANNER_KEY3)))
			return true;
		return false;
	}
}
