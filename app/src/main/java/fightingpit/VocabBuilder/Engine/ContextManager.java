package fightingpit.VocabBuilder.Engine;

import android.content.Context;

/**
 * Created by abhinavgarg on 30/06/16.
 */
public final class ContextManager {

    static Context sMainActivityContext = null;
    static Context sCurrentActivityContext = null;

    public static void setMainActivityContext(Context mainActivityContext) {
        sMainActivityContext = mainActivityContext;
    }

    public static void setCurrentActivityContext(Context currentActivityContext) {
        sCurrentActivityContext = currentActivityContext;
    }

    public static Context getMainActivityContext() {
        return sMainActivityContext;
    }

    public static Context getCurrentActivityContext() {
        return sCurrentActivityContext;
    }
}
