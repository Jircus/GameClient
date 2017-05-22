/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import gui.Game;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocol.Message;

/**
 * Client thread for communication with server
 * @author Jircus
 */
public class GameClient implements Runnable {
    
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String name;
    private String opponentsName;
    private Socket gameSocket;
    private Socket chatSocket;
    private Game game;
    private ChatClient chat;
    
    /**
     * Creates new instance
     * @param host
     * @param port
     * @param secondPort
     * @param name
     * @param game 
     */
    public GameClient(String host, int port, int secondPort, String name, Game game) {
        this.name = name;
        this.game = game;
        try {
            gameSocket = new Socket(host, port);
            chatSocket = new Socket(host, secondPort);
            System.out.println("connected to " + gameSocket + " and " + chatSocket);
        }
        catch (IOException ex) {
            System.out.println("Coud not connect to server");
            game.notConnected();
        }
        new Thread(this).start();
    }

    /**
     * Runs the thread that communicates with server
     */
    @Override
    public void run() {
        try {
            output = new ObjectOutputStream(gameSocket.getOutputStream());
            output.writeObject(name);
            System.out.println("Sending name " + name);
            input = new ObjectInputStream(gameSocket.getInputStream());
            game.setSymbol(input.readObject().toString());
            System.out.println("Symbol is set");
            input.readObject();
            System.out.println("Connection checked");
            Boolean b = (Boolean)input.readObject();
            opponentsName = input.readObject().toString();
            game.setGameEnabled(b, opponentsName);
            System.out.println("Opponent's name is " + opponentsName);
            chat = new ChatClient(chatSocket, game);
            new Thread(chat).start();
            System.out.println("Started thread for chat");
            while(true) {
                Message message = (Message)input.readObject();
                game.opponentsTurn(message.getRowIndex(), message.getColIndex(),
                        message.isWon(), opponentsName);
                System.out.println("Opponent placed symbol in row " + message.getRowIndex()
                        + " and column " + message.getColIndex());
                game.setReadyLabel();
            }
        }
        catch (ClassNotFoundException ex) {
            Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(IOException e){
            System.out.println("Opponent has disconnected");
            game.opponentDisconnected();
        }
    }
    
    /**
     * Sends player's move to server
     * @param rowIndex
     * @param colIndex
     * @param won 
     */
    public void sendMove(int rowIndex, int colIndex, boolean won) {
        Message message = new Message(rowIndex, colIndex, won);
        System.out.println("You have placed symbol in row " + message.getRowIndex()
                + " and column " + message.getColIndex());
        game.setWaitingLabel(opponentsName);
        try {
            output.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Sends message
     * @param message 
     */
    public void sendMessage(String message) {
        chat.sendMessage(message);
    }
}
