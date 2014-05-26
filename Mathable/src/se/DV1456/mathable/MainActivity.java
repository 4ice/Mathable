package se.DV1456.mathable;

import java.util.HashMap;
import java.util.Random;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.options.*;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.*;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.*;
import org.andengine.extension.tmx.*;
import org.andengine.extension.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.*;
import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.*;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.Constants;
import org.andengine.util.debug.Debug;

import se.DV1456.mathable.tile.Tile;
import android.os.Bundle;
import android.util.DisplayMetrics;


public class MainActivity extends SimpleBaseGameActivity implements IOnSceneTouchListener, IScrollDetectorListener, IPinchZoomDetectorListener 
{
	// ===========================================================
	// Constants
	// ===========================================================

	final DisplayMetrics displayMetrics = new DisplayMetrics();



	// ===========================================================
	// Fields
	// ===========================================================

	//Screen resolution
	private int CAMERA_WIDTH;
	private int CAMERA_HEIGHT;

	private ZoomCamera mZoomCamera;

	//Equal to jFrame in standard java.
	private Scene mScene;

	//Used for zooming and moving the camera.
	private SurfaceScrollDetector mScrollDetector;
	private PinchZoomDetector mPinchZoomDetector;
	private float mPinchZoomStartedCameraZoomFactor;

	//Used for loading pictures and such.
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private ITextureRegion mTileTextureRegion;

	//Used for the board.
	private TMXLayer tmxLayer;
	private TMXTiledMap mTMXTiledMap;
	
	private HashMap<Tile, ITextureRegion> mTileTotextureRegionMap;


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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public EngineOptions onCreateEngineOptions() {

		//Gets the screen resolution and saves it in CAMERA_WIDTH and CAMERA_HEIGHT.
		this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		this.CAMERA_WIDTH = displayMetrics.widthPixels;
		this.CAMERA_HEIGHT = displayMetrics.heightPixels;


		//Initialize the camera
		this.mZoomCamera = new ZoomCamera(0, 0, this.CAMERA_WIDTH, this.CAMERA_HEIGHT);


		return new EngineOptions(true, ScreenOrientation.PORTRAIT_SENSOR, new FillResolutionPolicy(), this.mZoomCamera);

	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/numbers/");
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1600, 500, TextureOptions.BILINEAR);
		this.mTileTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "rowTemplateFixed.png", 0, 0);
		this.mBitmapTextureAtlas.load();
		
		
		//Saves the png-pos for each tile in mTileTotextureRegionMap
		this.mTileTotextureRegionMap = new HashMap<Tile, ITextureRegion>();
		
		for(final Tile tile : Tile.values())
		{
			final ITextureRegion tileTextureRegion = TextureRegionFactory.extractFromTexture(this.mBitmapTextureAtlas, tile.getTexturePositionX(), tile.getTexturePositionY(), tile.getTILE_SIZE(), tile.getTILE_SIZE());
			this.mTileTotextureRegionMap.put(tile, tileTextureRegion);
		}
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		//Initialize the Scene (Equal to jFrame)
		this.mScene = new Scene();
		this.mScene.setOnAreaTouchTraversalFrontToBack();

		//scroll is for moving around the board
		this.mScrollDetector = new SurfaceScrollDetector(this);
		//pinch is for zooming
		this.mPinchZoomDetector = new PinchZoomDetector(this);
		//listener
		this.mScene.setOnSceneTouchListener(this);
		//enable touch events in the scene
		this.mScene.setTouchAreaBindingOnActionDownEnabled(true);

		//1. Load TMX (The board)
		try {
			final TMXLoader tmxLoader = new TMXLoader(this.getAssets(), this.mEngine.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, this.getVertexBufferObjectManager(), new ITMXTilePropertiesListener() {
				@Override
				public void onTMXTileWithPropertiesCreated(final TMXTiledMap pTMXTiledMap, final TMXLayer pTMXLayer, final TMXTile pTMXTile, final TMXProperties<TMXTileProperty> pTMXTileProperties) {
				}
			});
			this.mTMXTiledMap = tmxLoader.loadFromAsset("tmx/gameBoard.tmx");
		} catch (final TMXLoadException e) {
			Debug.e(e);
		}

		//Configure the camera
		this.tmxLayer = this.mTMXTiledMap.getTMXLayers().get(0);
		this.mZoomCamera.setBounds(0f, 0f, tmxLayer.getWidth(), tmxLayer.getHeight());
		this.mZoomCamera.setBoundsEnabled(true);
		
