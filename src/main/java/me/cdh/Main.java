package me.cdh;

import static me.cdh.Utils.DEFAULT_TITLE;
import static me.cdh.Utils.scaleImage;
import static me.cdh.Utils.tabFont;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;

public final class Main {

    public static final JFrame mainUI;
    public static final JMenuBar menuBar;
    public static final JMenu menu, settings, theme;
    public static final JRadioButtonMenuItem light, dark;

    public static final JMenuItem newFile, open, closeCurrPage, searchAndReplace, save, saveAs, exit;

    public static final JTabbedPane tabPane;
    public static final EditArea textArea;

    public static final List<EditArea> bufferList;
    public static final JScrollPane displayTextPane;

    public static final JLabel lineDisplay;
    public static final JButton indentBtn;
    public static final JPanel statusBar;

    static {
        FlatMacDarkLaf.setup();
        mainUI = new JFrame();
        menuBar = new JMenuBar();
        menu = new JMenu();
        menu.setIcon(scaleImage("me/cdh/img/menu.svg"));
        settings = new JMenu("Settings");
        theme = new JMenu("Theme");
        light = new JRadioButtonMenuItem("Light");
        dark = new JRadioButtonMenuItem("Dark");
        dark.doClick();
        var bGroup = new ButtonGroup();
        bGroup.add(light);
        bGroup.add(dark);
        newFile = new JMenuItem("New");
        open = new JMenuItem("Open");
        closeCurrPage = new JMenuItem("Close Current Page");
        save = new JMenuItem("Save");
        saveAs = new JMenuItem("Save As");
        exit = new JMenuItem("Exit");
        bufferList = new ArrayList<>();
        bufferList.add(new EditArea());
        textArea = new EditArea();
        tabPane = new JTabbedPane(JTabbedPane.TOP);
        displayTextPane = new JScrollPane(bufferList.getFirst());
        tabPane.addTab(DEFAULT_TITLE, displayTextPane);
        tabPane.setTabComponentAt(0, new Container() {
            {
                setLayout(new BorderLayout());
                add(new JLabel(DEFAULT_TITLE) {
                    {
                        setSize(150, 20);
                        setFont(tabFont);
                    }
                }, BorderLayout.CENTER);
                add(new JButton(scaleImage("me/cdh/img/close.svg")) {
                    {
                        setFocusPainted(false);
                        setContentAreaFilled(false);
                    }
                }, BorderLayout.EAST);
            }
        });
        lineDisplay = new JLabel("Total Lines: 1");
        indentBtn = new JButton("4 spaces") {
            {
                setContentAreaFilled(false);
                setBorderPainted(false);
                var two = new JMenuItem("2 spaces");
                var four = new JMenuItem("4 spaces");
                two.addActionListener(e -> {
                    var index = tabPane.getSelectedIndex();
                    if (index != -1) {
                        setText(two.getText());
                        bufferList.get(index).setTabSize(2);
                    }
                });
                four.addActionListener(e -> {
                    var index = tabPane.getSelectedIndex();
                    if (index != -1) {
                        setText(four.getText());
                        bufferList.get(index).setTabSize(4);
                    }
                });
                var pop = new JPopupMenu() {
                    {
                        add(two);
                        add(four);
                    }
                };
                addActionListener(e -> pop.show(this, 0, getHeight()));
            }
        };
        var statusBarBox = Box.createHorizontalBox();
        statusBarBox.add(new JLabel(Utils.ENCODING));
        statusBarBox.add(indentBtn);
        statusBar = new JPanel() {
            {
                setLayout(new BorderLayout());
                var dimension = new Dimension(getPreferredSize());
                dimension.height = 18;
                setPreferredSize(dimension);
                add(lineDisplay, BorderLayout.WEST);
                add(statusBarBox, BorderLayout.EAST);
            }
        };
        searchAndReplace = new JMenuItem();
    }

    static void initFrame() {
        mainUI.setSize(1000, 800);
        mainUI.setLocationRelativeTo(null);
        mainUI.setVisible(true);
        mainUI.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        mainUI.setLayout(new BorderLayout());
        mainUI.add(tabPane, BorderLayout.CENTER);
        mainUI.add(statusBar, BorderLayout.SOUTH);
    }

    static void initMenuBar() {
        menu.add(newFile);
        menu.add(open);
        menu.add(settings);
        settings.add(theme);
        theme.add(dark);
        theme.add(light);
        menu.addSeparator();
        menu.add(save);
        menu.add(saveAs);
        menu.addSeparator();
        menu.add(closeCurrPage);
        menu.add(exit);
        menuBar.add(menu);
    }

    static void init() {
        initFrame();
        initMenuBar();
        mainUI.setJMenuBar(menuBar);
        Registration.INSTANCE.registerMenuItemListener();
        mainUI.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                Registration.exitOrNot();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::init);
        if (args.length > 0 && !args[0].isBlank()) {
            var filePath = Path.of(args[0]).toAbsolutePath();
            var buildStr = new StringBuilder();
            var container = (Container) tabPane.getTabComponentAt(tabPane.getSelectedIndex());
            var label = (JLabel) container.getComponent(0);
            label.setText(filePath.toFile().getName());
            try (var reader = Files.newBufferedReader(filePath)) {
                reader.lines().forEach(str -> {
                    buildStr.append(str);
                    buildStr.append("\n");
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            bufferList.getFirst().setText(buildStr.toString());
        }
    }
}