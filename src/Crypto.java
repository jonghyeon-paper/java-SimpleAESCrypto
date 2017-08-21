import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
	
	private static Crypto instance;
	
	private String cipherKey;
	private String ivKey;
	private IvParameterSpec iv;
	private SecretKey keySpec;
	
	private boolean intialize;
	
	/**
	 * Initialize key and vi 
	 * @throws UnsupportedEncodingException
	 */
	private Crypto(boolean intialize) throws UnsupportedEncodingException {
		this.intialize = intialize;
		
		if (this.intialize) {
			// 인스턴스화시 새로운 키와 초기화벡터를 생성한다.
			generateKeyAndIv();
		} else {
			// 인스턴스화시 고정된 키와 초기화벡터를 사용한다.
			this.cipherKey = "zM5XJhSyqnqi6TKSvnG04w==";
			this.ivKey = "kCXFAHVvvNcdvZKrM5d+wg==";
			this.keySpec = new SecretKeySpec(Base64.getDecoder().decode(cipherKey), "AES");
			this.iv = new IvParameterSpec(Base64.getDecoder().decode(ivKey));
		}
	}
	
	public static Crypto getInstance() {
		if (instance == null) {
			try {
				instance = new Crypto(true);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return instance;
	}
	
	public String encrypt(String plainText) {
		String cipherTextString = "";
		try {
			byte[] cipherTextByte = transform(Cipher.ENCRYPT_MODE, plainText.getBytes());
			cipherTextString = Base64.getEncoder().encodeToString(cipherTextByte);
			//System.out.println("encrypted string: " + cipherTextString);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		
		return cipherTextString;
	}

	public String decrypt(String cipherText) {
		String plainTextString = "";
		
		byte[] plainTextByte;
		try {
			plainTextByte = transform(Cipher.DECRYPT_MODE, Base64.getDecoder().decode(cipherText));
			plainTextString = new String(plainTextByte);
			//System.out.println("decrypted string: " + plainTextString);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		
		return plainTextString;
	}
	
	private byte[] transform(int mode, byte[] message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		cipher.init(mode, keySpec, iv);
		return cipher.doFinal(message);
	}
	
	private void generateKeyAndIv() {
		try {
			KeyGenerator generator = KeyGenerator.getInstance("AES");
			SecureRandom randomForKey = SecureRandom.getInstance("SHA1PRNG");
			generator.init(128, randomForKey);
			SecretKey secureKey = generator.generateKey();
			this.keySpec = secureKey;
			
			String generateKey = Base64.getEncoder().encodeToString(secureKey.getEncoded());
			System.out.println("generated key : " + generateKey);
			
			SecureRandom randomForIv = SecureRandom.getInstance("SHA1PRNG");
			byte[] ivByte = new byte[16];
			randomForIv.nextBytes(ivByte);
			this.iv = new IvParameterSpec(ivByte);
			
			String generateIv = Base64.getEncoder().encodeToString(ivByte);
			System.out.println("generated iv : " + generateIv);
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		// test code
		String text = "hello world!!";
		System.out.println(text);
		
		String cipherText = Crypto.getInstance().encrypt(text);
		String plainText = Crypto.getInstance().decrypt(cipherText);
		
		System.out.println(cipherText);
		System.out.println(plainText);
	}
}
