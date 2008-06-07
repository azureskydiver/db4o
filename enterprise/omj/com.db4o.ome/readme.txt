BUILD
-----

Ant build has not been tested at all, yet. For a manual build from within Eclipse:

1. Create a folder named "eclipse" anywhere you like.
2. Create an empty file ".eclipseextension" in this folder.
3. In the package explorer, choose "Export" -> "Deployable Features".
4. Select "com.db4o.ome.feature" project.
5. In "Destination", check "Directory" and point it to the "eclipse" folder created in step 1.
6. "Finish". You should now find "features" and "plugins" folders below "eclipse".
7. In the target Eclipse install, choose "Help" -> "Software Updates" -> "Manage Configuration".
8. If you have an earlier version of OMEJ installed, select it and choose "Disable". Confirm restart, go to this dialog again.
9. Choose "Add an Extension Location" and point it to the "eclipse" folder.

UPDATE DB4O VERSION
-------------------

1. Build a new db4o jar.
2. Copy it to lib/ in "com.db4o.ome" project.
3. Open META-INF/MANIFEST.MF in "com.db4o.ome" project.
4. In the "Runtime" tab, remove the old jar from the "Classpath" entries and add the new one.
5. In the "Overview" tab, choose "Update class path settings".
6. Remove the old jar from lib/.