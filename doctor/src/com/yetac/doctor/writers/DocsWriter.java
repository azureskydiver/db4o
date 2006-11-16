/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor.writers;

import com.yetac.doctor.cmd.*;
import com.yetac.doctor.workers.*;

public interface DocsWriter {
    
    public Files files();
    
    public void beginEmbedded(DocsFile source) throws Exception;
    
    public void endEmbedded() throws Exception;
    
    public void start(Files files) throws Exception;

    public void setSource(DocsFile source) throws Exception;

    public void write(Command command);

    public void write(Anchor command) throws Exception;
    
    public void write(Bold command) throws Exception;

    public void write(Center command) throws Exception;

    public void write(Comment command) throws Exception;

    public void write(Code command) throws Exception;    
    
    public void write(Embed command) throws Exception;

    public void write(Graphic command) throws Exception;
    
    public void write(IgnoreCR command) throws Exception;

    public void write(Italic command) throws Exception;

    public void write(OutputConsole command) throws Exception;
    
    public void write(Left command) throws Exception;

    public void write(Link command) throws Exception;
    
    public void write(NewPage command) throws Exception;

    public void write(Outline command) throws Exception;

    public void write(Right command) throws Exception;

    public void write(Source command) throws Exception;

    public void write(Text command) throws Exception;

    public void write(Variable command) throws Exception;
    
    public void write(Xamine command) throws Exception;

    public void end();

}