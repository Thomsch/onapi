/* ============================================================================
 * Nom du fichier   : ScreenAdapter.java
 * ============================================================================
 * Date de création : 4 mai 2013
 * ============================================================================
 * Auteurs          : Crescenzio Fabio
 *                    Decorvet Grégoire
 *                    Jaquier Kevin
 *                    Schweizer Thomas
 * ============================================================================
 */
package game.screens;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;

/**
 * Adapteur pour pas que les screens n'aient à implémenter les méthodes dont
 * elles ne se servent pas.
 * 
 * @author Crescenzio Fabio
 * @author Decorvet Grégoire
 * @author Jaquier Kevin
 * @author Schweizer Thomas
 * 
 */
public abstract class ScreenAdapter implements Screen, InputProcessor {

   @Override
   public boolean keyDown(int keycode) {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean keyUp(int keycode) {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean keyTyped(char character) {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean touchDown(int screenX, int screenY, int pointer, int button) {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean touchUp(int screenX, int screenY, int pointer, int button) {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean touchDragged(int screenX, int screenY, int pointer) {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean mouseMoved(int screenX, int screenY) {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean scrolled(int amount) {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public void render(float delta) {
      // TODO Auto-generated method stub

   }

   @Override
   public void resize(int width, int height) {
      // TODO Auto-generated method stub

   }                                                                                                                                                                                            
   
   @Override
   public void show() {
      // TODO Auto-generated method stub

   }

   @Override
   public void hide() {
      // TODO Auto-generated method stub

   }

   @Override
   public void pause() {
      // TODO Auto-generated method stub

   }

   @Override
   public void resume() {
      // TODO Auto-generated method stub

   }

   @Override
   public void dispose() {
      // TODO Auto-generated method stub

   }

}
