package no.sandramoen.drawingGame.screens.gameplay;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.drawingGame.actors.Basket;
import no.sandramoen.drawingGame.actors.Fish;
import no.sandramoen.drawingGame.actors.Gjedda;
import no.sandramoen.drawingGame.actors.Player;
import no.sandramoen.drawingGame.actors.map.ImpassableTerrain;
import no.sandramoen.drawingGame.actors.map.TilemapActor;
import no.sandramoen.drawingGame.utils.BaseGame;

public class MapLoader {
    public Player player;
    public Gjedda gjedda;
    public Basket basket;
    public Array<Fish> fishes;
    public Array<ImpassableTerrain> impassables;

    private TilemapActor tilemap;
    private Stage mainStage;
    private Stage waterStage;

    public MapLoader(Stage mainStage, Stage waterStage, TilemapActor tilemap,
                     Player player, Gjedda gjedda, Basket basket,
                     Array<Fish> fishes, Array<ImpassableTerrain> impassables) {
        this.tilemap = tilemap;
        this.mainStage = mainStage;
        this.waterStage = waterStage;

        this.player = player;
        this.gjedda = gjedda;
        this.basket = basket;
        this.fishes = fishes;
        this.impassables = impassables;

        initializePlayer();
        initializeBasket();
        initializeImpassables();
        initializeGjedda();
        initializeFish();
    }

    private void initializeFish() {
        for (MapObject obj : tilemap.getTileList("actors", "fish")) {
            MapProperties props = obj.getProperties();
            float x = props.get("x", Float.class) * BaseGame.UNIT_SCALE;
            float y = props.get("y", Float.class) * BaseGame.UNIT_SCALE;
            boolean isFrozen = props.get("isFrozen", Boolean.class);
            fishes.add(new Fish(x, y, waterStage, isFrozen, impassables));
        }
    }

    private void initializeImpassables() {
        for (MapObject obj : tilemap.getTileList("actors", "impassable")) {
            MapProperties props = obj.getProperties();
            float x = props.get("x", Float.class) * BaseGame.UNIT_SCALE;
            float y = props.get("y", Float.class) * BaseGame.UNIT_SCALE;
            float width = props.get("width", Float.class) * BaseGame.UNIT_SCALE;
            float height = props.get("height", Float.class) * BaseGame.UNIT_SCALE;
            impassables.add(new ImpassableTerrain(x, y, width, height, mainStage));
        }
    }

    private void initializeGjedda() {
        MapObject startPoint = tilemap.getTileList("actors", "gjedda").get(0);
        float x = startPoint.getProperties().get("x", Float.class) * BaseGame.UNIT_SCALE;
        float y = startPoint.getProperties().get("y", Float.class) * BaseGame.UNIT_SCALE;
        gjedda = new Gjedda(x, y, waterStage, impassables);
    }

    private void initializeBasket() {
        MapObject startPoint = tilemap.getTileList("actors", "basket").get(0);
        float x = startPoint.getProperties().get("x", Float.class) * BaseGame.UNIT_SCALE;
        float y = startPoint.getProperties().get("y", Float.class) * BaseGame.UNIT_SCALE;
        basket = new Basket(x, y, mainStage);
    }

    private void initializePlayer() {
        MapObject startPoint = tilemap.getTileList("actors", "player").get(0);
        float x = startPoint.getProperties().get("x", Float.class) * BaseGame.UNIT_SCALE;
        float y = startPoint.getProperties().get("y", Float.class) * BaseGame.UNIT_SCALE;
        player = new Player(x, y, mainStage);
    }
}
