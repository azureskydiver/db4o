/*
 * Created on Jan 14, 2005
 */
package com.db4o.browser.gui.views;

import java.util.Map;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import com.db4o.browser.DuckType;
import com.swtworkbench.community.xswt.XSWT;

/**
 * Class DbBrowserPane.  A SWT control that can be used as the basis for a
 * database browser.  This implementation defines the UI in an layout.xswt
 * file.
 * 
 * @author djo
 */
public class DbBrowserPane extends Composite {
	
	/**
     * Construct a DbBrowserPane object.
     * 
     * The standard SWT constructor.  Note that although this class extends
     * Composite, it does not make sense to set a layout manager on it.
     * 
	 * @param parent The SWT parent control
	 * @param style SWT style bits.  Accepts the same style bits as Composite
	 */
	public DbBrowserPane(Composite parent, int style) {
		super(parent, style);
        parent.setLayout(new FillLayout());
        setLayout(new FillLayout());
		Rectangle displayBounds = parent.getDisplay().getBounds();
		if (displayBounds.width > 480 && displayBounds.height > 480)
//	        contents = XSWT.createl(this, "layout.xswt", getClass());
			contents = XSWT.createl(this, "layout-desktop.xswt", getClass());
		else
			contents = XSWT.createl(this, "layout.xswt", getClass());
	}
    
    private Map contents = null;
    
    /**
     * Returns the Path Label.  On desktop platforms this can be null.
     * 
     * @return the Path label from the layout or null if there is none.
     */
    public Label getPathLabel() {
        return (Label) contents.get("PathLabel");
    }
    
    /**
     * Returns the Search text box
     * 
     * @return the search text box
     */
    public Text getSearch() {
        return (Text) contents.get("Search");
    }
    
    /**
     * Returns the button next to the search text box
     * 
     * @return the clear search button
     */
    public Button getSearchButton() {
        return (Button) contents.get("ClearSearchButton");
    }
    
    /**
     * Method getObjectTree.  Returns the object tree.
     * 
     * @return Tree the object tree.
     */
    public Tree getObjectTree() {
        return (Tree) contents.get("ObjectTree");
    }
    
    /**
     * Method GetFieldArea.  Returns the area where the field names and
     * values will be displayed.
     * 
     * @return Composite the field display area
     */
    public Composite getFieldArea() {
        return (Composite) contents.get("FieldArea");
    }
	
	/**
	 * Method GetLeftButton.  Returns the "back" button.
	 * 
	 * @return Button the "back" navigation button
	 */
	public ISelectionSource getLeftButton() {
		return (ISelectionSource) DuckType.implement(ISelectionSource.class, contents.get("LeftButton"));
	}
	
	/**
	 * Method GetRightButton.  Returns the "forward" button.
	 * 
	 * @return Button the "forward" navigation button
	 */
	public ISelectionSource getRightButton() {
		return (ISelectionSource) DuckType.implement(ISelectionSource.class, contents.get("RightButton"));
	}

}


