package client.ui;

import javax.swing.*;
import java.awt.*;

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
}
