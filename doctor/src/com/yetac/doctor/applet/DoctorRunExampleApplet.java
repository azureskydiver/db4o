package com.yetac.doctor.applet;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class DoctorRunExampleApplet extends Applet {
    private String runclassname;
    private String runmethodname;

    public void init() {
        runclassname=getParameter("exampleclass");
        runmethodname=getParameter("examplemethod");
        
        setBackground(new Color(0xef,0xef,0xef));
        
        Button runbutton=new Button("Run");
        runbutton.setBackground(UIConfig.DB4O_GREEN);
        runbutton.setForeground(UIConfig.DB4O_GREY);
        runbutton.setFont(UIConfig.BUTTON_FONT);
        runbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                DoctorConsoleApplet console=(DoctorConsoleApplet)getAppletContext().getApplet(DoctorConsoleApplet.APPLETNAME);
                console.runExample(runclassname,runmethodname);
            }            
        });
        setLayout(new FlowLayout());
        add(runbutton);
    } 
}
