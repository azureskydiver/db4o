/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.standalone;

import java.lang.reflect.Method;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;

import com.swtworkbench.community.xswt.metalogger.Logger;

public class ImageMgr implements DisposeListener {

    private Image image;

    public ImageMgr(Image image) {
        this.image = image;
    }
    
    public ImageMgr(Control receiver, Image image) {
        this.image = image;
        try {
            Method setImage = receiver.getClass().getDeclaredMethod("setImage", new Class[] {Image.class});
            setImage.invoke(receiver, new Object[] {image});
        } catch (Exception e) {
            Logger.log().error(e, "Unable to setImage()");
            throw new RuntimeException(e);
        } 
        receiver.addDisposeListener(this);
    }
    
    public void widgetDisposed(DisposeEvent e) {
        if (!image.isDisposed())
            image.dispose();
    }
    
    public Image get() {
        return image;
    }

}
