import javax.swing.JTextArea;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.jar.JarFile;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONArray;

public class FileOrganizer {

    private static final Set<String> KNOWN_CLIENT_SIDE_MOD_IDS = Set.of("modmenu", "litematica", "minimap");

    public static void organizeFiles(File downloadedFiles, JTextArea logArea) {
        String logPrefix = (logArea != null) ? "" : "CLI: ";
        if (logArea != null) logArea.append("Organizing files...\n");
        else System.out.println(logPrefix + "Organizing files...");

        File modsFolder = new File(downloadedFiles, "mods");

        File clientModsFolder = new File(downloadedFiles, "client_mods");
        if (!clientModsFolder.exists()) {
            clientModsFolder.mkdir();
        }

        if (modsFolder.exists() && modsFolder.isDirectory()) {
            for (File modFile : modsFolder.listFiles()) {
                if (isClientSideMod(modFile)) {
                    try {
                        Files.move(modFile.toPath(), clientModsFolder.toPath().resolve(modFile.getName()), StandardCopyOption.REPLACE_EXISTING);
                    } catch (Exception e) {
                        if (logArea != null) logArea.append("Error moving client-side mod: " + modFile.getName() + "\n");
                        else System.err.println(logPrefix + "Error moving client-side mod: " + modFile.getName());
                        e.printStackTrace();
                    }
                }
            }
        }

        if (logArea != null) logArea.append("Files organized.\n");
        else System.out.println(logPrefix + "Files organized.");
    }

    private static boolean isClientSideMod(File modFile) {
        if (KNOWN_CLIENT_SIDE_MOD_IDS.contains(modFile.getName().replace(".jar", "").toLowerCase())) {
            return true;
        }

        try (JarFile jarFile = new JarFile(modFile)) {
            if (jarFile.getEntry("fabric.mod.json") != null) {
                try (InputStream input = jarFile.getInputStream(jarFile.getEntry("fabric.mod.json"))) {
                    JSONObject json = new JSONObject(new JSONTokener(input));
                    return json.has("environment") && "client".equalsIgnoreCase(json.getString("environment"));
                }
            }

            if (jarFile.getEntry("mcmod.info") != null) {
                try (InputStream input = jarFile.getInputStream(jarFile.getEntry("mcmod.info"))) {
                    JSONArray jsonArray = new JSONArray(new JSONTokener(input));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject mod = jsonArray.getJSONObject(i);
                        if (mod.has("clientOnly") && mod.getBoolean("clientOnly")) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
