import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;

public class ModPackInstallerGUI extends JFrame {

    private JComboBox<String> modPackList;
    private JComboBox<String> versionList;
    private JTextArea logArea;
    private JButton downloadButton;
    private JButton saveLogButton;
    private JButton saveSettingsButton;
    private String selectedPack;
    private String selectedVersion;

    public ModPackInstallerGUI() {
        setTitle("Modpack Installer");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        applyTheme();
        initializeMenuBar();
        initializeComponents();
        initializeModPacks();
    }

    private void applyTheme() {
        String theme = ModPackInstaller.getCurrentTheme();
        if (theme.equals(ModPackInstaller.getLightTheme())) {
            getContentPane().setBackground(Color.WHITE);
        } else if (theme.equals(ModPackInstaller.getDarkTheme())) {
            getContentPane().setBackground(new Color(45, 45, 45));  // Darker gray for dark mode
        }
        updateButtonStyles(theme.equals(ModPackInstaller.getLightTheme()));
    }

    private void initializeMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem saveSettingsMenuItem = new JMenuItem("Save Settings", createSaveIcon());
        saveSettingsMenuItem.setToolTipText("Save the current settings");
        saveSettingsMenuItem.addActionListener(e -> saveUserSettings());
        JMenuItem exitMenuItem = new JMenuItem("Exit", createExitIcon());
        exitMenuItem.setToolTipText("Exit the application");
        exitMenuItem.addActionListener(e -> System.exit(0));
        fileMenu.add(saveSettingsMenuItem);
        fileMenu.add(exitMenuItem);

        JMenu viewMenu = new JMenu("View");
        JMenuItem lightThemeMenuItem = new JMenuItem("Light Theme", createLightThemeIcon());
        lightThemeMenuItem.setToolTipText("Switch to light theme");
        lightThemeMenuItem.addActionListener(e -> {
            ModPackInstaller.setTheme(ModPackInstaller.getLightTheme());
            applyTheme();
            SwingUtilities.updateComponentTreeUI(this);
        });
        JMenuItem darkThemeMenuItem = new JMenuItem("Dark Theme", createDarkThemeIcon());
        darkThemeMenuItem.setToolTipText("Switch to dark theme");
        darkThemeMenuItem.addActionListener(e -> {
            ModPackInstaller.setTheme(ModPackInstaller.getDarkTheme());
            applyTheme();
            SwingUtilities.updateComponentTreeUI(this);
        });
        viewMenu.add(lightThemeMenuItem);
        viewMenu.add(darkThemeMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        setJMenuBar(menuBar);
    }

