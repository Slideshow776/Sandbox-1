package no.sandramoen.sandbox.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import no.sandramoen.sandbox.actors.utils.BaseActor;

public class Fish extends BaseActor {

    public Fish(float x, float y, Stage stage) {
        super(x, y, stage);
        loadImage("whitePixel");
        setColor(Color.LIGHT_GRAY);
        setSize(50f, 20f);
        centerAtPosition(x, y);

        setBoundaryRectangle();
    }

    public void fadeAndRemove() {
        addAction(Actions.sequence(
                Actions.fadeOut(1f),
                Actions.run(() -> remove())
        ));
    }
}
