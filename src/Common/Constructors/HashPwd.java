package Common.Constructors;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Hashes a given password using MD5 for network transmission
 * and data storage.
 */
public class HashPwd {
    String hashPwdText = null;

    /**
     * Hashes a given password using MD5 Digest
     * @param pwdText the password to hash
     * @return MD5 hash string of given password.
     */
    public String HashPwd(String pwdText) {
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(pwdText.getBytes());
            //Get the hash's bytes
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            hashPwdText = sb.toString();
            return hashPwdText;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashPwdText;
    }
}
