package TTS;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class passwordSalter {

	public static String salt_password(String str) {
		
		// throw exception if hashing algorithm doesn't exist
		
		try {
		
		// use SecureRandom generator
		// SHA1PRNG is a hashing algorithm
		
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		
		byte[] b = str.getBytes();
		
		sr.nextBytes(b);
		
		return Arrays.toString(b);
		
		}
		
		catch (NoSuchAlgorithmException e) {
			
			System.out.println(e);
			
		}
		return str;
		
	}
}
