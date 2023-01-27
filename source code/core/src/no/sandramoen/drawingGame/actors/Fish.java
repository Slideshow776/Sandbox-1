package no.sandramoen.drawingGame.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.drawingGame.actors.map.ImpassableTerrain;
import no.sandramoen.drawingGame.actors.map.TilemapActor;
import no.sandramoen.drawingGame.actors.utils.BaseActor;

public class Fish extends BaseActor {
    public boolean isRemoved;
    public static final float REMOVE_TIME = 1f;

    private boolean isFrozen;
    private final float ACCELERATION = 6;
    private final float MAX_MOVEMENT_SPEED = 3;
    private float changeDirectionCounter;
    private final float CHANGE_DIRECTION_FREQUENCY = 2;
    private Array<ImpassableTerrain> impassables;

    public Fish(float x, float y, Stage stage, boolean isFrozen, Array<ImpassableTerrain> impassables) {
        super(x, y, stage);
        this.isFrozen = isFrozen;
        this.impassables = impassables;

        loadImage("whitePixel");
        setColor(Color.BLACK);
        setSize(2, 2);
        setOrigin(Align.center);
        setRotation(MathUtils.random(0f, 360f));

        setBoundaryRectangle();

        if (!isFrozen) {
            MathUtils.randomBoolean();
            setAcceleration(ACCELERATION);
            setMaxSpeed(MAX_MOVEMENT_SPEED);
            setDeceleration(ACCELERATION);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!isFrozen) {
            periodicallyChangeDirection(delta);
            keepInsideScreen();
            accelerateAtAngle(getRotation());
            applyPhysics(delta);

            for (ImpassableTerrain impassableTerrain : impassables)
                preventOverlap(impassableTerrain);
        }
    }

    public void fadeAndRemove(RunnableAction removeFromList) {
        addAction(Actions.sequence(
                Actions.fadeOut(REMOVE_TIME),
                removeFromList,
                Actions.run(() -> {
                    isRemoved = true;
                    remove();
                })
        ));
    }

    private void periodicallyChangeDirection(float delta) {
        changeDirectionCounter += delta;
        if (changeDirectionCounter >= CHANGE_DIRECTION_FREQUENCY) {
            addAction(Actions.rotateBy(MathUtils.random(-45f, 45f), CHANGE_DIRECTION_FREQUENCY));
            changeDirectionCounter = 0;
        }
    }

    private void keepInsideScreen() {
        if (getX() > TilemapActor.mapWidth)
            addAction(Actions.rotateTo(180, 1f));
        else if (getX() < 0)
            addAction(Actions.rotateTo(0, 1f));
        if (getY() > TilemapActor.mapHeight)
            addAction(Actions.rotateTo(270, 1f));
        else if (getY() < 0)
            addAction(Actions.rotateTo(90, 1f));
    }
}
