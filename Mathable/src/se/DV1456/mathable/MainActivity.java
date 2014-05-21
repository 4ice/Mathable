package se.DV1456.mathable;

import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.Constants;
import org.andengine.util.debug.Debug;

import android.util.DisplayMetrics;
import android.widget.Toast;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga
 *
 * @author Nicolas Gramlich
 * @since 15:13:46 - 15.06.2010
 */
public class MainActivity extends SimpleBaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	final DisplayMetrics displayMetrics = new DisplayMetrics();
	
	
	// ===========================================================
	// Fields
	// ===========================================================

	private int CAMERA_WIDTH;
	private int CAMERA_HEIGHT;
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private ITextureRegion mFaceTextureRegion;
	private TMXTiledMap mTMXTiledMap;
	protected int nrOfBlanks;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public EngineOptions onCreateEngineOptions() {
		
		//Gets the screen resolution and saves it in CAMERA_WIDTH and HEIGHT.
		this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		this.CAMERA_WIDTH = displayMetrics.widthPixels;
		this.CAMERA_HEIGHT = displayMetrics.heightPixels;
		
		Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), camera);
		
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/numbers/");

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 100, 100, TextureOptions.BILINEAR);
		this.mFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "2.png", 0, 0);
		this.mBitmapTextureAtlas.load();		
	}

	
	
	
	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		
		final Scene scene = new Scene();
		
		
		
		final float centerX = (this.CAMERA_WIDTH - this.mFaceTextureRegion.getWidth()) / 2;
		final float centerY = (this.CAMERA_HEIGHT - this.mFaceTextureRegion.getHeight()) / 2;
		final Sprite face = new Sprite(centerX, centerY, this.mFaceTextureRegion, this.getVertexBufferObjectManager()) {
			//moves the sprite
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.getX() > this.getWidth() / 2)
				{
					this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
				}
				return true;
			}
		};
		
		//1. Create a rectangle with the grid that has the gameboard
		try {
			final TMXLoader tmxLoader = new TMXLoader(this.getAssets(), this.mEngine.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, this.getVertexBufferObjectManager(), new ITMXTilePropertiesListener() {
				@Override
				public void onTMXTileWithPropertiesCreated(final TMXTiledMap pTMXTiledMap, final TMXLayer pTMXLayer, final TMXTile pTMXTile, final TMXProperties<TMXTileProperty> pTMXTileProperties) {
					/* We are going to count the tiles that have the property "cactus=true" set. */
					if(pTMXTileProperties.containsTMXProperty("property", "0")) {
						MainActivity.this.nrOfBlanks++;
					}
				}
			});
			this.mTMXTiledMap = tmxLoader.loadFromAsset("tmx/gameBoard.tmx");

			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(MainActivity.this, "There are: " + MainActivity.this.nrOfBlanks + " Blank tiles left...", Toast.LENGTH_LONG).show();
				}
			});
		} catch (final TMXLoadException e) {
			Debug.e(e);
		}
		
		final TMXLayer tmxLayer = this.mTMXTiledMap.getTMXLayers().get(0);
		scene.attachChild(tmxLayer);
		
		/* Now we are going to create a rectangle that will  always highlight the tile below the feet of the pEntity. */
		final Rectangle currentTileRectangle = new Rectangle(0, 0, this.mTMXTiledMap.getTileWidth(), this.mTMXTiledMap.getTileHeight(), this.getVertexBufferObjectManager());
		currentTileRectangle.setColor(1, 0, 0, 0.25f);
		scene.attachChild(currentTileRectangle);

		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				/* Get the scene-coordinates of the players feet. */
				final float[] playerFootCordinates = face.convertLocalToSceneCoordinates(12, 31);

				/* Get the tile the feet of the player are currently waking on. */
				final TMXTile tmxTile = tmxLayer.getTMXTileAt(playerFootCordinates[Constants.VERTEX_INDEX_X], playerFootCordinates[Constants.VERTEX_INDEX_Y]);
				if(tmxTile != null) {
					// tmxTile.setTextureRegion(null); <-- Rubber-style removing of tiles =D
					currentTileRectangle.setPosition(tmxTile.getTileX(), tmxTile.getTileY());
				}
			}
		});
		
		
		
		
		
		
		
		
		//2. create a row of 7 tiles that the user can put down on the table.
		
		scene.setBackground(new Background(0, 0, 0));
		scene.attachChild(face);
		scene.registerTouchArea(face);
		scene.setTouchAreaBindingOnActionDownEnabled(true);

		return scene;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
