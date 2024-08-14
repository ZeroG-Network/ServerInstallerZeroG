import javax.swing.JTextArea;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Installer {

    private static final String SERVER_INSTALLER_URL_TEMPLATE = "https://some-url.com/server-installer/%s";

    public static void installServerFiles(File downloadedFiles, JTextArea logArea) {
        String logPrefix = (logArea != null) ? "" : "CLI: ";
        if (logArea != null) logArea.append("Installing server files...\n");
        else System.out.println(logPrefix + "Installing server files...");

        try {
            String modLoaderVersion = "1.18.2"; // This should be dynamically determined based on the modpack

            URI uri = new URI(String.format(SERVER_INSTALLER_URL_TEMPLATE, modLoaderVersion));
            URL installerUrl = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) installerUrl.openConnection();
            InputStream inputStream = connection.getInputStream();

            Path serverDirectory = Paths.get(downloadedFiles.getPath(), "server");
            Files.createDirectories(serverDirectory);
            Path installerFilePath = serverDirectory.resolve("server-installer.jar");

            try (FileOutputStream outputStream = new FileOutputStream(installerFilePath.toFile())) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            Process process = new ProcessBuilder("java", "-jar", installerFilePath.toString(), "--installServer")
                    .directory(serverDirectory.toFile())
                    .start();
            process.waitFor();

            if (logArea != null) logArea.append("Server files installed.\n");
            else System.out.println(logPrefix + "Server files installed.");

        } catch (Exception e) {
            if (logArea != null) logArea.append("Error installing server files.\n");
            else System.err.println(logPrefix + "Error installing server files.");
            e.printStackTrace();
        }
    }
}
