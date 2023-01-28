package no.sandramoen.drawingGame.screens.shell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import no.sandramoen.drawingGame.screens.BaseScreen;
import no.sandramoen.drawingGame.screens.gameplay.LevelScreen;
import no.sandramoen.drawingGame.utils.BaseGame;
import no.sandramoen.drawingGame.utils.GameUtils;

public class LevelSelectScreen extends BaseScreen {
    @Override
    public void initialize() {
        addTextButtons();
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.Q)
            Gdx.app.exit();
        return super.keyDown(keycode);
    }

    private void addTextButtons() {
        uiTable.defaults()
                .width(Gdx.graphics.getWidth() * .15f)
                .height(Gdx.graphics.getHeight() * .075f)
                .spaceTop(Gdx.graphics.getHeight() * .01f);
        for (int i = 0; i < BaseGame.maps.size; i++)
            uiTable.add(levelButton(i)).row();
        uiTable.defaults().reset();
    }

    private TextButton levelButton(Integer levelNumber) {
        String buttonText = "Level " + levelNumber;
        if (levelNumber == 0) buttonText = "Test level";
        TextButton button = new TextButton(buttonText, BaseGame.mySkin);
        button.addListener(
                (Event event) -> {
                    if (GameUtils.isTouchDownEvent(event))
                        BaseGame.setActiveScreen(new LevelScreen(BaseGame.maps.get(levelNumber)));
                    return false;
                }
        );
        return button;
    }
}
