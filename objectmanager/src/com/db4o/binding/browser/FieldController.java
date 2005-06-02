/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.browser;

import java.util.Date;
import java.util.HashMap;

import com.db4o.binding.converter.IConverter;
import com.db4o.binding.converters.ConvertBoolean2String;
import com.db4o.binding.converters.ConvertByte2String;
import com.db4o.binding.converters.ConvertCharacter2String;
import com.db4o.binding.converters.ConvertDate2String;
import com.db4o.binding.converters.ConvertDouble2String;
import com.db4o.binding.converters.ConvertFloat2String;
import com.db4o.binding.converters.ConvertInteger2String;
import com.db4o.binding.converters.ConvertLong2String;
import com.db4o.binding.converters.ConvertObject2String;
import com.db4o.binding.converters.ConvertShort2String;
import com.db4o.binding.converters.ConvertString2Boolean;
import com.db4o.binding.converters.ConvertString2Byte;
import com.db4o.binding.converters.ConvertString2Character;
import com.db4o.binding.converters.ConvertString2Date;
import com.db4o.binding.converters.ConvertString2Double;
import com.db4o.binding.converters.ConvertString2Float;
import com.db4o.binding.converters.ConvertString2Integer;
import com.db4o.binding.converters.ConvertString2Long;
import com.db4o.binding.converters.ConvertString2Object;
import com.db4o.binding.converters.ConvertString2Short;
import com.db4o.binding.converters.TheIdentityConverter;
import com.db4o.binding.converters.TheNullConverter;
import com.db4o.binding.field.IFieldController;
import com.db4o.binding.verifier.IVerifier;
import com.db4o.binding.verifiers.ByteVerifier;
import com.db4o.binding.verifiers.DateVerifier;
import com.db4o.binding.verifiers.DoubleVerifier;
import com.db4o.binding.verifiers.FloatVerifier;
import com.db4o.binding.verifiers.IntVerifier;
import com.db4o.binding.verifiers.LongVerifier;
import com.db4o.binding.verifiers.ShortVerifier;
import com.db4o.binding.verifiers.reusable.ReadOnlyVerifier;
import com.db4o.binding.verifiers.reusable.RegularExpressionVerifier;
import com.db4o.browser.model.IDatabase;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.Reflector;
import com.swtworkbench.community.xswt.metalogger.Logger;

public abstract class FieldController implements IFieldController {
    protected IDatabase database;
    protected Reflector reflector;

