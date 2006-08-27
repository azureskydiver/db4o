/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;

/**
 * @exclude
 */
public class UntypedMarshaller1 extends UntypedMarshaller{
    
    public boolean useNormalClassRead(){
        return false;
    }
    
    public void deleteEmbedded(YapWriter reader) {
        int payLoadOffset = reader.readInt();
        if (payLoadOffset > 0) {
            int linkOffset = reader._offset;
            reader._offset = payLoadOffset;
            int yapClassID = reader.readInt();
            YapClass yc = reader.getStream().getYapClass(yapClassID);
            if(yc != null){
                yc.deleteEmbedded(_family, reader);
            }
            reader._offset = linkOffset;
        }
    }
    
    public Object read(YapWriter reader) throws CorruptionException{
        
        Object ret = null;
        
        int payLoadOffSet = reader.readInt();
        if(payLoadOffSet == 0){
            return null;
        }
        
        int linkOffSet = reader._offset;
        reader._offset = payLoadOffSet;
        
        int yapClassID = reader.readInt();
        
        YapClass yc = reader.getStream().getYapClass(yapClassID);
        if(yc != null){
            ret = yc.read(_family, reader, true);
        }
        
        reader._offset = linkOffSet;
        
        return ret;
    }
    
    public Object readQuery(Transaction trans, YapReader reader, boolean toArray) throws CorruptionException{
        
        Object ret = null;
        
        int payLoadOffSet = reader.readInt();
        if(payLoadOffSet == 0){
            return null;
        }
        
        int linkOffSet = reader._offset;
        reader._offset = payLoadOffSet;
        
        int yapClassID = reader.readInt();
        
        YapClass yc = trans.stream().getYapClass(yapClassID);
        if(yc != null){
            ret = yc.readQuery(trans, _family, false, reader, toArray);
        }
        
        reader._offset = linkOffSet;
        
        return ret;
    }

    
    public TypeHandler4 readArrayHandler(Transaction trans, YapReader[] reader) {
        
        int payLoadOffSet = reader[0].readInt();
        if(payLoadOffSet == 0){
            return null;
        }

        TypeHandler4 ret = null;

        reader[0]._offset = payLoadOffSet;
        
        int yapClassID = reader[0].readInt();
        
        YapClass yc = trans.stream().getYapClass(yapClassID);
        if(yc != null){
            ret = yc.readArrayHandler(trans, _family, reader);
        }
        return ret;
    }
    
    public QCandidate readSubCandidate(YapReader reader, QCandidates candidates, boolean withIndirection) {
        int payLoadOffSet = reader.readInt();
        if(payLoadOffSet == 0){
            return null;
        }
        
        QCandidate ret = null;

        int linkOffSet = reader._offset;
        reader._offset = payLoadOffSet;
        
        int yapClassID = reader.readInt();
        
        YapClass yc = candidates.i_trans.stream().getYapClass(yapClassID);
        if(yc != null){
            ret = yc.readSubCandidate(_family, reader, candidates, false);
        }
        reader._offset = linkOffSet;
        
        return ret;
    }

    
    public Object writeNew(Object obj, boolean restoreLinkOffset, YapWriter writer) {
        if (obj == null) {
            writer.writeInt(0);
            return new Integer(0);
        }
        
        YapClass yc = YapClass.forObject(writer.getTransaction(), obj, false);
        
        if(yc == null){
            writer.writeInt(0);
            return new Integer(0);
        }
        
        
        writer.writeInt(writer._payloadOffset);
        int linkOffset = writer._offset;
        writer._offset = writer._payloadOffset;
        
        
        writer.writeInt(yc.getID());
        
        yc.writeNew(_family, obj, false, writer, false, false);
        
        if(writer._payloadOffset < writer._offset){
            writer._payloadOffset = writer._offset;
        }
        
        if(restoreLinkOffset){
            writer._offset = linkOffset;
        }
        
        return obj;
    }

}
