/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;


/**
 * @exclude 
 */
public class DTrace {
    
    public static final boolean enabled = false;
    
    private static void breakPoint(){
        int placeBreakPointHere = 1;
    }
    
    private static final Object init(){
        if(enabled){
            
            // address: 244346, 44
            
            // addRange(5130);
            
            addRange(55);
            
            // addRangeWithLength(1000000, 10000000);
            
            // addRangeWithLength(25876597, 44);
            // addRange(7274611);
            // addRange(17920);
            
            BIND = new DTrace(true, true, "bind", true);
            CLOSE = new DTrace(true, true, "close", true);
            COMMIT = new DTrace(true, false, "commit", true);
            CONTINUESET = new DTrace(true, true, "continueset", true);
            FREE = new DTrace(true, true, "free", true);
            FREE_ON_COMMIT = new DTrace(true, true, "trans freeOnCommit", true);
            FREE_ON_ROLLBACK = new DTrace(true, true, "trans freeOnRollback", true);
            GET_SLOT = new DTrace(true, true, "getSlot", true);
            NEW_INSTANCE = new DTrace(true, true, "newInstance", true);
            READ_ID = new DTrace(true, true, "read ID", true);
            READ_SLOT = new DTrace(true, true, "read slot", true);
            REFERENCE_REMOVED = new DTrace(true, true, "reference removed", true);
            REGULAR_SEEK = new DTrace(true, true, "regular seek", true);
            REMOVE_FROM_CLASS_INDEX = new DTrace(true, true, "trans removeFromClassIndexTree", true);
            TRANS_COMMIT = new DTrace(false, false, "trans commit", false);
            TRANS_DELETE = new DTrace(true, true, "trans delete", true);
            TRANS_DONT_DELETE = new DTrace(true, true, "trans dontDelete", true);
            YAPCLASS_BY_ID = new DTrace(true, true, "yapclass by id", true);
            WRITE_BYTES = new DTrace(true, true, "writeBytes", true); 
            WRITE_UPDATE_DELETE_MEMBERS = new DTrace(true, true, "trans writeUpdateDeleteMembers", true);
            
            // turnAllOffExceptFor(new DTrace[] {FREE, FREE_ON_COMMIT});
            turnAllOffExceptFor(new DTrace[] {YAPCLASS_BY_ID});
         
        }
        return null;
    }
    
    private DTrace(boolean enabled_, boolean break_, String tag_, boolean log_){
        if(enabled){
	        _enabled = enabled_;
	        _break = break_;
	        _tag = tag_;
	        _log = log_;
            if(all == null){
                all = new DTrace[100];
            }
            all[current++] = this;
        }
    }
    
    private boolean _enabled;
    private boolean _break;
    private boolean _log;
    private String _tag;
    
    private static long[] rangeStart;
    private static long [] rangeEnd;
    private static int rangeCount;
    
    public static DTrace BIND;
    public static DTrace CLOSE;
    public static DTrace COMMIT;
    public static DTrace CONTINUESET;
    public static DTrace FREE;
    public static DTrace FREE_ON_COMMIT;
    public static DTrace FREE_ON_ROLLBACK;
    public static DTrace GET_SLOT;
    public static DTrace NEW_INSTANCE;
    public static DTrace READ_ID;
    public static DTrace READ_SLOT;
    public static DTrace REFERENCE_REMOVED;
    public static DTrace REGULAR_SEEK;
    public static DTrace REMOVE_FROM_CLASS_INDEX;
    public static DTrace TRANS_COMMIT;
    public static DTrace TRANS_DONT_DELETE;
    public static DTrace TRANS_DELETE;
    public static DTrace YAPCLASS_BY_ID;
    
    public static DTrace WRITE_BYTES;
    public static DTrace WRITE_UPDATE_DELETE_MEMBERS;
    
    private static final Object forInit = init();
    
    private static DTrace all[];
    private static int current;
    
    public void log(){
        if(enabled){
            log(-1);
        }
    }
    
    public void log(long p){
        if(enabled){
            logLength(p, 1);
        }
    }
    
    public void logInfo(String info){
        if(enabled){
            logEnd(-1,0, info );
        }
    }
    
    public void log(long p, String info){
        if(enabled){
            logEnd(p, 0, info);
        }
        
    }
    
    public void logLength(long start, long length){
        if(enabled){
            logEnd(start, start + length - 1);
        }
    }
    
    public void logEnd(long start, long end){
        if(enabled){
            logEnd(start, end, null);
        }
    }
    
    public void logEnd(long start, long end, String info){
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
	        if(inRange || (start == -1 )){
	            if(_log){
	                StringBuffer sb = new StringBuffer(":");
	                if(start != 0){
	                    sb.append(formatInt(start));
	                    sb.append(":");
	                }
	                if(end != 0  && start != end){
		                sb.append(formatInt(end));
	                }else{
                        sb.append(formatInt(0));
                    }
                    sb.append(":");
                    if(info != null){
                        sb.append(" " + info + " ");
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
    
    public static void addRange(long pos){
        if(enabled){
            addRangeWithEnd(pos, pos);
        }
    }
    
    public static void addRangeWithLength(long start, long length){
        if(enabled){
            addRangeWithEnd(start, start + length - (long)1);
        }
    }
    
    public static void addRangeWithEnd(long start, long end){
        if(enabled){
	        if(rangeStart == null){
	            rangeStart = new long[100];
	            rangeEnd = new long[100];
	        }
	        rangeStart[rangeCount] = start;
	        rangeEnd[rangeCount] = end;
	        rangeCount++;
        }
    }
    
    private String formatInt(long i){
        if(enabled){
            String str = "              ";
            if( i != 0){
                str += i + " ";
            }
            return str.substring(str.length() - 12);
        }
        return null;
    }
    
    private static void turnAllOffExceptFor(DTrace[] these){
        if(enabled){
            for (int i = 0; i < all.length; i++) {
                if(all[i] == null){
                    break;
                }
                boolean turnOff = true;
                for (int j = 0; j < these.length; j++) {
                    if(all[i] == these[j]){
                        turnOff = false;
                        break;
                    }
                }
                if(turnOff){
                    all[i]._break = false;
                    all[i]._enabled = false;
                    all[i]._log = false;
                }
            }
        }
    }
    
}
