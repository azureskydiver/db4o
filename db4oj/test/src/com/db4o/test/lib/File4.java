/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.lib;

import java.io.*;
import java.util.zip.*;

/**
 * sorry, very very old code, recently included for crash simulator,
 * needs fix and look over
 */
public class File4 extends java.io.File {
    public File4(String path) {
        super(path);
    }

    public File4(String path, String file) {
        this(assemble(path, file));
    }

    public static String assemble(String pathName, String fileName) {
        return String4._right(pathName, separator)
            ? pathName + fileName
            : pathName + separator + fileName;
    }

    public File4 copy(String toPath, String filter) {
        try {
            new File4(toPath).mkdirs();
            new File4(toPath).delete();

            if (isDirectory()) {
                String[] files = list();
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        if (filter == null || String4._right(files[i], filter)) {
                            File4 child = new File4(getAbsolutePath(), files[i]);
                            child.copy(assemble(toPath, files[i]), filter);
                        }
                    }
                }
            } else {

                final int bufferSize = 64000;

                RandomAccessFile rafIn = new RandomAccessFile(getAbsolutePath(), "r");
                RandomAccessFile rafOut = new RandomAccessFile(toPath, "rw");
                long len = rafIn.length();
                byte[] bytes = new byte[bufferSize];

                while (len > 0) {
                    len -= bufferSize;
                    if (len < 0) {
                        bytes = new byte[(int) (len + bufferSize)];
                    }
                    rafIn.read(bytes);
                    rafOut.write(bytes);
                }

                rafIn.close();
                rafOut.close();
            }
            return new File4(toPath);
        } catch (Exception e) {
            System.out.println("File.copy failed.");
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public File4 copy(String toPath) {
        return copy(toPath, null);
    }

    public boolean delete() {
        return delete(null);
    }

    public boolean delete(String filter) {
        String[] files = list();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File4 child = new File4(getAbsolutePath(), files[i]);
                if (child.isDirectory() || filter == null || String4._right(files[i], filter)) {
                    child.delete(filter);
                }
                new File4(getAbsolutePath(), files[i]).delete(filter);
            }
        }
        if (filter == null || String4._right(getName(), filter)) {
        	return super.delete();
        }else{
        	return false;
        }
    }
    
    public String directory(){
    	String absolute = getAbsolutePath(); 
    	if(isDirectory()){
    		return absolute;
    	}
    	String4 s4 = new String4(absolute);
    	s4.splitRight(separator);
    	return s4.toString();
    }

    public String list(String filter) {
        StringBuffer sb = new StringBuffer();
        list(sb, filter);
        return sb.toString();
    }

    private void list(StringBuffer sb, String filter) {
        if (isDirectory()) {
            String[] files = list();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    File4 child = new File4(getAbsolutePath(), files[i]);
                    if (child.isDirectory()
                        || filter == null
                        || String4._right(files[i], filter)) {
                        child.list(sb, filter);
                    }
                }
            }
        } else {
            sb.append(getAbsolutePath());
            sb.append("\r\n");
        }
    }

    public void replace(String a_replace, String a_with) {
        String path = getAbsolutePath();
        String4 str = null;
        RandomAccessFile raf = null;
        int len = 0;
        byte l_bytes[] = null;
        try {
            if (!exists()) {
                throw new Exception();
            }
            raf = new RandomAccessFile(path, "rw");
        } catch (Exception e) {
            System.out.println("Failed to open file: " + path);
            return;
        }

        try {
            len = (int) raf.length();
            l_bytes = new byte[len];
            raf.read(l_bytes, 0, len);
            raf.close();
        } catch (Exception e) {
            System.out.println("File read denied:" + path);
            return;
        }

        str = new String4(new String(l_bytes));
        str.replace(a_replace, a_with);
        l_bytes = str.getString().getBytes();

        try {
            new File4(path).delete();
        } catch (Exception e) {
            System.out.println("File access denied: " + path);
            return;
        }

        try {
            raf = new RandomAccessFile(path, "rw");
            raf.write(l_bytes);
            raf.close();
        } catch (Exception e) {
            System.out.println("File access denied: " + path);
            return;
        }
    }
    
    public void unzip() throws IOException, FileNotFoundException{
    	
    	int BLOCK_LENGTH = 8000;
    	int read;
    	byte[] bytes = new byte[BLOCK_LENGTH];
    	
    	ZipInputStream zis = new ZipInputStream(new FileInputStream(this));
    	ZipEntry ze = zis.getNextEntry();
    	while(ze != null){
    		File file = new File(this.getParent(), ze.getName());
            parentFile(file).mkdirs();
    		FileOutputStream fos = new FileOutputStream(file);
    		while((read = zis.read(bytes,0,BLOCK_LENGTH)) > 0){
    			fos.write(bytes,0, read);
    		}
    		fos.flush();
    		fos.close();
    		ze = zis.getNextEntry();
    	}
    	zis.close();
    }
    
    private File parentFile(File forFile) {
        String path = forFile.getParent();
        if (path == null) return null;
        return new File(path);
     }

}
