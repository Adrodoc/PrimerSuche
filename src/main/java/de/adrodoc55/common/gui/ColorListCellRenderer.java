package de.adrodoc55.common.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ColorListCellRenderer implements ListCellRenderer<Color> {
    private final class ColorCell extends JComponent {
        private static final long serialVersionUID = 1L;
        private Color value;
        private boolean isSelected;

        public ColorCell(Color value, boolean isSelected) {
            super();
            this.value = value;
            this.isSelected = isSelected;
        }

        @Override
        public void paint(Graphics g) {
            if (isSelected) {
                g.setColor(Color.BLUE);
            } else {
                g.setColor(Color.WHITE);
            }
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(value);
            g.fillRect(3, 3, getWidth() - 6, getHeight() - 6);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(20, 20);
        }
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Color> list, Color value, int index,
            boolean isSelected, boolean cellHasFocus) {
        JComponent renderer = new ColorCell(value, isSelected);
        return renderer;
    }
}
