package com.db4o.test.jdk5;

// JDK1.5: static import
import static java.lang.Math.*;
import java.util.*;

// JDK1.5: annotations
@Db4oObjectClass(
		cascadeOnActivate=true,
		cascadeOnUpdate=true,
		maximumActivationDepth=3)
// JDK1.5: generics
public class Data<Item> {
    private Item item;
    // JDK1.5: typesafe enums
    private DataType type;
    // JDK1.5: generics
    private List<Integer> list;
    
    public Data(Item item,DataType type) {
        this.item=item;
        this.type=type;
        list=new ArrayList<Integer>();
    }

    // JDK1.5: varargs
    public void add(int ... is) {
        // JDK1.5: enhanced for with array
        for(int i : is) {
            // JDK1.5: boxing
            list.add(i);
        }
    }
    
    public int getMax() {
        int max=Integer.MIN_VALUE;
        // JDK1.5: enhanced for with collection / unboxing
        for(int i : list) {
            max=max(i,max);
        }
        return max;
    }
    
    public int getSize() {
        return list.size();
    }
    
    public Item getItem() {
        return item;
    }
    
    public DataType getType() {
        return type;
    }
}
