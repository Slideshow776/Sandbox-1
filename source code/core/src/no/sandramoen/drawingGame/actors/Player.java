package no.sandramoen.drawingGame.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.drawingGame.actors.utils.BaseActor;

public class Player extends BaseActor {
    private final float SPEED = .035f;
    private BaseActor collisionBox;

    public Player(float x, float y, Stage stage) {
        super(x, y, stage);
        loadImage("whitePixel");
        setColor(Color.MAGENTA);
        setSize(40f, 65f);
        centerAtPosition(x, y);

        collisionBox = new BaseActor(0, 0, stage);
        collisionBox.setSize(getWidth() * .4f, getHeight() * .2f);
        collisionBox.setX(collisionBox.getWidth() * .75f);
        collisionBox.setBoundaryRectangle();
        collisionBox.setDebug(true);
        addActor(collisionBox);
    }

    public void move(Array<Polyline> polylines) {
        moveAlongPolylines(polylines);
    }

    public void move(Array<Polyline> polylines, RunnableAction runnableAction) {
        moveAlongPolylines(polylines);
        addAction(Actions.after(runnableAction));
    }

    private void moveAlongPolylines(Array<Polyline> polylines) {
        for (Polyline polyline : polylines) {
            addAction(Actions.after(Actions.moveTo(
                    polyline.getX() - getWidth() / 2,
                    polyline.getY() - getHeight() / 2,
                    SPEED)
            ));
        }
    }

    public void die() {
        setOrigin(Align.bottom);
        clearActions();
        addAction(Actions.scaleTo(1, 0, .5f));
    }
}
