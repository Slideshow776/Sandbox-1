package no.sandramoen.drawingGame.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.drawingGame.actors.utils.BaseActor;

public class Basket extends BaseActor {
    public boolean isDead;
    public Basket(float x, float y, Stage stage) {
        super(x, y, stage);

        loadImage("whitePixel");
        setSize(65f, 55f);
        centerAtPosition(x, y);
        setColor(Color.LIME);
        setBoundaryRectangle();
    }

    public void die() {
        isDead = true;
        setOrigin(Align.bottom);
        clearActions();
        addAction(Actions.scaleTo(1, 0, .5f));
    }
}
