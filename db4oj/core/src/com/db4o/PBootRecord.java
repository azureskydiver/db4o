/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.ext.*;

/**
 * database boot record. Responsible for ID generation, version generation and
 * holding a reference to the Db4oDatabase object of the ObjectContainer
 * 
 * @exclude
 * @persistent
 */
public class PBootRecord extends P1Object implements Db4oTypeImpl, Internal4{

    transient YapFile         i_stream;
    public Db4oDatabase       i_db;
    public long               i_uuidGenerator;
    public long               i_versionGenerator;
    public int                i_generateVersionNumbers;
    public int                i_generateUUIDs;
    private transient boolean i_dirty;

    public MetaIndex          i_uuidMetaIndex;
    
    
    public PBootRecord(){
    }

    public int activationDepth() {
        return Integer.MAX_VALUE;
    }

    void init(Config4Impl a_config) {
        i_db = Db4oDatabase.generate();
        i_uuidGenerator = Unobfuscated.randomLong();
        i_uuidMetaIndex = new MetaIndex();
        initConfig(a_config);
        i_dirty = true;
    }

    boolean initConfig(Config4Impl a_config) {
        
        boolean modified = false;
        
        if(i_generateVersionNumbers != a_config.generateVersionNumbers()){
            i_generateVersionNumbers = a_config.generateVersionNumbers();
            modified = true;
        }
        
        if(i_generateUUIDs != a_config.generateUUIDs()){
            i_generateUUIDs = a_config.generateUUIDs();
            modified = true;
        }
        
        return modified;
        
        
        // Below is a reflection-based approach to copy all fields with the same name.
        // Let's stay in manual mode for now so db4o can run without reflection.
        
        
//        Class myClass = this.getClass();
//        Class configClass = a_config.getClass();
//        Field[] fields = myClass.getDeclaredFields();
//        for (int i = 0; i < fields.length; i++) {
//            try {
//                Field field = configClass.getField(fields[i].getName());
//                if (field != null) {
//                    Object obj = field.get(a_config);
//                    if (obj != null) {
//                        YapClass yc = i_stream.i_handlers.getYapClassStatic(
//                        		a_config.reflector().forObject(obj)
//                            );
//                        if (yc instanceof YapClassPrimitive) {
//                            YapJavaClass yjc = (YapJavaClass) ((YapClassPrimitive) yc).i_handler;
//                            if (!yjc.primitiveNull().equals(obj)) {
//                                fields[i].set(this, obj);
//                                modified = true;
//                            }
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                // e.printStackTrace();
//            }
//        }
//        return modified;
        
    }
    
    MetaIndex getUUIDMetaIndex(){
        
        // TODO: This is legacy code for old database files.
        // Newer versions create i_uuidMetaIndex when PBootRecord
        // is created. Remove this code after June 2006.
        if (i_uuidMetaIndex == null) {
            i_uuidMetaIndex = new MetaIndex();
            Transaction systemTrans = i_stream.getSystemTransaction();
            i_stream.showInternalClasses(true);
            i_stream.setInternal(systemTrans, this, false);
            i_stream.showInternalClasses(false);
            systemTrans.commit();
        }
        
        return i_uuidMetaIndex;
    }

    long newUUID() {
        i_dirty = true;
        return i_uuidGenerator++;
    }
    
    public void setDirty(){
        i_dirty = true;
    }

    public void store(int a_depth) {
        if (i_dirty) {
            i_versionGenerator++;
            i_stream.showInternalClasses(true);
            super.store(a_depth);
            i_stream.showInternalClasses(false);
        }
        i_dirty = false;
    }

    long version() {
        i_dirty = true;
        return i_versionGenerator;
    }

}