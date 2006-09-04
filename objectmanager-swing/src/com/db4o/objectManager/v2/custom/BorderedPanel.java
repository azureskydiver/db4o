package com.db4o.objectManager.v2.custom;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.Component;
import java.awt.Color;
import java.awt.BorderLayout;

/**
 * This doesn't extend JPanel so that subclass can support builder pattern (jgoodies)
 *
 * User: treeder
 * Date: Sep 3, 2006
 * Time: 4:25:58 PM
 */
public class BorderedPanel {

    private String title;
    private JPanel outer;

    public BorderedPanel(String title) {
        this.title = title;
        JPanel outer = new JPanel(new BorderLayout());
        Border b = new LineBorder(Color.GRAY, 1, true);
        TitledBorder b2 = new TitledBorder(b, title);
        this.outer = outer;
        this.outer.setBorder(b2);
    }


    public void add(JComponent p) {
        outer.add(p);
    }

    public JPanel getPanel(){
        return outer;
    }

}
