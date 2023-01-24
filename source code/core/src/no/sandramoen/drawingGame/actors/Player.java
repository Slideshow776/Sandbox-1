package no.sandramoen.drawingGame.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;
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
        collisionBox.setSize(Gdx.graphics.getWidth() * .0025f, Gdx.graphics.getHeight() * .0025f);
        collisionBox.setPosition(
                getWidth() / 2 - collisionBox.getWidth() / 2,
                getHeight() / 2 - collisionBox.getHeight() / 2
        );
        collisionBox.setBoundaryRectangle();
        collisionBox.setDebug(true);
        addActor(collisionBox);
    }

    public void move(Array<Polyline> polylines, RunnableAction runnableAction) {
        moveAlongPolylines(polylines);
        addAction(Actions.after(runnableAction));
    }

    public BaseActor getCollisionBox() {
        collisionBox.setPosition(
                getX() + getWidth() / 2 - collisionBox.getWidth() / 2,
                getY() + getHeight() / 2 - collisionBox.getHeight() / 2
        );
        return collisionBox;
    }

    public void die() {
        setOrigin(Align.bottom);
        clearActions();
        addAction(Actions.scaleTo(1, 0, .5f));
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
}
