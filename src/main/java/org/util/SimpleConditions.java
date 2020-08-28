package org.util;

import org.util.Condition;

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


}
