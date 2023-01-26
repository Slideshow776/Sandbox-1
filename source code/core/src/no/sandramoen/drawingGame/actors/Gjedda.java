package no.sandramoen.drawingGame.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.drawingGame.actors.map.ImpassableTerrain;

public class Gjedda extends Fish{
    public Gjedda(float x, float y, Stage stage, Array<ImpassableTerrain> impassables) {
        super(x, y, stage, false, impassables);
        setSize(200, 80);
        setColor(Color.FIREBRICK);
        setBoundaryRectangle();
    }
}
