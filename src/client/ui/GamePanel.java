package client.ui;

import common.GameMsg;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {

    private GameFrame parent;

    private ChattingPanel chatPanel;
    private JPanel boardArea;
    private JPanel rightPanel;

    // 게임 상태 데이터
    private JLabel[] playerNameLabels = new JLabel[4];
    private JLabel[] playerHpLabels = new JLabel[4];
    private JLabel[][] playerStoneLabels = new JLabel[4][5];
    private JPanel[] playerRows = new JPanel[4];

    private JLabel remainStonesLabel;

    // 비밀의 돌
    private JLabel[] secretStones = new JLabel[4];

    private int myPlayerIndex = -1;
    private int myServerIndex = -1;  // 서버에서의 내 인덱스
    private String currentTurnPlayer = "";

    private String[] playerNames = new String[4];
    private int myHandCount = 5;

    // 서버 인덱스 → 화면 인덱스 매핑
    private int[] serverToScreen = new int[4];

    public GamePanel(GameFrame parent) {
        this.parent = parent;
        setLayout(null);
        setBackground(new Color(240, 240, 240));
        setPreferredSize(new Dimension(1280, 720));

        // 채팅창
        chatPanel = new ChattingPanel();
        chatPanel.setBounds(20, 20, 250, 600);
        add(chatPanel);

        chatPanel.setSendListener(e -> {
            String text = chatPanel.consumeInputText();
            if (!text.isEmpty()) {
                GameMsg msg = new GameMsg(GameMsg.CHAT, parent.getNick(), text);
                parent.getNetwork().send(msg);
            }
        });

        // 중앙 보드 영역
        boardArea = new JPanel(null);
        boardArea.setBounds(290, 20, 670, 680);
        boardArea.setBackground(new Color(240, 240, 240));
        add(boardArea);

        // 플레이어 행
        int boxW = 650;
        int boxH = 140;
        int[][] pos = {{10, 10}, {10, 160}, {10, 310}, {10, 510}};

        for (int i = 0; i < 4; i++) {
            playerRows[i] = createPlayerRow(i, pos[i][0], pos[i][1], boxW, boxH);
            playerRows[i].setVisible(false);
            boardArea.add(playerRows[i]);
        }

        // 오른쪽 패널
        rightPanel = new JPanel(null);
        rightPanel.setBounds(980, 20, 260, 680);
        rightPanel.setBackground(new Color(245,245,245));
        add(rightPanel);

        // 카드 덱
        JLabel stackLabel = new JLabel(loadTileImage("card_stack", 140, 140));
        stackLabel.setBounds(55, 10, 140, 140);
        rightPanel.add(stackLabel);

        // 남은 돌
        remainStonesLabel = new JLabel("남은 돌: 36");
        remainStonesLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        remainStonesLabel.setBounds(55, 150, 200, 30);
        rightPanel.add(remainStonesLabel);

        // 비밀의 돌
        JLabel secretLabel = new JLabel("비밀의 돌 (공개)", SwingConstants.CENTER);
        secretLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        secretLabel.setBounds(60, 190, 140, 30);
        rightPanel.add(secretLabel);

        JLabel secretSubLabel = new JLabel("(4번 효과로 사용)", SwingConstants.CENTER);
        secretSubLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
        secretSubLabel.setBounds(40, 210, 180, 20);
        rightPanel.add(secretSubLabel);

        int startX = 60, startY = 240, w = 65, h = 90, gapX = 20, gapY = 15;
        for (int i = 0; i < 4; i++) {
            int row = i / 2, col = i % 2;
            secretStones[i] = new JLabel("?", SwingConstants.CENTER);
            secretStones[i].setFont(new Font("맑은 고딕", Font.BOLD, 28));
            secretStones[i].setOpaque(true);
            secretStones[i].setBackground(new Color(220, 220, 220));
            secretStones[i].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
            secretStones[i].setBounds(startX + col * (w + gapX), startY + row * (h + gapY), w, h);
            rightPanel.add(secretStones[i]);
        }

        RoundedButton exitButton = new RoundedButton("게임 나가기");
        exitButton.setBounds(55, 600, 150, 45);
        exitButton.addActionListener(e -> {
            parent.showRoom();
            resetGame();
        });
        rightPanel.add(exitButton);
    }

    private JPanel createPlayerRow(int playerIndex, int x, int y, int w, int h) {
        JPanel row = new JPanel(null);
        row.setBounds(x, y, w, h);
        row.setBackground(new Color(255, 255, 255));
        row.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));

        playerNameLabels[playerIndex] = new JLabel("Player " + (playerIndex + 1));
        playerNameLabels[playerIndex].setFont(new Font("맑은 고딕", Font.BOLD, 16));
        playerNameLabels[playerIndex].setBounds(10, 10, 200, 25);
        row.add(playerNameLabels[playerIndex]);

        playerHpLabels[playerIndex] = new JLabel("❤ 5");
        playerHpLabels[playerIndex].setFont(new Font("맑은 고딕", Font.BOLD, 22));
        playerHpLabels[playerIndex].setBounds(10, 45, 80, 30);
        row.add(playerHpLabels[playerIndex]);

        int startX = 110;
        for (int i = 0; i < 5; i++) {
            JLabel stone = new JLabel("", SwingConstants.CENTER);
            stone.setOpaque(true);
            stone.setBackground(new Color(200, 200, 200));
            stone.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
            stone.setBounds(startX + (i * 90), 10, 75, 110);
            playerStoneLabels[playerIndex][i] = stone;
            row.add(stone);
        }

        return row;
    }

    public void updateBoard(GameMsg msg) {
        if (msg.hp == null || msg.stones == null) return;

        int playerCount = Math.min(msg.hp.length, 4);

        // 서버 데이터를 화면 인덱스로 변환
        for (int serverIdx = 0; serverIdx < playerCount; serverIdx++) {
            int screenIdx = serverToScreen[serverIdx];

            playerRows[screenIdx].setVisible(true);
            playerHpLabels[screenIdx].setText("❤ " + msg.hp[serverIdx]);

            if (msg.hp[serverIdx] <= 0) {
                playerRows[screenIdx].setBackground(new Color(100, 100, 100));
                playerNameLabels[screenIdx].setText(playerNames[screenIdx] + " (탈락)");
            } else {
                if (screenIdx == myPlayerIndex) {
                    playerRows[screenIdx].setBackground(new Color(230, 234, 255)); // 파란색
                } else {
                    playerRows[screenIdx].setBackground(new Color(255, 255, 255));
                }
            }

            int visibleCards = 0;
            for (int j = 0; j < 5; j++) {
                if (j < msg.stones[serverIdx].length && msg.stones[serverIdx][j] != -1) {
                    int cardValue = msg.stones[serverIdx][j];

                    if (screenIdx == myPlayerIndex) {
                        // 내 손패: 뒷면
                        playerStoneLabels[screenIdx][j].setIcon(loadTileImage("tile_back", 75, 110));
                        playerStoneLabels[screenIdx][j].setText("");
                    } else {
                        // 상대 손패: 공개
                        if (cardValue > 0 && cardValue <= 8) {
                            playerStoneLabels[screenIdx][j].setIcon(loadTileImage("tile_" + cardValue, 75, 110));
                            playerStoneLabels[screenIdx][j].setText("");
                        } else {
                            playerStoneLabels[screenIdx][j].setIcon(loadTileImage("tile_back", 75, 110));
                            playerStoneLabels[screenIdx][j].setText("");
                        }
                    }
                    playerStoneLabels[screenIdx][j].setVisible(true);
                    visibleCards++;
                } else {
                    playerStoneLabels[screenIdx][j].setVisible(false);
                }
            }

            if (screenIdx == myPlayerIndex) {
                myHandCount = visibleCards;
            }
        }

        // 남은 돌
        if (msg.remainStones >= 0) {
            remainStonesLabel.setText("남은 돌: " + msg.remainStones);
        }

        // 비밀의 돌 업데이트
        if (msg.text != null && !msg.text.isEmpty()) {
            updateSecretStones(msg.text);
        }

        repaint();
    }

    /**
     * 비밀의 돌 업데이트
     */
    private void updateSecretStones(String stonesStr) {
        // "[1, 3, 5, 7]" 형식 파싱
        stonesStr = stonesStr.replace("[", "").replace("]", "").replace(" ", "");

        if (stonesStr.isEmpty()) {
            // 비밀의 돌 모두 소진
            for (JLabel stone : secretStones) {
                stone.setVisible(false);
            }
            return;
        }

        String[] nums = stonesStr.split(",");

        for (int i = 0; i < secretStones.length; i++) {
            if (i < nums.length) {
                int tileNum = Integer.parseInt(nums[i]);
                secretStones[i].setIcon(loadTileImage("tile_" + tileNum, 65, 90));
                secretStones[i].setText("");
                secretStones[i].setVisible(true);
            } else {
                secretStones[i].setVisible(false);
            }
        }
    }

    public void updateTurn(String playerName) {
        currentTurnPlayer = playerName;

        // 모든 플레이어 테두리 초기화
        for (int i = 0; i < 4; i++) {
            if (playerRows[i].isVisible()) {
                playerRows[i].setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
            }
        }

        // 현재 턴 플레이어에게 빨간색 테두리
        for (int i = 0; i < playerNames.length; i++) {
            if (playerNames[i] != null && playerNames[i].equals(playerName)) {
                playerRows[i].setBorder(BorderFactory.createLineBorder(new Color(255, 50, 50), 4));
                break;
            }
        }

        // 내 턴이면 팝업 표시 (문구 없이)
        if (playerName.equals(parent.getNick())) {
            SwingUtilities.invokeLater(() -> showSpellSelectionDialog());
        }
    }

    /**
     * 8가지 마법 선택 팝업
     */
    private void showSpellSelectionDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "마법 선택", true);
        dialog.setSize(750, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        JLabel titleLabel = new JLabel("외칠 마법을 선택하세요 (내 손패 중 있는 것만 성공)", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.add(titleLabel, BorderLayout.NORTH);

        // 8가지 마법 버튼 (올바른 순서)
        JPanel spellPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        spellPanel.setBackground(new Color(240, 240, 240));
        spellPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] spellNames = {
                "용용이의 분노",      // 1
                "어둠의 방랑자",      // 2
                "기분 좋은 바람",    // 3
                "부엉부엉 통신",      // 4
                "번개 폭풍",          // 5
                "눈보라 강타",        // 6
                "불덩이 작렬",        // 7
                "마법 물약"           // 8
        };

        for (int i = 0; i < 8; i++) {
            final int spellNumber = i + 1;  // 1~8

            JButton spellButton = new JButton();
            spellButton.setLayout(new BorderLayout());
            spellButton.setPreferredSize(new Dimension(150, 180));
            spellButton.setBackground(Color.WHITE);
            spellButton.setFocusPainted(false);
            spellButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));

            // 마법 이미지 (tile_1.png ~ tile_8.png)
            JLabel imageLabel = new JLabel(loadTileImage("tile_" + spellNumber, 120, 140));
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            spellButton.add(imageLabel, BorderLayout.CENTER);

            // 마법 이름
            JLabel nameLabel = new JLabel(spellNumber + ". " + spellNames[i], SwingConstants.CENTER);
            nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            spellButton.add(nameLabel, BorderLayout.SOUTH);

            spellButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(
                        dialog,
                        spellNumber + "번 " + spellNames[spellNumber - 1] + " 마법을 외치시겠습니까?",
                        "마법 외치기",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    castSpell(spellNumber);
                    dialog.dispose();
                }
            });

            spellPanel.add(spellButton);
        }

        dialog.add(spellPanel, BorderLayout.CENTER);

        JLabel infoLabel = new JLabel("※ 내 손패에 있는 번호와 일치해야 성공!", SwingConstants.CENTER);
        infoLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        infoLabel.setForeground(new Color(255, 50, 50));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.add(infoLabel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    /**
     * 마법 시전 (선택한 번호 전송)
     */
    private void castSpell(int spellNumber) {
        GameMsg msg = new GameMsg(GameMsg.CAST_SPELL, parent.getNick());
        msg.handIndex = spellNumber - 1;  // 서버에서 +1 하므로 -1 전송
        msg.targetId = null;

        parent.getNetwork().send(msg);
        addChat("[시스템] " + spellNumber + "번 마법을 외쳤습니다!");

        System.out.println("[DEBUG] 전송: spellNumber=" + spellNumber + ", handIndex=" + msg.handIndex);
    }

    /**
     * 타일 이미지 로드
     */
    private ImageIcon loadTileImage(String name, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/client/ui/img/" + name + ".png"));
            Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            // 이미지 로드 실패 시 빈 아이콘 반환
            return new ImageIcon();
        }
    }

    public void setPlayers(String[] players) {
        // 모든 행 초기화
        for (int i = 0; i < 4; i++) {
            playerRows[i].setVisible(false);
            playerNames[i] = null;
        }

        // 내 서버 인덱스 찾기
        myServerIndex = -1;
        for (int i = 0; i < players.length; i++) {
            if (players[i].equals(parent.getNick())) {
                myServerIndex = i;
                break;
            }
        }

        if (myServerIndex == -1) return;

        int playerCount = players.length;

        // 나는 항상 3번 위치
        myPlayerIndex = 3;

        // 서버 순서대로 화면에 배치 (나는 제외하고 0번부터)
        int screenIdx = 0;
        for (int serverIdx = 0; serverIdx < playerCount; serverIdx++) {
            if (serverIdx == myServerIndex) {
                // 나는 3번 위치
                serverToScreen[serverIdx] = 3;
                playerNames[3] = players[serverIdx];
                playerNameLabels[3].setText(players[serverIdx]);
                playerRows[3].setVisible(true);
                playerRows[3].setBackground(new Color(230, 234, 255)); // 파란색
            } else {
                // 다른 플레이어는 0번부터 순서대로
                serverToScreen[serverIdx] = screenIdx;
                playerNames[screenIdx] = players[serverIdx];
                playerNameLabels[screenIdx].setText(players[serverIdx]);
                playerRows[screenIdx].setVisible(true);
                screenIdx++;
            }
        }

        System.out.println("[플레이어 배치] 인원: " + playerCount + ", 내 서버 인덱스: " + myServerIndex + " → 화면 3번");
    }

    public void showGameEnd(String message) {
        JOptionPane.showMessageDialog(this, message, "게임 종료", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetGame() {
        myPlayerIndex = -1;
        currentTurnPlayer = "";
        myHandCount = 5;

        for (int i = 0; i < 4; i++) {
            playerRows[i].setVisible(false);
            playerNames[i] = null;
        }

        for (JLabel stone : secretStones) {
            stone.setText("?");
            stone.setBackground(new Color(220, 220, 220));
        }

        remainStonesLabel.setText("남은 돌: 36");
    }

    public void addChat(String line) {
        chatPanel.appendChat(line);
    }
}