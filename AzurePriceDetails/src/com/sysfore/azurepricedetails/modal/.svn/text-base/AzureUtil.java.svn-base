package com.sysfore.azurepricedetails.modal;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public class AzureUtil {

	private static byte[] key = { 0x74, 0x68, 0x69, 0x73, 0x49, 0x73, 0x41,
			0x53, 0x65, 0x63, 0x72, 0x65, 0x74, 0x4b, 0x65, 0x79 };

	/**
	 * Encrypt The string
	 */
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

	/**
	 * Decrypt of String
	 */
	public static String decrypt(String strToDecrypt) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			final SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			// byte[] decodedBytes = cipher.doFinal(strToDecrypt);
			final String decryptedString = new String(cipher.doFinal(Base64
					.decode(strToDecrypt, Base64.DEFAULT)));
			return decryptedString;
		} catch (Exception e) {

		}
		return null;
	}

}
