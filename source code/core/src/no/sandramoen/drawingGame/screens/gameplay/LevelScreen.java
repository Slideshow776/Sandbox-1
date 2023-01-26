package no.sandramoen.drawingGame.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.drawingGame.actors.Basket;
import no.sandramoen.drawingGame.actors.Fish;
import no.sandramoen.drawingGame.actors.Gjedda;
import no.sandramoen.drawingGame.actors.map.Ice;
import no.sandramoen.drawingGame.actors.map.ImpassableTerrain;
import no.sandramoen.drawingGame.actors.Player;
import no.sandramoen.drawingGame.actors.map.TilemapActor;
import no.sandramoen.drawingGame.actors.map.Water;
import no.sandramoen.drawingGame.actors.utils.BaseActor;
import no.sandramoen.drawingGame.ui.CancelDrawing;
import no.sandramoen.drawingGame.ui.Speedometer;
import no.sandramoen.drawingGame.ui.StaminaBar;
import no.sandramoen.drawingGame.utils.BaseGame;
import no.sandramoen.drawingGame.screens.BaseScreen;
import no.sandramoen.drawingGame.utils.GameUtils;
import no.sandramoen.drawingGame.utils.ShapeDrawer;

import com.github.tommyettinger.textra.TypingLabel;

public class LevelScreen extends BaseScreen {
    private boolean isDrawing;
    private boolean isGameOver;
    private boolean isGettingFish;
    private Vector2 touchDownPoint;

    private ShapeDrawer shapeDrawer;

    private Array<Fish> fishes;
    private int numLevelFishes;
    private Player player;
    private Gjedda gjedda;
    private Basket basket;
    private BaseActor ice;
    private Array<ImpassableTerrain> impassables;

    private TypingLabel fishLabel;
    private TypingLabel turnLabel;
    private CancelDrawing cancelDrawing;
    private StaminaBar staminaBar;
    private Speedometer speedometer;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private TilemapActor tilemap;

    private int numTurns;
    private float touchDraggedDistance;
    private Vector2 lastTouchDragged;

    @Override
    public void initialize() {
        // system
        touchDownPoint = new Vector2();
        shapeDrawer = new ShapeDrawer(mainStage);
        tilemap = new TilemapActor(BaseGame.testMap, mainStage);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        Gdx.gl20.glLineWidth(2);
        spriteBatch = new SpriteBatch();
        System.out.println("\nrestart\n");
        initializeActors();

        lastTouchDragged = new Vector2(player.getX(), player.getY());

        // GUI
        initializeGUI();
    }

    @Override
    public void update(float delta) {
        checkLooseConditions();
    }

    @Override
    public void drawGroundStage(float delta) {
        super.drawGroundStage(delta);
        // drawOutlinesAroundHolesInIce();
        drawMasks();
        drawMasked(delta);
    }

