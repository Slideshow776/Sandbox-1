package no.sandramoen.drawingGame.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class Gjedde extends Fish{
    public Gjedde(float x, float y, Stage stage) {
        super(x, y, stage, false);
        setSize(200, 80);
        setColor(Color.FIREBRICK);
        setBoundaryRectangle();
    }
}
