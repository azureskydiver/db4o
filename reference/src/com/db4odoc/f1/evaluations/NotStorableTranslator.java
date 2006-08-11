package com.db4odoc.f1.evaluations;

import com.db4o.*;
import com.db4o.config.*;

public class NotStorableTranslator 
    implements ObjectConstructor {
  public Object onStore(ObjectContainer container,
      Object applicationObject) {
    System.out.println("onStore for "+applicationObject);
    NotStorable notStorable=(NotStorable)applicationObject;
    return new Object[]{new Integer(notStorable.getId()),
        notStorable.getName()};
  }

  public Object onInstantiate(ObjectContainer container, 
      Object storedObject) {
    System.out.println("onInstantiate for "+storedObject);
    Object[] raw=(Object[])storedObject;
    int id=((Integer)raw[0]).intValue();
    String name=(String)raw[1];
    return new NotStorable(id,name);
  }

  public void onActivate(ObjectContainer container, 
      Object applicationObject, Object storedObject) {
    System.out.println("onActivate for "+applicationObject
        +" / "+storedObject);
  }

  public Class storedClass() {
    return Object[].class;
  }
}