package com.yetac.doctor.applet;

import java.applet.Applet;
import java.awt.Button;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Vector;


public class DoctorConsoleApplet extends Applet {
    
    public final static String APPLETNAME="doctorconsole";
    
    public static final String RUNNER_CLASS_PATH="RCP";
    
    private static final int BUTTON_WIDTH = 90;
    private static final int BUTTON_HEIGHT = 25;
    private static final int BUTTON_SPACE = 5;
    private static final int SCROLLBAR_WIDTH = 20;
    
    private ExampleRunner runner;
    private TextArea text;
    private Button resetbutton;
    private Button clearbutton;
    
    private ClassLoader classLoader;
    
    public void init() {
        String yapfilename=getParameter("yapfile");
        File userdir=new File(System.getProperty("user.home","."));
        try {
            runner=new ExampleRunner(getClass().getClassLoader(),new File(userdir,yapfilename));
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        
        text=new TextArea();
        text.setFont(UIConfig.CONSOLE_FONT);
        text.setForeground(UIConfig.DB4O_GREEN);
        text.setBackground(UIConfig.DB4O_GREY);
        
        resetbutton=new Button("Reset Database");
        resetbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                text.append("[FULL RESET]\n\n");
                runner.reset();
            }
        });
        
        resetbutton.setFont(UIConfig.BUTTON_FONT);
        resetbutton.setBackground(UIConfig.DB4O_GREEN);
        resetbutton.setForeground(UIConfig.DB4O_GREY);
        
        clearbutton=new Button("Clear");
        clearbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                text.setText("");
            }
        });
        clearbutton.setFont(UIConfig.BUTTON_FONT);
        clearbutton.setBackground(UIConfig.DB4O_GREEN);
        clearbutton.setForeground(UIConfig.DB4O_GREY);
        
        add(resetbutton);
        add(clearbutton);
        add(text);
        
        classLoader = this.getClass().getClassLoader();
        
        // loadSpecialClassLoader();
    }
    
    public void doLayout() {
        if(text != null){
            text.setBounds(0,0, this.getWidth(), this.getHeight());
        }
        int x = this.getWidth() - BUTTON_WIDTH - BUTTON_SPACE - SCROLLBAR_WIDTH;
        if(clearbutton != null){
            clearbutton.setBounds(x,BUTTON_SPACE,BUTTON_WIDTH ,BUTTON_HEIGHT);
        }
        x -= BUTTON_WIDTH;
        x -= BUTTON_SPACE;
        if(resetbutton != null){
            resetbutton.setBounds(x,BUTTON_SPACE,BUTTON_WIDTH , BUTTON_HEIGHT);
        }
    }
    
    
    /**
     * Experiment code, to allow to reach to db4o.jar and
     * F1 code, even if they are in a hierarchy above the
     * applet.
     * Performance was dreadful on Carl's machine, so this
     * setup is cancelled for the moment.
     */
    private void loadSpecialClassLoader(){
        Vector paths = new Vector();
        String urlString = null;
        int num = 0;
        urlString = getParameter(RUNNER_CLASS_PATH + num);
        while (urlString  !=  null) {
            urlString = "file://" + urlString;
            try{
                new URL(urlString);
                paths.add(urlString);
                text.append(urlString + "\n");
            }catch(MalformedURLException ex){
                text.append("Invalid: " + urlString );
            }
            num ++;
            urlString = getParameter(RUNNER_CLASS_PATH + num);
        }
        
        Object[] urlArray = paths.toArray();
        URL[] urls = new URL[urlArray.length];
        try{
	        for (int i = 0; i < urls.length; i++) {
	            urls[i] = new URL(urlArray[i].toString());
	            text.append(urls[i].toString());
	        }
	        URLClassLoader appletClassLoader = (URLClassLoader)getClass().getClassLoader();
	        classLoader = URLClassLoader.newInstance(urls, appletClassLoader);
	        text.append(classLoader.toString());
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    
    public void runExample(String classname,String methodname) {
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        try {
            runner.runExample(classname,methodname,out);
            out.close();
            text.append("["+methodname+"]\n");
            text.append(out.toString()+"\n");
            text.setCaretPosition(text.getText().length());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
