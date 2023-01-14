package no.sandramoen.sandbox;

import no.sandramoen.sandbox.screens.gameplay.LevelScreen;
import no.sandramoen.sandbox.utils.BaseGame;

public class MyGdxGame extends BaseGame {

	@Override
	public void create() {
		super.create();
		setActiveScreen(new LevelScreen());
	}
}

