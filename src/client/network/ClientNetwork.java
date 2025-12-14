package client.network;

import client.ui.GameFrame;
import common.GameMsg;

import javax.swing.*;
import java.io.IOException;
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

    // 서버 연결
    public boolean connect(String ip, int port, String nick) {
        try {
            socket = new Socket(ip, port);

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            connected = true;

            // 로그인 메시지
            GameMsg loginMsg = new GameMsg(GameMsg.LOGIN, nick);
            send(loginMsg);

            startReceiver();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            connected = false;
            return false;
        }
    }

    // 메시지 전송
    public void send(GameMsg msg) {
        if (!connected) return;
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            System.out.println("메시지 전송 실패: " + e.getMessage());
        }
    }

    // 수신 스레드
    private void startReceiver() {
        receiverThread = new Thread(() -> {
            try {
                while (connected) {
                    Object obj = in.readObject();
                    if (obj instanceof GameMsg msg) {
                        handleMessage(msg);
                    }
                }
            } catch (IOException e) {
                System.out.println("서버 연결 종료> " + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.out.println("알 수 없는 객체 수신 오류> " + e.getMessage());
            } finally {
                close();
            }
        });

        receiverThread.start();
    }

    // 서버 → 클라이언트 메시지 처리
    private void handleMessage(GameMsg msg) {

        SwingUtilities.invokeLater(() -> {
            switch (msg.mode) {

                case GameMsg.LOGIN_OK -> {
                    // 로그인 성공 → 로비로 이동
                    parent.showLobby();
                }

                case GameMsg.ROOM_LIST -> {
                    // 방 목록 업데이트
                    parent.updateLobbyRoomList(msg);
                }

                case GameMsg.ROOM_UPDATE -> {
                    // 방 플레이어 목록 업데이트
                    parent.updateRoomPlayers(msg);
                }

                case GameMsg.CHAT, GameMsg.CHAT_SYSTEM -> {
                    // 채팅 메시지
                    parent.updateChat(msg);
                }

                case GameMsg.GAME_START -> {
                    // 게임 시작 (게임 화면으로 전환)
                    parent.showGame();
                }

                case GameMsg.GAME_STATE -> {
                    // 게임 상태 업데이트 (HP, 손패 등)
                    parent.updateGameState(msg);
                }

                case GameMsg.TURN -> {
                    // 턴 알림
                    parent.updateTurn(msg);
                }

                case GameMsg.SPELL_RESULT -> {
                    // 마법 시전 결과
                    parent.showSpellResult(msg);
                }

                case GameMsg.GAME_END -> {
                    // 게임 종료
                    parent.handleGameEnd(msg);
                }

                default -> {
                    System.out.println("Unknown mode: " + msg.mode);
                }
            }
        });
    }

    // 종료
    public void close() {
        try {
            connected = false;
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println("클라이언트 닫기 오류> " + e.getMessage());
        }
    }
}