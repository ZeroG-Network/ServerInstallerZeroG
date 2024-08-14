import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class ModPackInstallerCLI {

    private Scanner scanner = new Scanner(System.in);
    private String selectedPack;
    private String selectedVersion;

    public void run() {
        System.out.println("Modpack Installer CLI");
        try {
            initializeModPacks();
            selectModPack();
            selectVersion();
            startDownloadAndInstall();
            saveUserSettings();
        } finally {
            scanner.close();
        }
    }

    private void initializeModPacks() {
        List<String> packs = CurseForgeAPIHandler.fetchModPacks(
            ModPackInstaller.getConfig("author", "MrWhiteFlamesYT,Itchydingo93").split(",")
        );
        System.out.println("Available Modpacks:");
        for (int i = 0; i < packs.size(); i++) {
            System.out.println((i + 1) + ": " + packs.get(i));
        }
    }

    private void selectModPack() {
        String preSelectPack = ModPackInstaller.getConfig("modpack", null);
        if (preSelectPack != null) {
            selectedPack = preSelectPack;
            System.out.println("Using pre-selected modpack: " + selectedPack);
        } else {
            System.out.print("Select a modpack by number: ");
            int packIndex = Integer.parseInt(scanner.nextLine()) - 1;
            List<String> packs = CurseForgeAPIHandler.fetchModPacks(
                ModPackInstaller.getConfig("author", "MrWhiteFlamesYT,Itchydingo93").split(",")
            );
            selectedPack = packs.get(packIndex);
            System.out.println("Selected modpack: " + selectedPack);
        }
    }

    private void selectVersion() {
        System.out.println("Fetching versions for: " + selectedPack);
        List<String> versions = CurseForgeAPIHandler.fetchModPackVersions(selectedPack);

        String preSelectVersion = ModPackInstaller.getConfig("version", null);
        if (preSelectVersion != null) {
            selectedVersion = preSelectVersion;
            System.out.println("Using pre-selected version: " + selectedVersion);
        } else {
            System.out.println("Available Versions:");
            for (int i = 0; i < versions.size(); i++) {
                System.out.println((i + 1) + ": " + versions.get(i));
            }
            System.out.print("Select a version by number: ");
            int versionIndex = Integer.parseInt(scanner.nextLine()) - 1;
            selectedVersion = versions.get(versionIndex);
            System.out.println("Selected version: " + selectedVersion);
        }
    }

    private void startDownloadAndInstall() {
        try {
            String customInstallPath = ModPackInstaller.getConfig("installPath", null);
            File downloadedFiles = Downloader.downloadPack(selectedPack, selectedVersion, customInstallPath, null);
            if (downloadedFiles == null) {
                System.err.println("Failed to download the modpack. Please check your connection or try again later.");
                return;
            }

            FileOrganizer.organizeFiles(downloadedFiles, null);
            Installer.installServerFiles(downloadedFiles, null);
            System.out.println("Installation completed.");

            runPostInstallationTasks(downloadedFiles.getPath());

        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("An error occurred during the installation process: " + ex.getMessage());
        }
    }

    private void runPostInstallationTasks(String installPath) {
        System.out.println("Running post-installation tasks...");
        PostInstallTaskHandler taskHandler = new PostInstallTaskHandler(null, installPath);
        taskHandler.executeTasks();
    }

    private void saveUserSettings() {
        Properties userSettings = new Properties();
        userSettings.setProperty("modpack", selectedPack);
        userSettings.setProperty("version", selectedVersion);
        String installPath = ModPackInstaller.getConfig("installPath", null);
        if (installPath != null) {
            userSettings.setProperty("installPath", installPath);
        }
        ModPackInstaller.saveSettings(userSettings);
        System.out.println("User settings saved.");
    }
}
