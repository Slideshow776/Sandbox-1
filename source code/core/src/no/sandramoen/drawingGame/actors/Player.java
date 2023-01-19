package no.sandramoen.drawingGame.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.drawingGame.actors.utils.BaseActor;

public class Player extends BaseActor {
    private final float SPEED = .035f;

    public Player(float x, float y, Stage stage) {
        super(x, y, stage);
        loadImage("whitePixel");
        setColor(Color.MAGENTA);
        setSize(40f, 65f);
        centerAtPosition(x, y);
    }

    public void movePlayer(Array<Polyline> polylines) {
        for (Polyline polyline : polylines) {
            addAction(Actions.after(Actions.moveTo(
                    polyline.getX() - getWidth() / 2,
                    polyline.getY() - getHeight() / 2,
                    SPEED)
            ));
        }
    }
}
