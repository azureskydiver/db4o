/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.converter;

import java.util.HashMap;

import com.db4o.binding.converters.ConvertBoolean2String;
import com.db4o.binding.converters.ConvertCharacter2String;
import com.db4o.binding.converters.ConvertDouble2String;
import com.db4o.binding.converters.ConvertFloat2String;
import com.db4o.binding.converters.ConvertInteger2String;
import com.db4o.binding.converters.ConvertLong2String;
import com.db4o.binding.converters.ConvertObject2String;
import com.db4o.binding.converters.ConvertString2Boolean;
import com.db4o.binding.converters.ConvertString2Character;
import com.db4o.binding.converters.ConvertString2Double;
import com.db4o.binding.converters.ConvertString2Float;
import com.db4o.binding.converters.ConvertString2Integer;
import com.db4o.binding.converters.ConvertString2Long;
import com.db4o.binding.converters.ConvertString2Object;
import com.db4o.binding.converters.TheIdentityConverter;

/**
 * Converter.  The base converter from which all converters can be found.
 *
 * @author djo
 */
public class Converter {
	private static HashMap converters;
    
    /*
     * Returns the set of converters to convert from a specified source class
     */
    private static HashMap getSourceClassConverters(Class sourceClass) {
        HashMap result = (HashMap) converters.get(sourceClass);
        
        if (result == null) {
            result = new HashMap();
            converters.put(sourceClass, result);
        }
        
        return result;
    }
    
    /**
     * Associate a particular converter with a particular pair of classes.
     * 
     * @param sourceClass The type to convert from
     * @param destClass The type to convert to
     * @param converter The IConverter
     */
    public static void associate(Class sourceClass, Class destClass, IConverter converter) {
        HashMap sourceClassConverters = getSourceClassConverters(sourceClass);
        sourceClassConverters.put(destClass, converter);
    }
    
    /**
     * Return an IConverter for a specific class2class conversion.
     * 
     * @param sourceClass
     * @param destClass
     * @return An appropriate IConverter
     */
    public static IConverter get(Class sourceClass, Class destClass) {
        if (sourceClass.equals(destClass))
            return TheIdentityConverter.IDENTITY;
        
        HashMap sourceClassConverters = (HashMap) converters.get(sourceClass);
        
        if (sourceClassConverters == null)
            throw new IllegalArgumentException("No converters from source class " + sourceClass.getName() + " have been registered");
        
        IConverter result = (IConverter) sourceClassConverters.get(destClass);
        
        if (result == null)
            throw new IllegalArgumentException("No converters for pair (" + sourceClass.getName() + ", " + destClass.getName() + ") have been registered");
        
        return result;
    }
    
    static {
        converters = new HashMap();
        
        associate(Object.class, String.class, new ConvertObject2String());
        associate(String.class, Object.class, new ConvertString2Object());
        
        associate(Character.TYPE, String.class, new ConvertCharacter2String());
        associate(String.class, Character.TYPE, new ConvertString2Character());

        associate(Boolean.TYPE, String.class, new ConvertBoolean2String());
        associate(String.class, Boolean.TYPE, new ConvertString2Boolean());
        
        associate(Integer.TYPE, String.class, new ConvertInteger2String());
        associate(String.class, Integer.TYPE, new ConvertString2Integer());
        
        associate(Long.TYPE, String.class, new ConvertLong2String());
        associate(String.class, Long.TYPE, new ConvertString2Long());
        
        associate(Float.TYPE, String.class, new ConvertFloat2String());
        associate(String.class, Float.TYPE, new ConvertString2Float());
        
        associate(Double.TYPE, String.class, new ConvertDouble2String());
        associate(String.class, Double.TYPE, new ConvertString2Double());
    }
}


