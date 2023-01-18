package no.sandramoen.sandbox.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ShortArray;

import no.sandramoen.sandbox.actors.Box;
import no.sandramoen.sandbox.actors.utils.BaseActor;

public class ShapeDrawer {
    public final int MAX_POLY_LINES = 400;
    public Array<Polyline> polylines;

    private final boolean IS_DEBUG = true;
    private final float DISTANCE_BETWEEN_POLYLINES = Gdx.graphics.getWidth() * .01f;
    private Array<Box> collisionBoxes;
    private Array<Polygon> triangles;
    private Stage stage;

    public ShapeDrawer(Stage stage) {
        this.stage = stage;

        collisionBoxes = new Array();
        polylines = new Array();
    }

    public boolean isItPossibleToDrawNewSegment(Vector2 start, Vector2 end) {
        if (polylines.size < MAX_POLY_LINES && isEnoughDistance(start, end))
            return true;
        return false;
    }

    public boolean drawNewLineSegment(Vector2 start, Vector2 end) {
        addPolyLine(start, end);
        collisionBoxes.add(drawPolyLine(polylines.peek(), Color.RED, 1f));
        return checkIfClosedShape();
    }

    public boolean isCollisionDetected(BaseActor baseActor) {
        if (triangles != null)
            for (Polygon triangle : triangles) {
                if (
                        triangle.contains(baseActor.getX(), baseActor.getY()) ||
                                triangle.contains(baseActor.getX() + baseActor.getWidth(), baseActor.getY() + baseActor.getHeight())
                ) return true;
            }
        return false;
    }


    private boolean isEnoughDistance(Vector2 start, Vector2 end) {
        if (!polylines.isEmpty())
            start = new Vector2(polylines.peek().getX(), polylines.peek().getY());
        if (GameUtils.isDistanceBigEnough(start, end, DISTANCE_BETWEEN_POLYLINES))
            return true;
        return false;
    }

    private void addPolyLine(Vector2 start, Vector2 end) {
        if (!polylines.isEmpty())
            start = new Vector2(polylines.peek().getX(), polylines.peek().getY());
        polylines.add(myPolyline(start.x, start.y, end.x, end.y));
    }


    private boolean checkIfClosedShape() {
        if (polylines.size < 3)
            return false;

        for (int i = 0; i < polylines.size - 1; i++) {
            if (isIndexTwoLastPlacesInArray(polylines, i)) {
                continue;
            } else if (collisionBoxes.peek().overlaps(collisionBoxes.get(i))) {
                triangles = triangulate(getClosedShape(i));
                return true;
            }
        }
        return false;
    }

    private boolean isIndexTwoLastPlacesInArray(Array array, int index) {
        if (array.get(array.size - 1) == array.get(index) || array.get(array.size - 2) == array.get(index))
            return true;
        return false;
    }

    private Array<Polyline> getClosedShape(int i) {
        Array<Polyline> closedShape = new Array();
        for (int j = 0; j < polylines.size - i - 1; j++)
            closedShape.add(polylines.get(i + j));
        return closedShape;
    }


    private void drawTriangle(Polygon triangle) {
        Array<Polyline> trianglePolylines = new Array();
        Color color = GameUtils.randomLightColor();
        for (int i = 0; i < 6; i += 2)
            trianglePolylines.add(myPolyline(
                    triangle.getVertices()[(i + 0) % 6],
                    triangle.getVertices()[(i + 1) % 6],
                    triangle.getVertices()[(i + 2) % 6],
                    triangle.getVertices()[(i + 3) % 6]
            ));

        for (Polyline polyline : trianglePolylines)
            drawPolyLine(polyline, color, 2);
    }

    private Box drawPolyLine(Polyline polyline, Color color, float height) {
        Box box = new Box(polyline.getOriginX(), polyline.getOriginY(), stage);
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


    public void reset() {
        clearBoxes(collisionBoxes);
        polylines.clear();
        triangles = null;
    }

    private void clearBoxes(Array<Box> boxes) {
        for (Box box : boxes)
            box.fadeAndRemove();
        boxes.clear();
    }

    private Polyline myPolyline(float startX, float startY, float endX, float endY) {
        Polyline polyline = new Polyline(new float[]{startX, startY, endX, endY});
        polyline.setOrigin(startX, startY);
        polyline.setPosition(endX, endY);
        return polyline;
    }


    private Array<Polygon> triangulate(Array<Polyline> openShape) {
        Array<Polyline> closedShape = closeTheShape(openShape);
        ShortArray triangles = computeTriangles(closedShape);
        return constructTriangles(closedShape, triangles);
    }

    private Array<Polyline> closeTheShape(Array<Polyline> openShape) {
        openShape.add(myPolyline(
                openShape.peek().getX(),
                openShape.peek().getY(),
                openShape.first().getOriginX(),
                openShape.first().getOriginY()
        ));
        return openShape;
    }

    private ShortArray computeTriangles(Array<Polyline> closedShape) {
        float[] points = new float[closedShape.size * 2];

        int i = 0;
        for (Polyline polyline2 : closedShape) {
            points[i] = polyline2.getOriginX();
            points[i + 1] = polyline2.getOriginY();
            i += 2;
        }

        EarClippingTriangulator earClippingTriangulator = new EarClippingTriangulator();
        return earClippingTriangulator.computeTriangles(points);
    }

    private Array<Polygon> constructTriangles(Array<Polyline> closedShape, ShortArray triangles) {
        Array<Polygon> polygonTriangles = new Array();
        for (int i = 0; i < triangles.size; i += 3) {
            Polygon triangle = new Polygon(new float[]{
                    closedShape.get(triangles.get(i + 0)).getOriginX(),
                    closedShape.get(triangles.get(i + 0)).getOriginY(),
                    closedShape.get(triangles.get(i + 1)).getOriginX(),
                    closedShape.get(triangles.get(i + 1)).getOriginY(),
                    closedShape.get(triangles.get(i + 2)).getOriginX(),
                    closedShape.get(triangles.get(i + 2)).getOriginY()
            });
            polygonTriangles.add(triangle);

            if (IS_DEBUG)
                drawTriangle(triangle);
        }
        return polygonTriangles;
    }
}
