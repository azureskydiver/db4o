/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.browser;

import java.util.Date;
import java.util.HashMap;

import org.eclipse.ve.sweet.converter.IConverter;
import org.eclipse.ve.sweet.converters.ConvertBoolean2String;
import org.eclipse.ve.sweet.converters.ConvertByte2String;
import org.eclipse.ve.sweet.converters.ConvertCharacter2String;
import org.eclipse.ve.sweet.converters.ConvertDate2String;
import org.eclipse.ve.sweet.converters.ConvertDouble2String;
import org.eclipse.ve.sweet.converters.ConvertFloat2String;
import org.eclipse.ve.sweet.converters.ConvertInteger2String;
import org.eclipse.ve.sweet.converters.ConvertLong2String;
import org.eclipse.ve.sweet.converters.ConvertObject2String;
import org.eclipse.ve.sweet.converters.ConvertShort2String;
import org.eclipse.ve.sweet.converters.ConvertString2Boolean;
import org.eclipse.ve.sweet.converters.ConvertString2Byte;
import org.eclipse.ve.sweet.converters.ConvertString2Character;
import org.eclipse.ve.sweet.converters.ConvertString2Date;
import org.eclipse.ve.sweet.converters.ConvertString2Double;
import org.eclipse.ve.sweet.converters.ConvertString2Float;
import org.eclipse.ve.sweet.converters.ConvertString2Integer;
import org.eclipse.ve.sweet.converters.ConvertString2Long;
import org.eclipse.ve.sweet.converters.ConvertString2Object;
import org.eclipse.ve.sweet.converters.ConvertString2Short;
import org.eclipse.ve.sweet.converters.TheIdentityConverter;
import org.eclipse.ve.sweet.converters.TheNullConverter;
import org.eclipse.ve.sweet.fieldviewer.IFieldViewer;
import org.eclipse.ve.sweet.metalogger.Logger;
import org.eclipse.ve.sweet.validator.IValidator;
import org.eclipse.ve.sweet.validators.ByteValidator;
import org.eclipse.ve.sweet.validators.DateValidator;
import org.eclipse.ve.sweet.validators.DoubleValidator;
import org.eclipse.ve.sweet.validators.FloatValidator;
import org.eclipse.ve.sweet.validators.IntValidator;
import org.eclipse.ve.sweet.validators.LongValidator;
import org.eclipse.ve.sweet.validators.ShortValidator;
import org.eclipse.ve.sweet.validators.reusable.ReadOnlyValidator;
import org.eclipse.ve.sweet.validators.reusable.RegularExpressionValidator;

import com.db4o.browser.model.IDatabase;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.Reflector;

public abstract class FieldController implements IFieldViewer {
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
        associate(c(Integer.TYPE), new IntValidator());
        associate(c(Byte.TYPE), new ByteValidator());
        associate(c(Short.TYPE), new ShortValidator());
        associate(c(Long.TYPE), new LongValidator());
        associate(c(Float.TYPE), new FloatValidator());
        associate(c(Double.TYPE), new DoubleValidator());
        associate(c(Integer.class), new IntValidator());
        associate(c(Byte.class), new ByteValidator());
        associate(c(Short.class), new ShortValidator());
        associate(c(Long.class), new LongValidator());
        associate(c(Float.class), new FloatValidator());
        associate(c(Double.class), new DoubleValidator());
        associate(c(Date.class), new DateValidator());
        
        // Regex-implemented verifiers here...
        associate(c(String.class), new RegularExpressionValidator(
                "/^.*$/", "/^.*$/", "Feel free to type anything"));
        associate(c(Character.TYPE), new RegularExpressionValidator(
                "/^.$|^$/", "/./", "Please type a character"));
        associate(c(Boolean.TYPE), new RegularExpressionValidator(
                "/^$|Y$|^y$|^Ye$|^ye$|^Yes$|^yes$|^T$|^t$|^Tr$|^tr$|^Tru$|^tru$|^True$|^true$|^N$|^n$|^No$|^no$|^F$|^f$|^Fa$|^fa$|^Fal$|^fal$|^Fals$|^fals$|^False$|^false$/", 
                "/Yes$|^yes$|^No$|^no$|^True$|^true$|^False$|^false/", 
                "Please type \"Yes\", \"No\", \"True\", or \"False\""));
        associate(c(Boolean.class), new RegularExpressionValidator(
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
    protected void associate(ReflectClass klass, IValidator verifier) {
        verifiers.put(klass, verifier);
    }
    
    /**
     * Return an IVerifier for a specific class.
     * 
     * @param klass The Class to verify
     * @return An appropriate IVerifier
     */
    protected IValidator get(ReflectClass klass) {
        IValidator result = (IValidator) verifiers.get(klass);
        if (result == null) {
            return ReadOnlyValidator.getDefault();
        }
        return result;
    }
    
}
