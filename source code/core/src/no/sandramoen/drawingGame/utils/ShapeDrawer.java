package no.sandramoen.drawingGame.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ShortArray;

import no.sandramoen.drawingGame.actors.map.TilemapActor;
import no.sandramoen.drawingGame.actors.utils.Box;
import no.sandramoen.drawingGame.actors.utils.BaseActor;

public class ShapeDrawer {
    public final int MAX_POLY_LINES = 100;
    public Array<Polyline> polylines;

    private final float DISTANCE_BETWEEN_POLYLINES = .5f;

    private Array<Box> lineSegmentBoxes;
    private Array<Polygon> triangles;
    private Array<Polygon> lastDrawnTriangles;
    private Array<Polygon> collisionPolygons;
    private Array<Polygon> lastDrawnPolygon;
    private Array<Polyline> closedShape;

    private Polygon collisionPolygon;
    private Stage stage;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;

    public ShapeDrawer(Stage stage) {
        this.stage = stage;

        lineSegmentBoxes = new Array();
        polylines = new Array();
        collisionPolygons = new Array();
        triangles = new Array();
        lastDrawnTriangles = new Array();
        closedShape = new Array();
        lastDrawnPolygon = new Array();

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        Gdx.gl20.glLineWidth(2);
        spriteBatch = new SpriteBatch();
    }

    public void drawMasks(Camera camera) {
        /* use the relative camera position, instead of the absolute screen position*/
        shapeRenderer.setProjectionMatrix(camera.combined);

        /* Clear our depth buffer info from previous frame. */
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);

        /* Set the depth function to LESS. */
        Gdx.gl.glDepthFunc(GL20.GL_LESS);

        /* Enable depth writing. */
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        /* Disable RGBA color writing. */
        Gdx.gl.glColorMask(false, false, false, false);

