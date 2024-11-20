package me.cdh;

import static me.cdh.Utils.defaultTitle;
import static me.cdh.Utils.scaleImage;
import static me.cdh.Utils.tabFont;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;

public final class Main {

    static final JFrame mainUI;

    static final JMenuBar menuBar;
    static final JMenu menu;
    static final JMenu settings;

    static final JMenuItem newFile;
    static final JMenuItem open;
    static final JMenuItem closeCurrPage;
    static final JMenuItem searchAndReplace;
    static final JMenuItem save;
    static final JMenuItem saveAs;
    static final JMenuItem exit;

    static final JTabbedPane tabPane;
    static final EditArea textArea;

    static final List<EditArea> bufferList;
    static final JScrollPane displayTextPane;

    static final JLabel lineDisplay;
    static final JButton indentBtn;
    static final JPanel statusBar;

    static {
        FlatMacDarkLaf.setup();
        mainUI = new JFrame();
        menuBar = new JMenuBar();
        menu = new JMenu();
        menu.setIcon(scaleImage("menu.png"));
        settings = new JMenu("Settings");
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
        tabPane.addTab(defaultTitle, displayTextPane);
        tabPane.setTabComponentAt(0, new Container() {
            {
                setLayout(new BorderLayout());
                add(new JLabel(defaultTitle) {
                    {
                        setSize(150, 20);
                        setFont(tabFont);
                    }
                }, BorderLayout.CENTER);
                add(new JButton(scaleImage("close.png")) {
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
        statusBar = new JPanel() {
            {
                setLayout(new BorderLayout());
                var dimension = new Dimension(getPreferredSize());
                dimension.height = 18;
                setPreferredSize(dimension);
                add(lineDisplay, BorderLayout.WEST);
                add(indentBtn, BorderLayout.EAST);
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
    }
}
