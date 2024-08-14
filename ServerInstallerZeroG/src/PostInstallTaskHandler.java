import javax.swing.JTextArea;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarFile;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class PostInstallTaskHandler {

    private JTextArea logArea;
    private String installPath;

    public PostInstallTaskHandler(JTextArea logArea, String installPath) {
        this.logArea = logArea;
        this.installPath = installPath;
    }

    public void executeTasks() {
        configureEnvironmentVariables();
        configureAdditionalFiles();
        runCustomScripts();
    }

    private void log(String message) {
        if (logArea != null) {
            logArea.append(message + "\n");
        } else {
            System.out.println(message);
        }
    }

    private void configureEnvironmentVariables() {
        log("Configuring environment variables...");
        String envVars = ModPackInstaller.getConfig("envVariables", null);
        if (envVars != null) {
            String[] variables = envVars.split(",");
            for (String var : variables) {
                String[] keyValue = var.split("=", 2);
                if (keyValue.length == 2) {
                    System.setProperty(keyValue[0], keyValue[1]);
                    log("Set environment variable: " + keyValue[0] + " = " + keyValue[1]);
                }
            }
        } else {
            log("No environment variables to configure.");
        }
    }

    private void configureAdditionalFiles() {
        log("Configuring additional files...");
        String configFilePath = ModPackInstaller.getConfig("additionalConfigFile", null);
        if (configFilePath != null) {
            Path configPath = Paths.get(installPath, configFilePath);
            if (Files.exists(configPath)) {
                try {
                    List<String> lines = Files.readAllLines(configPath);
                    for (String line : lines) {
                        log("Config: " + line);
                    }
                    // Apply any necessary changes to the config file
                } catch (Exception e) {
                    log("Failed to read config file: " + e.getMessage());
                }
            } else {
                log("Config file not found: " + configFilePath);
            }
        } else {
            log("No additional config files to configure.");
        }
    }

    private void runCustomScripts() {
        log("Running custom post-installation scripts...");
        String scriptPath = ModPackInstaller.getConfig("postInstallScript", null);
        if (scriptPath != null) {
            runScript(scriptPath, installPath);
        } else {
            log("No custom scripts to run.");
        }
    }

    private void runScript(String scriptPath, String workingDirectory) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(scriptPath);
            processBuilder.directory(new File(workingDirectory));

            if (!Files.exists(Paths.get(scriptPath))) {
                log("Post-installation script not found: " + scriptPath);
                return;
            }

            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            log("Post-installation script completed with exit code: " + exitCode);
        } catch (Exception e) {
            log("Failed to run post-installation script: " + e.getMessage());
        }
    }
}
