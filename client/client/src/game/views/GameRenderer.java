/* ============================================================================
 * Nom du fichier   : GameRenderer.java
 * ============================================================================
 * Date de création : 1 mai 2013
 * ============================================================================
 * Auteurs          : Crescenzio Fabio
 *                    Decorvet Grégoire
 *                    Jaquier Kevin
 *                    Schweizer Thomas
 * ============================================================================
 */
package game.views;

import game.models.GameModel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Gère l'affichage du jeu à l'écran.
 * 
 * @author Crescenzio Fabio
 * @author Decorvet Grégoire
 * @author Jaquier Kevin
 * @author Schweizer Thomas
 * 
 */
public class GameRenderer {

   /**
    * Modèle du jeu à afficher
    */
   private GameModel game;

   /**
    * Indique si on est en mode debug
    */
   private boolean debug;

   /**
    * Hauteur de la fenêtre graphique
    */
   private int width;

   /**
    * Largeur de la fenêtre graphique
    */
   private int height;

   /**
    * Scène des contrôles d'interface graphique
    */
   private Stage ui;

   /**
    * Gère l'affichage en mode debug
    */
   ShapeRenderer debugRenderer;

   // Contrôles de l'interface
   private Label lblOut;

   public GameRenderer(GameModel game, boolean debug) {
      this.game = game;
      this.debug = debug;
      if (debug) debugRenderer = new ShapeRenderer();
      ui = new Stage(width, height, true);
      Gdx.input.setInputProcessor(ui);
      initUI();
   }

   /**
    * Initialise l'interface graphique superposée
    */
   private void initUI() {
      // Charge les définitions d'apparence des contrôles
      final Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));

      Table table = new Table(skin);
      table.setFillParent(true);
      ui.addActor(table);

      lblOut = new Label("...", skin);
      table.add(lblOut);

      table.pack();
   }

   // temp
   public void update() {
      lblOut.setText("Pos : "
            + String.format("(%+3.2f, %+3.2f)", game.getPlayer().getPos().x,
                  game.getPlayer().getPos().y));
   }

   /**
    * Méthode appelée à chaque rafraîchissement de l'écran
    */
   public void render() {
      update();

      game.getPlayer().draw(ui.getSpriteBatch(), 1.0f);

      ui.act(Gdx.graphics.getDeltaTime());
      ui.draw();

      if (debug) debugRender();
   }

   /**
    * Affiche des données de debug à l'écran
    */
   public void debugRender() {

   }

   /**
    * (Re)définit la hauteur et la largeur de l'écran graphique
    * 
    * @param width
    *           Nouvelle largeur de l'écran graphique
    * @param height
    *           Nouvelle hauteur de l'écran graphique
    */
   public void setSize(int width, int height) {
      this.width = width;
      this.height = height;
      ui.setViewport(width, height, true);
   }

}
