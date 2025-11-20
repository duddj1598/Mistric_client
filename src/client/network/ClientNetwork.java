package client.network;

import client.ui.GameFrame;
import common.GameMsg;

import javax.swing.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientNetwork {

    private GameFrame parent;

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private Thread receiverThread;
    private boolean connected = false;

    public ClientNetwork(GameFrame parent) {
        this.parent = parent;
    }

    // =========================
    // 서버 연결
    // =========================
    public boolean connect(String ip, int port, String nick) {
        try {
            socket = new Socket(ip, port);

            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            connected = true;

            // 로그인 메시지
            GameMsg loginMsg = new GameMsg(GameMsg.LOGIN, nick);
            send(loginMsg);

            startReceiver();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            connected = false;
            return false;
        }
    }

    // =========================
    // 메시지 전송
    // =========================
    public void send(GameMsg msg) {
        if (!connected) return;
        try {
            out.writeObject(msg);
            out.flush();
        } catch (Exception e) {
            System.out.println("메시지 전송 실패: " + e.getMessage());
        }
    }

    // =========================
    // 수신 스레드
    // =========================
    private void startReceiver() {
        receiverThread = new Thread(() -> {
            try {
                while (connected) {
                    Object obj = in.readObject();
                    if (obj instanceof GameMsg msg) {
                        handleMessage(msg);
                    }
                }
            } catch (Exception e) {
                System.out.println("서버 연결 종료");
            } finally {
                close();
            }
        });

        receiverThread.setDaemon(true);
        receiverThread.start();
    }

    // =========================
    // 서버 → 클라이언트 메시지 처리
    // =========================
    private void handleMessage(GameMsg msg) {

        SwingUtilities.invokeLater(() -> {
            switch (msg.mode) {

                case GameMsg.LOGIN_OK -> {
                    // 로그인 성공 → 로비로 이동 요청
                    parent.showLobby();
                }

                case GameMsg.ROOM_LIST -> {
                    parent.updateLobbyRoomList(msg);
                }

                case GameMsg.ROOM_UPDATE -> {
                    parent.updateRoomPlayers(msg);
                }

                case GameMsg.CHAT, GameMsg.CHAT_SYSTEM -> {
                    parent.updateChat(msg);
                }

                case GameMsg.GAME_STATE, GameMsg.GAME_START -> {
                    parent.updateGameState(msg);
                }

                default -> {
                    System.out.println("Unknown mode: " + msg.mode);
                }
            }
        });
    }

    // =========================
    // 종료
    // =========================
    public void close() {
        try {
            connected = false;
            if (socket != null) socket.close();
        } catch (Exception ignored) {}
    }
}
