/* ============================================================================
 * Nom du fichier   : PlayersStatus.java
 * ============================================================================
 * Date de création : 1 juin 2013
 * ============================================================================
 * Auteurs          : Crescenzio Fabio
 *                    Decorvet Grégoire
 *                    Jaquier Kevin
 *                    Schweizer Thomas
 * ============================================================================
 */
package common.components.gameserver;

import java.io.Serializable;

import common.components.ObservableComponent;

/**
 * TODO
 * @author Crescenzio Fabio
 * @author Decorvet Grégoire
 * @author Jaquier Kevin
 * @author Schweizer Thomas
 *
 */
public class PlayerStatus extends ObservableComponent implements Serializable {

   /**
    * ID de sérialisation
    */
   private static final long serialVersionUID = -9212231828720224663L;

   private int slotNumber;
   
   private String name;
   
   private int teamNumber = 0;
   
   private boolean isReady = false;
   
   public PlayerStatus(String name, int slotNumber) {
      this.name = name;
      this.slotNumber = slotNumber;
   }
   
   public int getSlotNumber() {
      return slotNumber;
   }
   
   public boolean isReady() {
      return isReady;
   }
   
   public String getName() {
      return name;
   }
   
   public int getTeamNumber() {
      return teamNumber;
   }
   
   public void setName(String name) {
      this.name = name;
      setChangedAndNotifyObservers();
   }
   
   /**
    * Définit le numéro d'équipe du joueur à cet emplacement.
    * @param number - le numéro d'équipe, un 0 indique qu'il n'en a pas.
    */
   public void setTeamNumber(int number) {
      if (teamNumber != number && number >= 0) {
         teamNumber = number;
         setChangedAndNotifyObservers();
      }
   }
   
   /**
    * Définit le status d'emplacement libre (le joueur a quitté).
    */
   public void setLeft() {
      name = "";
      setChangedAndNotifyObservers();
   }
   
   public boolean isFree() {
      return name == null || name.isEmpty();
   }
   
   public void setReady(boolean ready) {
      if (isReady != ready) {
         isReady = ready;
         setChangedAndNotifyObservers();
      }
   }
   
   public void updateFrom(PlayerStatus status) {
      this.name = status.name;
      this.teamNumber = status.teamNumber;
      this.isReady = status.isReady;
      setChangedAndNotifyObservers();
   }
   
   @Override
   public String toString() {
      return (isReady ? " + " : " - ") + "[" + teamNumber + "] - " + name;
   }
   
}