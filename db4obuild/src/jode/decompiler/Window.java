/* Window - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.decompiler;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public class Window implements Runnable, ActionListener
{
    TextField classpathField;
    TextField classField;
    TextArea sourcecodeArea;
    TextArea errorArea;
    Checkbox verboseCheck;
    Checkbox prettyCheck;
    Button startButton;
    Button saveButton;
    String lastClassName;
    String lastClassPath;
    Frame frame;
    PrintWriter errStream;
    Decompiler decompiler = new Decompiler();
    Thread decompileThread;
    
    public class AreaWriter extends Writer
    {
	boolean initialized = false;
	private TextArea area;
	
	public AreaWriter(TextArea textarea) {
	    area = textarea;
	}
	
	public void write(char[] cs, int i, int i_0_) throws IOException {
	    if (!initialized) {
		area.setText("");
		initialized = true;
	    }
	    area.append(new String(cs, i, i_0_));
	}
	
	public void flush() {
	    /* empty */
	}
	
	public void close() {
	    /* empty */
	}
    }
    
    public Window(Container container) {
	buildComponents(container);
    }
    
    private void buildComponents(Container container) {
	if (container instanceof Frame)
	    frame = (Frame) container;
	container.setFont(new Font("dialog", 0, 10));
	classpathField = new TextField(50);
	classField = new TextField(50);
	sourcecodeArea = new TextArea(20, 80);
	errorArea = new TextArea(3, 80);
	verboseCheck = new Checkbox("verbose", true);
	prettyCheck = new Checkbox("pretty", true);
	startButton = new Button("start");
	saveButton = new Button("save");
	saveButton.setEnabled(false);
	sourcecodeArea.setEditable(false);
	errorArea.setEditable(false);
	Font font = new Font("monospaced", 0, 10);
	sourcecodeArea.setFont(font);
	errorArea.setFont(font);
	GridBagLayout gridbaglayout = new GridBagLayout();
	container.setLayout(gridbaglayout);
	GridBagConstraints gridbagconstraints = new GridBagConstraints();
	GridBagConstraints gridbagconstraints_1_ = new GridBagConstraints();
	GridBagConstraints gridbagconstraints_2_ = new GridBagConstraints();
	GridBagConstraints gridbagconstraints_3_ = new GridBagConstraints();
	GridBagConstraints gridbagconstraints_4_ = new GridBagConstraints();
	gridbagconstraints.fill = 0;
	gridbagconstraints_1_.fill = 2;
	gridbagconstraints_2_.fill = 1;
	gridbagconstraints_3_.fill = 0;
	gridbagconstraints_4_.fill = 0;
	gridbagconstraints.anchor = 13;
	gridbagconstraints_1_.anchor = 10;
	gridbagconstraints_3_.anchor = 17;
	gridbagconstraints_4_.anchor = 10;
	gridbagconstraints.anchor = 13;
	gridbagconstraints_1_.gridwidth = 0;
	gridbagconstraints_1_.weightx = 1.0;
	gridbagconstraints_2_.gridwidth = 0;
	gridbagconstraints_2_.weightx = 1.0;
	gridbagconstraints_2_.weighty = 1.0;
	container.add(new Label("class path: "), gridbagconstraints);
	container.add(classpathField, gridbagconstraints_1_);
	container.add(new Label("class name: "), gridbagconstraints);
	container.add(classField, gridbagconstraints_1_);
	container.add(verboseCheck, gridbagconstraints_3_);
	container.add(prettyCheck, gridbagconstraints_3_);
	gridbagconstraints.weightx = 1.0;
	container.add(new Label(), gridbagconstraints);
	container.add(startButton, gridbagconstraints_4_);
	gridbagconstraints_4_.gridwidth = 0;
	container.add(saveButton, gridbagconstraints_4_);
	container.add(sourcecodeArea, gridbagconstraints_2_);
	gridbagconstraints_2_.gridheight = 0;
	gridbagconstraints_2_.weighty = 0.0;
	container.add(errorArea, gridbagconstraints_2_);
	startButton.addActionListener(this);
	saveButton.addActionListener(this);
	errStream = new PrintWriter(new AreaWriter(errorArea));
	decompiler.setErr(errStream);
    }
    
    public void setClassPath(String string) {
	classpathField.setText(string);
    }
    
    public void setClass(String string) {
	classField.setText(string);
    }
    
    public synchronized void actionPerformed(ActionEvent actionevent) {
	Object object = actionevent.getSource();
	if (object == startButton) {
	    startButton.setEnabled(false);
	    decompileThread = new Thread(this);
	    sourcecodeArea.setText("Please wait, while decompiling...\n");
	    decompileThread.start();
	} else if (object == saveButton) {
	    if (frame == null)
		frame = new Frame();
	    FileDialog filedialog
		= new FileDialog(frame, "Save decompiled code", 1);
	    filedialog.setFile(lastClassName.substring
				   (lastClassName.lastIndexOf('.') + 1)
				   .concat(".java"));
	    filedialog.show();
	    String string = filedialog.getFile();
	    if (string != null) {
		try {
		    File file = new File(new File(filedialog.getDirectory()),
					 string);
		    FileWriter filewriter = new FileWriter(file);
		    filewriter.write(sourcecodeArea.getText());
		    filewriter.close();
		} catch (IOException ioexception) {
		    errorArea.setText("");
		    errStream
			.println("Couldn't write to file " + string + ": ");
		    ioexception.printStackTrace(errStream);
		} catch (SecurityException securityexception) {
		    errorArea.setText("");
		    errStream
			.println("Couldn't write to file " + string + ": ");
		    securityexception.printStackTrace(errStream);
		}
	    }
	}
    }
    
    public void run() {
	decompiler.setOption("verbose", verboseCheck.getState() ? "1" : "0");
	decompiler.setOption("pretty", prettyCheck.getState() ? "1" : "0");
	errorArea.setText("");
	saveButton.setEnabled(false);
	lastClassName = classField.getText();
	String string = classpathField.getText();
	if (!string.equals(lastClassPath)) {
	    decompiler.setClassPath(string);
	    lastClassPath = string;
	}
	try {
	    BufferedWriter bufferedwriter
		= new BufferedWriter(new AreaWriter(sourcecodeArea), 512);
	    try {
		decompiler.decompile(lastClassName, bufferedwriter, null);
	    } catch (IllegalArgumentException illegalargumentexception) {
		sourcecodeArea.setText
		    ("`" + lastClassName + "' is not a class name.\n"
		     + "You have to give a full qualified classname "
		     + "with '.' as package delimiter \n"
		     + "and without .class ending.");
		return;
	    }
	    saveButton.setEnabled(true);
	} catch (Throwable throwable) {
	    sourcecodeArea.setText
		("Didn't succeed.\nCheck the below area for more info.");
	    throwable.printStackTrace();
	} finally {
	    synchronized (this) {
		decompileThread = null;
		startButton.setEnabled(true);
	    }
	}
    }
    
    public static void main(String[] strings) {
	Frame frame
	    = new Frame("Jode (c) 1998-2001 Jochen Hoenicke <jochen@gnu.org>");
	Window window = new Window(frame);
	String string = System.getProperty("java.class.path");
	if (string != null)
	    window.setClassPath(string.replace(File.pathSeparatorChar, ','));
	String string_5_ = window.getClass().getName();
	window.setClass(string_5_);
	frame.addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent windowevent) {
		System.exit(0);
	    }
	});
	frame.pack();
	frame.show();
    }
}
