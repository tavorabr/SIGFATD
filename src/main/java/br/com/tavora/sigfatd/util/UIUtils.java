package br.com.tavora.sigfatd.util;

import javax.swing.*;
import java.awt.*;

public class UIUtils {

    public static void changeFontSize(Component component, int amount) {
        Font originalFont = component.getFont();
        if (originalFont != null) {
            float newSize = originalFont.getSize() + amount;
            Font newFont = originalFont.deriveFont(newSize);
            component.setFont(newFont);
        }

        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                changeFontSize(child, amount);
            }
        }
    }
}