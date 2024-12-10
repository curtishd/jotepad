package me.cdh;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;

public enum Utils {
    ;
    public static final String DEFAULT_TITLE = "New File";
    private static final String REGULAR_FONT = "/me/cdh/font/HackNerdFont-Regular.ttf";
    private static final String ITALIC_FONT = "/me/cdh/font/HackNerdFont-Italic.ttf";
    private static final String BOLD_FONT = "/me/cdh/font/HackNerdFont-Bold.ttf";
    public static final String ENCODING = Charset.defaultCharset().displayName();
    public static final Font messageBoxFont;
    public static final Font tabFont;
    public static final Font textFont;

    static {
        try {
            textFont = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(Utils.class.getResourceAsStream(REGULAR_FONT)))
                    .deriveFont(14f);
            tabFont = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(Utils.class.getResourceAsStream(ITALIC_FONT)))
                    .deriveFont(12f);
            messageBoxFont = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(Utils.class.getResourceAsStream(BOLD_FONT)))
                    .deriveFont(22f);
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ImageIcon scaleImage(String res) {
        var icon = new FlatSVGIcon(res, 0.1f).getImage();
        var resizedImg = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        var g = resizedImg.createGraphics();
        g.drawImage(icon, 0, 0, 20, 20, null);
        g.dispose();
        return new ImageIcon(icon);
    }
}