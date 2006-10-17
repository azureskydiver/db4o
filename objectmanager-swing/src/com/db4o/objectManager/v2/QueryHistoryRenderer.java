package com.db4o.objectManager.v2;

import javax.swing.*;
import java.awt.Component;

/**
 * User: treeder
 * Date: Oct 13, 2006
 * Time: 11:31:58 AM
 */
public class QueryHistoryRenderer extends JLabel implements ListCellRenderer {

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		this.setIcon(ResourceManager.createImageIcon("icons/16x16/history.png"));
		this.setText(value.toString());
		return this;
	}
}
