/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.verifiers.reusable;

import com.db4o.binding.verifier.IVerifier;

/**
 * ReadOnlyVerifier.
 *
 * @author djo
 */
public class ReadOnlyVerifier implements IVerifier {
    
    private static ReadOnlyVerifier singleton = null;
    
    public static ReadOnlyVerifier getDefault() {
        if (singleton == null) {
            singleton = new ReadOnlyVerifier();
        }
        return singleton;
    }

	/* (non-Javadoc)
	 * @see org.eclipse.jface.binding.verifier.IVerifier#verifyFragment(java.lang.String)
	 */
	public boolean verifyFragment(String fragment) {
		// No changes are allowed
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.binding.verifier.IVerifier#verifyFullValue(java.lang.String)
	 */
	public boolean verifyFullValue(String value) {
        // But the current value is accepted
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.binding.verifier.IVerifier#getHint()
	 */
	public String getHint() {
		return "No changes are allowed in this field";
	}

}
