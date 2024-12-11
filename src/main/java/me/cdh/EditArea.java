package me.cdh;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static me.cdh.Utils.*;

public final class EditArea extends JTextArea {

    private final JMenuItem cut, copy, paste;
    private final JPopupMenu rightClickMenu;

    public EditArea() {
        cut = new JMenuItem("Cut") {{
            addActionListener(e -> cut());
        }};
        copy = new JMenuItem("Copy") {{
            addActionListener(e -> copy());
        }};
        paste = new JMenuItem("Paste") {{
            addActionListener(e -> paste());
        }};
        rightClickMenu = new JPopupMenu() {{
            add(cut);
            add(copy);
            add(paste);
        }};
        setLineWrap(true);
        setWrapStyleWord(true);
        setTabSize(4);
        setFont(textFont);
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                Main.lineDisplay.setText("Total Lines: " + getLineCount());
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
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

    public void highLightText(int pos, int textLen) {
        if (textLen < 0) throw new IllegalArgumentException("text length should greater than 0!");
        var painter = new DefaultHighlighter.DefaultHighlightPainter(Color.orange);
        var highlighter = getHighlighter();
        try {
            var textToHighLighter = getText(pos, textLen);
            int position = 0;
            while ((position = getText().indexOf(textToHighLighter, position)) != -1) {
                highlighter.addHighlight(position, position + textToHighLighter.length(), painter);
                position += textToHighLighter.length();
            }
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }
}