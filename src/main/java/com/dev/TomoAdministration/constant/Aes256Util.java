package com.dev.TomoAdministration.constant;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class Aes256Util {

	
	private static volatile Aes256Util INSTANCE;
	 
	   final static String secretKey = "oingisprettyintheworld1234567890"; // 32byte
	   static String IV = ""; // 16byte
	 
	   public static Aes256Util getInstance() {
	      if (INSTANCE == null) {
	         synchronized (Aes256Util.class) {
	            if (INSTANCE == null)
	               INSTANCE = new Aes256Util();
	         }
	      }
	      return INSTANCE;
	   }
	 
	   private Aes256Util() {
	      IV = secretKey.substring(0, 16);
	   }
	 
	   // 암호화
	   public static String AES_Encode(String str)
	         throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException,
	         InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
	      byte[] keyData = secretKey.getBytes();
	 
	      SecretKey secureKey = new SecretKeySpec(keyData, "AES");
	 
	      Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
	      c.init(Cipher.ENCRYPT_MODE, secureKey, new IvParameterSpec(IV.getBytes()));
	 
	      byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
	      String enStr = new String(Base64.encodeBase64(encrypted));
	 
	      return enStr;
	   }
	 
	   // 복호화
	   public static String AES_Decode(String str)
	         throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException,
	         InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
	      byte[] keyData = secretKey.getBytes();
	      SecretKey secureKey = new SecretKeySpec(keyData, "AES");
	      Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
	      c.init(Cipher.DECRYPT_MODE, secureKey, new IvParameterSpec(IV.getBytes("UTF-8")));
	 
	      byte[] byteStr = Base64.decodeBase64(str.getBytes());
	 
	      return new String(c.doFinal(byteStr), "UTF-8");
	   }
}
