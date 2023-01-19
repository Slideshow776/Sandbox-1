package no.sandramoen.drawingGame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import no.sandramoen.drawingGame.actors.utils.BaseActor;
import no.sandramoen.drawingGame.utils.BaseGame;

public class StaminaBar extends BaseActor {
    private BaseActor progress;

    public StaminaBar(float x, float y, Stage stage) {
        super(x, y, stage);

        loadImage("whitePixel");
        setSize(Gdx.graphics.getWidth() * .5f, Gdx.graphics.getHeight() * .05f);
        setPosition(x - getWidth() / 2, y - getHeight());
        setColor(Color.ORANGE);

        progress = new BaseActor(0f, 0f, stage);
        progress.loadImage("whitePixel");
        progress.setColor(Color.YELLOW);
        progress.setSize(getWidth(), getHeight());
        addActor(progress);

        Label label = new Label("stamina", new Label.LabelStyle(BaseGame.mySkin.get("arcade26", BitmapFont.class), null));
        label.setColor(Color.DARK_GRAY);
        label.setFontScale(.5f);
        label.setPosition(getWidth() - label.getPrefWidth() * 1.1f, getHeight() * .3f);
        addActor(label);
    }

    public void decrement(float percent) {
        progress.addAction(Actions.sizeTo(getWidth() * percent, getHeight(), .1f));
    }

    public void reset() {
        progress.addAction(Actions.sizeTo(getWidth(), getHeight(), .5f, Interpolation.exp10In));
    }
}
