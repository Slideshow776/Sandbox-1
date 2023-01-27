package no.sandramoen.drawingGame.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.drawingGame.actors.map.ImpassableTerrain;
import no.sandramoen.drawingGame.actors.map.TilemapActor;

public class Gjedda extends Fish{
    public Gjedda(float x, float y, Stage stage, Array<ImpassableTerrain> impassables) {
        super(x, y, stage, false, impassables);
        loadImage("actors/fish/gjedda");
        setSize(12, 5);
        setOrigin(Align.center);
        setColor(Color.FIREBRICK);
        setBoundaryRectangle();
    }
}
