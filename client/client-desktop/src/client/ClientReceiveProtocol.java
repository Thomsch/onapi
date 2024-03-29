/* ============================================================================
 * Nom du fichier   : ClientReceiveProtocol.java
 * ============================================================================
 * Date de création : 19 mai 2013
 * ============================================================================
 * Auteurs          : Crescenzio Fabio
 *                    Decorvet Grégoire
 *                    Jaquier Kevin
 *                    Schweizer Thomas
 * ============================================================================
 */
package client;

import common.components.ConnectedUser;
import common.components.gameserver.PlayerStatus;
import common.connections.Channel;
import common.connections.protocol.ProtocolType;

import core.PlayersInformations;
import core.UsersInformations;

/**
 * Classe permettant de rassembler les protocoles utilisés par le client pour
 * réagir aux mises à jours en provenance du serveur.
 * <p>Utilisé lors d'une partie pour la réception des informations de ladite
 * partie.
 * 
 * <p><b>TODO</b> : définir les objets à modifier. Définir s'ils sont modifiés
 * ici ou donnés en valeur de retour.
 * 
 * @author Crescenzio Fabio
 * @author Decorvet Grégoire
 * @author Jaquier Kevin
 * @author Schweizer Thomas
 *
 */
public class ClientReceiveProtocol {

   private Channel channel;
   
   private GameLauncher launcher;
   
   public ClientReceiveProtocol(Channel channel, GameLauncher launcher) {
      this.channel = channel;
      this.launcher = launcher;
   }
   
   /**
    * Protocol à utiliser en réponse à un ping du serveur.
    */
   public void ping() {
      synchronized (channel) {
         channel.sendProtocolType(ProtocolType.PING);
      }
   }
   
   /**
    * Protocole bidon pour test.
    */
   @Deprecated
   public String updateMessage() {
      String message;
      
      synchronized (channel) {
         message = channel.receiveString();
      }
      
      return message;
   }
   
   /**
    * TODO
    * @param toDefine - TODO
    */
   public void lobbyUpdateSlotStatus(PlayersInformations players) {
      PlayerStatus status;
      
      synchronized (channel) {
         status = (PlayerStatus)channel.receiveObject();
      }
      
      if (players != null) {
      
         if (status.isFree()) {
            players.remove(status);
         }
         else {
            players.addOrUpdate(status);
         }
      }
      else {
         System.out.println("DEBUG et merde");
      }
      
   }
   
   /**
    * TODO
    * @param toDefine - TODO
    */
   public void lobbyUpdateGameReady(GameData data) {
      launcher.run(data);
   }
   
   public void adminUpdateServerSlotStatus(UsersInformations users) {
      ConnectedUser connectedUser;
      
      synchronized (channel) {
         connectedUser = (ConnectedUser)channel.receiveObject();
      }
      
      if(connectedUser.isConnected()) {
         users.addOrUpdate(connectedUser);
      }
      else {
         users.remove(connectedUser);
      }
   }
   
   public void adminKicked(GameData model) {
      String message = channel.receiveString();
      
      // TODO - temp en attendant la fonction fournie
      System.out.println("You have been kicked : " + message);
   }
   
}
