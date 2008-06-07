package com.db4o.omplus.crypto;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import org.apache.commons.codec.binary.Hex;

public class SymmetricEncrypter
{

 	private SecretKey _key;
    private Cipher _cipher;


    public SymmetricEncrypter(String encryptionScheme, String encryptionKey)
        throws InvalidKeyException, InvalidKeySpecException, NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException
    {
        if(encryptionKey == null)
            throw new IllegalArgumentException("No Encryption Key found");
        if(encryptionKey.trim().length() < 24)
            throw new IllegalArgumentException("Encryption Key Length < 24)");
        byte keyAsBytes[] = encryptionKey.getBytes();
        java.security.spec.KeySpec keySpec = null;
        if(encryptionScheme.equals("DESede"))
            keySpec = new DESedeKeySpec(keyAsBytes);
        else
        if(encryptionScheme.equals("DES"))
            keySpec = new DESKeySpec(keyAsBytes);
        else
            throw new IllegalArgumentException("Unsupported Scheme: " + encryptionScheme);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(encryptionScheme);
        _cipher = Cipher.getInstance(encryptionScheme);
        _key = keyFactory.generateSecret(keySpec);
    }

    public String encrypt(String unencryptedString)
        throws Exception
    {
        String encryptedString = null;
        if(unencryptedString != null)
        {
            byte unencryptedAsBytes[] = unencryptedString.getBytes();
            byte encryptedAsBytes[] = (byte[])null;
            synchronized(_cipher)
            {
                _cipher.init(1, _key);
                encryptedAsBytes = _cipher.doFinal(unencryptedAsBytes);
            }
            if(encryptedAsBytes != null)
                encryptedString = new String(Hex.encodeHex(encryptedAsBytes));
        }
        return encryptedString;
    }

    public String decrypt(String encryptedString)
        throws Exception
    {
        String decryptedString = null;
        if(encryptedString != null)
        {
            byte encryptedAsBytes[] = Hex.decodeHex(encryptedString.toCharArray());
            byte decryptedAsBytes[] = (byte[])null;
            synchronized(_cipher)
            {
                _cipher.init(2, _key);
                decryptedAsBytes = _cipher.doFinal(encryptedAsBytes);
            }
            if(decryptedAsBytes != null)
                decryptedString = new String(decryptedAsBytes);
        }
        return decryptedString;
    }


}
