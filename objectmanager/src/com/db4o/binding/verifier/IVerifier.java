/*
 * Copyright (C) 2005 db4objects, Inc.  http://www.db4o.com
 */
package com.db4o.binding.verifier;

/**
 * IVerifier.  Verify data entry for some value or data type.
 */
public interface IVerifier {
    public boolean verifyFragment(String fragment);
    
	public boolean verifyFullValue(String value);
    
    public String getHint();
}
