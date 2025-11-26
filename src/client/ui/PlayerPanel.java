package client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

class PlayerPanel extends JPanel {

    public PlayerPanel(String name, boolean isHost) {
        setLayout(new BorderLayout());
        setBackground(new Color(250,250,250));
        setBorder(BorderFactory.createCompoundBorder( // 두 Border를 합쳐서 테두리 + 패딩 공간을 동시에 적용
                BorderFactory.createLineBorder(new Color(200,200,200)),
                // 바깥쪽 테두리: 연한 회색 라인

                BorderFactory.createEmptyBorder(10,10,10,10)
                // 안쪽 여백: (위, 왼쪽, 아래, 오른쪽) 모두 10px
        ));
        JLabel icon = new JLabel("", SwingConstants.CENTER); // 프로필 아이콘 넣을 라벨. 텍스트는 없고 가운데 정렬만 지정
        icon.setPreferredSize(new Dimension(120,120)); // 아이콘 라벨의 희망 크기 설정 (120x120)
        icon.setIcon(createAvatarIcon()); // 임시 아이콘
        add(icon, BorderLayout.CENTER); // 아이콘 라벨을 패널 가운데(CENTER)에 배치
        JLabel nameLabel = new JLabel(name, SwingConstants.CENTER); // 플레이어 이름 표시 라벨. 가운데 정렬
        add(nameLabel, BorderLayout.SOUTH); // 이름 라벨을 아래(SOUTH)에 배치
        if (isHost) { // 방장일 경우에만 '(호스트)' 표시
            JLabel hostLabel = new JLabel("(호스트)", SwingConstants.CENTER); // 호스트 표시 라벨 생성
            hostLabel.setFont(hostLabel.getFont().deriveFont(Font.ITALIC, 11f)); // 기존 글꼴에 italic + 작은 크기로 변경
            add(hostLabel, BorderLayout.NORTH); // '(호스트)' 라벨을 위(NORTH)에 배치
        }
    }

    private Icon createAvatarIcon() {
        int size = 80; // 원형 아이콘 크기 80x80
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB); // 투명 배경(ARGB)을 가진 80x80 이미지 캔버스 생성
        Graphics2D g = img.createGraphics(); // 그림을 그리기 위한 그래픽 객체 얻기
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 계단현상 없애고 동그랗고 부드럽게 그려지는 안티앨리어싱 ON
        g.setColor(new Color(200,200,200)); // 연한 회색 색상 선택
        g.fillOval(0,0,size,size); // (0,0) 위치에서 크기(size,size) 만큼 꽉 찬 원 그리기
        g.dispose(); // 그래픽 리소스 정리(메모리 해제). 안 하면 메모리 누수 가능
        return new ImageIcon(img); // BufferedImage → Swing에서 사용할 수 있는 ImageIcon으로 변환하여 반환
    }
}
