package no.sandramoen.sandbox.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.EarClippingTriangulator;

import no.sandramoen.sandbox.actors.Box;
import no.sandramoen.sandbox.actors.Fish;
import no.sandramoen.sandbox.utils.BaseGame;
import no.sandramoen.sandbox.utils.GameUtils;
import no.sandramoen.sandbox.utils.Triangulation;

import com.badlogic.gdx.utils.ShortArray;
import com.github.tommyettinger.textra.TypingLabel;

public class LevelScreen extends no.sandramoen.sandbox.utils.BaseScreen {
    private Array<Box> shapeOfBoxes;
    private Array<Polyline> polyLines;
    private Array<Polyline> closedShape;
    private boolean isEndDraw;

    private Vector2 touchDownPoint;
    private final int MAX_POLY_LINES = 400;
    private final float DISTANCE = Gdx.graphics.getWidth() * .01f;

    private Fish fish;

    private TypingLabel staminaLabel;

    private Array<Polygon> triangles;

    @Override
    public void initialize() {
        shapeOfBoxes = new Array();
        polyLines = new Array();
        touchDownPoint = new Vector2();

        /*fish = new Fish(Gdx.graphics.getWidth() * .5f, Gdx.graphics.getHeight() * .5f, mainStage);*/

        staminaLabel = new TypingLabel(MAX_POLY_LINES + "", new Label.LabelStyle(BaseGame.mySkin.get("arcade26", BitmapFont.class), null));
        uiTable.add(staminaLabel).expandY().top().padTop(Gdx.graphics.getHeight() * .02f);

        /*triangulationTest();*/
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        isEndDraw = false;
        Vector3 worldCoordinates = mainStage.getCamera().unproject(new Vector3(screenX, screenY, 0f));
        touchDownPoint.x = worldCoordinates.x;
        touchDownPoint.y = worldCoordinates.y;
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        endTurn();
        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 worldCoordinates = mainStage.getCamera().unproject(new Vector3(screenX, screenY, 0f));

        if (!isEndDraw && polyLines.size < MAX_POLY_LINES && isTouchDraggedEnoughDistance(worldCoordinates)) {
            addPolyLine(worldCoordinates);
            shapeOfBoxes.add(drawPolyLine(polyLines.peek(), Color.RED, 1f));
            staminaLabel.setText(String.valueOf(MAX_POLY_LINES - polyLines.size));
            checkIfClosedShape();
        }
        return super.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE || keycode == Keys.Q)
            Gdx.app.exit();
        else if (keycode == Keys.R)
            BaseGame.setActiveScreen(new LevelScreen());
        else if (keycode == Keys.T)
            triangulateShape(closedShape);
        return super.keyDown(keycode);
    }

    private boolean isTouchDraggedEnoughDistance(Vector3 worldCoordinates) {
        if (polyLines.size == 0) {
            if (GameUtils.isDistanceBigEnough(
                    new Vector2(touchDownPoint.x, touchDownPoint.y),
                    new Vector2(worldCoordinates.x, worldCoordinates.y),
                    DISTANCE
            ))
                return true;
        } else if (polyLines.size > 0) {
            if (GameUtils.isDistanceBigEnough(
                    new Vector2(polyLines.get(polyLines.size - 1).getX(), polyLines.get(polyLines.size - 1).getY()),
                    new Vector2(worldCoordinates.x, worldCoordinates.y),
                    DISTANCE
            ))
                return true;
        } else {
            Gdx.app.error(getClass().getSimpleName(), "idk what happened trying to draw a PolyLine (size: " + polyLines.size + " )...");
        }

        return false;
    }

    private void checkIfClosedShape() {
        if (shapeOfBoxes.size > 2) {
            for (int i = 0; i < shapeOfBoxes.size - 1; i++) {
                if (shapeOfBoxes.get(shapeOfBoxes.size - 1) == shapeOfBoxes.get(i) || shapeOfBoxes.get(shapeOfBoxes.size - 2) == shapeOfBoxes.get(i)) {
                    continue;
                } else if (shapeOfBoxes.get(shapeOfBoxes.size - 1).overlaps(shapeOfBoxes.get(i))) {
                    // get closed shape
                    closedShape = new Array();
                    for (int j = 0; j < polyLines.size - i - 1; j++)
                        closedShape.add(polyLines.get(i + j));

                    // bridge the gap of the closed shape
                    Polyline polyline = new Polyline(new float[]{
                            closedShape.peek().getX(),
                            closedShape.peek().getY(),
                            closedShape.first().getOriginX(),
                            closedShape.first().getOriginY(),
                    });
                    polyline.setOrigin(closedShape.peek().getX(), closedShape.peek().getY());
                    polyline.setPosition(closedShape.first().getOriginX(), closedShape.first().getOriginY());
                    closedShape.add(polyline);

                    // draw shape to be triangulated
                    drawPolyLine(closedShape.first(), Color.CYAN, 1);
                    drawPolyLine(closedShape.peek(), Color.CYAN, 1).setOpacity(.5f);
                    for (int j = 1; j < closedShape.size - 1; j++)
                        drawPolyLine(closedShape.get(j), Color.BLUE, 1);

                    System.out.println("Will crash?: " + (getPolyLineAngle(closedShape.peek()) < 90) + ", rotation: " + getPolyLineAngle(closedShape.peek()));

                    // EarClippingTriangulator.compute

                    //TODO: 1) konverter alt til float[] og mat inn i triangulatoren
                    float[] touchPoints = new float[closedShape.size * 2];
                    for (int j = 0; j < closedShape.size - 1; j++) {
                        touchPoints[j] = closedShape.get(j).getOriginX();
                        touchPoints[j + 1] = closedShape.get(j + 1).getOriginY();
                    }
                    EarClippingTriangulator triangulator = new EarClippingTriangulator();
                    ShortArray triangles = triangulator.computeTriangles(touchPoints);

                    for (int j = 0; j < triangles.size; j++) {
                        System.out.println(triangles.get(j));
                    }

                    // 1st triangle
                    for (int j = 0; j < triangles.size; j+=3) {
                        Polygon triangle = new Polygon(new float[]{
                                closedShape.get(triangles.get(j + 0)).getOriginX(),
                                closedShape.get(triangles.get(j + 0)).getOriginY(),
                                closedShape.get(triangles.get(j + 1)).getOriginX(),
                                closedShape.get(triangles.get(j + 1)).getOriginY(),
                                closedShape.get(triangles.get(j + 2)).getOriginX(),
                                closedShape.get(triangles.get(j + 2)).getOriginY()
                        });
                        drawTriangle(triangle);
                    }

                    //TODO: 2) tegn triangulatoren

                    // TODO: triangulate here?
                    // triangulateShape(closedShape);

                    endTurn();
                    break;
                }
            }
        }
    }

    private void drawTriangle(Polygon triangle) {
        Array<Polyline> polyLines = new Array();
        Color randomColor = GameUtils.randomLightColor();
        for (int i = 0; i < 6; i += 2) {
            Polyline polyLine = new Polyline(new float[]{
                    triangle.getVertices()[(i + 0) % 6],
                    triangle.getVertices()[(i + 1) % 6],
                    triangle.getVertices()[(i + 2) % 6],
                    triangle.getVertices()[(i + 3) % 6]
            });
            polyLine.setOrigin(triangle.getVertices()[(i + 0) % 6], triangle.getVertices()[(i + 1) % 6]);
            polyLine.setPosition(triangle.getVertices()[(i + 2) % 6], triangle.getVertices()[(i + 3) % 6]);
            polyLines.add(polyLine);
        }

        for (Polyline polyline : polyLines)
            drawPolyLine(polyline, randomColor, 2);
    }

    private void addPolyLine(Vector3 worldCoordinates) {
        if (polyLines.size == 0) {
            Polyline polyline = new Polyline(new float[]{
                    MathUtils.floor(touchDownPoint.x),
                    MathUtils.floor(touchDownPoint.y),
                    MathUtils.floor(worldCoordinates.x),
                    MathUtils.floor(worldCoordinates.y)
            });
            polyline.setOrigin(MathUtils.floor(touchDownPoint.x), MathUtils.floor(touchDownPoint.y));
            polyline.setPosition(MathUtils.floor(worldCoordinates.x), MathUtils.floor(worldCoordinates.y));
            polyLines.add(polyline);
        } else if (polyLines.size > 0) {
            Polyline polyLine = new Polyline(new float[]{
                    MathUtils.floor(polyLines.get(polyLines.size - 1).getX()),
                    MathUtils.floor(polyLines.get(polyLines.size - 1).getY()),
                    MathUtils.floor(worldCoordinates.x),
                    MathUtils.floor(worldCoordinates.y)
            });
            polyLine.setOrigin(MathUtils.floor(polyLines.get(polyLines.size - 1).getX()), MathUtils.floor(polyLines.get(polyLines.size - 1).getY()));
            polyLine.setPosition(MathUtils.floor(worldCoordinates.x), MathUtils.floor(worldCoordinates.y));
            polyLines.add(polyLine);
        } else {
            Gdx.app.error(getClass().getSimpleName(), "idk what happened trying to draw a polyLine (size: " + polyLines.size + " )...");
        }
    }

    private Box drawPolyLine(Polyline polyline, Color color, float height) {
        Box box = new Box(polyline.getOriginX(), polyline.getOriginY(), mainStage);
        box.setSize(polyline.getLength(), height);
        box.setRotation(getPolyLineAngle(polyline));
        box.setBoundaryRectangle();
        box.setColor(color);
        return box;
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

    private void endTurn() {
        if (!isEndDraw) {
            isEndDraw = true;

            clearBoxes(shapeOfBoxes);
            System.out.println("----");
            polyLines.clear();

            touchDownPoint.x = 0;
            touchDownPoint.y = 0;

            staminaLabel.setText(String.valueOf(MAX_POLY_LINES));
        }
    }

    private void clearBoxes(Array<Box> boxes) {
        /*for (Box box : boxes)
            box.fadeAndRemove();
        boxes.clear();*/
    }

    private void triangulationTest() {
        /*Array<Vector2> vertices = new Array();
         *//*vertices.add(new Vector2(-4, 6));
        vertices.add(new Vector2(0, 2));
        vertices.add(new Vector2(2, 5));
        vertices.add(new Vector2(7, 0));
        vertices.add(new Vector2(5, -6));
        vertices.add(new Vector2(3, 3));
        vertices.add(new Vector2(0, -5));
        vertices.add(new Vector2(-6, 0));
        vertices.add(new Vector2(-2, 1));*//*
        vertices.add(new Vector2(0, 0));
        vertices.add(new Vector2(0, 10));
        vertices.add(new Vector2(10, 10));
        vertices.add(new Vector2(10, 0));

        Array<Polygon> trianglePolygons = Triangulation.triangulate(vertices);

        System.out.println("we found " + trianglePolygons.size + " triangles!");
        for (Polygon polygon : trianglePolygons) {
            for (float vertex : polygon.getVertices())
                System.out.println(vertex);
            System.out.println();
        }*/
    }

    private void triangulateShape(Array<Polyline> closedShape) {
        // TODO: collision detection via triangulation
        triangles = Triangulation.triangulate(closedShape);
        for (Polygon triangle : triangles)
            drawTriangle(triangle);
    }
}
