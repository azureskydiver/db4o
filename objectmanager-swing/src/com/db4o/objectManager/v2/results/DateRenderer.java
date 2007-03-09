package com.db4o.objectManager.v2.results;

import com.db4o.objectManager.v2.util.DateFormatter;
import com.db4o.objectManager.v2.MainPanel;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import java.util.Date;

/**
 * User: treeder
 * Date: Oct 16, 2006
 * Time: 10:13:46 PM
 */
public class DateRenderer extends DefaultTableCellRenderer implements TableCellRenderer {

	public DateRenderer() {
		super();
	}

	public void setValue(Object value) {
		setText((value == null) ? "" : MainPanel.dateFormatter.display((Date) value));
	}

}
