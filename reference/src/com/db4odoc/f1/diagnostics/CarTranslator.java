package com.db4odoc.f1.diagnostics;

import com.db4o.*;
import com.db4o.config.*;

public class CarTranslator 
    implements ObjectConstructor {
  public Object onStore(ObjectContainer container,
      Object applicationObject) {
    Car car =(Car)applicationObject;

    String fullModel;
    if (hasYear(car.getModel())){
    	fullModel = car.getModel();
    } else {
    	fullModel = car.getModel() + getYear(car.getModel());
    }
    return new Object[]{fullModel};
  }

  private String getYear(String carModel){
	  if (carModel.equals("BMW")){
		  return " 2002";
	  } else {
		  return " 1999";
	  }
	  
  }
  
  private boolean hasYear(String carModel){
	  return false;
  }
  
  public Object onInstantiate(ObjectContainer container, Object storedObject) {
    Object[] raw=(Object[])storedObject;
    String model=(String)raw[0];
    return new Car(model);
  }

  public void onActivate(ObjectContainer container, 
      Object applicationObject, Object storedObject) {
  }

  public Class storedClass() {
    return Object[].class;
  }
}