package com.db4o.browser.gui.standalone;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public abstract class Snippet {
    /**
     * Construct and run an Snippet.
     * <p>
     * This constructor does everything needed to actually construct and run
     * a SWT Snippet application.
     */
    public Snippet() {
        display = new Display();
        
        shell = new Shell(display);
        
        constructUI(shell);

        // Set the Shell's default size
        Point shellSize = getShellSize();
        Rectangle displaySize = display.getBounds();
        if (displaySize.width <= shellSize.x || displaySize.height <= shellSize.y)
            shell.setMaximized(true);
        else
            shell.setSize(shellSize);

        shell.open();
        
        runEventLoop();
        
        display.dispose();
        
        shutdown();
    }
    
    /**
     * Return or compute the default shell size.
     * 
     * @return Point the Shell's default size.
     */
    protected Point getShellSize() {
        return new Point(1024, 768);
    }
    
    protected Display display;
    
    /**
     * @return Returns the display.
     */
    public Display getDisplay() {
        return display;
    }
    
    /**
     * Runs the SWT event loop.
     */
    protected void runEventLoop() {
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
    }

    protected Shell shell;
    
    /**
     * @return the shell
     */
    protected Shell getShell() {
        return shell;
    }

    /**
     * Override/implement to construct your UI.
     * 
     * @param parent The SWT parent shell
     */
    protected abstract void constructUI(Shell parent);
     
    /**
     * This method is called after application shutdown.  Use it to print
     * out results, debug info, etc.
     */
    protected void shutdown() {
    }

}
