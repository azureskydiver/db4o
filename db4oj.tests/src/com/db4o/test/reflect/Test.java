/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.reflect;

/**
 * @decaf.ignore.jdk11
 */
public class Test {

    static protected void _assert(boolean condition) {
        _assert(condition, "Assertion failed.");
    }

    static protected void _assert(boolean condition, String msg) {
        if (!condition) {
            throw new RuntimeException(msg);
        }
    }

}
