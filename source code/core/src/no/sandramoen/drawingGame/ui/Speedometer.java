package no.sandramoen.drawingGame.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.drawingGame.actors.utils.BaseActor;
import no.sandramoen.drawingGame.utils.BaseGame;

public class Speedometer extends BaseActor {
    private BaseActor gauge;
    private int gaugeOffset = 90;
    private int gaugeMinimum = 225 - gaugeOffset;

    public Speedometer(float x, float y, Stage stage) {
        super(x, y, stage);
        loadImage("whitePixel");
        setColor(Color.BLUE);
        setSize(100, 100);
        setPosition(x - getWidth() / 2, y - getHeight());

        Label label = new Label("speed", new Label.LabelStyle(BaseGame.mySkin.get("arcade26", BitmapFont.class), null));
        label.setColor(Color.DARK_GRAY);
        label.setFontScale(.5f);
        label.setPosition(getWidth() * .5f - label.getPrefWidth() * .5f, getHeight() * -.2f);
        addActor(label);

        gauge = new BaseActor(0, 0, stage);
        gauge.loadImage("whitePixel");
        gauge.setColor(Color.CYAN);
        gauge.setSize(getWidth() * .05f, getHeight() * .4f);
        gauge.setOrigin(Align.bottom);
        gauge.setPosition(
                getWidth() * .5f - gauge.getWidth() * .5f,
                getHeight() * .5f
        );
        addActor(gauge);
        gauge.setRotation(gaugeMinimum);
    }

    public void setZero() {
        rotateTo(gaugeMinimum, 1, Interpolation.bounceOut);
    }

    public void set(float percent) {
        if (percent > 1) percent = 1;
        rotateTo(gaugeMinimum - (270 * percent), .4f, Interpolation.linear);
    }

    private void rotateTo(float rotation, float duration, Interpolation interpolation) {
        gauge.addAction(Actions.rotateTo(rotation, .4f, interpolation));
    }
}
