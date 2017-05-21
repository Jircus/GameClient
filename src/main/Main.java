/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import gui.Login;

/**
 * Main class that contains main method
 * @author Jircus
 */
public class Main {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        //Login login = new Login(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        Login login = new Login("localhost", 58585, 58586);
        login.setLocationRelativeTo(null);
        login.setVisible(true);
    }
    
}
