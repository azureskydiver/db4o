
package com.db4o.devtools.ant;

import java.util.regex.Pattern;

import org.apache.tools.ant.*;

public class FileHeadAntTask extends Task {
    
    String path;
    String header;
    Pattern before;
    String fileExt;
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public void setHeader(String header) {
        this.header = header;
    }
    
    public void setBeforePattern(String before) {
        this.before = Pattern.compile(before);
    }
    
    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }
    
    public void execute() throws BuildException {
		log("Looking for *." + fileExt + " files in " + path);
		
        FileHead fh = new FileHead(path, this);
        try {
            fh.run();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BuildException(e.getMessage());
        }
		
		log("done.");
    }
}
