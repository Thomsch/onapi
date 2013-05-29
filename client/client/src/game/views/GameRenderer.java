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
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
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
    * Camera de la vue
    */
   private OrthographicCamera cam;

   private Rectangle viewport;

   /**
    * Scène des contrôles d'interface graphique
    */
   private Stage ui;

   // Objets pour le rendu en mode debug
   private ShapeRenderer debugRenderer = new ShapeRenderer();
   private FPSLogger fpsLog;
   private Label lblDebug;

   private RayHandler handler;

   public GameRenderer(GameModel game, boolean debug) {
      this.game = game;
      this.debug = debug;

      // Résolution jeu
      this.height = 720;
      this.width = 1280;

      // Active la synchronisation verticale
      Gdx.graphics.setVSync(true);

      // Initialise la caméra
      this.cam = new OrthographicCamera(width, height);
      viewport = new Rectangle(0, 0, width, height);

      debugRenderer.setProjectionMatrix(cam.combined);
      fpsLog = new FPSLogger();

      // Initialise la scène de l'interface utilisateur
      ui = new Stage(width, height, true);

      Gdx.input.setInputProcessor(ui);
      initUI();

      // Initialise le gestionnaire de lumières
      handler = game.getRayHandler();
      handler.setBlurNum(3);

   }

   /**
    * Initialise l'interface graphique superposée
    */
   private void initUI() {
      // Charge les définitions d'apparence des contrôles
      final Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));

      Table table = new Table(skin);
      table.setFillParent(true);
      table.top().left();
      ui.addActor(table);

      lblDebug = new Label("...", skin);
      table.add(lblDebug);

      table.pack();
   }

   /**
    * Méthode appelée à chaque rafraîchissement de l'écran
    */
   public void render() {
      GL20 gl = Gdx.graphics.getGL20();

      // Met à jour l'état du moteur physique
      game.getWorld().step(1 / 60f, 6, 2);

      // Mise à jour de la caméra
      gl.glViewport((int) viewport.x, (int) viewport.y, (int) viewport.width,
            (int) viewport.height);
      Vector2.tmp.set(game.getPlayer().getPos().x - cam.position.x, game
            .getPlayer().getPos().y - cam.position.y);
      cam.translate(Vector2.tmp);
      cam.update();

      // Affichage des entités du jeu
      ui.getSpriteBatch().setProjectionMatrix(cam.combined);
      ui.getSpriteBatch().begin();
      game.getMap().draw(ui.getSpriteBatch(), 1.0f);
      game.getPlayer().draw(ui.getSpriteBatch(), 1.0f);
      ui.getSpriteBatch().end();

      // Affichage des informations de debug
      if (debug) debugRender();

      // Met à jour les lumières
      handler.setCombinedMatrix(cam.combined);
      handler.updateAndRender();

      // Affichage de l'interface graphique
      ui.act(Gdx.graphics.getDeltaTime());
      ui.draw();

   }

   /**
    * Affiche des données de debug à l'écran
    */
   public void debugRender() {
      fpsLog.log();
      lblDebug.setText(String.format("%s", game.getPlayer(), game.getMap()));

      debugRenderer.setProjectionMatrix(cam.combined);
      game.getMap().debugRender(debugRenderer);
      game.getPlayer().debugRender(debugRenderer);
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
      cam = new OrthographicCamera(width, height);
      viewport.setHeight(height);
      viewport.setWidth(width);
      ui.setViewport(width, height, true);
   }

}
