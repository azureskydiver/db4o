package com.db4odoc.android;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;

import android.app.Activity;
import android.os.Bundle;

// #example: open db4o on Android
public class Db4oOnAndroidExample  extends Activity  {	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		String filePath = this.getFilesDir() + "/android.db4o";
		ObjectContainer db = Db4oEmbedded.openFile(filePath);
		// do your stuff
		db.close();
    	
    }
}
// #end example
