
package com.db4o.devtools.ant;

import org.apache.tools.ant.*;

public class FileHeadAntTask extends Task {
    
    String path;
    byte[] header;
    byte[] before;
    String fileExt;
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public void setHeader(String header) {
        this.header = header.getBytes();
    }
    
    public void setBefore(String before) {
        this.before = before.getBytes();
    }
    
    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }
    
    public void execute() throws BuildException {
        FileHead fh = new FileHead(path, this);
        try {
            fh.run();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BuildException(e.getMessage());
        }
    }
}
