/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.verifiers.reusable;

import org.apache.oro.text.perl.Perl5Util;

import com.db4o.binding.verifier.IVerifier;

/**
 * RegularExpressionVerifier.  A Verifier that uses regular expressions to
 * specify verification rules.
 *
 * @author djo
 */
public class RegularExpressionVerifier implements IVerifier {
    
    private String fragmentRegex;
    private String fullValueRegex;
    private String hint;
    
	/**
     * Constructor RegularExpressionVerifier.
     * 
     * Verify input using regulare expressions.
     * 
	 * @param fragmentRegex
	 * @param fullValueRegex
	 * @param hint
	 */
	public RegularExpressionVerifier(String fragmentRegex,
			String fullValueRegex, String hint) {
		super();
		this.fragmentRegex = fragmentRegex;
		this.fullValueRegex = fullValueRegex;
		this.hint = hint;
	}
    
    private Perl5Util matcher = new Perl5Util();
    
	/* (non-Javadoc)
	 * @see org.eclipse.jface.binding.verifier.IVerifier#verifyFragment(java.lang.String)
	 */
	public boolean verifyFragment(String fragment) {
		return matcher.match(fragmentRegex, fragment);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.binding.verifier.IVerifier#verifyFullValue(java.lang.String)
	 */
	public boolean verifyFullValue(String value) {
		return matcher.match(fullValueRegex, value);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.binding.verifier.IVerifier#getHint()
	 */
	public String getHint() {
		return hint;
	}

}
