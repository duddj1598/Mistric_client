package client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ChattingPanel extends JPanel {
    private JTextArea chatArea;
    private JTextField textField;
    private JButton sendButton;

    public ChattingPanel() {
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setPreferredSize(new Dimension(280, 0));
        add(chatScroll, BorderLayout.CENTER);

        JPanel input = new JPanel(new BorderLayout());

        textField = new JTextField();
        input.add(textField, BorderLayout.CENTER);

        sendButton = new JButton("보내기");
        input.add(sendButton, BorderLayout.EAST);

        add(input, BorderLayout.SOUTH);
    }

    // ========================
    // 채팅 내용 추가
    // ========================
    public void appendChat(String line) {
        chatArea.append(line + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    // ========================
    // 전송 버튼/Enter에 대한 리스너 설정
    // (실제 네트워크 전송은 밖에서 처리)
    // ========================
    public void setSendListener(ActionListener l) {
        sendButton.addActionListener(l);
        textField.addActionListener(l); // 엔터 입력도 같은 동작
    }

    // ========================
    // 텍스트 입력값 가져오고 입력창 비우기
    // ========================
    public String consumeInputText() {
        String txt = textField.getText().trim();
        textField.setText("");
        return txt;
    }
}
