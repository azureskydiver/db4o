/*
 * Created on Jan 14, 2005
 */
package com.db4o.browser.gui;

import java.util.Map;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

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
	 * @param parent
	 * @param style
	 */
	public DbBrowserPane(Composite parent, int style) {
		super(parent, style);
        parent.setLayout(new FillLayout());
        setLayout(new FillLayout());
        contents = XSWT.createl(this, "layout.xswt", getClass());
	}
    
    private Map contents = null;
    
    /**
     * Returns the Path Label
     * 
     * @return the Path label from the layout
     */
    public Label getPath() {
        return (Label) contents.get("Path");
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
     * Returns the "Go" button next to the search text box
     * 
     * @return the search button
     */
    public Button getSearchButton() {
        return (Button) contents.get("SearchButton");
    }
    
    /**
     * Returns the hyperlink/tree area composite
     * 
     * @return the hyperlink/tree area composite
     */
    public Composite getHyperlinkArea() {
        return (Composite) contents.get("HyperlinkArea");
    }
    
    /**
     * Returns the List view composite
     * 
     * @return the list view composite
     */
    public Composite getListComposite() {
        return (Composite) contents.get("ListComposite");
    }
    
    /**
     * Returns the detail view composite
     * 
     * @return the detail view composite
     */
    public Composite getDetailComposite() {
        return (Composite) contents.get("DetailComposite");
    }
    
}
