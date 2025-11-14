package client.ui;

import javax.swing.*;
import java.awt.*;

class RoomPanel extends JPanel {
    private GameFrame parent;

    public RoomPanel(GameFrame parent) {
        this.parent = parent;

        setLayout(null);
        setBackground(new Color(240,240,240));

        // ============================
        // 1) 채팅 패널 (왼쪽 좁게)
        // ============================
        ChattingPanel chat = new ChattingPanel();
        chat.setBounds(20, 20, 260, 600);   // <<<<< 크기 줄인 부분
        add(chat);

        // ============================
        // 2) 플레이어 2 × 2 영역
        // ============================
        JPanel gridPanel = new JPanel(null);
        gridPanel.setBounds(310, 20, 920, 600);
        gridPanel.setBackground(new Color(230,230,230));
        gridPanel.setBorder(BorderFactory.createLineBorder(new Color(200,200,200), 2));
        add(gridPanel);

        // 플레이어 박스 크기
        int boxW = 420;
        int boxH = 260;

        // 4칸 위치 (2×2)
        int[][] pos = {
                {30,  30},          // Player 1
                {470, 30},          // Player 2
                {30,  320},         // Player 3
                {470, 320}          // Player 4
        };

        for (int i = 0; i < 3; i++) {
            PlayerPanel p = new PlayerPanel("플레이어 " + (i+1), i == 0);
            p.setBounds(pos[i][0], pos[i][1], boxW, boxH);
            gridPanel.add(p);
        }

        // ============================
        // 3) 하단 "게임 시작" 버튼
        // ============================
        JButton startBtn = new RoundedButton("게임 시작");
        startBtn.setBounds(540, 630, 200, 40);
        startBtn.addActionListener(e -> parent.showGame());
        add(startBtn);
    }
}
