package com.db4odoc.tutorial.utils;

/**
 * @author roman.stoffel@gamlor.info
 * @since 26.07.2010
 */
public interface OneArgAction<T> {
    void invoke(T arg);
}
