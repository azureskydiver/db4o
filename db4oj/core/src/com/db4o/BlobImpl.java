/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.types.*;

/**
 * Transfer of blobs to and from the db4o system,
 * if users use the Blob Db4oType.
 * 
 * @moveto com.db4o.inside.blobs
 * @exclude
 */
public class BlobImpl implements Blob, Cloneable, Db4oTypeImpl {

    public final static int COPYBUFFER_LENGTH=4096;
    
    public String fileName;
    public String i_ext;
    private transient File i_file;
    private transient BlobStatus i_getStatusFrom;
    public int i_length;
    private transient double i_status = Status.UNUSED;
    private transient YapStream i_stream;
    private transient Transaction i_trans;

    public int adjustReadDepth(int a_depth) {
        return 1;
    }
    
    public boolean canBind() {
        return true;
    }

    private String checkExt(File file) {
        String name = file.getName();
        int pos = name.lastIndexOf(".");
        if (pos > 0) {
            i_ext = name.substring(pos);
            return name.substring(0, pos);
        }
        
        i_ext = "";
        return name;
        
    }

    private void copy(File from, File to) throws IOException {
        to.delete();
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(from));
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(to));
        byte[] buffer=new byte[COPYBUFFER_LENGTH];
        int bytesread=-1;
        while ((bytesread=in.read(buffer))>=0) {
            out.write(buffer,0,bytesread);
        }
        out.flush();
        out.close();
        in.close();
    }

    public Object createDefault(Transaction a_trans) {
        BlobImpl bi = null;
        try {
            bi = (BlobImpl) this.clone();
            bi.setTrans(a_trans);
        } catch (CloneNotSupportedException e) {
            return null;
        }
        return bi;
    }

    public FileInputStream getClientInputStream() throws Exception {
        return new FileInputStream(i_file);
    }

    public FileOutputStream getClientOutputStream() throws Exception {
        return new FileOutputStream(i_file);
    }

    public String getFileName() {
        return fileName;
    }

    public int getLength() {
        return i_length;
    }

    public double getStatus() {
        if (i_status == Status.PROCESSING && i_getStatusFrom != null) {
            return i_getStatusFrom.getStatus();
        }
        if (i_status == Status.UNUSED) {
            if (i_length > 0) {
                i_status = Status.AVAILABLE;
            }
        }
        return i_status;
    }

    public void getStatusFrom(BlobStatus from) {
        i_getStatusFrom = from;
    }

    public boolean hasClassIndex() {
        return false;
    }

    public void readFrom(File file) throws IOException {
        if (!file.exists()) {
            throw new IOException(Messages.get(41, file.getAbsolutePath()));
        }
        i_length = (int) file.length();
        checkExt(file);
        if (i_stream.isClient()) {
            i_file = file;
            ((BlobTransport)i_stream).readBlobFrom(i_trans, this, file);
        } else {
            readLocal(file);
        }
    }

    public void readLocal(File file) throws IOException {
        boolean copied = false;
        if (fileName == null) {
            File newFile = new File(serverPath(), file.getName());
            if (!newFile.exists()) {
                copy(file, newFile);
                copied = true;
                fileName = newFile.getName();
            }
        }
        if (!copied) {
            copy(file, serverFile(checkExt(file), true));
        }
        synchronized (i_stream.i_lock) {
            i_stream.setInternal(i_trans, this, false);
        }
        i_status = Status.COMPLETED;
    }
    
    public void preDeactivate(){
        // do nothing
    }

    public File serverFile(String promptName, boolean writeToServer) throws IOException {
        synchronized (i_stream.i_lock) {
            i_stream.activate1(i_trans, this, 2);
        }
        String path = serverPath();
        i_stream.configImpl().ensureDirExists(path);
        if (writeToServer) {
            if (fileName == null) {
                if (promptName != null) {
                    fileName = promptName;
                } else {
                    fileName = "b_" + System.currentTimeMillis();
                }
                String tryPath = fileName + i_ext;
                int i = 0;
                while (new File(path, tryPath).exists()) {
                    tryPath = fileName + "_" + i++ +i_ext;
                    if (i == 99) {
                        // should never happen
                        i_status = Status.ERROR;
                        throw new IOException(Messages.get(40));
                    }
                }
                fileName = tryPath;
                synchronized (i_stream.i_lock) {
                    i_stream.setInternal(i_trans, this, false);
                }
            }
        } else {
            if (fileName == null) {
                throw new IOException(Messages.get(38));
            }
        }
        String lastTryPath = path + File.separator + fileName;
        if (!writeToServer) {
            if (!(new File(lastTryPath).exists())) {
                throw new IOException(Messages.get(39));
            }
        }
        return new File(lastTryPath);
    }

    private String serverPath() throws IOException {
        String path = i_stream.configImpl().blobPath();
        if (path == null) {
            path = "blobs";
        }
        i_stream.configImpl().ensureDirExists(path);
        return path;
    }

    public void setStatus(double status) {
        i_status = status;
    }

    public void setTrans(Transaction a_trans) {
        i_trans = a_trans;
        i_stream = a_trans.stream();
    }

    public void writeLocal(File file) throws IOException {
        copy(serverFile(null, false), file);
        i_status = Status.COMPLETED;
    }

    public void writeTo(File file) throws IOException {
        if (getStatus() == Status.UNUSED) {
            throw new IOException(Messages.get(43));
        }
        if (i_stream.isClient()) {
            i_file = file;
            i_status = Status.QUEUED;
            ((BlobTransport)i_stream).writeBlobTo(i_trans, this, file);
        } else {
            writeLocal(file);
        }
    }
    
    public void replicateFrom(Object obj) {
        // do nothing
    }
    
    public Object storedTo(Transaction a_trans){
        return this;
    }
    
    public void setYapObject(YapObject a_yapObject) {
        // not necessary
    }

 
}