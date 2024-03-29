package client;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class GameLauncher {
   GameData initData;
   private boolean debug;

   public GameLauncher(boolean debug) {
      this.debug = debug;
   }

   public void run(GameData gameData) {
      LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
      cfg.title = "ONAPI";
      cfg.useGL20 = true;
      cfg.width = 1280;
      cfg.height = 720;

      new LwjglApplication(new Onapi(this.debug, gameData), cfg);
   }

}
