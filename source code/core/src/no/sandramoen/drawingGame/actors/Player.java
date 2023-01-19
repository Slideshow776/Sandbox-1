package no.sandramoen.drawingGame.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;

import no.sandramoen.drawingGame.actors.utils.BaseActor;

public class Player extends BaseActor {
    public Player(float x, float y, Stage stage) {
        super(x, y, stage);
        loadImage("whitePixel");
        setColor(Color.MAGENTA);
        setSize(25f, 25f);
        centerAtPosition(x, y);
    }
}