//		//Creates a button
//		ButtonSprite bn = new ButtonSprite(250, 250, this.mTileTextureRegion, this.getVertexBufferObjectManager()) {
//			@Override
//			public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
//				if(pTouchEvent.isActionDown()) {
//					mScene.detachChild(this);
//					mScene.attachChild(tmxLayer);
//					try {
//						for(int i = 0; i < 7; i++)
//						{
//							addTile(Tile.getRandomTile(), 0, 0);
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//				return super.onAreaTouched(pTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
//			}
//		};
//		mScene.attachChild(bn);
//		this.mScene.registerTouchArea(bn);
		
		this.mScene.setBackground(new Background(0, 0, 0));
		
		
		mScene.attachChild(tmxLayer);
		try {
			for(int i = 0; i < 7; i++)
			{
				addTile(Tile.getRandomTile(), 0, 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return this.mScene;
	}

	/*** 3 Functions for handling scroll (Move camera) ***/
	@Override
	public void onScrollStarted(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
		this.scrollHelper(pDistanceX, pDistanceY);
	}

	@Override
	public void onScroll(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
		this.scrollHelper(pDistanceX, pDistanceY);
	}

	@Override
	public void onScrollFinished(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
		this.scrollHelper(pDistanceX, pDistanceY);
	}

	/*** 3 Functions for handling zoom ***/
	@Override
	public void onPinchZoomStarted(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent) {
		this.mPinchZoomStartedCameraZoomFactor = this.mZoomCamera.getZoomFactor();
	}

	@Override
	public void onPinchZoom(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent, final float pZoomFactor) {
		this.zoomHelper(pZoomFactor);
	}

	@Override
	public void onPinchZoomFinished(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent, final float pZoomFactor) {
		this.zoomHelper(pZoomFactor);
	}


	//Prevent user from zooming and scrolling at the same time.
	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		this.mPinchZoomDetector.onTouchEvent(pSceneTouchEvent);

		if(this.mPinchZoomDetector.isZooming()) {
			this.mScrollDetector.setEnabled(false);
		} else {
			if(pSceneTouchEvent.isActionDown()) {
				this.mScrollDetector.setEnabled(true);
			}
			this.mScrollDetector.onTouchEvent(pSceneTouchEvent);
		}

		return true;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public void zoomHelper(final float pZoomFactor)
	{
		this.mZoomCamera.setZoomFactor(this.mPinchZoomStartedCameraZoomFactor * pZoomFactor);
	}
	public void scrollHelper(final float pDistanceX, final float pDistanceY)
	{
		final float zoomFactor = this.mZoomCamera.getZoomFactor();
		this.mZoomCamera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);
	}
	
	private void addTile(final Tile pTile, final int pX, final int pY)
	{
		final Sprite tile = new Sprite(pX, pY, this.mTileTotextureRegionMap.get(pTile), this.getVertexBufferObjectManager()) {
			//moves the sprite
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
				//If the tile is released, snap it into grid.
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP)
				{
					//Gets the coordinates for the box below the center of the tile
					final float[] tileCordinates = this.convertLocalToSceneCoordinates(50, 50);

					// Get the box that the player tries to put a tile on and save it in tmxTile.
					final TMXTile tmxTile = tmxLayer.getTMXTileAt(tileCordinates[Constants.VERTEX_INDEX_X], tileCordinates[Constants.VERTEX_INDEX_Y]);
					if(tmxTile != null) {
						TMXProperties<TMXTileProperty> pTMXTileProperties = mTMXTiledMap.getTMXTileProperties(tmxTile.getGlobalTileID());


						//1. Check if it's possible to put a tile on selected box.
						if(pTMXTileProperties.containsTMXProperty("used", "false"))
						{
							/*NOTE: This check could be done when the user tries 
									to play the tile that was put there if we 
									want a play button instead of playing instantly. */


							//2. check what kind of box, and then check if valid or not.

							//If the box is normal
							if(pTMXTileProperties.containsTMXProperty("property", "0"))
							{
								//try all math-operations for the tile
								this.setPosition(tmxTile.getTileX(), tmxTile.getTileY());
							}
							//If the box is a plus-sign
							else if(pTMXTileProperties.containsTMXProperty("property", "5"))
							{
								//try +
								this.setPosition(tmxTile.getTileX(), tmxTile.getTileY());								
							}
							//if the box is a minus-sign
							else if(pTMXTileProperties.containsTMXProperty("property", "6"))
							{
								//try -
								this.setPosition(tmxTile.getTileX(), tmxTile.getTileY());
							}
							//if the box is a multiplication-sign
							else if(pTMXTileProperties.containsTMXProperty("property", "7"))
							{
								//try *
								this.setPosition(tmxTile.getTileX(), tmxTile.getTileY());
							}
							//if the box is a division sign
							else if(pTMXTileProperties.containsTMXProperty("property", "8"))
							{
								//try /
								this.setPosition(tmxTile.getTileX(), tmxTile.getTileY());
							}
							//If the tile is a dublett
							else if(pTMXTileProperties.containsTMXProperty("property", "9"))
							{
								//try all math-operations for the tile
								this.setPosition(tmxTile.getTileX(), tmxTile.getTileY());								
							}
							//If the tile is a triplett
							else if(pTMXTileProperties.containsTMXProperty("property", "10"))
							{
								//try all math-operations for the tile
								this.setPosition(tmxTile.getTileX(), tmxTile.getTileY());
							}
							else
							{
								//put back in row
							}
						}
						//If box is already used, put the tile back in the row of available tiles
						else
						{
							//put back in row
						}
					}
				}
				return true;
			}
		};
		this.mScene.attachChild(tile);
		this.mScene.registerTouchArea(tile);
	}
	
	
	
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}


/** USE FOR TEST OUTPUT! **/

//this.runOnUiThread(new Runnable() {
//	@Override
//	public void run() {
//		Toast.makeText(MainActivity.this, "Test", Toast.LENGTH_LONG).show();
//		}
//	});