/* ============================================================================
 * Nom du fichier   : Core.java
 * ============================================================================
 * Date de création : 1 mai 2013
 * ============================================================================
 * Auteurs          : Crescenzio Fabio
 *                    Decorvet Grégoire
 *                    Jaquier Kevin
 *                    Schweizer Thomas
 * ============================================================================
 */
package core;

import gui.LogsFrame;
import gui.logs.Log;
import gui.logs.LogPanel;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;

import utils.RandomGenerator;

import common.components.AccountType;
import common.components.UserAccount;
import common.connections.Channel;

import core.exceptions.PortException;
import core.lobby.Lobby;
import database.DBController;

/**
 * TODO
 * @author Crescenzio Fabio
 * @author Decorvet Grégoire
 * @author Jaquier Kevin
 * @author Schweizer Thomas
 *
 */
public class Core {
   
   private static final int CLIENT_TIMEOUT = 15000;
   private static final int DEFAULT_PORT = 1234;
   
   private static final String DATABASE_DIRECTORY = "database";
   private static final String DATABASE_NAME = "onapi.db";
   private static String databasePath;
   
   private LogsFrame logsFrame;
   
   private Log log;
   
   private boolean exit = false;
   
   private InetAddress[] inetAddresses = null;
   
   // Port principal d'écoute
   private Port serverPort;
   
   // Port pour les mises à jours
   private Port updatePort;
   private AcceptUpdateConnections updatePortActivity;
   
   private DBController dbController;
   
   private LinkedList<UserConnectionManager> connections = new LinkedList<>();
   
   private LinkedList<ExpectedConnection> expectedConnections = new LinkedList<>();
   private LinkedList<EstablishedNewConnection> establishedNewConnections = new LinkedList<>();
   
   private Lobby lobby;
   
   public Core(boolean enableLogFrame) {
      init(enableLogFrame);
      
      if (!initSuccessful()) {
         throw new RuntimeException("Server not successfully initialized");
      }
   }
   
   public void addLogPanel(LogPanel panel) {
      if (logsFrame != null) {
         synchronized(logsFrame) {
            logsFrame.addLogPanel(panel);
         }
      }
   }
   
   public void removeLogPanel(LogPanel panel) {
      if (logsFrame != null) {
         synchronized(logsFrame) {
            logsFrame.removeLogPanel(panel);
         }
      }
   }
   
   public void setLogPanelTitle(LogPanel panel, String title) {
      if (logsFrame != null) {
         synchronized(logsFrame) {
            logsFrame.setLogPanelTitle(panel, title);
         }
      }
   }
   
   public void start() {
      
      if(logsFrame != null) {
         logsFrame.setVisible(true);
         logsFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
      }
      
      log.push("Onapi server started.");
      
      for (int i = 0 ; i < inetAddresses.length ; i++) {
         log.push("Network interface " + i +
               (i == 0 ? " (suggested)" : "") + "\n" +
             " - name    : " + inetAddresses[i].getHostName() + "\n" +
             " - address : " + inetAddresses[i].getHostAddress() + "\n" +
             " - port    : " + getPortNumber());
      }
      
      while(!exit) {
         
         try {
            Socket socket = serverPort.accept();
            
            System.out.println("DEBUG - new client !");
            
            // Démarre le processus de gestion d'un nouveau client
            UserConnectionManager userConnection =
                  new UserConnectionManager(this, socket, CLIENT_TIMEOUT);
            
//            Thread thread = new Thread(userConnection);
//            thread.start();
            
            // Enregistre le nouveau client
            synchronized(connections) {
               connections.add(userConnection);
            }
            
            
         }
         catch (PortException e) {
            log.push("Unable to accept new connection : " + e.getMessage());
         }
         
         
      }
      
   }
   
   public void removeConnection(UserConnectionManager userConnection) {
      synchronized(connections) {
         connections.remove(userConnection);
      }
   }
   
   public InetAddress getInetAdress() {
      return inetAddresses[0];
   }
   
