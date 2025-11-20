package client.ui;

import common.GameMsg;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {

    private GameFrame parent;

    private ChattingPanel chatPanel;
    private JPanel boardArea;
    private JPanel rightPanel;
    private RoundedButton endTurnButton;

    public GamePanel(GameFrame parent) {
        this.parent = parent;
        setLayout(null);
        setBackground(new Color(240, 240, 240));
        setPreferredSize(new Dimension(1280, 720));

        // ==============================
        // 1) 채팅창
        // ==============================
        chatPanel = new ChattingPanel();
        chatPanel.setBounds(20, 20, 250, 600);
        add(chatPanel);

        // 채팅 전송 → 서버로 CHAT 전송
        chatPanel.setSendListener(e -> {
            String text = chatPanel.consumeInputText();
            if (!text.isEmpty()) {
                GameMsg msg = new GameMsg(
                        GameMsg.CHAT,
                        parent.getNick(),
                        text
                );
                parent.getNetwork().send(msg);
            }
        });

        // ==============================
        // 2) 중앙 보드 (상대 3 + 나 1)
        // ==============================
        boardArea = new JPanel(null);
        boardArea.setBounds(290, 20, 670, 680);
        boardArea.setBackground(new Color(240, 240, 240));
        add(boardArea);

        int boxW = 650;
        int boxH = 140;

        int[][] pos = {
                {10,  10},   // 상대 1
                {10, 160},   // 상대 2
                {10, 310},   // 상대 3
                {10, 510}    // 나
        };

        for (int i = 0; i < 2; i++) { // 일단 2명만 상대 예시
            boardArea.add(createOpponentRow(i, pos[i][0], pos[i][1], boxW, boxH));
        }
        boardArea.add(createMyPlayerRow(3, pos[3][0], pos[3][1], boxW, boxH));

        // ==============================
        // 3) 오른쪽 패널
        // ==============================
        rightPanel = new JPanel(null);
        rightPanel.setBounds(980, 20, 260, 680);
        rightPanel.setBackground(new Color(245,245,245));
        add(rightPanel);

        // 카드 덱 이미지
        ImageIcon stackIcon = null;
        try {
            stackIcon = new ImageIcon(
                    getClass().getResource("/client/ui/img/card_stack.png")
            );
        } catch (Exception e) {
            System.out.println("이미지 로딩 실패: card_stack.png");
        }

        if (stackIcon != null) {
            Image scaled = stackIcon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
            stackIcon = new ImageIcon(scaled);
        }

        JLabel stackLabel = new JLabel(stackIcon);
        stackLabel.setBounds(55, 10, 140, 140);
        rightPanel.add(stackLabel);

        JLabel remainLabel = new JLabel("남은 돌: 16");
        remainLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        remainLabel.setBounds(55, 150, 200, 30);
        rightPanel.add(remainLabel);

        // 비밀의 돌 2×2
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

            stone.setBounds(
                    startX + col * (w + gapX),
                    startY + row * (h + gapY),
                    w, h
            );
            rightPanel.add(stone);
        }

        endTurnButton = new RoundedButton("턴 종료");
        endTurnButton.setBounds(55, 580, 150, 45);
        endTurnButton.setEnabled(false); // 나중에 내 턴일 때만 활성화
        rightPanel.add(endTurnButton);
    }

    private JPanel createOpponentRow(int index, int x, int y, int w, int h) {
        JPanel row = new JPanel(null);
        row.setBounds(x, y, w, h);
        row.setBackground(new Color(255,255,255));
        row.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));

        JLabel heart = new JLabel("❤ 5");
        heart.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        heart.setBounds(10, 10, 80, 30);
        row.add(heart);

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

    // =============================
    // 네트워크 → 채팅창 반영
    // =============================
    public void addChat(String line) {
        chatPanel.appendChat(line);
    }

    // =============================
    // 네트워크 → 보드 상태 갱신
    // (지금은 TODO, 나중에 hp, stones 보고 그리면 됨)
    // =============================
    public void updateBoard(GameMsg msg) {
        // TODO: 서버에서 실제 게임 상태 보내줄 때 쓰임

        // 예: 나중에 msg.hp, msg.stones, msg.remainStones 사용
        System.out.println("게임 상태 갱신 도착!");
    }
}
