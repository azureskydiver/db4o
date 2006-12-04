package com.db4odoc.evaluations;

import com.db4o.*;

public class TranslatorExample  {
	public final static String YAPFILENAME="formula1.yap";
  public static void main(String[] args) {
    tryStoreWithoutCallConstructors();
    tryStoreWithCallConstructors();
    storeWithTranslator();
  }
  // end main

  public static void tryStoreWithoutCallConstructors() {
		Db4o.configure().exceptionsOnNotStorable(false);
	    Db4o.configure().objectClass(NotStorable.class)
	        .callConstructor(false);
	    tryStoreAndRetrieve();
  }
  // end tryStoreWithoutCallConstructors

  public static void tryStoreWithCallConstructors() {
    Db4o.configure().exceptionsOnNotStorable(true);
    Db4o.configure().objectClass(NotStorable.class)
        .callConstructor(true);
    tryStoreAndRetrieve();
  }
  // end tryStoreWithCallConstructors

  public static void storeWithTranslator() {
    Db4o.configure().objectClass(NotStorable.class)
        .translate(new NotStorableTranslator());
    tryStoreAndRetrieve();
  }
  // end storeWithTranslator

  public static void tryStoreAndRetrieve() {
    ObjectContainer db=Db4o.openFile(YAPFILENAME);
    try {
      NotStorable notStorable = new NotStorable(42,"Test");
      System.out.println("ORIGINAL: "+notStorable);
      db.set(notStorable);
    }
    catch(Exception exc) {
      System.out.println(exc.toString());
      return;
    }
    finally {
      db.close();
    }
    db=Db4o.openFile(YAPFILENAME);
    try {
      ObjectSet result=db.get(NotStorable.class);
      while(result.hasNext()) {
        NotStorable notStorable=(NotStorable)result.next();
        System.out.println("RETRIEVED: "+notStorable);
        db.delete(notStorable);
      }
    }
    finally {
      db.close();
    }
  }
  // end tryStoreAndRetrieve
}