   public int getPortNumber() {
      return serverPort.getPortNumber();
   }
   
   public int getUpdatePortNumber() {
      return updatePort.getPortNumber();
   }
   
   public UserAccount checkAuthentification(String login, String password) {
      UserAccount account = null;
      
      dbController.openConnection();
      account = dbController.checkUserConnection(login, password);
      dbController.closeConnection();
      
      return account;
   }
   
   public UserAccount createAccount(String login, String password, AccountType type) {
      UserAccount account = null;
      String role;
      
      switch (type) {
         case ADMINISTRATOR :
            role = "admin";
            break;
            
         case USER :
         default :
            role = "user";
      }
      
      dbController.openConnection();
      account = dbController.createUser(login, password, role);
      dbController.closeConnection();
      
      return account;
   }
   
   public Lobby getFreeLoby() {
      if (lobby.getNumberOfFreeSlots() > 0) {
         return lobby;
      }
      else {
         return null;
      }
   }
   
   public void askForUpdateChannel(UserConnectionManager connection, int code) {
      synchronized(expectedConnections) {
         expectedConnections.add(new ExpectedConnection(connection, code));
         expectedConnections.notifyAll();
      }
   }
   
   public Channel waitForUpdateChannel(int code) {
      Channel channel = null;
      
      while (channel == null) {
         synchronized (establishedNewConnections) {
            
            while (establishedNewConnections.isEmpty()) {
               try {
                  establishedNewConnections.wait();
               }
               catch (InterruptedException e) { }
            }
            
            Iterator<EstablishedNewConnection> it = establishedNewConnections.iterator();
            EstablishedNewConnection connection;
            while(it.hasNext()) {
               connection = it.next();
               if (connection.updateChannelCode == code) {
                  channel = connection.updateChannel;
                  it.remove();
               }
            }
            
         }
      }
      
      return channel;
   }
   
   private InetAddress[] getIps() {
      LinkedList<InetAddress> ipAddresses = new LinkedList<InetAddress>();

      try {
         Enumeration<NetworkInterface> interfaces = 
               NetworkInterface.getNetworkInterfaces();

         while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (!networkInterface.isVirtual() && networkInterface.isUp()) {

               Enumeration<InetAddress> ipEnum =
                     networkInterface.getInetAddresses();
               InetAddress ia;
               String address;

               while (ipEnum.hasMoreElements()) {
                  ia = ipEnum.nextElement();
                  address = ia.getHostAddress().toString();

                  // Ne prendre que les IPv4
                  if (address.length() < 16) {
                     // Ne pas prendre l'adresse IP Local ou une IPv6 courte
                     if (!address.startsWith("127")
                         && address.indexOf(":") < 0) {

                        ipAddresses.add(ia);
                     }
                  }

               }
            }

         }
      }
      catch (IOException e) {
         System.out.println("No network card !");
      }

      InetAddress[] adresses = new InetAddress[ipAddresses.size()];
      
