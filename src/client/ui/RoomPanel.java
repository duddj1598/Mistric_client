package client.ui;

import common.GameMsg;

import javax.swing.*;
import java.awt.*;

class RoomPanel extends JPanel {
    private GameFrame parent;

    private JPanel centerPanel;
    private ChattingPanel chat; // ★ 변수로 저장

    public RoomPanel(GameFrame parent) {
        this.parent = parent;

        setLayout(null);
        setBackground(new Color(240,240,240));

        // ===========================
        // 1) 채팅 패널
        // ===========================
        chat = new ChattingPanel();
        chat.setBounds(20, 20, 260, 600);
        add(chat);

        //  방 채팅 → 서버로 CHAT 메시지 보내기
        chat.setSendListener(e -> {
            String text = chat.consumeInputText();
            if (!text.isEmpty()) {
                GameMsg msg = new GameMsg(
                        GameMsg.CHAT,
                        parent.getNick(),
                        text
                );
                parent.getNetwork().send(msg);
            }
        });

        // ===========================
        // 2) 플레이어 패널
        // ===========================
        centerPanel = new JPanel(null);
        centerPanel.setBounds(310, 20, 920, 600);
        centerPanel.setBackground(new Color(230,230,230));
        centerPanel.setBorder(BorderFactory.createLineBorder(new Color(200,200,200), 2));
        add(centerPanel);

        // ===========================
        // 3) 게임 시작 버튼
        // ===========================
        JButton startBtn = new RoundedButton("게임 시작");
        startBtn.setBounds(440, 630, 200, 40);
        startBtn.addActionListener(e -> {
            GameMsg msg = new GameMsg(GameMsg.GAME_START, parent.getNick(), null);
            parent.getNetwork().send(msg);
        });
        add(startBtn);

        // ===========================
        // 4) 로비로 나가기
        // ===========================
        JButton backBtn = new RoundedButton("로비로 나가기");
        backBtn.setBounds(660, 630, 200, 40);
        backBtn.addActionListener(e -> {
            parent.getNetwork().send(new GameMsg(
                    GameMsg.ROOM_LEAVE,
                    parent.getNick(),
                    null
            ));
            parent.showLobby();
        });
        add(backBtn);
    }

    // ==================================
    // 서버 → ROOM_UPDATE 수신 시 표현
    // ==================================
    public void updatePlayers(String[] players) {
        centerPanel.removeAll();

        int boxW = 420, boxH = 260;
        int[][] pos = {
                {30,  30},
                {470, 30},
                {30,  320},
                {470, 320}
        };

        for (int i = 0; i < players.length && i < 4; i++) {
            PlayerPanel p = new PlayerPanel(players[i], i == 0);
            p.setBounds(pos[i][0], pos[i][1], boxW, boxH);
            centerPanel.add(p);
        }

        centerPanel.revalidate();
        centerPanel.repaint();
    }
    public void addChat(String line) {
        chat.appendChat(line);
    }

}
