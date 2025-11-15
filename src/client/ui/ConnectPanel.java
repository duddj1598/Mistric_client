package client.ui;

import javax.swing.*;
import java.awt.*;

class ConnectPanel extends JPanel {
    private GameFrame parent;
    JTextField ipField, portField, nickField;
    JButton connectBtn, exitBtn;

    public ConnectPanel(GameFrame parent) {
        this.parent = parent;

        setLayout(null);  // 절대 위치
        setBackground(new Color(230, 230, 230));

        // ======================
        // 제목
        // ======================
        JLabel title = new JLabel("아브라카왓 - 서버 접속", SwingConstants.CENTER);
        title.setFont(new Font("맑은고딕", Font.BOLD, 32));
        title.setBounds(340, 80, 600, 50);
        add(title);

        // ======================
        // Nick
        // ======================
        JLabel nickLabel = new JLabel("Nick :");
        nickLabel.setBounds(420, 180, 100, 30);
        add(nickLabel);

        nickField = new JTextField("Player");
        nickField.setBounds(520, 180, 300, 30);
        add(nickField);

        // ======================
        // IP
        // ======================
        JLabel ipLabel = new JLabel("Server IP :");
        ipLabel.setBounds(420, 230, 100, 30);
        add(ipLabel);

        ipField = new JTextField("127.0.0.1");
        ipField.setBounds(520, 230, 300, 30);
        add(ipField);

        // ======================
        // Port
        // ======================
        JLabel portLabel = new JLabel("Port :");
        portLabel.setBounds(420, 280, 100, 30);
        add(portLabel);

        portField = new JTextField("5555");
        portField.setBounds(520, 280, 300, 30);
        add(portField);

        // ======================
        // 버튼들
        // ======================
        connectBtn = new RoundedButton("접속하기");
        connectBtn.setBounds(420, 350, 190, 45);
        add(connectBtn);

        exitBtn = new RoundedButton("게임종료");
        exitBtn.setBounds(630, 350, 190, 45);
        add(exitBtn);

        // ======================
        // ACTION
        // ======================
        connectBtn.addActionListener(e -> parent.showLobby());
        exitBtn.addActionListener(e -> System.exit(0));
    }
}