    private void initializeComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        modPackList = new JComboBox<>();
        modPackList.addActionListener(new ModPackSelectionListener());
        modPackList.setToolTipText("Select a modpack from the list");
        JLabel modPackLabel = new JLabel("Select Modpack:");
        modPackLabel.setFont(new Font("Arial", Font.BOLD, 14));
        modPackLabel.setForeground(getTextColor());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        mainPanel.add(modPackLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.8;
        mainPanel.add(modPackList, gbc);

        versionList = new JComboBox<>();
        versionList.setEnabled(false);
        versionList.setToolTipText("Select the version of the modpack");
        JLabel versionLabel = new JLabel("Select Version:");
        versionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        versionLabel.setForeground(getTextColor());
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        mainPanel.add(versionLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.8;
        mainPanel.add(versionList, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        downloadButton = createStyledButton("Download Pack", createDownloadIcon(), "Start downloading the selected modpack");
        downloadButton.addActionListener(new DownloadButtonListener());
        saveLogButton = createStyledButton("Save Log", createSaveLogIcon(), "Save the log to a file");
        saveLogButton.addActionListener(new SaveLogButtonListener());
        saveSettingsButton = createStyledButton("Save Settings", createSettingsIcon(), "Save your current settings");
        saveSettingsButton.addActionListener(new SaveSettingsButtonListener());
        buttonPanel.add(downloadButton);
        buttonPanel.add(saveLogButton);
        buttonPanel.add(saveSettingsButton);

        logArea = new JTextArea();
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setEditable(false);
        logArea.setForeground(getTextColor());
        logArea.setBackground(getTextAreaBackgroundColor());
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBorder(BorderFactory.createTitledBorder("Log"));

        add(mainPanel, BorderLayout.NORTH);
        add(logScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Icon icon, String toolTip) {
        JButton button = new JButton(text, icon);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
            BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(toolTip);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219));
            }
        });

        button.setUI(new StyledButtonUI());
        return button;
    }

    private void updateButtonStyles(boolean isLightTheme) {
        Color baseColor = isLightTheme ? new Color(52, 152, 219) : new Color(76, 175, 80);
        Color hoverColor = isLightTheme ? new Color(41, 128, 185) : new Color(67, 160, 71);

        for (Component comp : getContentPane().getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                button.setBackground(baseColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(hoverColor, 2),
                    BorderFactory.createEmptyBorder(10, 25, 10, 25)
                ));
            }
        }
    }

    private Color getTextColor() {
        return ModPackInstaller.getCurrentTheme().equals(ModPackInstaller.getLightTheme()) ? Color.BLACK : Color.WHITE;
    }

    private Color getTextAreaBackgroundColor() {
        return ModPackInstaller.getCurrentTheme().equals(ModPackInstaller.getLightTheme()) ? Color.WHITE : new Color(60, 63, 65);
    }

    private Icon createSaveIcon() {
        return createShapeIcon(Color.GREEN, 16, 16, g -> {
            g.fillRect(2, 2, 12, 12);
            g.setColor(Color.WHITE);
            g.fillRect(4, 4, 8, 8);
        });
    }

    private Icon createExitIcon() {
        return createShapeIcon(Color.RED, 16, 16, g -> {
            g.drawLine(2, 2, 14, 14);
            g.drawLine(2, 14, 14, 2);
        });
    }

    private Icon createDownloadIcon() {
        return createShapeIcon(Color.BLUE, 16, 16, g -> {
            g.fillPolygon(new int[]{4, 8, 12}, new int[]{2, 14, 2}, 3);
        });
    }

    private Icon createSaveLogIcon() {
        return createShapeIcon(new Color(255, 165, 0), 16, 16, g -> {
            g.fillRect(2, 2, 12, 12);
            g.setColor(Color.WHITE);
            g.fillRect(4, 4, 8, 8);
        });
    }

    private Icon createSettingsIcon() {
        return createShapeIcon(Color.GRAY, 16, 16, g -> {
            g.fillOval(4, 4, 8, 8);
        });
    }

    private Icon createLightThemeIcon() {
        return createShapeIcon(new Color(255, 223, 0), 16, 16, g -> {
            g.fillOval(4, 4, 8, 8);
        });
    }

    private Icon createDarkThemeIcon() {
        return createShapeIcon(new Color(105, 105, 105), 16, 16, g -> {
            g.fillOval(4, 4, 8, 8);
        });
    }

    private Icon createShapeIcon(Color color, int width, int height, IconPainter painter) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(color);
                g2d.translate(x, y);
                painter.paint(g2d);
                g2d.dispose();
            }

            @Override
            public int getIconWidth() {
                return width;
            }

            @Override
            public int getIconHeight() {
                return height;
            }
        };
    }

    private interface IconPainter {
        void paint(Graphics2D g);
    }

    private void initializeModPacks() {
        List<String> packs = CurseForgeAPIHandler.fetchModPacks(
            ModPackInstaller.getConfig("author", "MrWhiteFlamesYT,Itchydingo93").split(",")
        );
        for (String pack : packs) {
            modPackList.addItem(pack);
        }

        String preSelectPack = ModPackInstaller.getConfig("modpack", null);
        if (preSelectPack != null) {
            modPackList.setSelectedItem(preSelectPack);
            versionList.setEnabled(true);
            List<String> versions = CurseForgeAPIHandler.fetchModPackVersions(preSelectPack);
            for (String version : versions) {
                versionList.addItem(version);
            }
            String preSelectVersion = ModPackInstaller.getConfig("version", null);
            if (preSelectVersion != null) {
                versionList.setSelectedItem(preSelectVersion);
            }
        }
    }

    private class ModPackSelectionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            selectedPack = (String) modPackList.getSelectedItem();
            logArea.append("Fetching versions for: " + selectedPack + "\n");
            versionList.removeAllItems();

            List<String> versions = CurseForgeAPIHandler.fetchModPackVersions(selectedPack);
            for (String version : versions) {
                versionList.addItem(version);
            }

            versionList.setEnabled(true);
        }
    }

    private class DownloadButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            selectedPack = (String) modPackList.getSelectedItem();
            selectedVersion = (String) versionList.getSelectedItem();
            logArea.append("Starting download for pack: " + selectedPack + " (Version: " + selectedVersion + ")\n");

            new Thread(() -> startDownloadAndInstall()).start();
        }
    }

    private void startDownloadAndInstall() {
        try {
            String customInstallPath = ModPackInstaller.getConfig("installPath", null);
            File downloadedFiles = Downloader.downloadPack(selectedPack, selectedVersion, customInstallPath, logArea);
            if (downloadedFiles == null) {
                showRetryDialog("Failed to download the modpack. Please check your connection or try again later.");
                return;
            }

            FileOrganizer.organizeFiles(downloadedFiles, logArea);
            Installer.installServerFiles(downloadedFiles, logArea);
            logArea.append("Installation completed.\n");

            runPostInstallationTasks(downloadedFiles.getPath());

        } catch (Exception ex) {
            ex.printStackTrace();
            showRetryDialog("An error occurred during the installation process: " + ex.getMessage());
        }
    }

    private void runPostInstallationTasks(String installPath) {
        logArea.append("Running post-installation tasks...\n");
        PostInstallTaskHandler taskHandler = new PostInstallTaskHandler(logArea, installPath);
        taskHandler.executeTasks();

        if (ModPackInstaller.getConfig("interactivePostInstall", "false").equalsIgnoreCase("true")) {
            configureEnvironmentVariablesGUI();
            configureAdditionalFilesGUI();
        }
    }

    private void configureEnvironmentVariablesGUI() {
        logArea.append("Configuring environment variables via GUI...\n");
        String envVars = JOptionPane.showInputDialog(this, "Enter environment variables (key=value pairs, separated by commas):", "Environment Variables", JOptionPane.QUESTION_MESSAGE);
        if (envVars != null && !envVars.trim().isEmpty()) {
            String[] variables = envVars.split(",");
            for (String var : variables) {
                String[] keyValue = var.split("=", 2);
                if (keyValue.length == 2) {
                    System.setProperty(keyValue[0], keyValue[1]);
                    logArea.append("Set environment variable: " + keyValue[0] + " = " + keyValue[1] + "\n");
                }
            }
        } else {
            logArea.append("No environment variables configured via GUI.\n");
        }
    }

    private void configureAdditionalFilesGUI() {
        logArea.append("Configuring additional files via GUI...\n");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Additional Configuration File");
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File configFile = fileChooser.getSelectedFile();
            logArea.append("Selected configuration file: " + configFile.getAbsolutePath() + "\n");

            try {
                List<String> lines = Files.readAllLines(configFile.toPath());
                for (String line : lines) {
                    logArea.append("Config: " + line + "\n");
                }
            } catch (IOException e) {
                logArea.append("Failed to read config file: " + e.getMessage() + "\n");
            }
        } else {
            logArea.append("No additional configuration file selected via GUI.\n");
        }
    }

    private void showRetryDialog(String message) {
        SwingUtilities.invokeLater(() -> {
            int result = JOptionPane.showConfirmDialog(this, message + "\nWould you like to retry?", "Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                logArea.append("Retrying...\n");
                startDownloadAndInstall();
            } else {
                logArea.append("Operation canceled by user.\n");
            }
        });
    }

    private void saveUserSettings() {
        Properties userSettings = new Properties();
        userSettings.setProperty("modpack", selectedPack);
        userSettings.setProperty("version", selectedVersion);
        String installPath = ModPackInstaller.getConfig("installPath", null);
        if (installPath != null) {
            userSettings.setProperty("installPath", installPath);
        }
        userSettings.setProperty("theme", ModPackInstaller.getCurrentTheme());
        ModPackInstaller.saveSettings(userSettings);
        logArea.append("User settings saved.\n");
    }

    private class SaveLogButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showSaveDialog(ModPackInstallerGUI.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File logFile = fileChooser.getSelectedFile();
                saveLogToFile(logFile);
            }
        }
    }

    private void saveLogToFile(File logFile) {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(logFile))) {
            writer.write(logArea.getText());
            logArea.append("Log saved to: " + logFile.getAbsolutePath() + "\n");
        } catch (Exception e) {
            showErrorDialog("Failed to save the log file.");
        }
    }

    private void showErrorDialog(String message) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE));
    }

    private class SaveSettingsButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            saveUserSettings();
        }
    }
}

// StyledButtonUI class
class StyledButtonUI extends BasicButtonUI {

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        JButton button = (JButton) c;
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        JButton button = (JButton) c;
        paintBackground(g, button);
        super.paint(g, c);
    }

    private void paintBackground(Graphics g, JButton button) {
        Graphics2D g2d = (Graphics2D) g.create();
        int h = button.getHeight();
        int w = button.getWidth();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color color = button.getModel().isRollover() ? new Color(41, 128, 185) : new Color(52, 152, 219);
        g2d.setColor(color);
        g2d.fillRoundRect(0, 0, w, h, 15, 15);
        g2d.dispose();
    }
}
