package no.sandramoen.sandbox.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/*
 * Ear Clipping Triangulation
 *
 * Copied from @author: "Two-Bit Coding" @ youtube.com
 * "Polygon Triangulation [1] - Overview of Ear Clipping" => https://www.youtube.com/watch?v=QAdfkylpYwc&t=282s
 */
public class Triangulation {

    public static Array<Polygon> triangulate(Array<Polyline> shapePolyLines) {
        Array<Vector2> vertices = getVertices(shapePolyLines);

        if (vertices == null) {
            Gdx.app.error(Triangulation.class.getSimpleName(), "Triangulation: The vertex list is null.");
            return null;
        }

        if (vertices.size < 3) {
            Gdx.app.error(Triangulation.class.getSimpleName(), "Triangulation: The vertex must have at least three vertices.");
            return null;
        }

        if (vertices.size > 1024) {
            Gdx.app.error(Triangulation.class.getSimpleName(), "Triangulation: The max vertex list length is 1024");
            return null;
        }

        if (!isSimplePolygon(shapePolyLines)) {
            Gdx.app.error(Triangulation.class.getSimpleName(), "Triangulation: The vertex list does not define a simple polygon.");
            return null;
        }

        if (isCoLinearEdges(vertices)) {
            Gdx.app.error(Triangulation.class.getSimpleName(), "Triangulation: The vertex contains collinear edges.");
            return null;
        }

        Array<Integer> indexList = new Array();
        for (int i = 0; i < vertices.size; i++)
            indexList.add(i);

        int totalTriangleCount = vertices.size - 2;
        int totalTrianglesIndexCount = totalTriangleCount * 3;
        int maxReflexAngles = vertices.size - 3;

        int[] triangles = new int[totalTrianglesIndexCount];
        int triangleIndexCount = 0;
        Array<Polygon> trianglePolygons = new Array();

        // Polygon Triangulation: Ear Clipping
        // System.out.println("Attempting to triangulate shape... " + indexList.size);
        while (indexList.size > 3) {
            // System.out.println("while loop");
            for (int i = 0; i < indexList.size; i++) {
                // System.out.println("for loop");
                int a = indexList.get(i);
                int b = (int) getNextItemOfLoopingArray(indexList, i - 1);
                int c = (int) getNextItemOfLoopingArray(indexList, i + 1);

                Vector2 va = vertices.get(a);
                Vector2 vb = vertices.get(b);
                Vector2 vc = vertices.get(c);

                Vector2 va_to_vb = vb.cpy().sub(va.cpy());
                Vector2 va_to_vc = vc.cpy().sub(va.cpy());

                // rule 1, is ear test vertex convex?
                float crossProduct = va_to_vb.cpy().crs(va_to_vc.cpy());
                if (!isClockwise(vertices))
                    crossProduct *= -1;

                if (crossProduct < 0) {// reflex angle detected
                    // System.out.println("reflex angle detected, at index i: " + i + ". Continuing...");
                    continue;
                }

                boolean isEar = true;

                // rule 2, Does test ear contain any polygon vertices?
                for (int j = 0; j < vertices.size; j++) {
                    if (j == a || j == b || j == c) {
                        // System.out.println("Point is already on triangle we are testing, at index i: " + i + ". Continuing...");
                        continue;
                    }

                    Vector2 p = vertices.get(j).cpy();
                    if (isPointInTriangle(p, vb.cpy(), va.cpy(), vc.cpy())) {
                        isEar = false;
                        // System.out.println("Ear contains a polygon vertex, at index i: " + i + ". Continuing...");
                        break;
                    }
                }

                if (isEar) { // success: passed rule 1 and 2
                    triangles[triangleIndexCount++] = b;
                    triangles[triangleIndexCount++] = a;
                    triangles[triangleIndexCount++] = c;
                    // System.out.println("Ear detected! removing index i: " + i + ", indexList.size: " + indexList.size);
                    indexList.removeIndex(i);

                    trianglePolygons.add(getTrianglePolygon(vb, va, vc));
                    break;
                }
            }
        }
        System.out.println("adding last triangle...");

        triangles[triangleIndexCount++] = indexList.get(0);
        triangles[triangleIndexCount++] = indexList.get(1);
        triangles[triangleIndexCount++] = indexList.get(2);

        trianglePolygons.add(getTrianglePolygon(
                vertices.get(indexList.get(0)),
                vertices.get(indexList.get(1)),
                vertices.get(indexList.get(2))
        ));


        if (triangles.length == 0)
            Gdx.app.error(Triangulation.class.getSimpleName(), "Triangulation: no triangles found, length of triangle list is: " + triangles.length);

        System.out.println("Triangulation success! \nThe shape was broken into " + trianglePolygons.size + " triangles");
        return trianglePolygons;
    }

