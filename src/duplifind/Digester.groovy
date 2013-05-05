package duplifind

import java.security.MessageDigest

class Digester {

	static final int BLOCK_SIZE = 50 * 1024 * 1024

	static String getDigest(File f, String algorithm) {
		if (f?.isFile()){
			def md = MessageDigest.getInstance(algorithm)
			f.eachByte(BLOCK_SIZE) { byte[] buffer, int bytesRead ->
				md.update(buffer, 0, bytesRead)
			}
			return (new BigInteger(1, md.digest())).toString(16).padLeft(32, '0')
		}
		return null
	}

	static String getMD5(File f) {
		return getDigest(f, "MD5")
	}

	static String getSHA1(File f) {
		return getDigest(f, "SHA1")
	}
}


