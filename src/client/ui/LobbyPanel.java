package client.ui;

import common.GameMsg;

import javax.swing.*;
import java.awt.*;

class LobbyPanel extends JPanel {

    private GameFrame parent;

    private JPanel roomListContainer;

    public LobbyPanel(GameFrame parent) {
        this.parent = parent;

        setLayout(null);
        setBackground(new Color(230, 230, 230));

        JLabel title = new JLabel("게임 로비", SwingConstants.CENTER);
        title.setFont(new Font("맑은고딕", Font.BOLD, 32));
        title.setBounds(0, 20, 1280, 50);
        add(title);

        roomListContainer = new JPanel(null);
        roomListContainer.setBounds(140, 100, 1000, 320);
        roomListContainer.setBackground(new Color(240,240,240));
        add(roomListContainer);

        JButton createBtn = new RoundedButton("새로운 방 만들기");
        createBtn.setBounds(350, 460, 250, 50);
        createBtn.addActionListener(e -> {
            String roomName = JOptionPane.showInputDialog(
                    parent,
                    "방 이름을 입력하세요",
                    "새 방 만들기",
                    JOptionPane.PLAIN_MESSAGE
            );

            if (roomName != null) {
                roomName = roomName.trim();
                if (!roomName.isEmpty()) {
                    GameMsg msg = new GameMsg(
                            GameMsg.ROOM_CREATE,
                            parent.getNick(),
                            roomName
                    );
                    parent.getNetwork().send(msg);
                }
            }
        });
        add(createBtn);

        JButton disconnectBtn = new RoundedButton("접속 종료");
        disconnectBtn.setBounds(680, 460, 250, 50);
        disconnectBtn.addActionListener(e -> {
            parent.disconnectFromServer();   // 서버 소켓 종료
            parent.showConnect();            // 접속 화면으로 이동
        });

        add(disconnectBtn);
    }

    // 서버에서 ROOM_LIST 수신 → 여기로
    public void updateRooms(String[] rooms) {

        // ★ 아무 방도 없는 경우
        if (rooms.length == 0 ||
                (rooms.length == 1 && rooms[0].trim().isEmpty())) {

            roomListContainer.removeAll();
            roomListContainer.revalidate();
            roomListContainer.repaint();
            return;
        }

        roomListContainer.removeAll();

        int startY = 0;

        for (int i = 0; i < rooms.length; i++) {
            JPanel row = new JPanel(null);
            row.setBackground(new Color(250, 250, 250));
            row.setBounds(0, startY + i * 80, 1000, 70);
            row.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 2));

            JLabel roomLabel = new JLabel(rooms[i]);
            roomLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
            roomLabel.setBounds(20, 20, 500, 30);
            row.add(roomLabel);

            JButton enterBtn = new RoundedButton("입장하기");
            enterBtn.setBounds(800, 15, 150, 40);

            int index = i;
            enterBtn.addActionListener(e -> {
                parent.getNetwork().send(new GameMsg(GameMsg.ROOM_ENTER, parent.getNick(), rooms[index]));
            });

            row.add(enterBtn);
            roomListContainer.add(row);
        }

        roomListContainer.revalidate();
        roomListContainer.repaint();
    }

}
