/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * base class for all database aware objects
 */
class P1Object implements Db4oTypeImpl{
    
    private transient Transaction i_trans;
    private transient YapObject i_yapObject;
    
    public P1Object(){
    }
    
    P1Object(Transaction a_trans){
        i_trans = a_trans;
    }
    
    public void activate (Object a_obj, int a_depth){
        if(i_trans != null){
            if(a_depth < 0){
                i_trans.i_stream.activate1(i_trans, a_obj);
            }else{
                i_trans.i_stream.activate1(i_trans, a_obj, a_depth);
            }
        }
    }
    
    public int activationDepth(){
        return 1;
    }
    
    public int adjustReadDepth(int a_depth) {
        return a_depth;
    }
    
    void checkActive(){
        if(i_trans != null){
		    if(i_yapObject == null){
		        i_yapObject = i_trans.i_stream.getYapObject(this);
		        if(i_yapObject == null){
		            i_trans.i_stream.set(this);
		            i_yapObject = i_trans.i_stream.getYapObject(this);
		        }
		    }
		    if(validYapObject()){
		        i_yapObject.activate(i_trans, this, activationDepth(), false);
		    }
        }
    }

    public Object createDefault(Transaction a_trans) {
        throw YapConst.virtualException();
    }
    
    void deactivate(){
        if(validYapObject()){
            i_yapObject.deactivate(i_trans, activationDepth());
        }
    }
    
    void delete(){
        if(i_trans != null){
	        if(i_yapObject == null){
	            i_yapObject = i_trans.i_stream.getYapObject(this);
	        }
	        if(validYapObject()){
	            i_trans.i_stream.delete3(i_trans,i_yapObject,this, 0);
	        }
        }
    }
    
    protected void delete(Object a_obj){
        if(i_trans != null){
            i_trans.i_stream.delete(a_obj);
        }
    }
    
    protected long getIDOf(Object a_obj){
        if(i_trans == null){
            return 0;
        }
        return i_trans.i_stream.getID(a_obj);
    }
    
    protected Transaction getTrans(){
        return i_trans;
    }
    
    public boolean hasClassIndex() {
        return false;
    }
    
    public void preDeactivate(){
        // virtual, do nothing
    }

    public void setTrans(Transaction a_trans){
        i_trans = a_trans;
    }

    public void setYapObject(YapObject a_yapObject) {
        i_yapObject = a_yapObject;
    }
    
    protected void store(Object a_obj){
        if(i_trans != null){
            i_trans.i_stream.setInternal(i_trans, a_obj, true);
        }
    }
    
    public Object storedTo(Transaction a_trans){
        i_trans = a_trans;
        return this;
    }
    
    Object streamLock(){
        if(i_trans != null){
	        i_trans.i_stream.checkClosed();
	        return i_trans.i_stream.lock();
        }
        return this;
    }
    
    void store(int a_depth){
        if(i_trans != null){
            if(i_yapObject == null){
                i_yapObject = i_trans.i_stream.getYapObject(this);
                if(i_yapObject == null){
                    i_trans.i_stream.setInternal(i_trans, this, true);
                    i_yapObject = i_trans.i_stream.getYapObject(this);
                    return;
                }
            }
            update(a_depth);
        }
    }
    
    void update(){
        update(activationDepth());
    }
    
    void update(int depth){
        if(validYapObject()){
            i_trans.i_stream.beginEndSet(i_trans);
            i_yapObject.writeUpdate(i_trans, depth);
            i_trans.i_stream.checkStillToSet();
            i_trans.i_stream.beginEndSet(i_trans);
        }
    }
    
    private boolean validYapObject(){
        return (i_trans != null) && (i_yapObject != null) && (i_yapObject.getID() > 0);
    }
    
    
}
