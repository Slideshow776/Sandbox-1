package no.sandramoen.drawingGame.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.drawingGame.actors.Fish;
import no.sandramoen.drawingGame.actors.Player;
import no.sandramoen.drawingGame.actors.utils.BaseActor;
import no.sandramoen.drawingGame.ui.StaminaBar;
import no.sandramoen.drawingGame.utils.BaseGame;
import no.sandramoen.drawingGame.utils.BaseScreen;
import no.sandramoen.drawingGame.utils.GameUtils;
import no.sandramoen.drawingGame.utils.ShapeDrawer;

import com.github.tommyettinger.textra.TypingLabel;

public class LevelScreen extends BaseScreen {
    private boolean isPlaying;
    private Vector2 touchDownPoint;

    private ShapeDrawer shapeDrawer;

    private Array<BaseActor> fishes;
    private Player player;
    private int numTurns;
    private Array<BaseActor> waterTriangles;

    private TypingLabel fishLabel;
    private TypingLabel turnLabel;
    private StaminaBar staminaBar;

    @Override
    public void initialize() {
        touchDownPoint = new Vector2();

        shapeDrawer = new ShapeDrawer(mainStage);

        fishes = new Array();
        spawnRandomFish();
        spawnRandomFish();
        player = new Player(Gdx.graphics.getWidth() * .5f, Gdx.graphics.getHeight() * .5f, mainStage);
        waterTriangles = new Array();

        initializeGUI();
    }

    @Override
    public void update(float delta) {
        if (shapeDrawer.isCollisionDetected(player)) {
            player.die();
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 worldCoordinates = mainStage.getCamera().unproject(new Vector3(screenX, screenY, 0f));
        if (GameUtils.isWithinDistance(
                new Vector2(worldCoordinates.x, worldCoordinates.y),
                new Vector2(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2),
                Gdx.graphics.getWidth() * .075f)
        ) {
            isPlaying = true;
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
        Vector3 worldCoordinates = mainStage.getCamera().unproject(new Vector3(screenX, screenY, 0f));
        if (isPlaying && shapeDrawer.isItPossibleToDrawNewSegment(touchDownPoint, new Vector2(worldCoordinates.x, worldCoordinates.y))) {
            boolean isClosedShape = shapeDrawer.drawNewLineSegment(touchDownPoint, new Vector2(worldCoordinates.x, worldCoordinates.y));

            updateStaminaBar();

            collisionDetection();
            if (isClosedShape)
                endTurn();
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
            if (shapeDrawer.isCollisionDetected(fish)) {
                ((Fish) fish).fadeAndRemove();
                fishes.removeValue(fish, false);
                fishLabel.setText((fishes.size - 1) + "");
            }
        }
    }

    private void endTurn() {
        if (isPlaying) {
            player.movePlayer(shapeDrawer.polylines);
            resetTurn();
        }
    }

    private void resetTurn() {
        isPlaying = false;
        shapeDrawer.reset();
        touchDownPoint.set(0, 0);
        staminaBar.reset();
        spawnRandomFish();
        fishLabel.setText("Remaining fishes: " + fishes.size);
        turnLabel.setText("Turns: " + ++numTurns);
    }

    private void updateStaminaBar() {
        float percent = (shapeDrawer.MAX_POLY_LINES - shapeDrawer.polylines.size) / (float) shapeDrawer.MAX_POLY_LINES;
        staminaBar.decrement(percent);
    }

    private void spawnRandomFish() {
        if (fishes.size < 3)
            fishes.add(new Fish(
                    MathUtils.random(0, Gdx.graphics.getWidth()),
                    MathUtils.random(0, Gdx.graphics.getHeight()),
                    mainStage
            ));
    }

    private void initializeGUI() {
        staminaBar = new StaminaBar(Gdx.graphics.getWidth() * .5f, Gdx.graphics.getHeight() * .98f, uiStage);

        fishLabel = new TypingLabel("{FASTER}Remaining fishes: " + fishes.size, new Label.LabelStyle(BaseGame.mySkin.get("arcade26", BitmapFont.class), null));
        fishLabel.setColor(Color.LIME);
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
