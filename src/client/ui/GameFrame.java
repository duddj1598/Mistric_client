package client.ui;

import client.network.ClientNetwork;
import common.GameMsg;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {

    private CardLayout cardLayout = new CardLayout();
    private JPanel container = new JPanel(cardLayout);

    private ConnectPanel connectPanel;
    private LobbyPanel lobbyPanel;
    private RoomPanel roomPanel;
    private GamePanel gamePanel;

    private ClientNetwork clientNetwork;
    private String nick;

    public GameFrame() {
        setTitle("아브라카왓 멀티 - 클라이언트");
        setSize(1280, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        clientNetwork = new ClientNetwork(this);

        connectPanel = new ConnectPanel(this);
        lobbyPanel   = new LobbyPanel(this);
        roomPanel    = new RoomPanel(this);
        gamePanel    = new GamePanel(this);

        container.add(connectPanel, "CONNECT");
        container.add(lobbyPanel,   "LOBBY");
        container.add(roomPanel,    "ROOM");
        container.add(gamePanel,    "GAME");

        add(container);

        showConnect();
    }

    // 네트워크 / 닉네임 접근자
    public ClientNetwork getNetwork() { return clientNetwork; }
    public String getNick() { return nick; }

    // 서버 접속 시도
    public void connectToServer(String ip, int port, String nick) {
        this.nick = nick;
        boolean ok = clientNetwork.connect(ip, port, nick);

        if (!ok) {
            JOptionPane.showMessageDialog(this, "서버 연결 실패!");
        }
    }

    // 서버에서 온 메시지에 따른 화면/데이터 갱신
    public void updateLobbyRoomList(GameMsg msg) {
        if (msg.text != null) {
            String[] rooms = msg.text.split("\\|");
            lobbyPanel.updateRooms(rooms);
        }
    }

    public void updateRoomPlayers(GameMsg msg) {
        if (msg.text != null) {
            String[] players = msg.text.split("\\|");
            roomPanel.updatePlayers(players);

            // 게임 패널에도 플레이어 정보 전달
            gamePanel.setPlayers(players);

            showRoom();
        }
    }

    public void updateChat(GameMsg msg) {
        String line = msg.user + ": " + msg.text;
        roomPanel.addChat(line);
        gamePanel.addChat(line);
    }

    public void updateGameState(GameMsg msg) {
        // 게임 상태 수신 시 게임 화면으로 이동 + 보드 갱신
        showGame();
        gamePanel.updateBoard(msg);
    }

    // 턴 알림
    public void updateTurn(GameMsg msg) {
        if (msg.text != null) {
            gamePanel.updateTurn(msg.text);
        }
    }

    // 마법 시전 결과
    public void showSpellResult(GameMsg msg) {
        if (msg.text != null) {
            gamePanel.addChat("[시스템] " + msg.text);
        }
    }

    // 게임 종료
    public void handleGameEnd(GameMsg msg) {
        if (msg.text != null) {
            gamePanel.showGameEnd(msg.text);
        }
    }

    // 화면 전환
    public void showConnect() { cardLayout.show(container, "CONNECT"); }
    public void showLobby()   { cardLayout.show(container, "LOBBY"); }
    public void showRoom()    { cardLayout.show(container, "ROOM"); }
    public void showGame()    { cardLayout.show(container, "GAME"); }

    // 서버 연결 끊기
    public void disconnectFromServer() {
        if (clientNetwork != null) {
            clientNetwork.close();
        }
        showConnect();
    }
}