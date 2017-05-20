/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

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
public class Client implements Runnable {
    
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String name;
    private String opponentsName;
    private final Thread thread;
    private Socket socket;
    private Game game;
    
    /**
     * Creates new instance
     * @param host
     * @param port
     * @param name
     * @param game 
     */
    public Client(String host, int port, String name, Game game) {
        this.name = name;
        this.game = game;
        try {
            socket = new Socket(host, port);
            System.out.println("connected to " + socket);
        }
        catch (IOException ex) {
            System.out.println("Coud not connect to server");
            game.notConnected();
        }
        thread = new Thread(this);
        thread.start();
    }

    /**
     * Runs the thread that communicates with server
     */
    @Override
    public void run() {
        try {
        output = new ObjectOutputStream(socket.getOutputStream());
        output.writeObject(name);
        System.out.println("Sending name " + name);
        input = new ObjectInputStream(socket.getInputStream());
        game.setSymbol(input.readObject().toString());
        System.out.println("Symbol is set");
        input.readObject();
        System.out.println("Connection checked");
        Boolean b = (Boolean)input.readObject();
        opponentsName = input.readObject().toString();
        game.setGameEnabled(b, opponentsName);
            while(true) {
                Message message = (Message)input.readObject();
                game.oponentsTurn(message.getRowIndex(), message.getColIndex(),
                        message.isWon(), opponentsName);
                System.out.println("Oponent placed symbol in row " + message.getRowIndex()
                        + " and column " + message.getColIndex());
                game.setReadyLabel();
            }
        }
        catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(IOException e){
            System.out.println("Oponent has disconnected");
            game.oponentDisconnected();
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
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
