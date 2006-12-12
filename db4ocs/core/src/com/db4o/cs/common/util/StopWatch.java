package com.db4o.cs.common.util;

/**
 * User: treeder
 * Date: Dec 11, 2006
 * Time: 4:27:51 PM
 */
public class StopWatch extends ThreadLocal{

    protected StopWatchData initialValue() {
        return new StopWatchData();
    }


    public StopWatchData get() {
        return (StopWatchData) super.get();
    }

    public void start() {
        get().start();
    }

    public void stop() {
        get().stop();
    }

    public int count() {
        return get().count();
    }

    public void reset() {
        get().reset();
    }

    public long totalDuration() {
        return get().totalDuration();
    }

    public double average() {
        return get().average();
    }

    class StopWatchData {
        long startTime;
           private long totalDuration;
           private int counter;

        public void start() {
            startTime = System.currentTimeMillis();
        }

        public void stop() {
            long endTime = System.currentTimeMillis();
            totalDuration += (endTime - startTime);
            counter++;
        }

        public void reset() {
            startTime = 0;
            totalDuration = 0;
            counter = 0;
        }

        public double average() {
            if (counter == 0) return 0.0;
            return (1.0 * totalDuration / counter);
        }

        public long totalDuration() {
            return totalDuration;
        }

        public int count() {
            return counter;
        }
    }
}
