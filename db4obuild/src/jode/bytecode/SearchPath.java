/* SearchPath - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.bytecode;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import jode.GlobalOptions;

public class SearchPath
{
    public static final char altPathSeparatorChar = ',';
    URL[] bases;
    byte[][] urlzips;
    File[] dirs;
    ZipFile[] zips;
    String[] zipDirs;
    Hashtable[] zipEntries;
    
    private static void addEntry(Hashtable hashtable, String string) {
	String string_0_ = "";
	int i = string.lastIndexOf("/");
	if (i != -1) {
	    string_0_ = string.substring(0, i);
	    string = string.substring(i + 1);
	}
	Vector vector = (Vector) hashtable.get(string_0_);
	if (vector == null) {
	    vector = new Vector();
	    hashtable.put(string_0_, vector);
	    if (string_0_ != "")
		addEntry(hashtable, string_0_);
	}
	vector.addElement(string);
    }
    
    private void fillZipEntries(int i) {
	Enumeration enumeration = zips[i].entries();
	zipEntries[i] = new Hashtable();
	while (enumeration.hasMoreElements()) {
	    ZipEntry zipentry = (ZipEntry) enumeration.nextElement();
	    String string = zipentry.getName();
	    if (zipDirs[i] != null) {
		if (!string.startsWith(zipDirs[i]))
		    continue;
		string = string.substring(zipDirs[i].length());
	    }
	    if (!zipentry.isDirectory() && string.endsWith(".class"))
		addEntry(zipEntries[i], string);
	}
    }
    
    private void readURLZip(int i, URLConnection urlconnection) {
	int i_1_ = urlconnection.getContentLength();
	if (i_1_ <= 0)
	    i_1_ = 10240;
	else
	    i_1_++;
	urlzips[i] = new byte[i_1_];
	try {
	    InputStream inputstream = urlconnection.getInputStream();
	    int i_2_ = 0;
	    for (;;) {
		int i_3_ = Math.max(inputstream.available(), 1);
		if (i_2_ + inputstream.available() > urlzips[i].length) {
		    byte[] is
			= new byte[Math.max(2 * urlzips[i].length,
					    i_2_ + inputstream.available())];
		    System.arraycopy(urlzips[i], 0, is, 0, i_2_);
		    urlzips[i] = is;
		}
		int i_4_ = inputstream.read(urlzips[i], i_2_,
					    urlzips[i].length - i_2_);
		if (i_4_ == -1)
		    break;
		i_2_ += i_4_;
	    }
	    if (i_2_ < urlzips[i].length) {
		byte[] is = new byte[i_2_];
		System.arraycopy(urlzips[i], 0, is, 0, i_2_);
		urlzips[i] = is;
	    }
	} catch (IOException ioexception) {
	    GlobalOptions.err.println
		("IOException while reading remote zip file " + bases[i]);
	    bases[i] = null;
	    urlzips[i] = null;
	    return;
	}
	try {
	    ZipInputStream zipinputstream
		= new ZipInputStream(new ByteArrayInputStream(urlzips[i]));
	    zipEntries[i] = new Hashtable();
	    ZipEntry zipentry;
	    while ((zipentry = zipinputstream.getNextEntry()) != null) {
		String string = zipentry.getName();
		if (zipDirs[i] != null) {
		    if (!string.startsWith(zipDirs[i]))
			continue;
		    string = string.substring(zipDirs[i].length());
		}
		if (!zipentry.isDirectory() && string.endsWith(".class"))
		    addEntry(zipEntries[i], string);
		zipinputstream.closeEntry();
	    }
	    zipinputstream.close();
	} catch (IOException ioexception) {
	    GlobalOptions.err
		.println("Remote zip file " + bases[i] + " is corrupted.");
	    bases[i] = null;
	    urlzips[i] = null;
	    zipEntries[i] = null;
	}
    }
    
    public SearchPath(String string) {
	int i = 1;
	int i_5_ = string.indexOf(File.pathSeparatorChar);
	while (i_5_ != -1) {
	    i_5_ = string.indexOf(File.pathSeparatorChar, i_5_ + 1);
	    i++;
	}
	if (File.pathSeparatorChar != ',') {
	    int i_6_ = string.indexOf(',');
	    while (i_6_ != -1) {
		i_6_ = string.indexOf(',', i_6_ + 1);
		i++;
	    }
	}
	bases = new URL[i];
	urlzips = new byte[i][];
	dirs = new File[i];
	zips = new ZipFile[i];
	zipEntries = new Hashtable[i];
	zipDirs = new String[i];
	int i_7_ = 0;
	int i_8_ = 0;
	while (i_8_ < string.length()) {
	    int i_9_;
	    for (i_9_ = i_8_; (i_9_ < string.length()
			       && string.charAt(i_9_) != File.pathSeparatorChar
			       && string.charAt(i_9_) != ','); i_9_++) {
		/* empty */
	    }
	    int i_10_ = i_8_;
	while_0_:
	    while (i_9_ > i_8_ && i_9_ < string.length()
		   && string.charAt(i_9_) == ':') {
		for (/**/; i_10_ < i_9_; i_10_++) {
		    char c = string.charAt(i_10_);
		    if ((c < 'A' || c > 'Z') && (c < 'a' || c > 'z')
			&& (c < '0' || c > '9') && "+-".indexOf(c) == -1)
			break while_0_;
		}
		i_9_++;
		i_10_++;
		for (/**/; (i_9_ < string.length()
			    && string.charAt(i_9_) != File.pathSeparatorChar
			    && string.charAt(i_9_) != ','); i_9_++) {
		    /* empty */
		}
	    }
	    String string_11_ = string.substring(i_8_, i_9_);
	    i_8_ = i_9_;
	    boolean bool = false;
	    do {
		if (string_11_.startsWith("jar:")) {
		    i_10_ = 0;
		    do
			i_10_ = string_11_.indexOf('!', i_10_);
		    while (i_10_ != -1 && i_10_ != string_11_.length() - 1
			   && string_11_.charAt(i_10_ + 1) != '/');
		    if (i_10_ == -1 || i_10_ == string_11_.length() - 1) {
			GlobalOptions.err.println("Warning: Illegal jar url "
						  + string_11_ + ".");
			break;
		    }
		    zipDirs[i_7_] = string_11_.substring(i_10_ + 2);
		    if (!zipDirs[i_7_].endsWith("/"))
			zipDirs[i_7_] = zipDirs[i_7_] + "/";
		    string_11_ = string_11_.substring(4, i_10_);
		    bool = true;
		}
		i_10_ = string_11_.indexOf(':');
		if (i_10_ != -1 && i_10_ < string_11_.length() - 2
		    && string_11_.charAt(i_10_ + 1) == '/'
		    && string_11_.charAt(i_10_ + 2) == '/') {
		    try {
			bases[i_7_] = new URL(string_11_);
			try {
			    URLConnection urlconnection
				= bases[i_7_].openConnection();
			    if (bool || string_11_.endsWith(".zip")
				|| string_11_.endsWith(".jar")
				|| urlconnection.getContentType()
				       .endsWith("/zip"))
				readURLZip(i_7_, urlconnection);
			} catch (IOException ioexception) {
			    /* empty */
			} catch (SecurityException securityexception) {
			    GlobalOptions.err.println
				("Warning: Security exception while accessing "
				 + bases[i_7_] + ".");
			}
		    } catch (MalformedURLException malformedurlexception) {
			bases[i_7_] = null;
			dirs[i_7_] = null;
		    }
		} else {
		    try {
			dirs[i_7_] = new File(string_11_);
			if (bool || !dirs[i_7_].isDirectory()) {
			    try {
				zips[i_7_] = new ZipFile(dirs[i_7_]);
			    } catch (IOException ioexception) {
				dirs[i_7_] = null;
			    }
			}
		    } catch (SecurityException securityexception) {
			GlobalOptions.err.println
			    ("Warning: SecurityException while accessing "
			     + string_11_ + ".");
			dirs[i_7_] = null;
		    }
		}
	    } while (false);
	    i_8_++;
	    i_7_++;
	}
    }
    
    public boolean exists(String string) {
	String string_12_
	    = (File.separatorChar != '/'
	       ? string.replace('/', File.separatorChar) : string);
	for (int i = 0; i < dirs.length; i++) {
	    if (zipEntries[i] != null) {
		if (zipEntries[i].get(string) != null)
		    return true;
		String string_13_ = "";
		String string_14_ = string;
		int i_15_ = string.lastIndexOf('/');
		if (i_15_ >= 0) {
		    string_13_ = string.substring(0, i_15_);
		    string_14_ = string.substring(i_15_ + 1);
		}
		Vector vector = (Vector) zipEntries[i].get(string_13_);
		if (vector != null && vector.contains(string_14_))
		    return true;
	    } else {
		if (bases[i] != null) {
		    try {
			URL url = new URL(bases[i], string);
			URLConnection urlconnection = url.openConnection();
			urlconnection.connect();
			urlconnection.getInputStream().close();
			return true;
		    } catch (IOException ioexception) {
			continue;
		    }
		}
		if (dirs[i] != null) {
		    if (zips[i] != null) {
			String string_16_ = (zipDirs[i] != null
					     ? zipDirs[i] + string : string);
			ZipEntry zipentry = zips[i].getEntry(string_16_);
			if (zipentry != null)
			    return true;
		    } else {
			try {
			    File file = new File(dirs[i], string_12_);
			    if (file.exists())
				return true;
			} catch (SecurityException securityexception) {
			    /* empty */
			}
		    }
		}
	    }
	}
	return false;
    }
    
    public InputStream getFile(String string) throws IOException {
	String string_17_
	    = (File.separatorChar != '/'
	       ? string.replace('/', File.separatorChar) : string);
	for (int i = 0; i < dirs.length; i++) {
	    if (urlzips[i] != null) {
		ZipInputStream zipinputstream
		    = new ZipInputStream(new ByteArrayInputStream(urlzips[i]));
		String string_18_
		    = zipDirs[i] != null ? zipDirs[i] + string : string;
		ZipEntry zipentry;
		while ((zipentry = zipinputstream.getNextEntry()) != null) {
		    if (zipentry.getName().equals(string_18_))
			return zipinputstream;
		    zipinputstream.closeEntry();
		}
	    } else {
		if (bases[i] != null) {
		    try {
			URL url = new URL(bases[i], string);
			URLConnection urlconnection = url.openConnection();
			urlconnection.setAllowUserInteraction(true);
			return urlconnection.getInputStream();
		    } catch (SecurityException securityexception) {
			GlobalOptions.err.println
			    ("Warning: SecurityException while accessing "
			     + bases[i] + string);
			securityexception.printStackTrace(GlobalOptions.err);
			continue;
		    } catch (FileNotFoundException filenotfoundexception) {
			continue;
		    }
		}
		if (dirs[i] != null) {
		    if (zips[i] != null) {
			String string_19_ = (zipDirs[i] != null
					     ? zipDirs[i] + string : string);
			ZipEntry zipentry = zips[i].getEntry(string_19_);
			if (zipentry != null)
			    return zips[i].getInputStream(zipentry);
		    } else {
			try {
			    File file = new File(dirs[i], string_17_);
			    if (file.exists())
				return new FileInputStream(file);
			} catch (SecurityException securityexception) {
			    GlobalOptions.err.println
				("Warning: SecurityException while accessing "
				 + dirs[i] + string_17_);
			}
		    }
		}
	    }
	}
	throw new FileNotFoundException(string);
    }
    
    public boolean isDirectory(String string) {
	String string_20_
	    = (File.separatorChar != '/'
	       ? string.replace('/', File.separatorChar) : string);
	for (int i = 0; i < dirs.length; i++) {
	    if (dirs[i] != null) {
		if (zips[i] != null && zipEntries[i] == null)
		    fillZipEntries(i);
		if (zipEntries[i] != null) {
		    if (zipEntries[i].containsKey(string))
			return true;
		} else {
		    try {
			File file = new File(dirs[i], string_20_);
			if (file.exists())
			    return file.isDirectory();
		    } catch (SecurityException securityexception) {
			GlobalOptions.err.println
			    ("Warning: SecurityException while accessing "
			     + dirs[i] + string_20_);
		    }
		}
	    }
	}
	return false;
    }
    
    public Enumeration listFiles(final String dirName) {
	return new Enumeration() {
	    int pathNr;
	    Enumeration zipEnum;
	    int fileNr;
	    String localDirName
		= (File.separatorChar != '/'
		   ? dirName.replace('/', File.separatorChar) : dirName);
	    File currentDir;
	    String[] files;
	    String nextName;
	    
	    public String findNextFile() {
		for (;;) {
		    if (zipEnum != null) {
			if (zipEnum.hasMoreElements())
			    return (String) zipEnum.nextElement();
			zipEnum = null;
		    }
		    if (files != null) {
			while (fileNr < files.length) {
			    String string = files[fileNr++];
			    if (string.endsWith(".class"))
				return string;
			    if (string.indexOf(".") == -1) {
				File file = new File(currentDir, string);
				if (file.exists() && file.isDirectory())
				    return string;
			    }
			}
			files = null;
		    }
		    if (pathNr == dirs.length)
			return null;
		    if (zips[pathNr] != null && zipEntries[pathNr] == null)
			SearchPath.this.fillZipEntries(pathNr);
		    if (zipEntries[pathNr] != null) {
			Vector vector
			    = (Vector) zipEntries[pathNr].get(dirName);
			if (vector != null)
			    zipEnum = vector.elements();
		    } else if (dirs[pathNr] != null) {
			try {
			    File file = new File(dirs[pathNr], localDirName);
			    if (file.exists() && file.isDirectory()) {
				currentDir = file;
				files = file.list();
				fileNr = 0;
			    }
			} catch (SecurityException securityexception) {
			    GlobalOptions.err.println
				("Warning: SecurityException while accessing "
				 + dirs[pathNr] + localDirName);
			}
		    }
		    pathNr++;
		}
	    }
	    
	    public boolean hasMoreElements() {
		return nextName != null || (nextName = findNextFile()) != null;
	    }
	    
	    public Object nextElement() {
		if (nextName == null)
		    return findNextFile();
		String string = nextName;
		nextName = null;
		return string;
	    }
	};
    }
}
