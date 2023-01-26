package no.sandramoen.drawingGame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public abstract class BaseScreen implements Screen, InputProcessor {
    protected Stage waterStage;
    protected Stage mainStage;
    protected Stage uiStage;
    protected Table uiTable;
    private boolean pause;

    public BaseScreen() {
        waterStage = new Stage();
        waterStage.setViewport(new ScreenViewport());

        mainStage = new Stage();
        mainStage.setViewport(new ScreenViewport());

        uiTable = new Table();
        uiTable.setFillParent(true);
        uiStage = new Stage();
        uiStage.addActor(uiTable);
        uiStage.setViewport(new ScreenViewport());

        initialize();
    }

    public abstract void initialize();

    public abstract void update(float delta);

    @Override
    public void render(float delta) {
        uiStage.act(delta);
        if (!pause) {
            mainStage.act(delta);
            waterStage.act(delta);
            update(delta);
        }

        Gdx.gl.glClearColor(0.035f, 0.039f, 0.078f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        waterStage.getViewport().apply();
        drawGroundStage(delta);

        mainStage.getViewport().apply();
        mainStage.draw();

        uiStage.getViewport().apply();
        uiStage.draw();
    }

    public void drawGroundStage(float delta) {
        waterStage.draw();
    }

    @Override
    public void show() {
        InputMultiplexer im = (InputMultiplexer) Gdx.input.getInputProcessor();
        im.addProcessor(this);
        im.addProcessor(uiStage);
        im.addProcessor(mainStage);
        im.addProcessor(waterStage);
    }

    @Override
    public void hide() {
        InputMultiplexer im = (InputMultiplexer) Gdx.input.getInputProcessor();
        im.removeProcessor(this);
        im.removeProcessor(uiStage);
        im.removeProcessor(mainStage);
        im.removeProcessor(waterStage);
    }

    @Override
    public void resize(int width, int height) {
        waterStage.getViewport().update(width, height, true);
        mainStage.getViewport().update(width, height, true);
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public void pause() {
        pause = true;
    }

    @Override
    public void resume() {
        pause = false;
    }

    @Override
    public void dispose() {
    }
}
