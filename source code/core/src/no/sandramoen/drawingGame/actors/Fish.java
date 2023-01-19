package no.sandramoen.drawingGame.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.drawingGame.actors.utils.BaseActor;

public class Fish extends BaseActor {
    private boolean isFrozen = MathUtils.randomBoolean();
    private final float ACCELERATION = 500;
    private final float MAX_MOVEMENT_SPEED = 50;

    private float changeDirectionCounter;
    private final float CHANGE_DIRECTION_FREQUENCY = 2;

    public Fish(float x, float y, Stage stage) {
        super(x, y, stage);
        loadImage("whitePixel");
        setColor(Color.LIGHT_GRAY);
        setSize(50f, 20f);
        centerAtPosition(x, y);
        setOrigin(Align.center);

        setBoundaryRectangle();

        setAcceleration(ACCELERATION);
        setMaxSpeed(MAX_MOVEMENT_SPEED);
        setDeceleration(ACCELERATION);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (true) {//!isFrozen) {
            periodicallyChangeDirection(delta);
            keepInsideScreen();
            accelerateAtAngle(getRotation());
            applyPhysics(delta);
        }
    }

    public void fadeAndRemove() {
        addAction(Actions.sequence(
                Actions.fadeOut(1f),
                Actions.run(() -> remove())
        ));
    }

    private void periodicallyChangeDirection(float delta) {
        changeDirectionCounter += delta;
        if (changeDirectionCounter >= CHANGE_DIRECTION_FREQUENCY) {
            addAction(Actions.rotateBy(MathUtils.random(-90f, 90f), CHANGE_DIRECTION_FREQUENCY));
            changeDirectionCounter = 0;
        }
    }

    private void keepInsideScreen() {
        if (getX() > Gdx.graphics.getWidth())
            setRotation(180);
        else if (getX() < 0)
            setRotation(0);
        if (getY() > Gdx.graphics.getHeight())
            setRotation(270);
        else if (getY() < 0)
            setRotation(90);
    }
}
