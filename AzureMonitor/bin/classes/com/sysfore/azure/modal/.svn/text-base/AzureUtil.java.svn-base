package com.sysfore.azure.modal;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public class AzureUtil {

	/*
	 * static byte[] keyBytes = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05,
	 * 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11,
	 * 0x12, 0x13, 0x14, 0x15, 0x16, 0x17 };
	 * 
	 * public byte[] encrypt(String Str) throws Exception { SecretKey key = new
	 * SecretKeySpec(keyBytes, "AES"); Cipher cipher =
	 * Cipher.getInstance("AES/ECB/PKCS7Padding", "BC"); IvParameterSpec ips =
	 * new IvParameterSpec(keyBytes); byte[] inpBytes = Str.getBytes();
	 * cipher.init(Cipher.ENCRYPT_MODE, key, ips); return
	 * cipher.doFinal(inpBytes); }
	 * 
	 * public byte[] decrypt(String Str) throws Exception { SecretKey key = new
	 * SecretKeySpec(keyBytes, "AES"); Cipher cipher =
	 * Cipher.getInstance("AES/ECB/PKCS7Padding", "BC"); IvParameterSpec ips =
	 * new IvParameterSpec(keyBytes); byte[] inpBytes = Str.getBytes();
	 * cipher.init(Cipher.DECRYPT_MODE, key, ips); return
	 * cipher.doFinal(inpBytes); }
	 */
	private static byte[] key = { 0x74, 0x68, 0x69, 0x73, 0x49, 0x73, 0x41,
			0x53, 0x65, 0x63, 0x72, 0x65, 0x74, 0x4b, 0x65, 0x79 };// "thisIsASecretKey";

	public static String encrypt(String strToEncrypt) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			final SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] encodedBytes = cipher.doFinal(strToEncrypt.getBytes());
			final String encryptedString = new String(Base64.encodeToString(
					encodedBytes, Base64.DEFAULT));
			return encryptedString;
		} catch (Exception e) {

		}
		return null;

	}

	public static String decrypt(String strToDecrypt) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			final SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			// byte[] decodedBytes = cipher.doFinal(strToDecrypt);
			final String decryptedString = new String(cipher.doFinal(Base64.decode(strToDecrypt, Base64.DEFAULT)));
			return decryptedString;
		} catch (Exception e) {

		}
		return null;
	}

	
}
