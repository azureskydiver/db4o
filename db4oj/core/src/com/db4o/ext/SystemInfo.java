/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.ext;


/**
 * provides information about system state and system settings.
 */
public interface SystemInfo {
    
    /**
     * returns the number of entries in the Freespace Manager.
     * <br><br>A high value for the number of freespace entries
     * is an indication that the database is fragmented and 
     * that defragment should be run.  
     * @return the number of entries in the Freespace Manager.
     */
    public int freespaceEntryCount();
    
    /**
     * returns the freespace size in the database in bytes.
     * <br><br>When db4o stores modified objects, it allocates
     * a new slot for it. During commit the old slot is freed.
     * Free slots are collected in the freespace manager, so
     * they can be reused for other objects.
     * <br><br>This method returns a sum of the size of all  
     * free slots in the database file.
     * <br><br>To reclaim freespace run defragment.
     * @return  the freespace size in the database in bytes.
     */
    public long freespaceSize();

    /**
     * Returns the total size of database on disc.
     * @return total size of database on disc
     */
    public long totalSize();

}