      return ipAddresses.toArray(adresses);
      
   }
   
   private void init(boolean enableLogFrame) {
      
      System.out.print("DEBUG - init server port on "+ DEFAULT_PORT + " ...");
      serverPort = new Port(DEFAULT_PORT);
      serverPort.activateFreePort();
      System.out.println("done.");
      
      System.out.print("DEBUG - init update port on " + (serverPort.getPortNumber() + 1) + " ...");
      updatePort = new Port(serverPort.getPortNumber() + 1);
      updatePort.activateFreePort();
      updatePortActivity = new AcceptUpdateConnections(updatePort);
      System.out.println("done.");
      
      System.out.print("Start getting ips...");
      try {
         inetAddresses = getIps();
      }
      catch (Exception e) {
         System.err.println("Unable to obtain the IP address of the server");
      }
      System.out.println("done.");
      
      System.out.print("Creating log...");
      log = new Log("main");
      if (enableLogFrame) {
         System.out.println("done.");
         System.out.print("Creating log frame...");
         logsFrame = new LogsFrame("Onapi - Server", 15, 15, 500, 400);
         logsFrame.addLogPanel(log.createLogPanel());
      }
      System.out.println("done.");
      
      // Base de données
      System.out.print("Create path to database...");
      File file = new File("");
      file = file.getAbsoluteFile();
      
      // Astuce pour palier au problème du lancement depuis Eclipse ou depuis
      // une console
      if (file.getName().equalsIgnoreCase("bin")) {
         file = file.getParentFile();
      }
      
      databasePath = file.getAbsolutePath() + File.separator
            + DATABASE_DIRECTORY + File.separator + DATABASE_NAME;
      System.out.println("done.");
      
      log.push("Database used : " + databasePath);
      
      System.out.print("Create dbController...");
      dbController = new DBController(databasePath);
      System.out.println("done.");
      
      System.out.print("Create lobby...");
      lobby = new Lobby(10);
      System.out.println("done.");
      
   }
   
   private boolean initSuccessful(){
      return inetAddresses != null && inetAddresses.length > 0
            && databasePath != null;
   }
   
   private class ExpectedConnection {
      
      public UserConnectionManager userConnection;
      public int updateChannelCode;
      
      private ExpectedConnection(UserConnectionManager userConnection,
                                 int updateChannelCode) {
         this.userConnection = userConnection;
         this.updateChannelCode = updateChannelCode;
      }
      
   }
   
   private class EstablishedNewConnection {
      
      public Channel updateChannel;
      public int updateChannelCode;
      
      private EstablishedNewConnection(Channel updateChannel,
                                       int updateChannelCode) {
         this.updateChannel = updateChannel;
         this.updateChannelCode = updateChannelCode;
      }
      
      
   }
   
   private class AcceptUpdateConnections implements Runnable {
      private Port port;
      
      private boolean exit = false;
      private Thread activity;
      
      private Socket socket;
      
      private AcceptUpdateConnections(Port port) {
         this.port = port;
         activity = new Thread(this);
         activity.start();
      }

      @Override
      public void run() {
         int nbExpected = 0;
         while (!exit) {
            try {
               
               while (nbExpected == 0) {
                  synchronized(expectedConnections) {
                     nbExpected = expectedConnections.size();
                     
                     if (nbExpected == 0) {
                        try {
                           expectedConnections.wait();
                        }
                        catch (InterruptedException e) { }
                     }
                  }
               }
               
               System.out.println("DEBUG - wait for new client on update port.");
               
               socket = updatePort.accept();
               
//               synchronized(expectedConnections) {
//                  nbExpected = expectedConnections.size();
//               }
               
               if (nbExpected > 0) {
                  System.out.println("DEBUG - new client on update port !");
                  
                  (new Thread(new Runnable() {
                     private Socket socket = AcceptUpdateConnections.this.socket;
                     @Override
                     public void run() {
                        Channel channel = new Channel(socket, CLIENT_TIMEOUT);
                        int code = channel.receiveInt();
                        
                        // Cherche le bon client en attente
                        synchronized (expectedConnections) {
                           Iterator<ExpectedConnection> it = expectedConnections.iterator();
                           ExpectedConnection connection;
                           while(it.hasNext()) {
                              connection = it.next();
                              if (connection.updateChannelCode == code) {
                                 synchronized (connections) {
                                    connections.add(connection.userConnection);
                                 }
                                 
                                 synchronized (establishedNewConnections) {
                                    establishedNewConnections.add(new EstablishedNewConnection(channel, code));
                                    
                                    establishedNewConnections.notifyAll();
                                 }
                                 
                                 // Libération du code utilisé
                                 RandomGenerator.freeConnectionCode(connection.updateChannelCode);
                                 it.remove();
                                 
                                 
                              }
                           }
                        }
                     }
                  })).start();
               }
               
            }
            catch (PortException e) {
               // TODO oups alors
            }
         }
      }
      
   }

}
