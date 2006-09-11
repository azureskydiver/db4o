package com.db4o.objectManager.v2.uiHelper;

import com.db4o.objectManager.v2.ResourceManager;
import com.db4o.objectManager.v2.MainFrame;

import javax.swing.*;
import java.awt.Component;

/**
 * User: treeder
 * Date: Sep 11, 2006
 * Time: 4:15:49 PM
 */
public class OptionPaneHelper {
    public static void showErrorMessage(Component frame, String msg, String title){
        JOptionPane.showMessageDialog(frame, msg, title, JOptionPane.ERROR_MESSAGE, ResourceManager.createImageIcon("icons/32x32/warning.png"));
    }

    public static void showSuccessDialog(MainFrame frame, String msg, String title) {
        JOptionPane.showMessageDialog(frame, msg, title, JOptionPane.INFORMATION_MESSAGE, ResourceManager.createImageIcon("icons/32x32/warning.png"));
    }
}
