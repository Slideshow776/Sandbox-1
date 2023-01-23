package no.sandramoen.drawingGame.utils;

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

import no.sandramoen.drawingGame.actors.Box;
import no.sandramoen.drawingGame.actors.utils.BaseActor;

public class ShapeDrawer {
    public final int MAX_POLY_LINES = 60;
    public Array<Polyline> polylines;
    public Array<Polygon> triangles;

    private final float DISTANCE_BETWEEN_POLYLINES = Gdx.graphics.getWidth() * .01f;
    private Array<Box> collisionBoxes;
    private Array<Polygon> collisionPolygons;
    private Polygon collisionPolygon;
    private Array<Polyline> closedShape;
    private Stage stage;
    private Array<Polygon> trianglesToBeAdded;

    public ShapeDrawer(Stage stage) {
        this.stage = stage;

        collisionBoxes = new Array();
        polylines = new Array();
        collisionPolygons = new Array();
        triangles = new Array();
        trianglesToBeAdded = new Array();
    }

    public boolean isItPossibleToDrawNewSegment(Vector2 start, Vector2 end) {
        if (polylines.size < MAX_POLY_LINES && isEnoughDistance(start, end))
            return true;
        return false;
    }

    public boolean drawNewLineSegment(Vector2 start, Vector2 end) {
        addPolyLine(start, end);
        collisionBoxes.add(drawPolyLine(polylines.peek(), Color.DARK_GRAY, 1f, false));
        return checkIfClosedShape();
    }

    public void addTriangles() {
        for (Polygon triangle : trianglesToBeAdded)
            triangles.add(triangle);
    }

    public void drawClosedShape() {
        for (Polyline polyline : closedShape)
            drawPolyLine(polyline, Color.BLUE, 4, false);

        for (Polygon triangle : triangles)
            drawTriangle(triangle);
    }

    public boolean isCollisionDetected(BaseActor baseActor) {
        return collisionByShape(collisionPolygons, baseActor);
        // return collisionByTriangles(baseActor);
    }

    public boolean isCollisionDetectedOnLastDrawnShape(BaseActor baseActor) {
        Array<Polygon> temp = new Array();
        temp.add(collisionPolygon);
        return collisionByShape(temp, baseActor);
    }

    public void reset() {
        clearBoxes(collisionBoxes);
        polylines.clear();/*
        triangles = null;*/
    }

    public void addCollisionPolygon() {
        if (collisionPolygon != null)
            collisionPolygons.add(collisionPolygon);
    }


    private boolean collisionByTriangles(BaseActor baseActor) {
        if (triangles != null) // collision by triangles
            for (Polygon triangle : triangles) {
                if (
                        triangle.contains(baseActor.getX(), baseActor.getY()) ||
                                triangle.contains(baseActor.getX() + baseActor.getWidth(), baseActor.getY() + baseActor.getHeight())
                ) return true;
            }
        return false;
    }

    private boolean collisionByShape(Array<Polygon> collisionPolygons, BaseActor baseActor) {
        for (Polygon polygon : collisionPolygons)
            if (
                    polygon.contains(new Vector2(baseActor.getX(), baseActor.getY())) &&
                            polygon.contains(new Vector2(baseActor.getX(), baseActor.getY() + baseActor.getHeight())) &&
                            polygon.contains(new Vector2(baseActor.getX() + baseActor.getWidth(), baseActor.getY() + baseActor.getHeight())) &&
                            polygon.contains(new Vector2(baseActor.getX() + baseActor.getWidth(), baseActor.getY()))
            )
                return true;
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
                triangulate(getClosedShape(i));
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
        Color color = new Color(0, 1, 1, MathUtils.random(.2f, .3f));
        for (int i = 0; i < 6; i += 2)
            trianglePolylines.add(myPolyline(
                    triangle.getVertices()[(i + 0) % 6],
                    triangle.getVertices()[(i + 1) % 6],
                    triangle.getVertices()[(i + 2) % 6],
                    triangle.getVertices()[(i + 3) % 6]
            ));

        for (Polyline polyline : trianglePolylines)
            drawPolyLine(polyline, color, 2, true);
    }

    private Box drawPolyLine(Polyline polyline, Color color, float height, boolean fade) {
        Box box = new Box(polyline.getOriginX(), polyline.getOriginY(), stage);
        box.setSize(polyline.getLength(), height);
        box.setRotation(getPolyLineAngle(polyline));
        box.setBoundaryRectangle();
        box.setColor(color);
        if (fade)
            box.fadeAndRemove();
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


    private void triangulate(Array<Polyline> openShape) {
        closedShape = closeTheShape(openShape);
        ShortArray triangles = computeTriangles(closedShape);
        constructTriangles(closedShape, triangles);
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

        collisionPolygon = new Polygon(points);

        EarClippingTriangulator earClippingTriangulator = new EarClippingTriangulator();
        return earClippingTriangulator.computeTriangles(points);
    }

    private void constructTriangles(Array<Polyline> closedShape, ShortArray triangulatedTriangles) {
        for (int i = 0; i < triangulatedTriangles.size; i += 3) {
            Polygon triangle = new Polygon(new float[]{
                    closedShape.get(triangulatedTriangles.get(i + 0)).getOriginX(),
                    closedShape.get(triangulatedTriangles.get(i + 0)).getOriginY(),
                    closedShape.get(triangulatedTriangles.get(i + 1)).getOriginX(),
                    closedShape.get(triangulatedTriangles.get(i + 1)).getOriginY(),
                    closedShape.get(triangulatedTriangles.get(i + 2)).getOriginX(),
                    closedShape.get(triangulatedTriangles.get(i + 2)).getOriginY()
            });
            trianglesToBeAdded.add(triangle);
        }
    }
}
