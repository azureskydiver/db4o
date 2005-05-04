package com.db4o.f1.chapter6;

import com.db4o.*;
import com.db4o.f1.*;

public class TranslatorExample extends Util {
  public static void main(String[] args) {
    tryStoreWithCallConstructors();
    tryStoreWithoutCallConstructors();
    storeWithTranslator();
  }

  public static void tryStoreWithCallConstructors() {
    Db4o.configure().objectClass(NotStorable.class)
        .callConstructor(true);
    tryStoreAndRetrieve();
  }

  public static void tryStoreWithoutCallConstructors() {
    Db4o.configure().objectClass(NotStorable.class)
        .callConstructor(false);
    tryStoreAndRetrieve();
  }

  public static void storeWithTranslator() {
    Db4o.configure().objectClass(NotStorable.class)
        .translate(new NotStorableTranslator());
    tryStoreAndRetrieve();
  }

  public static void tryStoreAndRetrieve() {
    Db4o.configure().exceptionsOnNotStorable(true);
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
}