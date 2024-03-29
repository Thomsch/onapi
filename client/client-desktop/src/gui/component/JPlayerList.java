/* ============================================================================
 * Nom du fichier   : JPlayerList.java
 * ============================================================================
 * Date de création : 14 juin 2013
 * ============================================================================
 * Auteurs          : Crescenzio Fabio
 *                    Decorvet Grégoire
 *                    Jaquier Kevin
 *                    Schweizer Thomas
 * ============================================================================
 */
package gui.component;

import gui.models.GameServerPlayerStatus;

import javax.swing.JList;

import core.PlayerInfo;

/**
 * 
 * TODO
 * @author Crescenzio Fabio
 * @author Decorvet Grégoire
 * @author Jaquier Kevin
 * @author Schweizer Thomas
 *
 */
public class JPlayerList extends JList<PlayerInfo> {

   private static final long serialVersionUID = -3753161992210297207L;
   
   private GameServerPlayerStatus model;
      
   public JPlayerList(int slotNumber) {
      model = new GameServerPlayerStatus(slotNumber);
      setModel(model);
   }
   
   public void update(PlayerInfo updatedUser) {
      model.get(updatedUser.getSlotNumber()).update(updatedUser);
      
      repaint();
   }
   
}
