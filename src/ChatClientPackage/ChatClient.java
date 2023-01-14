package ChatClientPackage;

import ChatServerPackage.ChatServerIF;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ChatClient extends UnicastRemoteObject implements ChatClientIF, Runnable {

    private final ChatServerIF chatServer;
    private String name = null;
    JTextArea textArea;
    JTextField inputField;

    protected ChatClient(String name, ChatServerIF chatServer) throws RemoteException {
        this.name = name;
        this.chatServer = chatServer;
        chatServer.registerChatClient(this);
    }

    @Override
    public void retrieveMessage(String message) throws RemoteException {
        System.out.println(message);
        textArea.append(message +"\n");
    }

    @Override
    public void run() {
        //
        //String message;

        JFrame clientFrame = new JFrame(name + " frame");
        clientFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        clientFrame.setSize(400,400);
        clientFrame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder( 10,  10, 10, 10));
        clientFrame.add(mainPanel, BorderLayout.CENTER);


        /*TEXT AREA PANEL */
        JPanel northPanel = new JPanel();
        northPanel.setBorder(BorderFactory.createEmptyBorder( 10,  10, 10, 10));
        //northPanel.setLayout();
        mainPanel.add(northPanel,BorderLayout.WEST);
        //COMPONENTS
        //TEXT AREA - incoming and sent messages
        textArea = new JTextArea("");
        textArea.setBounds(10,30, 200,200);
        textArea.setSize(200,200);
        textArea.setBackground(new Color(108, 148, 147));
        textArea.setHighlighter(null);
        textArea.setEditable(false);
        northPanel.add(textArea);

        /*MESSAGE SENDING PANEL - TEXT FIELD AND SEND BUTTON*/
        JPanel southPanel = new JPanel();
        southPanel.setBorder(BorderFactory.createEmptyBorder( 10,  10, 10, 10));
        southPanel.setLayout(new GridLayout(1,2));
        mainPanel.add(southPanel,BorderLayout.SOUTH);
        //COMPONENTS
        //INPUT TEXT FIELD - message to send
        inputField = new JTextField( "input tf");
        inputField.setSize(200,50);
        southPanel.add(inputField);

        // SEND BUTTON
        JButton sendBtn = new JButton("SEND");
        sendBtn.setBounds(50,50,100,50);
        southPanel.add(sendBtn);



        /*LIST OF USERS, CONNECT BUTTON, NAME TEXT FIELD, REGISTER BUTTON*/
        JPanel eastPanel = new JPanel();
        eastPanel.setBorder(BorderFactory.createEmptyBorder( 10,  10, 10, 10));
        GridLayout myGridLayout = new GridLayout(4,1);
        eastPanel.setLayout(myGridLayout);
        mainPanel.add(eastPanel,BorderLayout.EAST);
        //COMPONENTS
        JButton connectButton = new JButton("CONNECT");
        eastPanel.add(connectButton);
        JTextField nameTf = new JTextField("username");
        eastPanel.add(nameTf);
        JButton  registerButton = new JButton("REGISTER");
        eastPanel.add(registerButton);








//        JButton registerBtn = new JButton("REGISTER");
//        mainPanel.add(registerBtn,BorderLayout.EAST);
//
//        JTextField registerTf = new JTextField("Nickname");
//        mainPanel.add(registerTf);


        clientFrame.setVisible(true);


            sendBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        buttonPressed();
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                }
            });


//        while(true){
//
//
//            try{
//                chatServer.broadcastMessage(name + " : " + message);
//            }
//            catch (RemoteException e){
//                e.printStackTrace();
//                System.out.println("nie udalo sie");
//            }
//        }



    }

    public void buttonPressed() throws RemoteException {
        String message;
        message = inputField.getText();
        inputField.setText("");
        chatServer.broadcastMessage(name + " : " + message);
    }
}
