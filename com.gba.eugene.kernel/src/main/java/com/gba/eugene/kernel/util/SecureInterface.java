package com.gba.eugene.kernel.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

public interface SecureInterface {

    /** Class Name implementing SecureInterface	*/
    public static final String	SYSTEM_SECURE = "SYSTEM_SECURE";
    /** Default Class Name implementing SecureInterface	*/
    public static final String	SYSTEM_SECURE_DEFAULT = "com.gba.eugene.util.Secure";

    /** Clear Text Indicator xyz	*/
    public static final String		CLEARVALUE_START = "xyz";
    /** Clear Text Indicator		*/
    public static final String		CLEARVALUE_END = "";
    /** Encrypted Text Indiactor ~	*/
    public static final String		ENCRYPTEDVALUE_START = "~";
    /** Encrypted Text Indiactor ~	*/
    public static final String		ENCRYPTEDVALUE_END = "~";

    /**
     *	Encryption.
     *  @param value clear value
     *  @return encrypted String
     */
    public String encrypt (String value);

    /**
     *	Decryption.
     *  @param value encrypted value
     *  @return decrypted String
     */
    public String decrypt (String value);

    /**
     *	Encryption.
     * 	The methods must recognize clear text values
     *  @param value clear value
     *  @return encrypted String
     */
    public Integer encrypt (Integer value);

    /**
     *	Decryption.
     * 	The methods must recognize clear text values
     *  @param value encrypted value
     *  @return decrypted String
     */
    public Integer decrypt (Integer value);

    /**
     *	Encryption.
     * 	The methods must recognize clear text values
     *  @param value clear value
     *  @return encrypted String
     */
    public BigDecimal encrypt (BigDecimal value);

    /**
     *	Decryption.
     * 	The methods must recognize clear text values
     *  @param value encrypted value
     *  @return decrypted String
     */
    public BigDecimal decrypt (BigDecimal value);

    /**
     *	Encryption.
     * 	The methods must recognize clear text values
     *  @param value clear value
     *  @return encrypted String
     */
    public Timestamp encrypt (Timestamp value);

    /**
     *	Decryption.
     * 	The methods must recognize clear text values
     *  @param value encrypted value
     *  @return decrypted String
     */
    public Timestamp decrypt (Timestamp value);


    /**
     *  Convert String to Digest.
     *  JavaScript version see - http://pajhome.org.uk/crypt/md5/index.html
     *
     *  @param value message
     *  @return HexString of message (length = 32 characters)
     */
    public String getDigest (String value);

    /**
     * 	Checks, if value is a valid digest
     *  @param value digest string
     *  @return true if valid digest
     */
    public boolean isDigest (String value);

    /**
     *  Convert String and salt to SHA-512 hash with iterations
     *  https://www.owasp.org/index.php/Hashing_Java
     *
     *  @param value message
     *  @return HexString of message (length = 128 characters)
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public String getSHA512Hash (int iterations, String value, byte[] salt) throws NoSuchAlgorithmException, UnsupportedEncodingException;
}
