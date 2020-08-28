package org;

import org.util.Condition;

import java.util.Calendar;
import java.util.Date;

public class SimpleConditions {

    public static Condition stopAfterMilliseconds(long millis) {
        return new Condition() {
            boolean isNotInitialised = true;
            long startTime;

            @Override
            public boolean get() {
                if (isNotInitialised) init();
                return System.currentTimeMillis() < startTime;
            }

            private void init() {
                startTime = System.currentTimeMillis() + millis;
                isNotInitialised = false;
            }
        };
    }

    public static Condition stopAfterSeconds(long seconds) {
        return stopAfterMilliseconds(seconds * 1000);
    }

    public static Condition stopAtDate(Date target) {
        return () -> new Date().getTime() < target.getTime();
    }

}
