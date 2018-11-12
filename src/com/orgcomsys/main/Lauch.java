/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orgcomsys.main;

import com.orgcomsys.impl.AuthenticationImpl;
import com.orgcomsys.inter.Authentication;
import com.orgcomsys.utility.DatabaseConnection;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author Mallika
 */
public class Lauch {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println(DatabaseConnection.getConnection());
        try { 
         // Instantiating the implementation class
         AuthenticationImpl authenticationImpl = new AuthenticationImpl();
    
         // Exporting the object of implementation class  
         // (here we are exporting the remote object to the authentication) 
         Authentication authentication = 
            (Authentication) UnicastRemoteObject.exportObject(authenticationImpl, 0);  
         
         // Binding the remote object (authentication) in the registry 
         Registry registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT); 
         
         registry.rebind("Authentication", authentication);  
         System.err.println("Server ready"); 
      } catch (Exception e) { 
         System.err.println("Server exception: " + e.toString()); 
         e.printStackTrace(); 
      } 
    }
    
}
