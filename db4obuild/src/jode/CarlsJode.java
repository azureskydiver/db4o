package jode;

import jode.decompiler.*;

/**
 * @author carl
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CarlsJode {

    public static void main(String[] args) {
		// "--contrafo",
		// "--inner", 
    	
    	String[] arg = {"--anonymous", "--dest", "c:\\aa", "c:\\aa\\db4o.jar"};
		// String[] arg = {"--dest", "c:\\aj", "c:\\aj\\ajto.zip"};
		// String[] arg = {"--verify=no","--dest", "c:\\aj", "c:\\aj\\ajdb4o.zip"};
		// String[] arg = {"--debug=interpreter","--dest", "c:\\aj", "c:\\aj\\ajdb4o.zip"};
		// String[] arg = {"--help"};
		// String[] arg = {"--version"};
		// String[] arg = {"com.db4o.ADebug"};
    	Main.main(arg);
    	
    }
}
