package me.cdh;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import static me.cdh.Main.*;
import static me.cdh.Utils.*;

public enum Registration {
    INSTANCE;
    static File userSelectedFile;
    private static final Logger log = Logger.getLogger("Listener logger");

    void registerMenuItemListener() {
        createFileBtnRegistration();
        openBtnAction();
        closeCurrPageBtnRegistration();
        saveBtnRegistration();
        saveAsBtnRegistration();
        exitBtnAction();
        themeChange();
    }

    static void themeChange() {
        dark.addActionListener(e -> EventQueue.invokeLater(() -> {
            FlatAnimatedLafChange.showSnapshot();
            FlatMacDarkLaf.setup();
            FlatLaf.updateUI();
            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        }));
        light.addActionListener(e -> EventQueue.invokeLater(() -> {
            FlatAnimatedLafChange.showSnapshot();
            FlatMacLightLaf.setup();
            FlatLaf.updateUI();
            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        }));
    }

    static void labelPopup(String message) {
        var messageLabel = new JLabel(message);
        messageLabel.setFont(Utils.messageBoxFont);
        var windowBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        var frame = new JFrame() {{
            setUndecorated(true);
            setAlwaysOnTop(true);
            setResizable(false);
            setLayout(new BorderLayout());
            var panel = new JPanel() {{
                setBorder(new EtchedBorder());
                add(messageLabel, BorderLayout.CENTER);
            }};
            var x = 2 * windowBounds.width / 5;
            var y = 3 * windowBounds.height / 4;
            setSize(300, 40);
            add(panel);
            setVisible(true);
            setLocation(x, y);
        }};
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                frame.dispose();
            }
        }, 2000L, 1L);
    }

    static void exitOrNot() {
        for (int i = 0; i < tabPane.getTabCount(); i++) {
            if (!bufferList.get(i).getText().isBlank()) {
                var result = JOptionPane.showConfirmDialog(null, "Are you sure to exit?", "Confirm Exit", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_NO_OPTION) System.exit(0);
            } else System.exit(0);
        }
    }

    static void exitBtnAction() {
        exit.addActionListener(e -> exitOrNot());
    }

    static void createFileBtnRegistration() {
        newFile.addActionListener(e -> {
            var container = deleteBtnInTabRegistration(DEFAULT_TITLE);
            tabPane.addTab(null, new JScrollPane(new EditArea()));
            var index = tabPane.getSelectedIndex();
            if (index != -1) {
                tabPane.setTabComponentAt(bufferList.size(), container);
                bufferList.add(new EditArea());
                tabPane.setSelectedIndex(index + 1);
            }
        });
    }

    static void openBtnAction() {
        open.addActionListener(e -> {
            var chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            var currentTextArea = new EditArea();
            lb:
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                userSelectedFile = chooser.getSelectedFile();
                tabPane.addTab(null, new JScrollPane(currentTextArea));
                bufferList.add(currentTextArea);
                for (int i = 0; i < tabPane.getTabCount(); i++) {
                    if (tabPane.getTitleAt(i).equals(chooser.getSelectedFile().getName())) {
                        labelPopup("File is already open!");
                        break lb;
                    }
                }
                log.info(userSelectedFile.getName());
                var lastIndex = bufferList.size() - 1;
                var container = deleteBtnInTabRegistration(userSelectedFile.getName());
                tabPane.setTabComponentAt(lastIndex, container);
                tabPane.setSelectedIndex(lastIndex);
                tabPane.setTitleAt(lastIndex, userSelectedFile.getName());
                var curIndex = tabPane.getSelectedIndex();
                try (var reader = new BufferedReader(new FileReader(userSelectedFile))) {
                    String line;
                    var content = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        content.append(line);
                        content.append("\n");
                        currentTextArea.append(line);
                        currentTextArea.append("\n");
                    }
                    bufferList.get(curIndex).setText(content.toString());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                tabPane.setSelectedIndex(curIndex);
            } else log.info("Open cancel");
        });
    }

    static void saveBtnRegistration() {
        save.addActionListener(e -> {
            if (userSelectedFile == null) {
                saveFile();
            } else {
                var index = tabPane.getSelectedIndex();
                if (index != -1) {
                    var bufferContent = bufferList.get(index).getText();
                    try (var writer = new FileWriter(userSelectedFile)) {
                        writer.write(bufferContent);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    labelPopup("Saved!");
                } else log.info("Save cancel");
            }
        });
    }

    static void saveAsBtnRegistration() {
        saveAs.addActionListener(e -> saveAsFile());
    }

    static void closeCurrPageBtnRegistration() {
        closeCurrPage.addActionListener(e -> {
            var selectedIndex = tabPane.getSelectedIndex();
            if (selectedIndex != -1 && bufferList.size() > 1) {
                tabPane.removeTabAt(selectedIndex);
                bufferList.remove(selectedIndex);
            } else log.info("Do not close buffer");
        });
    }

    static Container deleteBtnInTabRegistration(String label) {
        var btn = new JButton(scaleImage("me/cdh/img/close.svg"));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        var container = new Container() {{
            setSize(100, 20);
            setLayout(new BorderLayout());
            add(new JLabel(label) {{
                setFont(tabFont);
            }}, BorderLayout.CENTER);
            add(btn, BorderLayout.EAST);
        }};
        btn.addActionListener(e -> {
            var index = tabPane.indexOfTabComponent(container);
            if (index >= 0 && bufferList.size() > 1) {
                var tabIndex = tabPane.indexOfTab(tabPane.getTitleAt(index));
                tabPane.removeTabAt(tabIndex);
                bufferList.remove(tabIndex);
            } else log.info("Do not close tab");
        });
        return container;
    }

    static void saveFile() {
        var idx = tabPane.getSelectedIndex();
        if (idx != -1 && userSelectedFile != null && userSelectedFile.isFile() && userSelectedFile.getAbsoluteFile().exists()) {
            var filePath = userSelectedFile.getAbsolutePath();
            var contentToWrite = bufferList.get(idx).getText();
            try (var writer = new FileWriter(filePath)) {
                writer.write(contentToWrite);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else saveAsFile();
    }

    static void saveAsFile() {
        var chooser = new JFileChooser() {{
            setDialogTitle("Save File");
        }};
        var result = chooser.showSaveDialog(mainUI);
        if (result == JFileChooser.APPROVE_OPTION) {
            var fileToSave = chooser.getSelectedFile();
            var fileName = fileToSave.getName();
            var idx = tabPane.getSelectedIndex();
            if (idx != -1 && !fileToSave.exists()) {
                var content = bufferList.get(idx).getText();
                try (var writer = new FileWriter(fileToSave)) {
                    writer.write(content);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (idx != -1 && fileToSave.exists()) {
                var option = JOptionPane.showConfirmDialog(null, "File is exist, cover it or not?", "Current file", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    var content = bufferList.get(idx).getText();
                    try (var writer = new FileWriter(fileToSave)) {
                        writer.write(content);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else log.info("Save cancel");
            }
        }
    }
}