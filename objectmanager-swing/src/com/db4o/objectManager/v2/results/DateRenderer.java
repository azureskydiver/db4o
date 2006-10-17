package com.db4o.objectManager.v2.results;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import java.text.DateFormat;

/**
 * User: treeder
 * Date: Oct 16, 2006
 * Time: 10:13:46 PM
 */
public class DateRenderer extends DefaultTableCellRenderer implements TableCellRenderer {
	static DateFormat formatter;

	public DateRenderer() {
		super();
	}

	public void setValue(Object value) {
		if (formatter == null) {
			formatter = DateFormat.getDateTimeInstance();
		}
		setText((value == null) ? "" : formatter.format(value));
	}

}
