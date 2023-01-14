package no.sandramoen.sandbox.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class GameUtils {

    public static void saveGameState() {
        BaseGame.preferences.putBoolean("loadPersonalParameters", true);
        BaseGame.preferences.putFloat("musicVolume", BaseGame.musicVolume);
        BaseGame.preferences.putFloat("soundVolume", BaseGame.soundVolume);
        BaseGame.preferences.putFloat("voiceVolume", BaseGame.voiceVolume);
        BaseGame.preferences.flush();
    }

    public static void loadGameState() {
        BaseGame.preferences = Gdx.app.getPreferences("Sandbox1GameState");
        BaseGame.loadPersonalParameters = BaseGame.preferences.getBoolean("loadPersonalParameters");
        BaseGame.musicVolume = BaseGame.preferences.getFloat("musicVolume");
        BaseGame.soundVolume = BaseGame.preferences.getFloat("soundVolume");
        BaseGame.voiceVolume = BaseGame.preferences.getFloat("voiceVolume");
    }

    /*public static void setWidgetHoverColor(final Widget widget) {
        widget.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                widget.setColor(BaseGame.redColor);
                BaseGame.hoverOverEnterSound.play(BaseGame.soundVolume);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                widget.setColor(BaseGame.whiteColor);
            }
        });
    }*/

    public static void playLoopingMusic(Music music) {
        music.setVolume(BaseGame.musicVolume);
        music.setLooping(true);
        music.play();
    }

    public static void playLoopingMusic(Music music, float volume) {
        music.setVolume(volume);
        music.setLooping(true);
        music.play();
    }

    public static void stopAllMusic() {

    }

    public static float normalizeValue(float value, float min, float max) {
        return (value - min) / (max - min);
    }

    public static boolean isTouchDownEvent(Event event) {
        return event instanceof InputEvent && ((InputEvent) event).getType() == InputEvent.Type.touchDown;
    }

    public static ShaderProgram initShaderProgram(String vertexShader, String fragmentShader) {
        ShaderProgram.pedantic = false;
        ShaderProgram shaderProgram = new ShaderProgram(vertexShader, fragmentShader);
        if (!shaderProgram.isCompiled())
            Gdx.app.error(GameUtils.class.getSimpleName(), "Error: Couldn't compile shader => " + shaderProgram.getLog());
        return shaderProgram;
    }

    public static void printLoadingTime(String tag, String message, long startTime) {
        long endTime = System.currentTimeMillis();
        Gdx.app.log(tag, message + " took " + (endTime - startTime) + " ms to load.");
    }

    public static boolean isDistanceBigEnough(Vector2 k1, Vector2 k2, Float distance) {
        return distanceBetween(k1, k2) >= distance;
    }

    public static boolean isWithinDistance(Vector2 k1, Vector2 k2, Float distance) {
        return distanceBetween(k1, k2) <= distance;
    }

    public static float distanceBetween(Vector2 k1, Vector2 k2) {
        return (float) Math.sqrt(Math.pow(Math.abs(k2.x - k1.x), 2) + Math.pow(Math.abs(k2.y - k1.y), 2));
    }
}
