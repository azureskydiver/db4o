/* Applet - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.decompiler;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

public class Applet extends java.applet.Applet
{
    private final int BORDER = 10;
    private final int BEVEL = 2;
    private Window jodeWin = new Window(this);
    private Insets myInsets;
    private Color pageColor;
    
    public Insets getInsets() {
	if (myInsets == null) {
	    Insets insets = super.getInsets();
	    myInsets = new Insets(insets.top + 10, insets.left + 10,
				  insets.bottom + 10, insets.right + 10);
	}
	return myInsets;
    }
    
    public void paint(Graphics graphics) {
	super.paint(graphics);
	Color color = this.getBackground();
	Color color_0_ = color.brighter();
	Color color_1_ = color.darker();
	Dimension dimension = this.getSize();
	graphics.setColor(pageColor);
	graphics.fillRect(0, 0, 10, 10);
	graphics.fillRect(dimension.width - 10, 0, 10, 10);
	graphics.fillRect(dimension.width - 10, dimension.height - 10, 10, 10);
	graphics.fillRect(0, dimension.height - 10, 10, 10);
	graphics.setColor(color_0_);
	graphics.fillArc(0, 0, 20, 20, 90, 90);
	graphics.fillArc(dimension.width - 20, 0, 20, 20, 45, 45);
	graphics.fillArc(0, dimension.height - 20, 20, 20, 180, 45);
	graphics.fillRect(10, 0, dimension.width - 20, 2);
	graphics.fillRect(0, 10, 2, dimension.height - 20);
	graphics.setColor(color_1_);
	graphics.fillArc(dimension.width - 20, 0, 20, 20, 0, 45);
	graphics.fillArc(0, dimension.height - 20, 20, 20, 225, 45);
	graphics.fillArc(dimension.width - 20, dimension.height - 20, 20, 20,
			 -90, 90);
	graphics.fillRect(10, dimension.height - 2, dimension.width - 20, 2);
	graphics.fillRect(dimension.width - 2, 10, 2, dimension.height - 20);
	graphics.setColor(color);
	graphics.fillArc(2, 2, 16, 16, 90, 90);
	graphics.fillArc(dimension.width - 18, 2, 16, 16, 0, 90);
	graphics.fillArc(2, dimension.height - 20 + 2, 16, 16, 180, 90);
	graphics.fillArc(dimension.width - 18, dimension.height - 18, 16, 16,
			 -90, 90);
    }
    
    public void init() {
	String string = this.getParameter("pagecolor");
	if (string == null)
	    string = "ffffff";
	pageColor = new Color(Integer.parseInt(string, 16));
	string = this.getParameter("bgcolor");
	if (string != null)
	    this.setBackground(new Color(Integer.parseInt(string, 16)));
	String string_2_ = this.getParameter("classpath");
	if (string_2_ != null)
	    jodeWin.setClassPath(string_2_);
	String string_3_ = this.getParameter("class");
	if (string_3_ != null)
	    jodeWin.setClass(string_3_);
    }
}
