package me.cdh;

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
    private static File userSelectedFile;
    private static final Logger log = Logger.getLogger("Listener logger");

    void registerMenuItemListener() {
        createFileBtnRegistration();
        openBtnAction();
        closeCurrPageBtnRegistration();
        saveBtnRegistration();
        saveAsBtnRegistration();
        exitBtnAction();
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
            var container = deleteBtnInTabRegistration(defaultTitle);
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
        saveAs.addActionListener(e -> saveFile());
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
        var btn = new JButton(scaleImage("close.png"));
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
        var chooser = new JFileChooser();
        var result = chooser.showSaveDialog(mainUI);
        chooser.setDialogTitle("Save File");
        if (result == JFileChooser.APPROVE_OPTION) {
            var fileToSave = chooser.getSelectedFile();
            var fileName = fileToSave.getName();
            var bufferIndex = tabPane.getSelectedIndex();
            if (bufferIndex != -1 && fileToSave.exists()) {
                var content = bufferList.get(bufferIndex).getText();
                var overwrite = JOptionPane.showConfirmDialog(null, "File is exists,cover it or not?", "Current file", JOptionPane.YES_NO_OPTION);
                if (overwrite == JOptionPane.YES_NO_OPTION) {
                    try (var writer = new FileWriter(fileToSave, true)) {
                        writer.write(content);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    var container = (Container) tabPane.getTabComponentAt(tabPane.getSelectedIndex());
                    var label = (JLabel) container.getComponent(0);
                    label.setText(fileName);
                    labelPopup("Saved");
                } else log.info("Save cancel");
            } else {
                var fileAbsolutePath = fileToSave.getAbsolutePath();
                var file = new File(fileAbsolutePath);
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException("create file fail");
                }
                var content = bufferList.get(bufferIndex).getText();
                try (var writer = new FileWriter(file)) {
                    writer.write(content);
                } catch (IOException e) {
                    throw new RuntimeException("write fail");
                }
            }
        }
    }
}