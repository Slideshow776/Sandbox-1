package no.sandramoen.drawingGame.actors.map;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.drawingGame.actors.utils.BaseActor;
import no.sandramoen.drawingGame.utils.BaseGame;

public class Water extends BaseActor {
    private Array<TextureAtlas.AtlasRegion> animationImages = new Array();

    public Water(float x, float y, Stage stage) {
        super(x, y, stage);
        setSize(TiledMapActor.mapWidth, TiledMapActor.mapHeight);

        animationImages.add(BaseGame.textureAtlas.findRegion("water/waterTest0"));/*
        animationImages.add(BaseGame.textureAtlas.findRegion("water/waterTest1"));*/
        animation = new Animation(2f, animationImages, Animation.PlayMode.LOOP);
    }
}
