package com.db4o.db4ounit.common.cs;

import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

public class COR756TestCase extends AbstractDb4oTestCase {

    public static void main(String[] args) {
        new COR756TestCase().runClientServer();
    }

    public static class A {
        public boolean isReferencedBy(B b) {
            return this.equals(b.a);
        }
    }

    public static class B {
        public A a;
    }

    public static class BReferencedFromAPredicate extends Predicate {

        public A _a;

        public BReferencedFromAPredicate(A a) {
            _a = a;
        }

        public boolean match(Object b) {
            return _a.isReferencedBy((B)b);
        }

    }

    public void _test() {
        A a = new A();
        B b = new B();
        b.a = a;
        ObjectContainer oc = db();
        oc.store(b);
        oc.commit();
        Assert.areEqual(1, oc.query(new BReferencedFromAPredicate(a)).size());
    }

}