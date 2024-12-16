package toolkit;

import javafx.application.Platform;

public class JavaFXTestUtils {

    private static boolean toolkitInitialized = false;

    // java toolkit for java application thread
    public static void initializeToolkit() {
        if (!toolkitInitialized) {
            Platform.startup(() -> {});
            toolkitInitialized = true;
        }
    }
}
