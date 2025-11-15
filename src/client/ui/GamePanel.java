package client.ui;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {

    private GameFrame parent;

    private ChattingPanel chatPanel;
    private JPanel boardArea;
    private JPanel rightPanel;

    public GamePanel(GameFrame parent) {
        this.parent = parent;
        setLayout(null);
        setBackground(new Color(240, 240, 240));
        setPreferredSize(new Dimension(1280, 720));

        // =========================================================
        // 1) 채팅창
        // =========================================================
        chatPanel = new ChattingPanel();
        chatPanel.setBounds(20, 20, 250, 600);   // 폭 조금 줄임
        add(chatPanel);

        // =========================================================
        // 2) 중앙 보드 (상대 3 + 나 1)
        // =========================================================
        boardArea = new JPanel(null);
        boardArea.setBounds(290, 20, 670, 680);
        boardArea.setBackground(new Color(240, 240, 240));
        add(boardArea);

        // 플레이어 박스
        int boxW = 650;
        int boxH = 140;   // 기존 160 → 140

        int[][] pos = {
                {10,  10},   // 상대 1
                {10, 160},   // 상대 2
                {10, 310},   // 상대 3
                {10, 510}    // 나 (맨 아래)
        };

        // 상대 플레이어 3명
        for (int i = 0; i < 2; i++) {
            boardArea.add(createOpponentRow(i, pos[i][0], pos[i][1], boxW, boxH));
        }

        // 내 플레이어
        boardArea.add(createMyPlayerRow(3, pos[3][0], pos[3][1], boxW, boxH));

        // =========================================================
        // 3) 오른쪽 패널
        // =========================================================
        rightPanel = new JPanel(null);
        rightPanel.setBounds(980, 20, 260, 680);
        rightPanel.setBackground(new Color(245,245,245));
        add(rightPanel);

// =======================================
// 카드 스택 이미지 (정상 경로 로딩 방식)
// =======================================
        ImageIcon stackIcon = null;

        try {
            stackIcon = new ImageIcon(
                    getClass().getResource("/client/ui/img/card_stack.png")
            );
        } catch (Exception e) {
            System.out.println("이미지 로딩 실패: " + e.getMessage());
        }

// 이미지 크기 조절
        if (stackIcon != null) {
            Image scaled = stackIcon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
            stackIcon = new ImageIcon(scaled);
        }

        JLabel stackLabel = new JLabel(stackIcon);
        stackLabel.setBounds(55, 10, 140, 140);
        rightPanel.add(stackLabel);


// =======================================
// 남은 돌 표시
// =======================================
        JLabel remainLabel = new JLabel("남은 돌: 16");
        remainLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        remainLabel.setBounds(55, 150, 200, 30);
        rightPanel.add(remainLabel);

// =======================================
// 비밀의 돌 2x2
// =======================================
        int startX = 40;
        int startY = 200;
        int w = 80, h = 110;
        int gapX = 20;
        int gapY = 20;

        for (int i = 0; i < 4; i++) {
            ImageIcon backIcon = loadImage("/client/ui/img/card_back.png", w, h);
            JLabel stone = new JLabel(backIcon);

            int row = i / 2;
            int col = i % 2;

            stone.setBounds(startX + col*(w+gapX),
                    startY + row*(h+gapY),
                    w, h);

            rightPanel.add(stone);
        }

    }

    // =========================================================
    // 상대 플레이어 행
    // =========================================================
    private JPanel createOpponentRow(int index, int x, int y, int w, int h) {
        JPanel row = new JPanel(null);
        row.setBounds(x, y, w, h);
        row.setBackground(new Color(255, 0, 0));
        row.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));

        // 생명력
        JLabel heart = new JLabel("❤ 5");
        heart.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        heart.setBounds(10, 10, 80, 30);
        row.add(heart);

        // 마법의 돌 (크기 축소됨)
        int startX = 110;
        for (int i = 0; i < 5; i++) {
            JLabel stone = new JLabel();
            stone.setOpaque(true);
            stone.setBackground(new Color(200,200,200));
            stone.setBounds(startX + (i * 90), 10, 75, 110);
            row.add(stone);
        }

        return row;
    }

    // =========================================================
    // 나 (플레이어 자신의 행)
    // =========================================================
    private JPanel createMyPlayerRow(int index, int x, int y, int w, int h) {
        JPanel row = new JPanel(null);
        row.setBounds(x, y, w, h);
        row.setBackground(new Color(230, 234, 255));
        row.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));

        JLabel heart = new JLabel("❤ 5");
        heart.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        heart.setBounds(10, 10, 80, 30);
        row.add(heart);

        int startX = 110;
        for (int i = 0; i < 5; i++) {
            ImageIcon backIcon = loadImage("/client/ui/img/card_back.png", 75, 110);
            JLabel stone = new JLabel(backIcon);

            stone.setBounds(startX + (i * 90), 10, 75, 110);
            row.add(stone);
        }

        return row;
    }
    private ImageIcon loadImage(String path, int w, int h) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            System.out.println("이미지 로딩 실패: " + path);
            return null;
        }
    }

}