        /* Render mask elements. */
        shapeRenderer.begin();
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);

        if (!triangles.isEmpty())
            for (Polygon polygon : triangles)
                shapeRenderer.triangle(
                        polygon.getVertices()[0],
                        polygon.getVertices()[1],
                        polygon.getVertices()[2],
                        polygon.getVertices()[3],
                        polygon.getVertices()[4],
                        polygon.getVertices()[5]
                );

        shapeRenderer.flush();
        shapeRenderer.end();
    }

    public void drawMasked(float delta, BaseActor baseActor, Camera camera) {
        /* use the relative camera position, instead of the absolute screen position*/
        spriteBatch.setProjectionMatrix(camera.combined);

        /* Enable RGBA color writing. */
        Gdx.gl.glColorMask(true, true, true, true);

        /* Set the depth function to LESS. */
        Gdx.gl.glDepthFunc(GL20.GL_LESS);

        /* Render masked elements. */
        spriteBatch.begin();
        spriteBatch.draw(baseActor.animation.getKeyFrame(delta), 0, 0, TilemapActor.mapWidth, TilemapActor.mapHeight);
        spriteBatch.end();
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }

    private void drawOutlinesAroundHolesInIce() { // TODO:
        throw new java.lang.UnsupportedOperationException("Not supported yet.");
        /*shapeRenderer.begin();
        shapeRenderer.setColor(Color.BLACK);
        for (Polygon polygon : collisionPolygons)
            shapeRenderer.polygon(polygon.getVertices());
        shapeRenderer.end();*/
    }

    public boolean isEnoughDistanceToDrawNewSegment(Vector2 start, Vector2 end) {
        if (polylines.size < MAX_POLY_LINES && isEnoughDistance(start, end))
            return true;
        return false;
    }

    public void drawNewLineSegment(Vector2 start, Vector2 end) {
        addPolyLine(start, end);
        lineSegmentBoxes.add(drawPolyLine(polylines.peek(), Color.GRAY, 1 / 8f, false));
    }

    public void addTriangles() {
        for (Polygon triangle : lastDrawnTriangles)
            triangles.add(triangle);
    }

    public void drawClosedShape() {
        for (Polyline polyline : closedShape)
            drawPolyLine(polyline, Color.BLUE, 4, false);

        for (Polygon triangle : triangles)
            drawTriangle(triangle);
    }

    public boolean isCollisionDetected(BaseActor baseActor) {
        // return collisionByShape(collisionPolygons, baseActor);
        return collisionByTriangles(baseActor);
    }

    public boolean isCollisionDetectedOnLastDrawnShapes(BaseActor baseActor) {
        return collisionByShape(lastDrawnPolygon, baseActor);
    }

    public void reset() {
        polylines.clear();
        clearBoxes(lineSegmentBoxes);
    }

    public void addCollisionPolygon() {
        if (collisionPolygon != null) {
            collisionPolygons.add(collisionPolygon);

            // detect overlapping polygons
            // while (detectOverlappingPolygons());
        }
    }

    private boolean detectOverlappingPolygons() {
        for (int i = 0; i < collisionPolygons.size; i++) {
            jLoop:
            for (int j = 0; j < collisionPolygons.size; j++) {
                Polygon polygonA = collisionPolygons.get(i);
                Polygon polygonB = collisionPolygons.get(j);

                if (polygonA == polygonB)
                    continue;

                for (int k = 0; k < polygonB.getVertices().length - 1; k++) {
                    if (polygonA.contains(new Vector2(
                            polygonB.getVertices()[k],
                            polygonB.getVertices()[k + 1]
                    ))) {
                        if (polygonA.getBoundingRectangle().width < polygonB.getBoundingRectangle().width) {
                            collisionPolygons.removeValue(polygonA, false);
                            return true;
                        } else if (polygonA.getBoundingRectangle().width > polygonB.getBoundingRectangle().width) {
                            collisionPolygons.removeValue(polygonB, false);
                            return true;
                        }
                        break jLoop;
                    }
                }
            }
        }
        return false;
    }


    private boolean collisionByTriangles(BaseActor baseActor) {
        if (triangles != null)
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
                    polygon != null &&
                            (
                                    polygon.contains(new Vector2(baseActor.getX(), baseActor.getY())) &&
                                            polygon.contains(new Vector2(baseActor.getX(), baseActor.getY() + baseActor.getHeight())) &&
                                            polygon.contains(new Vector2(baseActor.getX() + baseActor.getWidth(), baseActor.getY() + baseActor.getHeight())) &&
                                            polygon.contains(new Vector2(baseActor.getX() + baseActor.getWidth(), baseActor.getY()))
                            )
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


    public void checkIfClosedShape() {
        lastDrawnPolygon.clear();
        if (polylines.size < 3)
            return;

        for (int i = 2; i < lineSegmentBoxes.size; i++) {
            for (int j = 2; j < lineSegmentBoxes.size; j++) {

                // don't collide with self
                if (lineSegmentBoxes.get(i) == lineSegmentBoxes.get(j))
                    continue;

                // don't collide with neighbors
                if (i == j + 1 || i == j - 1)
                    continue;

                // collide with everything else
                if (lineSegmentBoxes.get(i).overlaps(lineSegmentBoxes.get(j))) {
                    triangulate(getClosedShape(i, j));

                    // only check that closed shape once
                    if (i < j)
                        i = j;
                    else if (j < i)
                        j = i;
                }
            }
        }
    }

    private Array<Polyline> getClosedShape(int i, int j) {
        Array<Polyline> closedShape = new Array();
        for (int k = Math.min(i, j); k < Math.max(i, j); k++)
            closedShape.add(polylines.get(k));

        if (closedShape.isEmpty())
            Gdx.app.error(getClass().getSimpleName(), "getClosedShape => failed to create array => i: " + i + ", j: " + j);
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
            drawPolyLine(polyline, color, 1 / 8f, true);
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
        addTriangles();
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
        for (Polyline polyline : closedShape) {
            points[i] = polyline.getOriginX();
            points[i + 1] = polyline.getOriginY();
            i += 2;
        }

        collisionPolygon = new Polygon(points);
        lastDrawnPolygon.add(new Polygon(points));

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
            lastDrawnTriangles.add(triangle);
        }
    }
}
