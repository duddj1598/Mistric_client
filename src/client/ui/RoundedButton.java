package client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * RoundedButton
 * ------------------------------
 * - JButton을 상속받아 "둥근 모서리 + 커스텀 배경" 버튼을 만든 클래스.
 * - Swing 기본 버튼 스타일을 제거하고 직접 그리는 방식으로 UI를 만든다.
 * - Hover(마우스 오버) 시 색상 변경 기능도 포함한다.
 */
class RoundedButton extends JButton {

    // 기본 배경색 (연한 녹색 톤)
    private Color background = new Color(200,230,190);

    // 마우스 오버 시 배경색
    private Color hover = new Color(180,210,170);

    /**
     * 생성자
     * @param text 버튼에 표시할 텍스트
     */
    public RoundedButton(String text) {
        super(text);

        // Swing의 기본 버튼 배경 채우기 기능 OFF → 직접 그릴 예정
        setContentAreaFilled(false);

        // 포커스 시 테두리 표시 제거
        setFocusPainted(false);

        // 일반 버튼의 외곽선/테두리 제거
        setBorderPainted(false);

        // 배경을 직접 그릴 것이므로 Opaque(false)
        setOpaque(false);

        // 텍스트 색상
        setForeground(Color.DARK_GRAY);

        // 버튼 폰트 크기 약간 크게
        setFont(getFont().deriveFont(Font.BOLD, 14f));

        // 버튼 내부 여백(padding)
        setMargin(new Insets(8,12,8,12));

        // 마우스 오버 색상 변경 이벤트 처리
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(hover);  // hover 색 적용
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(new Color(200,230,190));  // 기본색 복구
            }
        });
    }

    /**
     * paintComponent(Graphics g)
     * ------------------------------
     * Swing이 버튼을 그릴 때 호출되는 메서드를 오버라이드한다.
     * 여기서 우리는 기본 버튼 대신,
     * 1) 둥근 사각형을 배경으로 직접 그린 후
     * 2) 텍스트는 super.paintComponent(g)로 그린다.
     */
    @Override
    public void paintComponent(Graphics g) {

        // Graphics2D로 업캐스팅 (더 많은 기능 사용 가능)
        Graphics2D g2 = (Graphics2D) g.create();

        // 안티알리아싱 → 둥근 모서리가 부드럽게 표현됨
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 현재 버튼 배경색으로 둥근 사각형 그리기
        g2.setColor(background);
        g2.fill(new RoundRectangle2D.Float(
                0, 0, getWidth(), getHeight(),
                16, 16  // 둥근 정도(arcWidth, arcHeight)
        ));

        // 텍스트 등 기본 구성은 원래 JButton 방식으로 그리기
        super.paintComponent(g2);

        // Graphics 자원 해제
        g2.dispose();
    }

    /**
     * setBackground(Color bg)
     * ------------------------------
     * - JButton의 기본 setBackground를 override하여
     *   우리가 사용하는 background 변수에 적용한다.
     * - repaint() 호출로 즉시 화면 갱신.
     */
    @Override
    public void setBackground(Color bg) {
        this.background = bg;
        repaint();  // 색상 변경 즉시 반영
    }
}
