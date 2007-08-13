/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.convert.conversions.*;


/**
 * temporary code to be able to factor around ObjectMarshaller code 
 * 
 * @exclude
 */
public class MarshallingSpike {
    
    public static final boolean enabled = false;

    public static MarshallerFamily[] marshallerFamily() {
        
        return new MarshallerFamily[] {
            
            new MarshallerFamily(
                0,
                new ArrayMarshaller0(),
                new ClassMarshaller0(),
                new FieldMarshaller0(),
                new ObjectMarshaller0(), 
                new PrimitiveMarshaller0(),
                new StringMarshaller0(),
                new UntypedMarshaller0()),
            
            new MarshallerFamily(
                ClassIndexesToBTrees_5_5.VERSION,
                new ArrayMarshaller1(),
                new ClassMarshaller1(),
                new FieldMarshaller0(),
                new ObjectMarshaller1(), 
                new PrimitiveMarshaller1(),
                new StringMarshaller1(),
                new UntypedMarshaller1()),
        
            new MarshallerFamily(
                FieldIndexesToBTrees_5_7.VERSION,
                new ArrayMarshaller1(),
                new ClassMarshaller2(),
                new FieldMarshaller1(),
                new ObjectMarshaller1(), 
                new PrimitiveMarshaller1(),
                new StringMarshaller1(),
                new UntypedMarshaller1()),

            
            new MarshallerFamily(
                7,
                new ArrayMarshaller1(),
                new ClassMarshaller2(),
                new FieldMarshaller1(),
                new ObjectMarshallerSpike(), 
                new PrimitiveMarshaller1(),
                new StringMarshaller1(),
                new UntypedMarshaller1()),
        };
    }

    public static int familyVersion() {
        return 3;
    }

}
