package no.sandramoen.drawingGame.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import no.sandramoen.drawingGame.actors.utils.BaseActor;
import no.sandramoen.drawingGame.utils.BaseGame;

public class CancelDrawing extends BaseActor {
    public CancelDrawing(float x, float y, Stage stage) {
        super(x, y, stage);

        loadImage("GUI/cancel");
        setSize(100, 100);
        setPosition(x - getWidth() / 2, y - getHeight());
        setBoundaryRectangle();

        Label label = new Label("cancel drawing", new Label.LabelStyle(BaseGame.mySkin.get("arcade26", BitmapFont.class), null));
        label.setColor(Color.DARK_GRAY);
        label.setFontScale(.5f);
        label.setPosition(getWidth() * .5f - label.getPrefWidth() * .5f, getHeight() * -.2f);
        addActor(label);
    }
}
