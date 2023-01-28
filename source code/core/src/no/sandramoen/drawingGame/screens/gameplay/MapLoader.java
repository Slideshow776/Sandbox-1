package no.sandramoen.drawingGame.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.drawingGame.actors.Basket;
import no.sandramoen.drawingGame.actors.Fish;
import no.sandramoen.drawingGame.actors.Gjedda;
import no.sandramoen.drawingGame.actors.Player;
import no.sandramoen.drawingGame.actors.map.ImpassableTerrain;
import no.sandramoen.drawingGame.actors.map.TiledMapActor;
import no.sandramoen.drawingGame.utils.BaseGame;

public class MapLoader {
    public Player player;
    public Gjedda gjedda;
    public Basket basket;
    public Array<Fish> fishes;
    public Array<ImpassableTerrain> impassables;

    private TiledMapActor tilemap;
    private Stage mainStage;
    private Stage waterStage;

    public MapLoader(Stage mainStage, Stage waterStage, TiledMapActor tilemap,
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
        for (MapObject mapObject : tilemap.getTileList("actors", "fish")) {
            MapProperties mapProperties = mapObject.getProperties();
            float x = mapProperties.get("x", Float.class) * BaseGame.UNIT_SCALE;
            float y = mapProperties.get("y", Float.class) * BaseGame.UNIT_SCALE;
            boolean isFrozen = mapProperties.get("isFrozen", Boolean.class);
            fishes.add(new Fish(x, y, waterStage, isFrozen, impassables));
        }
    }

    private void initializeImpassables() {
        for (MapObject mapObject : tilemap.getTileList("actors", "impassable")) {
            MapProperties mapProperties = mapObject.getProperties();
            float x = mapProperties.get("x", Float.class) * BaseGame.UNIT_SCALE;
            float y = mapProperties.get("y", Float.class) * BaseGame.UNIT_SCALE;
            float width = mapProperties.get("width", Float.class) * BaseGame.UNIT_SCALE;
            float height = mapProperties.get("height", Float.class) * BaseGame.UNIT_SCALE;
            impassables.add(new ImpassableTerrain(x, y, width, height, mainStage));
        }
    }

    private void initializeGjedda() {
        String layerName = "actors";
        String propertyName = "gjedda";
        if (tilemap.getTileList(layerName, propertyName).size() == 1) {
            MapObject mapObject = tilemap.getTileList(layerName, propertyName).get(0);
            float x = mapObject.getProperties().get("x", Float.class) * BaseGame.UNIT_SCALE;
            float y = mapObject.getProperties().get("y", Float.class) * BaseGame.UNIT_SCALE;
            gjedda = new Gjedda(x, y, waterStage, impassables);
        } else if (tilemap.getTileList(layerName, propertyName).size() > 1) {
            Gdx.app.error(getClass().getSimpleName(), "Error found more than one property: " + propertyName + " on layer: " + layerName + "!");
        } else {
            gjedda = null;
        }
    }

    private void initializeBasket() {
        String layerName = "actors";
        String propertyName = "basket";
        if (tilemap.getTileList(layerName, propertyName).size() == 1) {
            MapObject mapObject = tilemap.getTileList(layerName, propertyName).get(0);
            float x = mapObject.getProperties().get("x", Float.class) * BaseGame.UNIT_SCALE;
            float y = mapObject.getProperties().get("y", Float.class) * BaseGame.UNIT_SCALE;
            basket = new Basket(x, y, mainStage);
        } else if (tilemap.getTileList(layerName, propertyName).size() > 1) {
            Gdx.app.error(getClass().getSimpleName(), "Error => found more than one property: " + propertyName + " on layer: " + layerName + "!");
        } else {
            Gdx.app.error(getClass().getSimpleName(), "Error => found no property: " + propertyName + " on layer: " + layerName + "!");
            basket = null;
        }
    }

    private void initializePlayer() {
        String layerName = "actors";
        String propertyName = "player";
        if (tilemap.getTileList(layerName, propertyName).size() == 1) {
            MapObject mapObject = tilemap.getTileList(layerName, propertyName).get(0);
            float x = mapObject.getProperties().get("x", Float.class) * BaseGame.UNIT_SCALE;
            float y = mapObject.getProperties().get("y", Float.class) * BaseGame.UNIT_SCALE;
            player = new Player(x, y, mainStage);
        } else if (tilemap.getTileList(layerName, propertyName).size() > 1) {
            Gdx.app.error(getClass().getSimpleName(), "Error => found more than one property: " + propertyName + " on layer: " + layerName + "!");
        } else {
            Gdx.app.error(getClass().getSimpleName(), "Error => found no property: " + propertyName + " on layer: " + layerName + "!");
            player = null;
        }
    }
}
