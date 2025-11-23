package client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
class RoundedButton extends JButton {

    private Color normalColor = new Color(200, 230, 190);
    private Color hoverColor  = new Color(180, 210, 170);
    private boolean hover = false;

    public RoundedButton(String text) {
        super(text);

        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);

        setForeground(Color.DARK_GRAY);
        setFont(getFont().deriveFont(Font.BOLD, 14f));

        // hover 감지
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                hover = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        // 부드러운 둥근 모양
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // hover 여부에 따라 배경색 선택
        g2.setColor(hover ? hoverColor : normalColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

        super.paintComponent(g);
    }
}
