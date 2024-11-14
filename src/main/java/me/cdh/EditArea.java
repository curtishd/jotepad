package me.cdh;

import javax.swing.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static me.cdh.Utils.*;

public final class EditArea extends JTextArea {

    private final JMenuItem cut;
    private final JMenuItem copy;
    private final JMenuItem paste;
    private final JPopupMenu rightClickMenu;
    
    {
        cut = new JMenuItem("Cut") {{
            addActionListener(e -> EditArea.super.cut());
        }};
        copy = new JMenuItem("Copy") {{
            addActionListener(e -> EditArea.super.copy());
        }};
        paste = new JMenuItem("Paste") {{
            addActionListener(e -> EditArea.super.paste());
        }};
        rightClickMenu = new JPopupMenu() {{
            add(cut);
            add(copy);
            add(paste);
        }};
    }

    public EditArea() {
        setLineWrap(true);
        setWrapStyleWord(true);
        setTabSize(4);
        setFont(contentFont);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Main.lineDisplay.setText("Total Lines: " + getLineCount());
                if (e.getButton() == MouseEvent.BUTTON3) {
                    showPopupMenu(e);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                showPopupMenu(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopupMenu(e);
            }
        });
    }

    private void showPopupMenu(MouseEvent e) {
        if (e.isPopupTrigger()) {
            rightClickMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}
