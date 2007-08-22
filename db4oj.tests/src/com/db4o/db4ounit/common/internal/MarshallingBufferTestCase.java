/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.internal;

import com.db4o.internal.*;

import db4ounit.*;


public class MarshallingBufferTestCase implements TestCase {
    
    private static final int DATA_1 = 111;  
    private static final byte DATA_2 = (byte)2; 
    private static final int DATA_3 = 333; 
    private static final int DATA_4 = 444; 
    private static final int DATA_5 = 55; 
    
    public void testWrite(){
        MarshallingBuffer buffer = new MarshallingBuffer();
        
        buffer.writeInt(DATA_1);
        buffer.writeByte(DATA_2);
        
        Buffer content = inspectContent(buffer);
        
        Assert.areEqual(DATA_1, content.readInt());
        Assert.areEqual(DATA_2, content.readByte());
    }

    public void testTransferLastWrite(){
        
        MarshallingBuffer buffer = new MarshallingBuffer();
        
        buffer.writeInt(DATA_1);
        int lastOffset = offset(buffer);
        buffer.writeByte(DATA_2);
        
        MarshallingBuffer other = new MarshallingBuffer();
        
        buffer.transferLastWriteTo(other);
        
        Assert.areEqual(lastOffset, offset(buffer));
        
        Buffer content = inspectContent(other);
        Assert.areEqual(DATA_2, content.readByte());
    }

    private int offset(MarshallingBuffer buffer) {
        return buffer.testDelegate().offset();
    }
    
    private Buffer inspectContent(MarshallingBuffer buffer) {
        Buffer bufferDelegate = buffer.testDelegate();
        bufferDelegate.offset(0);
        return bufferDelegate;
    }
    
    public void testChildren(){
        MarshallingBuffer buffer = new MarshallingBuffer();
        buffer.writeInt(DATA_1);
        buffer.writeByte(DATA_2);
        
        MarshallingBuffer child = buffer.addChild(true);
        child.writeInt(DATA_3);
        child.writeInt(DATA_4);
        
        buffer.mergeChildren(0);
        
        Buffer content = inspectContent(buffer);
        Assert.areEqual(DATA_1, content.readInt());
        Assert.areEqual(DATA_2, content.readByte());
        
        int address = content.readInt();
        content.offset(address);
        
        Assert.areEqual(DATA_3, content.readInt());
        Assert.areEqual(DATA_4, content.readInt());
    }

    
    public void testGrandChildren(){
        MarshallingBuffer buffer = new MarshallingBuffer();
        buffer.writeInt(DATA_1);
        buffer.writeByte(DATA_2);
        
        MarshallingBuffer child = buffer.addChild(true);
        child.writeInt(DATA_3);
        child.writeInt(DATA_4);
        
        MarshallingBuffer grandChild = child.addChild(true);
        grandChild.writeInt(DATA_5);
        
        buffer.mergeChildren(0);
        
        Buffer content = inspectContent(buffer);
        Assert.areEqual(DATA_1, content.readInt());
        Assert.areEqual(DATA_2, content.readByte());
        
        int address = content.readInt();
        content.offset(address);
        
        Assert.areEqual(DATA_3, content.readInt());
        Assert.areEqual(DATA_4, content.readInt());
        
        address = content.readInt();
        content.offset(address);
        Assert.areEqual(DATA_5, content.readInt());
        
    }
    
    public void testLinkOffset(){
        
        int linkOffset = 7;
        
        MarshallingBuffer buffer = new MarshallingBuffer();
        buffer.writeInt(DATA_1);
        buffer.writeByte(DATA_2);
        
        MarshallingBuffer child = buffer.addChild(true);
        child.writeInt(DATA_3);
        child.writeInt(DATA_4);
        
        MarshallingBuffer grandChild = child.addChild(true);
        grandChild.writeInt(DATA_5);
        
        buffer.mergeChildren(linkOffset);
        
        Buffer content = inspectContent(buffer);
        
        Buffer extendedBuffer = new Buffer(content.length() + linkOffset);
        
        content.copyTo(extendedBuffer, 0, linkOffset, content.length());
        
        extendedBuffer.offset(linkOffset);
        
        Assert.areEqual(DATA_1, extendedBuffer.readInt());
        Assert.areEqual(DATA_2, extendedBuffer.readByte());
        
        int address = extendedBuffer.readInt();
        extendedBuffer.offset(address);
        
        Assert.areEqual(DATA_3, extendedBuffer.readInt());
        Assert.areEqual(DATA_4, extendedBuffer.readInt());
        
        address = extendedBuffer.readInt();
        extendedBuffer.offset(address);
        Assert.areEqual(DATA_5, extendedBuffer.readInt());
        
    }

    
    public void testLateChildrenWrite(){
        MarshallingBuffer buffer = new MarshallingBuffer();
        buffer.writeInt(DATA_1);
        MarshallingBuffer child = buffer.addChild(true);
        child.writeInt(DATA_3);
        buffer.writeByte(DATA_2);
        child.writeInt(DATA_4);
        buffer.mergeChildren(0);
        
        Buffer content = inspectContent(buffer);
        Assert.areEqual(DATA_1, content.readInt());
        
        int address = content.readInt();
        content.readInt();  // length
        
        Assert.areEqual(DATA_2, content.readByte());
        
        content.seek(address);
        Assert.areEqual(DATA_3, content.readInt());
        Assert.areEqual(DATA_4, content.readInt());
        
    }
    

}
