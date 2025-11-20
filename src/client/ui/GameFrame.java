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
    private String nick; // 내 닉네임

    public GameFrame() {
        setTitle("아브라카왓 멀티 - UI Prototype");
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

    // =========================
    // 네트워크 / 닉네임 접근자
    // =========================
    public ClientNetwork getNetwork() { return clientNetwork; }

    public String getNick() { return nick; }

    // =========================
    // 서버 접속 시도
    // =========================
    public void connectToServer(String ip, int port, String nick) {
        this.nick = nick; // 내가 누군지 저장

        boolean ok = clientNetwork.connect(ip, port, nick);

        // showLobby()는 서버에서 LOGIN_OK가 왔을 때 처리
        if (!ok) {
            JOptionPane.showMessageDialog(this, "서버 연결 실패!");
        }
    }

    // =========================
    // 서버에서 온 메시지에 따른 화면/데이터 갱신
    // =========================
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
            // 방 정보 받은 시점에 방 화면으로 이동
            showRoom();
        }
    }

    public void updateChat(GameMsg msg) {
        String line = msg.user + ": " + msg.text;

        // 방에 있을 경우
        roomPanel.addChat(line);

        // 게임 패널 채팅에도 표시
        gamePanel.addChat(line);
    }


    public void updateGameState(GameMsg msg) {
        // 게임 상태 수신 시 게임 화면으로 이동 + 보드 갱신
        showGame();
        gamePanel.updateBoard(msg);
    }

    // =========================
    // 화면 전환
    // =========================
    public void showConnect() { cardLayout.show(container, "CONNECT"); }
    public void showLobby()   { cardLayout.show(container, "LOBBY"); }
    public void showRoom()    { cardLayout.show(container, "ROOM"); }
    public void showGame()    { cardLayout.show(container, "GAME"); }

    // =========================
    // 서버 연결 끊기 (나중에 로비→접속화면 돌아갈 때 사용 가능)
    // =========================
    public void disconnectFromServer() {
        if (clientNetwork != null) {
            clientNetwork.close();
        }
        showConnect();
    }
}
