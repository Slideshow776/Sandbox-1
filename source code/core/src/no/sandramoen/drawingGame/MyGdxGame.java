package no.sandramoen.drawingGame;

import no.sandramoen.drawingGame.screens.gameplay.LevelScreen;
import no.sandramoen.drawingGame.utils.BaseGame;

public class MyGdxGame extends BaseGame {

	@Override
	public void create() {
		super.create();
		setActiveScreen(new LevelScreen(BaseGame.testMap));
	}
}

