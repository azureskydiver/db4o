/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor.cmd;

import java.awt.*;
import javax.swing.*;

import java.io.*;

import com.yetac.doctor.writers.*;

public class Graphic extends Command{

    public void resolve()  throws Exception {
        detectParameters();
        String fileName = new String(parameter); 
        String sourcePath = sourcePath();
        if(new File(sourcePath).exists()) {
            String destinationPath = source.files.task.getOutputPath() + "/docs/" + fileName;
            source.files.copyFile(sourcePath, destinationPath);
        }else {
            ignore = true;
            source.files.task.log("Graphic file does not exist: " + sourcePath);
        }
    }

    public void write(DocsWriter writer) throws Exception{
        writer.write(this);
    }
    
    public Image getImage() {
        if(! ignore) {
            return new ImageIcon(sourcePath()).getImage();    
        }
        return null;
    }
    
    public String sourcePath (){
        return source.files.task.inputImages() + "/" + new String(parameter);
    }
}