    private void drawMasks() {
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
        shapeRenderer.set(ShapeType.Filled);

        if (!shapeDrawer.triangles.isEmpty())
            for (Polygon polygon : shapeDrawer.triangles)
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

    private void drawMasked(float delta) {
        /* Enable RGBA color writing. */
        Gdx.gl.glColorMask(true, true, true, true);

        /* Set the depth function to LESS. */
        Gdx.gl.glDepthFunc(GL20.GL_LESS);

        /* Render masked elements. */
        spriteBatch.begin();
        spriteBatch.draw(ice.animation.getKeyFrame(delta), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 worldCoordinates = waterStage.getCamera().unproject(new Vector3(screenX, screenY, 0f));
        if (GameUtils.isWithinDistance(
                new Vector2(worldCoordinates.x, worldCoordinates.y),
                new Vector2(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2),
                Gdx.graphics.getWidth() * .04f)
        ) {
            isDrawing = true;
            cancelDrawing.addAction(Actions.alpha(1, 1f));
            touchDownPoint.set(worldCoordinates.x, worldCoordinates.y);
        }
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        endTurn();
        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (isDrawing) {
            Vector3 worldCoordinates = waterStage.getCamera().unproject(new Vector3(screenX, screenY, 0f));

            if (isOutOfBounds(worldCoordinates))
                return super.touchDragged(screenX, screenY, pointer);

            if (isOnImpassableTerrain(worldCoordinates)) {
                touchUp(screenX, screenY, pointer, 0);
                return super.touchDragged(screenX, screenY, pointer);
            }

            if (cancelDrawing.getBoundaryPolygon().contains(new Vector2(worldCoordinates.x, worldCoordinates.y))) {
                isDrawing = false;
                shapeDrawer.reset();
                return super.touchDragged(screenX, screenY, pointer);
            }

            if (shapeDrawer.isEnoughDistanceToDrawNewSegment(touchDownPoint, new Vector2(worldCoordinates.x, worldCoordinates.y))) {
                shapeDrawer.drawNewLineSegment(touchDownPoint, new Vector2(worldCoordinates.x, worldCoordinates.y));
                updateStaminaBar();
                updateSpeedometer(worldCoordinates);
            }
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

    private void collisionDetection() {
        for (BaseActor fish : fishes) {
            if (shapeDrawer.isCollisionDetectedOnLastDrawnShapes(fish)) {
                isGettingFish = true;
                RunnableAction removeFromList = Actions.run(() -> {
                    fishes.removeValue((Fish) fish, false);
                    isGettingFish = false;
                });
                ((Fish) fish).fadeAndRemove(removeFromList);
                fishLabel.setText("{FASTER}Remaining fishes: " + (fishes.size - 1));
            }
        }

        if (shapeDrawer.isCollisionDetected(basket))
            basket.die();

        if (!basket.isDead)
            basket.addAction(Actions.sequence(
                    Actions.delay(Fish.REMOVE_TIME * 1.1f),
                    Actions.run(() -> {
                        if (!isGettingFish && player.overlaps(basket) && numLevelFishes - fishes.size > 0 && !isGameOver) {
                            isGameOver = true;
                            pause();
                            System.out.println("level complete! You got " + (numLevelFishes - fishes.size) + " out of " + numLevelFishes + " fishes..");
                        }
                    })
            ));
    }

    private void endTurn() {
        if (isDrawing) {
            RunnableAction doThisAfter = Actions.run(() -> {
                staminaBar.reset();
                shapeDrawer.checkIfClosedShape();
                collisionDetection();
                shapeDrawer.reset();
            });
            player.move(shapeDrawer.polylines, doThisAfter);
            resetTurn();
        }
    }

    private void resetTurn() {
        isDrawing = false;
        speedometer.setZero();
        touchDownPoint.set(0, 0);
        fishLabel.setText("Remaining fishes: " + fishes.size);
        turnLabel.setText("Turns: " + ++numTurns);
        cancelDrawing.addAction(Actions.alpha(.25f, 1f));
    }

    private void updateStaminaBar() {
        float percent = (shapeDrawer.MAX_POLY_LINES - shapeDrawer.polylines.size) / (float) shapeDrawer.MAX_POLY_LINES;
        staminaBar.set(percent);
    }

    private void updateSpeedometer(Vector3 worldCoordinates) {
        touchDraggedDistance = new Vector2(worldCoordinates.x, worldCoordinates.y).sub(lastTouchDragged).len();
        speedometer.set(GameUtils.normalizeValue(touchDraggedDistance, 0, 80));
        lastTouchDragged.set(worldCoordinates.x, worldCoordinates.y);
    }

    private void drawOutlinesAroundHolesInIce() {
        shapeRenderer.begin();
        shapeRenderer.setColor(Color.BLACK);
        for (Polygon polygon : shapeDrawer.collisionPolygons)
            shapeRenderer.polygon(polygon.getVertices());
        shapeRenderer.end();
    }

    private boolean isOutOfBounds(Vector3 worldCoordinates) {
        if (
                worldCoordinates.x > Gdx.graphics.getWidth() ||
                        worldCoordinates.x < 0 ||
                        worldCoordinates.y > Gdx.graphics.getHeight() ||
                        worldCoordinates.y < 0
        )
            return true;
        return false;
    }

    private boolean isOnImpassableTerrain(Vector3 worldCoordinates) {
        for (ImpassableTerrain impassableTerrain : impassables)
            if (impassableTerrain.getBoundaryPolygon().contains(new Vector2(worldCoordinates.x, worldCoordinates.y)))
                return true;
        return false;
    }

    private void checkLooseConditions() {
        if (shapeDrawer.isCollisionDetected(player.getCollisionBox()) && !player.isDead) {
            player.die();
            System.out.println("You fell in the water!");
        }

        if (player.getCollisionBox().overlaps(gjedda) && !player.isDead) {
            for (ImpassableTerrain impassableTerrain : impassables) {
                if (player.overlaps(impassableTerrain))
                    return;
            }
            System.out.println("Gjedda got you!");
            player.die();
        }
    }

    private void initializeActors() {
        // actors
        fishes = new Array();
        impassables = new Array();

        ice = new Ice(0, 0, waterStage);
        new Water(0, 0, waterStage);

        MapLoader mapLoader = new MapLoader(mainStage, waterStage, tilemap, player, gjedda, basket, fishes, impassables);
        player = mapLoader.player;
        impassables = mapLoader.impassables;
        gjedda = mapLoader.gjedda;
        fishes = mapLoader.fishes;
        numLevelFishes = fishes.size;
        basket = mapLoader.basket;
    }

    private void initializeGUI() {
        cancelDrawing = new CancelDrawing(Gdx.graphics.getWidth() * .1f, Gdx.graphics.getHeight() * .98f, uiStage);
        staminaBar = new StaminaBar(Gdx.graphics.getWidth() * .5f, Gdx.graphics.getHeight() * .98f, uiStage);
        speedometer = new Speedometer(Gdx.graphics.getWidth() * .85f, Gdx.graphics.getHeight() * .98f, uiStage);

        fishLabel = new TypingLabel("{FASTER}Remaining fishes: " + fishes.size, new Label.LabelStyle(BaseGame.mySkin.get("arcade26", BitmapFont.class), null));
        fishLabel.setColor(Color.FOREST);
        fishLabel.setAlignment(Align.center);

        turnLabel = new TypingLabel("Turns: " + numTurns, new Label.LabelStyle(BaseGame.mySkin.get("arcade26", BitmapFont.class), null));
        turnLabel.setColor(Color.FOREST);
        turnLabel.setAlignment(Align.center);

        uiTable.padTop(staminaBar.getHeight() + Gdx.graphics.getHeight() * .02f);
        uiTable.defaults().padTop(Gdx.graphics.getHeight() * .02f);
        uiTable.add(fishLabel).prefWidth(Gdx.graphics.getWidth()).row();
        uiTable.add(turnLabel).prefWidth(Gdx.graphics.getWidth()).expandY().top();
        /*uiTable.setDebug(true);*/
    }
}
