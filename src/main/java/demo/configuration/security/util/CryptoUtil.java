package demo.configuration.security.util;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encryption and Decryption of plain text string using PBE(Password Based
 * Encryption and Decryption)
 */
public final class CryptoUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(CryptoUtil.class);
	// Encryption Algorithm
	private static final String ENCRYPTION_ALGO = "PBEWithMD5AndDES";
	// Character set for Base64 encoding / decoding
	private static final String CHARSET = "UTF-8";
	// 8-byte Salt
	private static final byte[] salt = { (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32, (byte) 0x56, (byte) 0x35,
			(byte) 0xE3, (byte) 0x03 };
	// Iteration count
	private static final int ITERATION_COUNT = 19;

	/**
	 * Encrypts the given plain text using the <b>PBEWithMD5AndDES</b> algorithm
	 * with the given password, salt and iteration count
	 * 
	 * @param plainText
	 *            the plain text to be encoded
	 * @param password
	 *            the password to be used for encryption
	 * @return the encrypted string or null if an exception occurred during
	 *         encryption
	 */
	public static final String encrypt(String plainText, String password) {

		try {
			KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT);
			SecretKey key = SecretKeyFactory.getInstance(ENCRYPTION_ALGO).generateSecret(keySpec);
			AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, ITERATION_COUNT);
			Cipher cipher = Cipher.getInstance(key.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			byte[] in = plainText.getBytes(CHARSET);
			byte[] out = cipher.doFinal(in);
			return new String(Base64.encodeBase64(out));
		} catch (Exception e) {
			LOGGER.error("Exception occurred while encrypting plain text", e);
			return null;
		}
	}

	/**
	 * Decrypts the given encrypted text using the <b>PBEWithMD5AndDES</b>
	 * algorithm with the given password, salt and iteration count
	 * 
	 * @param encryptedText
	 *            the encrypted text to be encoded
	 * @param password
	 *            the password to be used for decryption
	 * @return the decrypted plain text or null if an exception occurred during
	 *         decryption
	 */
	public static final String decrypt(String encryptedText, String password) {

		try {
			KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT);
			SecretKey key = SecretKeyFactory.getInstance(ENCRYPTION_ALGO).generateSecret(keySpec);
			AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, ITERATION_COUNT);
			Cipher cipher = Cipher.getInstance(key.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
			byte[] enc = Base64.decodeBase64(encryptedText);
			byte[] utf8 = cipher.doFinal(enc);
			return new String(utf8, CHARSET);
		} catch (Exception e) {
			LOGGER.error("Exception occurred while decrypting encrypted text", e);
			return null;
		}
	}

	public static void main(String[] args) {

		String password = args.length > 0 ? args[0] : "SamplePassword";
		String plainText = args.length > 1 ? args[1] : "SamplePlainText";
		String encryptedText = encrypt(plainText, password);
		String decryptedText = decrypt(encryptedText, password);
		LOGGER.info("Original text: {}", plainText);
		LOGGER.info("Encrypted text: {}", encryptedText);
		LOGGER.info("Original text after decryption: {}", decryptedText);
	}
}
