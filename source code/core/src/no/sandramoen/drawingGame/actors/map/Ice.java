package no.sandramoen.drawingGame.actors.map;

import com.badlogic.gdx.scenes.scene2d.Stage;

import no.sandramoen.drawingGame.actors.utils.BaseActor;

public class Ice extends BaseActor {
    public Ice(float x, float y, Stage stage) {
        super(x, y, stage);
        loadImage("iceTest");
        setSize(TiledMapActor.mapWidth, TiledMapActor.mapHeight);
    }
}