    public static Object getNextItemOfLoopingArray(Array array, int index) {
        if (index >= array.size)
            return array.get(index % array.size);
        else if (index < 0)
            return array.get(index % array.size + array.size);
        else
            return array.get(index);
    }

    private static boolean isPointInTriangle(Vector2 p, Vector2 a, Vector2 b, Vector2 c) {
        Vector2 ab = b.sub(a);
        Vector2 bc = c.sub(b);
        Vector2 ca = a.sub(c);

        Vector2 ap = p.sub(a);
        Vector2 bp = p.sub(b);
        Vector2 cp = p.sub(c);

        float cross1 = ab.crs(ap);
        float cross2 = bc.crs(bp);
        float cross3 = ca.crs(cp);

        if (cross1 > 0 || cross2 > 0 || cross3 > 0)
            return false;
        return true;
    }

    private static Polygon getTrianglePolygon(Vector2 a, Vector2 b, Vector2 c) {
        return new Polygon(new float[]{
                a.x, a.y,
                b.x, b.y,
                c.x, c.y
        });
    }

    private static boolean isClockwise(Array<Vector2> vertices) {
        float sum = 0f;
        for (int i = 0; i < vertices.size - 1; i++) {
            sum += (vertices.get(i + 1).x - vertices.get(i).x) * (vertices.get(i + 1).y + vertices.get(i).y);
        }
        return sum > 0;
    }

    private static boolean isSimplePolygon(Array<Polyline> polyLines) {
        for (int i = 0; i < polyLines.size - 1; i++) {
            if (polyLines.get(i).getX() != polyLines.get(i + 1).getOriginX())
                return false;
            if (polyLines.get(i).getY() != polyLines.get(i + 1).getOriginY())
                return false;
        }
        if (polyLines.peek().getX() != polyLines.first().getOriginX())
            return false;
        if (polyLines.peek().getY() != polyLines.first().getOriginY())
            return false;

        return true;
    }

    private static boolean isCoLinearEdges(Array<Vector2> vertices) {
        for (int i = 0; i < vertices.size; i++) {
            for (int j = 0; j < vertices.size; j++) {
                if (vertices.get(i) == vertices.get(j))
                    continue;

                if (vertices.get(i).isCollinear(vertices.get(j)))
                    return true;
            }
        }
        return false;
    }

    private static float getPolygonArea(Array<Vector2> vertices) {
        float area = 0f;
        for (int i = 0; i < vertices.size; i++) {
            Vector2 a = vertices.get(i);
            Vector2 b = vertices.get((i + 1) % vertices.size);

            float dy = (a.y + b.y) / 2f;
            float dx = b.x - a.x;
            area += (dy * dx);
        }
        return Math.abs(area);
    }

    public static void printPolyLines(Array<Polyline> polyLines) {
        System.out.println("PolyLines consisting the shape's vertices ----------------");
        for (Polyline polyline : polyLines) {
            System.out.println("(" + polyline.getOriginX() + "," + polyline.getOriginY() + ")\t(" + polyline.getX() + ", " + polyline.getY() + ")]");
        }
        System.out.println("---------------------------------");
    }

    private static void printVertices(Array<Vector2> vertices) {
        System.out.println("The shape's vertices ----------------");
        for (Vector2 vertex : vertices) {
            System.out.println("(" + vertex.x + "," + vertex.y + ")");
        }
        System.out.println("---------------------------------");

    }

    private static Array<Vector2> getVertices(Array<Polyline> polyLines) {
        Array<Vector2> vertices = new Array();
        for (Polyline polyline : polyLines) {
            vertices.add(new Vector2(
                    polyline.getOriginX(),
                    polyline.getOriginY()
            ));
        }
        return vertices;
    }
}
