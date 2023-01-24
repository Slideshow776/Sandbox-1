package no.sandramoen.drawingGame.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;

import no.sandramoen.drawingGame.actors.utils.BaseActor;

public class Ice extends BaseActor {
    public Ice(float x, float y, Stage stage) {
        super(x, y, stage);
        loadImage("iceTest");
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
}
