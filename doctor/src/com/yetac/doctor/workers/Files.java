/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor.workers;

import java.io.*;
import java.util.*;

import com.yetac.doctor.*;

public class Files extends Configuration {

    public Map   filesByName;
    private Set sourceFiles;
    public Map   anchors;
    public Doctor task;

    public void findFiles(Doctor task) throws IOException {
        this.task = task;
        filesByName = new Hashtable();
        sourceFiles = new TreeSet();
        findFiles(task, task.inputDocs());
    }

    private void findFiles(Doctor task, String path) throws IOException {
        File file = new File(path);
        if (file.exists()) {
            String[] files = file.list();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    File subFile = new File(path + "/" + files[i]);
                    if (!subFile.isDirectory()) {
                        if (subFile.getName().endsWith(FILE_EXTENSION)) {
                            DocsFile source = new DocsFile(this, subFile);
                            sourceFiles.add(source);
                            filesByName.put(source.name, source);
                        }
                    } else {
                        if(! task.isInputFolderIgnored(subFile.getName())){
                            findFiles(task, subFile.getAbsolutePath());
                        }
                    }
                }
            }
        } else {
            throw new IOException("Path does not exist: " + path);
        }
    }

    public void parse() throws IOException, CloneNotSupportedException {
        if (sourceFiles != null) {
            Parser parser = new Parser();
            Iterator i = sourceFiles.iterator();
            while (i.hasNext()) {
                parser.parse((DocsFile) i.next());
            }
        }
    }

    public void resolve() throws Exception {
        anchors = new Hashtable();
        Iterator i = sourceFiles.iterator();
        while (i.hasNext()) {
            DocsFile source = (DocsFile) i.next();
            source.resolve();
        }
    }

    public void write() throws Exception {
        for (int i = 0; i < WRITERS.length; i++) {
            WRITERS[i].start(this);
            Iterator it = sourceFiles.iterator();
            while (it.hasNext()) {
                DocsFile source = (DocsFile) it.next();
                if(source.write != -1){
                    source.write = 1;
                }
            }
            it = sourceFiles.iterator();
            while (it.hasNext()) {
                DocsFile source = (DocsFile) it.next();
                if (source.write == 1) {
                    source.write = 0;
                    source.write(WRITERS[i]);
                }
            }
            WRITERS[i].end();
        }
    }

    public void copyFile(String fromPath, String toPath)
        throws IOException {

        new File(toPath).mkdirs();
        new File(toPath).delete();

        int bufferSize = 64000;

        RandomAccessFile rafIn = new RandomAccessFile(fromPath, "r");
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
    
    public void copyAllFiles(String srcdirpath,String targetdirpath) throws IOException {
        File srcdir=new File(srcdirpath);
        File targetdir=new File(targetdirpath);
        System.out.println("src: "+srcdir.getAbsolutePath());
        System.out.println("target: "+targetdir.getAbsolutePath());
        targetdir.mkdirs();
        File[] files=srcdir.listFiles();
        if (files == null) return;
        for (int idx=0;idx<files.length;idx++) {
            if(files[idx].isFile()) {
                copyFile(files[idx].getAbsolutePath(),new File(targetdir,files[idx].getName()).getAbsolutePath());
            }
        }
    }

    public String readFileStr(String path) throws IOException {
    	return readFileStr(new File(path));
    }

    public String readFileStr(File file) throws IOException {
		BufferedReader in=new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
		StringBuffer buf=new StringBuffer();
		String curLine=null;
		while((curLine=in.readLine())!=null) {
			buf.append(curLine);
			buf.append('\n');
		}
		in.close();
	    return buf.toString();
    }
}