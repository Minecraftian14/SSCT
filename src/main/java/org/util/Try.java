package org.util;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class Try {

    Catcher catcher;

    public Try(Catcher catcher) {
        this.catcher = catcher;
    }

    public void Catch(Runner runner) {
        try {
            runner.run();
        } catch (Exception e) {
            catcher.Catch(e);
        }
    }

    public <T> T CatchAndReturn(Giver<T> giver, T ifFail) {
        try {
            return giver.run();
        } catch (Exception e) {
            catcher.Catch(e);
        }
        return ifFail;
    }

    public static void JustCatch(Runner runner) {
        try {
            runner.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T JustCatchAndReturn(Giver<T> runner, T ifFail) {
        try {
            return runner.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ifFail;
    }

    public interface Runner {
        void run() throws Exception;
    }

    public interface Giver<T> {
        T run() throws Exception;
    }

    public interface Catcher {
        void Catch(Exception e);
    }
}
