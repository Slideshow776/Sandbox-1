package no.sandramoen.drawingGame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.github.tommyettinger.textra.TypingLabel;

import no.sandramoen.drawingGame.utils.BaseGame;

public class LevelOverlay extends Table {
    public TypingLabel mainLabel;
    public TypingLabel fishesLeftLabel;

    private ImageButton restartImageButton;
    private ImageButton nextLevelImageButton;

    public LevelOverlay() {
        mainLabel = new TypingLabel("", new Label.LabelStyle(BaseGame.mySkin.get("arcade64", BitmapFont.class), null));
        mainLabel.setAlignment(Align.center);

        restartImageButton = new ImageButton(BaseGame.mySkin);
        restartImageButton.getStyle().imageUp = new TextureRegionDrawable(BaseGame.textureAtlas.findRegion("GUI/restart"));

        fishesLeftLabel = new TypingLabel("", new Label.LabelStyle(BaseGame.mySkin.get("arcade26", BitmapFont.class), null));
        fishesLeftLabel.setAlignment(Align.center);

        nextLevelImageButton = new ImageButton(BaseGame.mySkin);
        nextLevelImageButton.getStyle().imageUp = new TextureRegionDrawable(BaseGame.textureAtlas.findRegion("GUI/next"));
        // nextLevelImageButton.setBackground(new TextureRegionDrawable(BaseGame.textureAtlas.findRegion("GUI/next")));


        float verticalPadding = Gdx.graphics.getHeight() * .02f;
        add(mainLabel).colspan(3).padBottom(verticalPadding).padLeft(Gdx.graphics.getWidth() * .02f).row();
        add(restartImageButton).padRight(verticalPadding);
        add(fishesLeftLabel);
        add(nextLevelImageButton).padLeft(verticalPadding);
/*
        setDebug(true);*/

        setLevelComplete(2, 5);
    }

    public void setLevelComplete(int numFishes, int totalFishes) {
        mainLabel.setText("{RAINBOW}Level complete!");
        mainLabel.restart();

        fishesLeftLabel.setText(numFishes + " / " + totalFishes + " fishes caught");
        fishesLeftLabel.restart();
    }

    public void setGameOver(int numFishes, int totalFishes) {
        mainLabel.setText("{CROWD}Game Over!");
        mainLabel.restart();

        fishesLeftLabel.setText(numFishes + " / " + totalFishes + " fishes caught");
        fishesLeftLabel.restart();
        removeActor(nextLevelImageButton);
    }
}
