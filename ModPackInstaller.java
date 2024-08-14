import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Properties;

public class ModPackInstaller {

    private static Properties config = new Properties();
    private static final String SETTINGS_FILE = "user_settings.properties";
    public static final String LIGHT_THEME = "Light";  // Public to allow access in other classes
    public static final String DARK_THEME = "Dark";    // Public to allow access in other classes
    private static String currentTheme = LIGHT_THEME;

    public static void main(String[] args) {
        loadConfiguration(args);

        if (args.length == 0 || isGuiAvailable()) {
            // If no arguments are provided or GUI is available, run the GUI version
            SwingUtilities.invokeLater(() -> new ModPackInstallerGUI().setVisible(true));
        } else {
            // If arguments are provided or running in a headless environment, run the CLI version
            new ModPackInstallerCLI().run();
        }
    }

    private static boolean isGuiAvailable() {
        return !GraphicsEnvironment.isHeadless();
    }

    private static void loadConfiguration(String[] args) {
        try {
            File configFile = new File("config.properties");
            if (configFile.exists()) {
                try (FileInputStream fis = new FileInputStream(configFile)) {
                    config.load(fis);
                }
            }

            File settingsFile = new File(SETTINGS_FILE);
            if (settingsFile.exists()) {
                try (FileInputStream fis = new FileInputStream(settingsFile)) {
                    config.load(fis);
                }
            }

            for (String arg : args) {
                String[] keyValue = arg.split("=", 2);
                if (keyValue.length == 2) {
                    config.setProperty(keyValue[0].replace("--", ""), keyValue[1]);
                }
            }

            currentTheme = config.getProperty("theme", LIGHT_THEME);
        } catch (Exception e) {
            System.err.println("Failed to load configuration: " + e.getMessage());
        }
    }

    public static String getConfig(String key, String defaultValue) {
        return config.getProperty(key, defaultValue);
    }

    public static void saveSettings(Properties settings) {
        try (FileOutputStream fos = new FileOutputStream(SETTINGS_FILE)) {
            settings.store(fos, "User Preferences");
            System.out.println("Settings saved to " + SETTINGS_FILE);
        } catch (IOException e) {
            System.err.println("Failed to save settings: " + e.getMessage());
        }
    }

    public static void setTheme(String theme) {
        currentTheme = theme;
        config.setProperty("theme", theme);
        saveSettings(config);
    }

    public static String getCurrentTheme() {
        return currentTheme;
    }

    public static String getLightTheme() {
        return LIGHT_THEME;
    }

    public static String getDarkTheme() {
        return DARK_THEME;
    }
}
