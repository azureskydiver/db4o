package com.db4o.browser.gui.controllers;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;

/**
 * This snippet has been ripped off the german java newsgroup as is.
 * (The containing posting <a8proe.t0.1@aljoscha-rittner.de> included
 * a public license statement.)
 * This is just a temporary hack. It should be removed asap in favor of
 * a) a generic reflector approach or b) custom classloader handling. 
 */

/**
 * Bietet die Möglichkeit, den System-ClassLoader dynamisch um weitere 
 * Pfade zu erweitern.
 * Der ClassLoaderExtender ist typischerweise nur was für Desktop- oder Server-
 * Anwendungen. Im Applet-Bereich verbietet sich der Einsatz, da der 
 * SecurityManager verhindert, dass fremde URLs dem ClassLoader hinzugefügt 
 * werden können.
 *
 * @author Aljoscha Rittner
 */
public class ClassLoaderExtender {
  /**
   * Sammelt aus einem Ordner alle Dateien mit der Endung jar und zip.
   *
   * @param extDir Ordner, in dem gesammelt werden soll.
   * @return Array der URLs zu den Archiven oder null.
   * @throws MalformedURLException Sollten die Archive nicht als URL zu 
   *         repräsentieren sein, gibt es eine MalformedURLException.
   */
  public static URL[] getArchives (File extDir) throws MalformedURLException {
    File[] jars = extDir.listFiles (new java.io.FilenameFilter () {
      public boolean accept (File dir, String name) {       
        String extension = name.substring (name.length()-4).toLowerCase();
        return extension.equals (".jar") || extension.equals (".zip");  
      }
    });
    int count = jars.length;
    if ( count > 0 ) {
      URL[] urls = new URL[count];
      for ( int i = 0; i < count; i++ ) {        
        urls[i] = jars[i].toURL();
      }      
      return urls;
    } return null;
  }
  
  /**
   * <p>
   * Fügt das Array der URLs zu dem ClassPath des System-ClassLoaders hinzu.
   * Es werden nur URLs hinzugefügt, die noch nicht im ClassPath existieren.
   * </p>
   * <p>
   * Hinzufügen eines Ordners mit allen jar-Bibliotheken (hier der Relative 
   * Ext-Ordner):<br>
   * <code> addToClassPath (getArchives (new File ("ext/"));</code><br>
   * </p>
   * <p>
   * Hinzufügen eines Ordners mit class-Dateien:<br>
   * <code> addToClassPath (new URL[] {new File ("classes/").toURL()});</code><br>
   * </p>
   * @param  urls               Array der URLs, die dem ClassPath hinzugefügt 
   *                            werden sollen.
   * @throws RuntimeException   Wenn der System-ClassLoader kein URLClassLoader 
   *                            ist, dann wird eine Runtime-Exception geworfen.
   * @throws SecurityException  Wenn der SecurityManager den Aufruf von 
   *                            protected Methoden verhindert, gibt es eine 
   *                            SecurityException.
   */
  public static void addToClassPath (URL[] urls) {
    boolean extended = false;
    ClassLoader loader = ClassLoaderExtender.class.getClassLoader ();
    Class loaderClazz = loader.getClass();
    Class urlLoaderClazz = getSuperClass (loaderClazz, "URLClassLoader"); 
    if ( urlLoaderClazz != null ) {
      try {
        java.lang.reflect.Method addURL = 
          urlLoaderClazz.getDeclaredMethod ("addURL", new Class[] {URL.class});
        addURL.setAccessible (true);
        
        java.lang.reflect.Method getURLs = 
          urlLoaderClazz.getDeclaredMethod ("getURLs", null);
        URL[] cp = (URL[]) getURLs.invoke (loader, null);
        
        urlLoop: 
        for ( int i = 0; i < urls.length; i++ ) {
          for ( int j = 0; j < cp.length; j++ ) {
            if ( urls[i].sameFile (cp[j]) ) continue urlLoop;
          }
          addURL.invoke (loader, new Object[] {urls[i]});
        }
        
        extended = true;
      } catch (NoSuchMethodException nsme) {
        nsme.printStackTrace ();
      } catch (IllegalAccessException iae) {
        iae.printStackTrace ();
      } catch (java.lang.reflect.InvocationTargetException ite) {
        ite.printStackTrace ();
      }
    }
    if ( !extended ) {
      throw new RuntimeException ("Please extend your classpath with: " + 
                                  buildCPDump (urls));
    }
  }
  
  private static Class getSuperClass (Class clazz, String declaredClazz) {
    do {
      if ( clazz.getName ().endsWith (declaredClazz) ) return clazz;      
    } while ( (clazz = clazz.getSuperclass ()) != null ); return null;
  }
  
  private static String buildCPDump (URL[] urls) {
    int count = urls.length;
    if ( count > 0 ) {
      StringBuffer b = new StringBuffer();
      for ( int i = 0; i < (count - 1); i++ ) {
        b.append (urls[i].getPath ()).append (';');
      }
      b.append (urls[count-1].getPath ());
      return b.toString ();
    } return null;
  }
}