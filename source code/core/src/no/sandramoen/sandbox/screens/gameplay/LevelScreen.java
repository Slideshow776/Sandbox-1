package no.sandramoen.sandbox.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.sandbox.actors.Box;
import no.sandramoen.sandbox.actors.Fish;
import no.sandramoen.sandbox.utils.BaseGame;
import no.sandramoen.sandbox.utils.GameUtils;

public class LevelScreen extends no.sandramoen.sandbox.utils.BaseScreen {
    private Box startBox;
    private Box endBox;
    private Array<Box> boxes;
    private Array<Polyline> polylines;

    private Vector2 touchDownPoint;
    private float drawTime = -1;
    private final float MAX_DRAW_TIME = 20;

    private Fish fish;

    @Override
    public void initialize() {
        boxes = new Array();
        polylines = new Array();
        touchDownPoint = new Vector2();

        fish = new Fish(Gdx.graphics.getWidth() * .5f, Gdx.graphics.getHeight() * .5f, mainStage);
    }

    @Override
    public void update(float delta) {
        if (drawTime >= 0 && drawTime <= MAX_DRAW_TIME)
            drawTime += delta;
/*
        System.out.println(drawTime);*/
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        drawTime = 0;

        Vector3 worldCoordinates = mainStage.getCamera().unproject(new Vector3(screenX, screenY, 0f));
        touchDownPoint.x = worldCoordinates.x;
        touchDownPoint.y = worldCoordinates.y;

        startBox = new Box(worldCoordinates.x, worldCoordinates.y, mainStage);
        startBox.setColor(Color.CYAN);
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        drawTime = -1;
        drawPolyLinesStartingPosition();

        for (Box box : boxes)
            box.fadeAndRemove();
        boxes.clear();
        polylines.clear();

        touchDownPoint.x = 0;
        touchDownPoint.y = 0;

        Vector3 worldCoordinates = mainStage.getCamera().unproject(new Vector3(screenX, screenY, 0f));
        endBox = new Box(worldCoordinates.x, worldCoordinates.y, mainStage);
        endBox.setColor(Color.YELLOW);

        startBox.fadeAndRemove();
        endBox.fadeAndRemove();

        System.out.println("\nresetting\n");

        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 worldCoordinates = mainStage.getCamera().unproject(new Vector3(screenX, screenY, 0f));

        if (drawTime <= MAX_DRAW_TIME && drawTime >= 0 && isEnoughDistance(worldCoordinates)) {
            addPolyLine(worldCoordinates);
            drawPolyLine();
            checkIfPolyLinesIsAClosedShape();
        }
        return super.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE || keycode == Keys.Q)
            Gdx.app.exit();
        else if (keycode == Keys.R)
            BaseGame.setActiveScreen(new LevelScreen());
        return super.keyDown(keycode);
    }

    private boolean isEnoughDistance(Vector3 worldCoordinates) {
        final float DISTANCE = Gdx.graphics.getWidth() * .05f;
        if (polylines.size == 0) {
            if (GameUtils.isDistanceBigEnough(
                    new Vector2(touchDownPoint.x, touchDownPoint.y),
                    new Vector2(worldCoordinates.x, worldCoordinates.y),
                    DISTANCE
            ))
                return true;
        } else if (polylines.size > 0) {
            if (GameUtils.isDistanceBigEnough(
                    new Vector2(polylines.get(polylines.size - 1).getX(), polylines.get(polylines.size - 1).getY()),
                    new Vector2(worldCoordinates.x, worldCoordinates.y),
                    DISTANCE
            ))
                return true;
        } else {
            Gdx.app.error(getClass().getSimpleName(), "idk what happened trying to draw a PolyLine (size: " + polylines.size + " )...");
        }

        return false;
    }

    private void checkIfPolyLinesIsAClosedShape() {
        if (boxes.size > 2) {
            for (int i = 0; i < boxes.size - 1; i++) {
                if (boxes.get(boxes.size - 1) == boxes.get(i) || boxes.get(boxes.size - 2) == boxes.get(i)) {
                    continue;
                } else if (boxes.get(boxes.size - 1).overlaps(boxes.get(i))) {
                    drawTime = -1;
                    break;
                }

            }
        }
    }

    private void addPolyLine(Vector3 worldCoordinates) {
        if (polylines.size == 0) {
            Polyline polyline = new Polyline(new float[]{
                    touchDownPoint.x,
                    touchDownPoint.y,
                    worldCoordinates.x,
                    worldCoordinates.y
            });
            polyline.setOrigin(touchDownPoint.x, touchDownPoint.y);
            polyline.setPosition(worldCoordinates.x, worldCoordinates.y);
            polylines.add(polyline);
        } else if (polylines.size > 0) {
            Polyline polyLine = new Polyline(new float[]{
                    polylines.get(polylines.size - 1).getX(),
                    polylines.get(polylines.size - 1).getY(),
                    worldCoordinates.x,
                    worldCoordinates.y
            });
            polyLine.setOrigin(polylines.get(polylines.size - 1).getX(), polylines.get(polylines.size - 1).getY());
            polyLine.setPosition(worldCoordinates.x, worldCoordinates.y);
            polylines.add(polyLine);
        } else {
            Gdx.app.error(getClass().getSimpleName(), "idk what happened trying to draw a polyLine (size: " + polylines.size + " )...");
        }
    }

    private void drawPolyLine() {
        Polyline polyline = polylines.get(polylines.size - 1);
        Box box = new Box(polyline.getOriginX(), polyline.getOriginY(), mainStage);
        box.setSize(polyline.getLength(), 10);
        box.setRotation(getPolyLineAngle(polyline));
        box.setBoundaryRectangle();
        boxes.add(box);
        // boxes.add(new Box(worldCoordinates.x, worldCoordinates.y, mainStage));
    }

    private float getPolyLineAngle(Polyline polyline) {
        float angle = 0;

        float deltaY = polyline.getY() - polyline.getOriginY();
        float deltaX = polyline.getX() - polyline.getOriginX();

        if (deltaY >= 0 && deltaX >= 0) // 1st quadrant
            angle = MathUtils.asin((deltaY) / polyline.getLength());
        else if (deltaY >= 0 && deltaX <= 0) // 2nd quadrant
            angle = MathUtils.acos((deltaX) / polyline.getLength());
        else if (deltaY <= 0 && deltaX <= 0) // 3rd quadrant
            angle = MathUtils.atan2(deltaY, deltaX);
        else if (deltaY <= 0 && deltaX >= 0) // 4th quadrant
            angle = MathUtils.asin((deltaY) / polyline.getLength());
        else
            Gdx.app.error(getClass().getSimpleName(), "Error getting angle, PolyLine belongs to no quadrant!");

        return angle * MathUtils.radiansToDegrees;
    }

    private void drawPolyLinesStartingPosition() {
        for (Polyline polyline : polylines) {
            Box box = new Box(polyline.getX(), polyline.getY(), mainStage);
            box.setColor(Color.GREEN);
            box.fadeAndRemove();
        }
    }
}
