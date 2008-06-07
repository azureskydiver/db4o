package com.db4o.omplus.crypto;

public class UserDetailsEncrypter {

	private String _userCode;
	private String _password;
    private SymmetricEncrypter _encrypter;

    public UserDetailsEncrypter(String userCode, String password,String encryptionKey)
        throws Exception
    {
        _userCode=userCode;
        _password=password;

        init(encryptionKey);
    }

    public UserDetailsEncrypter(String encryptedStr,String encryptionKey)
        throws Exception
    {
    	init(encryptionKey);
        decryptLink(encryptedStr);
    }

    public String getEncryptedString()
        throws Exception
    {
        StringBuffer encryptionBuffer = new StringBuffer();
        encryptionBuffer.append("?UC=");
        encryptionBuffer.append(_userCode);
        encryptionBuffer.append("&PWD=");
        encryptionBuffer.append(_password);
        String encryptedString = null;
        try
        {
            encryptedString = _encrypter.encrypt(encryptionBuffer.toString());
        }
        catch(Exception exp)
        {

            throw new Exception("Encrypting Report Link", exp);
        }
        return encryptedString;
    }

    private void init(String encryptionKey)
        throws Exception
    {
        try
        {
            if(encryptionKey == null || encryptionKey.length() == 0)
                encryptionKey = "We have Used Strong Encryption";
            _encrypter = new SymmetricEncrypter("DESede", encryptionKey);
        }
        catch(Exception exp)
        {
            throw new Exception("Symmetric Encryption Initialization", exp);
        }
    }

    private void decryptLink(String encryptedStr)
        throws Exception
    {
        try
        {
            String decryptedStr = _encrypter.decrypt(encryptedStr);
            int beginIndex = "?UC=".length();
            int endIndex = decryptedStr.indexOf("&PWD=");
            if(!decryptedStr.startsWith("?UC=") && endIndex > 0)
                throw new Exception("Invalid Cache Data Format");
            String userCodeStr = decryptedStr.substring(beginIndex, endIndex);
            String passwordStr = decryptedStr.substring(endIndex + "&PWD=".length());
            try
            {
                _userCode = userCodeStr;
                _password = passwordStr;
            }
            catch(NumberFormatException nfex)
            {

                throw new Exception("User code and Password Parsing", nfex);
            }
        }
        catch(Exception exp)
        {
        	throw exp;
        }
    }

	public String getPassword() {
		return _password;
	}

	public void setPassword(String _password) {
		this._password = _password;
	}

	public String getUserCode() {
		return _userCode;
	}

	public void setUserCode(String code) {
		_userCode = code;
	}



}