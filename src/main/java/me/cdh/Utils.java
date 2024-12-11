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
    public static final String ENCODING = Charset.defaultCharset().displayName();
    public static final Font textFont = new Font("Cascadia Mono", Font.PLAIN, 16);
    public static final Font tabFont = new Font("Cascadia Mono", Font.PLAIN, 14);
    public static final Font messageBoxFont = new Font("Cascadia Mono", Font.PLAIN, 22);

    public static ImageIcon scaleImage(String res) {
        var icon = new FlatSVGIcon(res, 0.1f).getImage();
        var resizedImg = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        var g = resizedImg.createGraphics();
        g.drawImage(icon, 0, 0, 20, 20, null);
        g.dispose();
        return new ImageIcon(icon);
    }
}