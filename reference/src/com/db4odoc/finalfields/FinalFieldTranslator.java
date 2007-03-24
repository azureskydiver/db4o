package com.db4odoc.finalfields;
import com.db4o.*;
import com.db4o.config.*;

// Translator allowing to store final fields on any Java version
public class FinalFieldTranslator    implements ObjectConstructor 
{
	
  public Object onStore(ObjectContainer container, Object applicationObject) {
    System.out.println("onStore for "+applicationObject);
    TestFinal notStorable=(TestFinal)applicationObject;
    // final fields values are stored to an array of objects
    return new Object[]{new Integer(notStorable._final_i), notStorable._final_s};
  }

  public Object onInstantiate(ObjectContainer container, Object storedObject){
    System.out.println("onInstantiate for "+storedObject);
    Object[] raw=(Object[])storedObject;
    // final fields values are restored from the array of objects
    int i=((Integer)raw[0]).intValue();
    String s = (String)raw[1];
    return new TestFinal(i,s);
  }

  public void onActivate(ObjectContainer container, Object applicationObject, Object storedObject) {
    System.out.println("onActivate for "+applicationObject
        +" / "+storedObject);
  }

  public Class storedClass() {
    return Object[].class;
  }
}