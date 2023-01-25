package no.sandramoen.drawingGame.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public class Gjedde extends Fish{
    public Gjedde(float x, float y, Stage stage, Array<ImpassableTerrain> impassables) {
        super(x, y, stage, false, impassables);
        setSize(200, 80);
        setColor(Color.FIREBRICK);
        setBoundaryRectangle();
    }
}
