/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;


/**
 * 
 */
public class DTrace {
    
    public static final boolean enabled = false;
    
    private static void breakPoint(){
        int placeBreakPointHere = 1;
    }
    
    private static final Object init(){
        if(enabled){
            
            // address: 244346, 44
            
            addRange(25876597);
            addRange(19818619);
            addRangeWithLength(25876597, 44);
            // addRange(7274611);
            // addRange(17920);
            
            COMMIT = new DTrace(true, false, "commit", true);
            FREE = new DTrace(true, true, "free", true);
            FREE_ON_COMMIT = new DTrace(true, true, "trans freeOnCommit", true);
            FREE_ON_ROLLBACK = new DTrace(true, true, "trans freeOnRollback", true);
            GET_SLOT = new DTrace(true, true, "getSlot", true);
            READ_ID = new DTrace(true, true, "read ID", true);
            READ_SLOT = new DTrace(true, true, "read slot", true);
            REFERENCE_REMOVED = new DTrace(true, true, "reference removed", true);
            REMOVE_FROM_CLASS_INDEX = new DTrace(true, true, "trans removeFromClassIndexTree", true);
            TRANS_DELETE = new DTrace(true, true, "trans delete", true);
            TRANS_DONT_DELETE = new DTrace(true, true, "trans dontDelete", true);
            WRITE_UPDATE_DELETE_MEMBERS = new DTrace(true, true, "trans writeUpdateDeleteMembers", true);
         
        }
        return null;
    }
    
    private DTrace(boolean enabled_, boolean break_, String tag_, boolean log_){
        if(enabled){
	        _enabled = enabled_;
	        _break = break_;
	        _tag = tag_;
	        _log = log_;
        }
    }
    
    private boolean _enabled;
    private boolean _break;
    private boolean _log;
    private String _tag;
    
    private static int[] rangeStart;
    private static int [] rangeEnd;
    private static int rangeCount;
    
    public static DTrace COMMIT;
    public static DTrace FREE;
    public static DTrace FREE_ON_COMMIT;
    public static DTrace FREE_ON_ROLLBACK;
    public static DTrace GET_SLOT;
    public static DTrace READ_ID;
    public static DTrace READ_SLOT;
    public static DTrace REFERENCE_REMOVED;
    public static DTrace REMOVE_FROM_CLASS_INDEX;
    public static DTrace TRANS_DONT_DELETE;
    public static DTrace TRANS_DELETE;
    public static DTrace WRITE_UPDATE_DELETE_MEMBERS;
    
    private static final Object forInit = init();
    
    public void log(){
        if(enabled){
            log(0);
        }
    }
    
    public void log(int p){
        if(enabled){
            logLength(p, 1);
        }
    }
    
    public void logLength(int start, int length){
        if(enabled){
            logEnd(start, start + length - 1);
        }
    }
    
    public void logEnd(int start, int end){
        if(enabled){
	        if(! _enabled){
	            return;
	        }
	        boolean inRange = false;
	        for (int i = 0; i < rangeCount; i++) {
	            if(start >= rangeStart[i] && start <= rangeEnd[i]){
	                inRange = true;
	                break;
	            }
                if(end != 0 && (end >= rangeStart[i] && end <= rangeEnd[i])){
                    inRange = true;
                    break;
                }
	        }
	        if(inRange || (start == 0 && end == 0)){
	            if(_log){
	                StringBuffer sb = new StringBuffer(":");
	                if(start != 0){
	                    sb.append(formatInt(start));
	                    sb.append(":");
	                }
	                if(end != 0  && start != end){
		                sb.append(formatInt(end));
		                sb.append(":");
	                }
                    sb.append(" ");
	                sb.append(_tag);
	                System.out.println(sb);
	            }
		        if(_break){
		            breakPoint();
		        }
	        }
        }
    }
    
    public static void addRange(int pos){
        if(enabled){
            addRangeWithEnd(pos, pos);
        }
    }
    
    public static void addRangeWithLength(int start, int length){
        if(enabled){
            addRangeWithEnd(start, start + length - 1);
        }
    }
    
    public static void addRangeWithEnd(int start, int end){
        if(enabled){
	        if(rangeStart == null){
	            rangeStart = new int[100];
	            rangeEnd = new int[100];
	        }
	        rangeStart[rangeCount] = start;
	        rangeEnd[rangeCount] = end;
	        rangeCount++;
        }
    }
    
    private String formatInt(int i){
        if(enabled){
            String str = "            " + i + " "; 
            return str.substring(str.length() - 12);
        }
        return null;
    }
    
}