    public FieldController(IDatabase database) {
        this.database = database;
        this.reflector = database.reflector();
        
        converters = new HashMap();
        
        associate(c(Object.class), c(String.class), new ConvertObject2String());
        associate(c(String.class), c(Object.class), new ConvertString2Object());
        
        associate(c(Character.TYPE), c(String.class), new ConvertCharacter2String());
        associate(c(String.class), c(Character.TYPE), new ConvertString2Character());
        associate(c(Character.class), c(String.class), new ConvertCharacter2String());
        associate(c(String.class), c(Character.class), new ConvertString2Character());
        
        associate(c(Date.class), c(String.class), new ConvertDate2String());
        associate(c(String.class), c(Date.class), new ConvertString2Date());

        associate(c(Boolean.TYPE), c(String.class), new ConvertBoolean2String());
        associate(c(Boolean.class), c(String.class), new ConvertBoolean2String());
        associate(c(String.class), c(Boolean.TYPE), new ConvertString2Boolean());
        associate(c(String.class), c(Boolean.class), new ConvertString2Boolean());
        
        associate(c(Integer.class), c(String.class), new ConvertInteger2String());
        associate(c(Integer.TYPE), c(String.class), new ConvertInteger2String());
        associate(c(String.class), c(Integer.TYPE), new ConvertString2Integer());
        associate(c(String.class), c(Integer.class), new ConvertString2Integer());
        
        associate(c(Byte.class), c(String.class), new ConvertByte2String());
        associate(c(Byte.TYPE), c(String.class), new ConvertByte2String());
        associate(c(String.class), c(Byte.TYPE), new ConvertString2Byte());
        associate(c(String.class), c(Byte.class), new ConvertString2Byte());
        
        associate(c(Short.class), c(String.class), new ConvertShort2String());
        associate(c(Short.TYPE), c(String.class), new ConvertShort2String());
        associate(c(String.class), c(Short.TYPE), new ConvertString2Short());
        associate(c(String.class), c(Short.class), new ConvertString2Short());
        
        associate(c(Long.class), c(String.class), new ConvertLong2String());
        associate(c(Long.TYPE), c(String.class), new ConvertLong2String());
        associate(c(String.class), c(Long.class), new ConvertString2Long());
        associate(c(String.class), c(Long.TYPE), new ConvertString2Long());
        
        associate(c(Float.class), c(String.class), new ConvertFloat2String());
        associate(c(Float.TYPE), c(String.class), new ConvertFloat2String());
        associate(c(String.class), c(Float.class), new ConvertString2Float());
        associate(c(String.class), c(Float.TYPE), new ConvertString2Float());
        
        associate(c(Double.class), c(String.class), new ConvertDouble2String());
        associate(c(Double.TYPE), c(String.class), new ConvertDouble2String());
        associate(c(String.class), c(Double.class), new ConvertString2Double());
        associate(c(String.class), c(Double.TYPE), new ConvertString2Double());

        verifiers = new HashMap();
        
        // Standalone verifiers here...
        associate(c(Integer.TYPE), new IntVerifier());
        associate(c(Byte.TYPE), new ByteVerifier());
        associate(c(Short.TYPE), new ShortVerifier());
        associate(c(Long.TYPE), new LongVerifier());
        associate(c(Float.TYPE), new FloatVerifier());
        associate(c(Double.TYPE), new DoubleVerifier());
        associate(c(Integer.class), new IntVerifier());
        associate(c(Byte.class), new ByteVerifier());
        associate(c(Short.class), new ShortVerifier());
        associate(c(Long.class), new LongVerifier());
        associate(c(Float.class), new FloatVerifier());
        associate(c(Double.class), new DoubleVerifier());
        associate(c(Date.class), new DateVerifier());
        
        // Regex-implemented verifiers here...
        associate(c(String.class), new RegularExpressionVerifier(
                "/^.*$/", "/^.*$/", "Feel free to type anything"));
        associate(c(Character.TYPE), new RegularExpressionVerifier(
                "/^.$|^$/", "/./", "Please type a character"));
        associate(c(Boolean.TYPE), new RegularExpressionVerifier(
                "/^$|Y$|^y$|^Ye$|^ye$|^Yes$|^yes$|^T$|^t$|^Tr$|^tr$|^Tru$|^tru$|^True$|^true$|^N$|^n$|^No$|^no$|^F$|^f$|^Fa$|^fa$|^Fal$|^fal$|^Fals$|^fals$|^False$|^false$/", 
                "/Yes$|^yes$|^No$|^no$|^True$|^true$|^False$|^false/", 
                "Please type \"Yes\", \"No\", \"True\", or \"False\""));
        associate(c(Boolean.class), new RegularExpressionVerifier(
                "/^$|^Y$|^y$|^Ye$|^ye$|^Yes$|^yes$|^T$|^t$|^Tr$|^tr$|^Tru$|^tru$|^True$|^true$|^N$|^n$|^No$|^no$|^F$|^f$|^Fa$|^fa$|^Fal$|^fal$|^Fals$|^fals$|^False$|^false$/", 
                "/^Yes$|^yes$|^No$|^no$|^True$|^true$|^False$|^false$/", 
                "Please type \"Yes\", \"No\", \"True\", or \"False\""));
    }
    
    protected ReflectClass c(String name) {
        return reflector.forName(name);
    }
    
    protected ReflectClass c(Class clazz) {
        return reflector.forClass(clazz);
    }
    
    private HashMap converters;
    
    /*
     * Returns the set of converters to convert from a specified source class
     */
    private HashMap getSourceClassConverters(ReflectClass sourceClass) {
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
    protected void associate(ReflectClass sourceClass, ReflectClass destClass, IConverter converter) {
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
    protected IConverter get(ReflectClass sourceClass, ReflectClass destClass) {
        if (sourceClass.equals(destClass))
            return TheIdentityConverter.IDENTITY;
        
        HashMap sourceClassConverters = (HashMap) converters.get(sourceClass);
        
        if (sourceClassConverters == null) {
            Logger.log().message("No converters for pair (" + sourceClass + ", " + destClass + ") have been registered");
            return TheNullConverter.NULL;
        }
        
        IConverter result = (IConverter) sourceClassConverters.get(destClass);
        
        if (result == null) {
            Logger.log().message("No converters for pair (" + sourceClass + ", " + destClass + ") have been registered");
            return TheNullConverter.NULL;
        }
        
        return result;
    }
    
    private static HashMap verifiers;
    
    /**
     * Associate a particular verifier with a particular Java class.
     * 
     * @param klass
     * @param verifier
     */
    protected void associate(ReflectClass klass, IVerifier verifier) {
        verifiers.put(klass, verifier);
    }
    
    /**
     * Return an IVerifier for a specific class.
     * 
     * @param klass The Class to verify
     * @return An appropriate IVerifier
     */
    protected IVerifier get(ReflectClass klass) {
        IVerifier result = (IVerifier) verifiers.get(klass);
        if (result == null) {
            return ReadOnlyVerifier.getDefault();
        }
        return result;
    }
    
}
