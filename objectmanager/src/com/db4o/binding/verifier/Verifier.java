/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.verifier;

import java.util.HashMap;


import com.db4o.binding.verifiers.DoubleVerifier;
import com.db4o.binding.verifiers.FloatVerifier;
import com.db4o.binding.verifiers.IntVerifier;
import com.db4o.binding.verifiers.LongVerifier;
import com.db4o.binding.verifiers.reusable.ReadOnlyVerifier;
import com.db4o.binding.verifiers.reusable.RegularExpressionVerifier;

/**
 * Verifier.  The base verifier from which all verifiers can be found.
 *
 * @author djo
 */
public class Verifier {
	private static HashMap verifiers;
    
    /**
     * Associate a particular verifier with a particular Java class.
     * 
     * @param klass
     * @param verifier
     */
    public static void associate(Class klass, IVerifier verifier) {
        verifiers.put(klass, verifier);
    }
    
    /**
     * Return an IVerifier for a specific class.
     * 
     * @param klass The Class to verify
     * @return An appropriate IVerifier
     */
    public static IVerifier get(Class klass) {
        IVerifier result = (IVerifier) verifiers.get(klass);
        if (result == null) {
            return ReadOnlyVerifier.getDefault();
        }
        return result;
    }
    
    static {
        verifiers = new HashMap();
        
        // Standalone verifiers here...
        associate(Integer.TYPE, new IntVerifier());
        associate(Long.TYPE, new LongVerifier());
        associate(Float.TYPE, new FloatVerifier());
        associate(Double.TYPE, new DoubleVerifier());
        
        // Regex-implemented verifiers here...
        associate(Character.TYPE, new RegularExpressionVerifier(
                "/^.$|^$/", "/./", "Please type a character"));
        associate(Boolean.TYPE, new RegularExpressionVerifier(
                "/Y|y|Ye|ye|Yes|yes|N|n|No|no/", "/Yes|yes|No|no/", "Please type \"Yes\" or \"No\""));
    }
}
