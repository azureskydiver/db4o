/* UnifyHash - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.util;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class UnifyHash extends AbstractCollection
{
    private static final int DEFAULT_CAPACITY = 11;
    private static final float DEFAULT_LOAD_FACTOR = 0.75F;
    private ReferenceQueue queue = new ReferenceQueue();
    private Bucket[] buckets;
    int modCount = 0;
    int size = 0;
    int threshold;
    float loadFactor;
    
    static class Bucket extends WeakReference
    {
	int hash;
	Bucket next;
	
	public Bucket(Object object, ReferenceQueue referencequeue) {
	    super(object, referencequeue);
	}
    }
    
    public UnifyHash(int i, float f) {
	loadFactor = f;
	buckets = new Bucket[i];
	threshold = (int) (f * (float) i);
    }
    
    public UnifyHash(int i) {
	this(i, 0.75F);
    }
    
    public UnifyHash() {
	this(11, 0.75F);
    }
    
    private void grow() {
	Bucket[] buckets = this.buckets;
	int i = this.buckets.length * 2 + 1;
	threshold = (int) (loadFactor * (float) i);
	this.buckets = new Bucket[i];
	for (int i_0_ = 0; i_0_ < buckets.length; i_0_++) {
	    Bucket bucket_1_;
	    for (Bucket bucket = buckets[i_0_]; bucket != null;
		 bucket = bucket_1_) {
		if (i_0_ != Math.abs(bucket.hash % buckets.length))
		    throw new RuntimeException("" + i_0_ + ", hash: "
					       + bucket.hash + ", oldlength: "
					       + buckets.length);
		int i_2_ = Math.abs(bucket.hash % i);
		bucket_1_ = bucket.next;
		bucket.next = this.buckets[i_2_];
		this.buckets[i_2_] = bucket;
	    }
	}
    }
    
    public final void cleanUp() {
	Bucket bucket;
	while ((bucket = (Bucket) queue.poll()) != null) {
	    int i = Math.abs(bucket.hash % buckets.length);
	    if (buckets[i] == bucket)
		buckets[i] = bucket.next;
	    else {
		Bucket bucket_3_;
		for (bucket_3_ = buckets[i]; bucket_3_.next != bucket;
		     bucket_3_ = bucket_3_.next) {
		    /* empty */
		}
		bucket_3_.next = bucket.next;
	    }
	    size--;
	}
    }
    
    public int size() {
	return size;
    }
    
    public Iterator iterator() {
	cleanUp();
	return new Iterator() {
	    private int bucket = 0;
	    private int known = modCount;
	    private Bucket nextBucket;
	    private Object nextVal;
	    
	    {
		internalNext();
	    }
	    
	    private void internalNext() {
		for (;;) {
		    if (nextBucket == null) {
			if (bucket == buckets.length)
			    break;
			nextBucket = buckets[bucket++];
		    } else {
			nextVal = nextBucket.get();
			if (nextVal != null)
			    break;
			nextBucket = nextBucket.next;
		    }
		}
	    }
	    
	    public boolean hasNext() {
		return nextBucket != null;
	    }
	    
	    public Object next() {
		if (known != modCount)
		    throw new ConcurrentModificationException();
		if (nextBucket == null)
		    throw new NoSuchElementException();
		Object object = nextVal;
		nextBucket = nextBucket.next;
		internalNext();
		return object;
	    }
	    
	    public void remove() {
		throw new UnsupportedOperationException();
	    }
	};
    }
    
    public Iterator iterateHashCode(final int hash) {
	cleanUp();
	return new Iterator() {
	    private int known = modCount;
	    private Bucket nextBucket
		= buckets[Math.abs(hash % buckets.length)];
	    private Object nextVal;
	    
	    {
		internalNext();
	    }
	    
	    private void internalNext() {
		for (/**/; nextBucket != null; nextBucket = nextBucket.next) {
		    if (nextBucket.hash == hash) {
			nextVal = nextBucket.get();
			if (nextVal != null)
			    break;
		    }
		}
	    }
	    
	    public boolean hasNext() {
		return nextBucket != null;
	    }
	    
	    public Object next() {
		if (known != modCount)
		    throw new ConcurrentModificationException();
		if (nextBucket == null)
		    throw new NoSuchElementException();
		Object object = nextVal;
		nextBucket = nextBucket.next;
		internalNext();
		return object;
	    }
	    
	    public void remove() {
		throw new UnsupportedOperationException();
	    }
	};
    }
    
    public void put(int i, Object object) {
	if (size++ > threshold)
	    grow();
	modCount++;
	int i_6_ = Math.abs(i % buckets.length);
	Bucket bucket = new Bucket(object, queue);
	bucket.hash = i;
	bucket.next = buckets[i_6_];
	buckets[i_6_] = bucket;
    }
    
    public Object unify(Object object, int i, Comparator comparator) {
	cleanUp();
	int i_7_ = Math.abs(i % buckets.length);
	for (Bucket bucket = buckets[i_7_]; bucket != null;
	     bucket = bucket.next) {
	    Object object_8_ = bucket.get();
	    if (object_8_ != null
		&& comparator.compare(object, object_8_) == 0)
		return object_8_;
	}
	put(i, object);
	return object;
    }
}
