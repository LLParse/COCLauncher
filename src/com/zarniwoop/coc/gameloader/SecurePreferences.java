/*
Copyright (C) 2012 Sveinung Kval Bakken, sveinung.bakken@gmail.com

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 */

package com.zarniwoop.coc.gameloader;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

public class SecurePreferences {

	public static class SecurePreferencesException extends RuntimeException {
		private static final long serialVersionUID = 4433828343099087333L;
		public SecurePreferencesException(Throwable e) {
			super(e);
		}
	}

	private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
	private static final String KEY_TRANSFORMATION = "AES/ECB/PKCS5Padding";
	private static final String SECRET_KEY_HASH_TRANSFORMATION = "SHA-256";
	private static final String CHARSET = "UTF-8";

	private final Cipher writer;
	private final Cipher reader;
	private final Cipher keyWriter;
	private final Cipher keyReader;
	private final SharedPreferences preferences;

	/**
	 * This will initialize an instance of the SecurePreferences class
	 * 
	 * @param context
	 *            your current context.
	 * @param preferenceName
	 *            name of preferences file (preferenceName.xml)
	 * @param secureKey
	 *            the key used for encryption, finding a good key scheme is
	 *            hard. Hardcoding your key in the application is bad, but
	 *            better than plaintext preferences. Having the user enter the
	 *            key upon application launch is a safe(r) alternative, but
	 *            annoying to the user.
	 * @throws SecurePreferencesException
	 */
	public SecurePreferences(Context context, String preferenceName,
			String secureKey) throws SecurePreferencesException {
		try {
			this.writer = Cipher.getInstance(TRANSFORMATION);
			this.reader = Cipher.getInstance(TRANSFORMATION);
			this.keyWriter = Cipher.getInstance(KEY_TRANSFORMATION);
			this.keyReader = Cipher.getInstance(KEY_TRANSFORMATION);
			this.preferences = context.getSharedPreferences(preferenceName, 0);
			initCiphers(secureKey);
		} catch (GeneralSecurityException e) {
			throw new SecurePreferencesException(e);
		} catch (UnsupportedEncodingException e) {
			throw new SecurePreferencesException(e);
		}
	}

	protected void initCiphers(String secureKey)
			throws UnsupportedEncodingException, NoSuchAlgorithmException,
			InvalidKeyException, InvalidAlgorithmParameterException {
		IvParameterSpec ivSpec = getIv();
		SecretKeySpec secretKey = getSecretKey(secureKey);

		writer.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
		reader.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
		keyWriter.init(Cipher.ENCRYPT_MODE, secretKey);
		keyReader.init(Cipher.DECRYPT_MODE, secretKey);
	}

	protected IvParameterSpec getIv() {
		byte[] iv = new byte[writer.getBlockSize()];
		System.arraycopy("fldsjfodasjifudslfjdsaofshaufihadsf".getBytes(), 0,
				iv, 0, writer.getBlockSize());
		return new IvParameterSpec(iv);
	}

	protected SecretKeySpec getSecretKey(String key)
			throws UnsupportedEncodingException, NoSuchAlgorithmException {
		byte[] keyBytes = createKeyBytes(key);
		return new SecretKeySpec(keyBytes, TRANSFORMATION);
	}

	protected byte[] createKeyBytes(String key)
			throws UnsupportedEncodingException, NoSuchAlgorithmException {
		MessageDigest md = MessageDigest
				.getInstance(SECRET_KEY_HASH_TRANSFORMATION);
		md.reset();
		byte[] keyBytes = md.digest(key.getBytes(CHARSET));
		return keyBytes;
	}

	public void put(String key, String value) {
		if (value == null) {
			preferences.edit().remove(toKey(key)).commit();
		} else {
			putValue(toKey(key), value);
		}
	}

	public boolean containsKey(String key) {
		return preferences.contains(toKey(key));
	}

	public void removeValue(String key) {
		preferences.edit().remove(toKey(key)).commit();
	}

	public String getString(String key) throws SecurePreferencesException {
		if (preferences.contains(toKey(key))) {
			String securedEncodedValue = preferences.getString(toKey(key), "");
			return decrypt(securedEncodedValue, reader);
		}
		return null;
	}
	
	public Map<String, String> getAllDecrypted() {
		Map<String,?> keys = preferences.getAll();

		Map<String, String> dec = new HashMap<String, String>();
		for(Map.Entry<String,?> entry : keys.entrySet()){
			String key = fromKey(entry.getKey());
			String value = getString(key);
			dec.put(key, value);
		 }
		return dec;
	}

	public void clear() {
		preferences.edit().clear().commit();
	}

	private String toKey(String key) {
		return encrypt(key, keyWriter);
	}
	
	private String fromKey(String key) {
		return decrypt(key, keyReader);
	}

	private void putValue(String key, String value)
			throws SecurePreferencesException {
		String secureValueEncoded = encrypt(value, writer);

		preferences.edit().putString(key, secureValueEncoded).commit();
	}

	@SuppressLint("NewApi")
	protected String encrypt(String value, Cipher writer)
			throws SecurePreferencesException {
		byte[] secureValue;
		try {
			secureValue = convert(writer, value.getBytes(CHARSET));
		} catch (UnsupportedEncodingException e) {
			throw new SecurePreferencesException(e);
		}
		String secureValueEncoded = Base64.encodeToString(secureValue,
				Base64.NO_WRAP);
		return secureValueEncoded;
	}

	@SuppressLint("NewApi")
	protected String decrypt(String securedEncodedValue, Cipher reader) {
		byte[] securedValue = Base64
				.decode(securedEncodedValue, Base64.NO_WRAP);
		byte[] value = convert(reader, securedValue);
		try {
			return new String(value, CHARSET);
		} catch (UnsupportedEncodingException e) {
			throw new SecurePreferencesException(e);
		}
	}

	private static byte[] convert(Cipher cipher, byte[] bs)
			throws SecurePreferencesException {
		try {
			return cipher.doFinal(bs);
		} catch (Exception e) {
			throw new SecurePreferencesException(e);
		}
	}
}