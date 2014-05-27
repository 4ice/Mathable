package se.DV1456.mathable;

import java.util.HashMap;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.options.*;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.*;
import org.andengine.entity.scene.background.Background;
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
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.Toast;



public class MainActivity extends SimpleBaseGameActivity implements IOnSceneTouchListener, IScrollDetectorListener, IPinchZoomDetectorListener 
{
	// ===========================================================
	// Constants
	// ===========================================================

	final DisplayMetrics displayMetrics = new DisplayMetrics();



	// ===========================================================
	// Fields
	// ===========================================================


	/*** VARIABLES FOR CREATING THE LOOKS AND SUCH! ***/
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

	/*** VARIABLES FOR THE GAME ITSELF! ***/
	boolean[][] usedTiles;
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

		//sets all boxes to available...
		this.usedTiles = new boolean[14][14];
		//...Except the four in the middle.
		this.usedTiles[7][7] = true;
		this.usedTiles[8][7] = true;
		this.usedTiles[7][8] = true;
		this.usedTiles[8][8] = true;



		//Saves the png-pos for each tile in mTileTotextureRegionMap
		this.mTileTotextureRegionMap = new HashMap<Tile, ITextureRegion>();

		for(final Tile tile : Tile.values())
		{
			//tileTextureRegion stores the image for each tile
			final ITextureRegion tileTextureRegion = TextureRegionFactory.extractFromTexture(this.mBitmapTextureAtlas, tile.getTexturePositionX(), tile.getTexturePositionY(), tile.getTILE_SIZE(), tile.getTILE_SIZE());
			this.mTileTotextureRegionMap.put(tile, tileTextureRegion);
		}
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		Player p1 = new Player("Kalle");
		Player p2 = new Player("Nisse");

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

		this.mScene.setBackground(new Background(0, 0, 0));


		mScene.attachChild(tmxLayer);
		//give the players seven tiles each
		try {
			for(int i = 0; i < 2; i++)
			{
				for(int j = 0; j < 7; j++)
				{
					if(i == 0 && p1.getNrOfTiles() != 7)
					{
						this.givePlayerTile(p1, Tile.getRandomTile());
					}
					else if (i == 1 && p2.getNrOfTiles() != 7)
					{
						this.givePlayerTile(p2, Tile.getRandomTile());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.addTile(p1.getTheTiles(), 0, 0);
		this.addTile(p2.getTheTiles(), 0, 100);
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



	private void givePlayerTile(Player player, final Tile pTile)
	{
		try
		{
			player.addTile(pTile);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	private void addTile(final Tile[] tile, final int pX, final int pY)
	{
		for(final Tile pTile : tile)
		{
			if(pTile != null)
			{
				final Sprite spriteTile = new Sprite(pX, pY, this.mTileTotextureRegionMap.get(pTile), this.getVertexBufferObjectManager()) {
					//moves the sprite
					@Override
					public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
						this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);

						if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN)
						{
							final float[] tilePreCordinates = this.convertLocalToSceneCoordinates(50, 50);
							usedTiles[(int)tilePreCordinates[0]/100][(int)tilePreCordinates [1]/100] = false;
							
							this.setZIndex(1);
							getParent().sortChildren();
						}
						//If the tile is released, snap it into grid.
						if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP)
						{
							//Gets the coordinates for the box below the center of the tile
							final float[] tileReleaseCordinates = this.convertLocalToSceneCoordinates(50, 50);

							// Get the box that the player tries to put a tile on and save it in tmxTile.
							final TMXTile tmxTile = tmxLayer.getTMXTileAt(tileReleaseCordinates[Constants.VERTEX_INDEX_X], tileReleaseCordinates[Constants.VERTEX_INDEX_Y]);
							if(tmxTile != null) {
								TMXProperties<TMXTileProperty> pTMXTileProperties = mTMXTiledMap.getTMXTileProperties(tmxTile.getGlobalTileID());

								//1. Check if it's possible to put a tile on selected box.
								if(!usedTiles[tmxTile.getTileX()/100][tmxTile.getTileY()/100])
								{
									playTile(pTile, this, tmxTile, pTMXTileProperties);

									this.setZIndex(0);
									getParent().sortChildren();
								}
								//If box is already used, put the tile at the closest position.
								else
								{

									this.setPosition(0,0);
									//usedTiles[(int)tilePreCordinates[0]][(int)tilePreCordinates[1]] = false;							
								}
							}
						}
						return true;
					}
				};
				this.mScene.attachChild(spriteTile);
				this.mScene.registerTouchArea(spriteTile);			
			}
		}
	}




	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private void playTile(Tile pTile, Sprite spriteTile, final TMXTile tmxTile, TMXProperties<TMXTileProperty> pTMXTileProperties)
	{
		/*NOTE: This check could be done when the user tries 
		to play the tile that was put there if we 
		want a play button instead of playing instantly. */


		//2. check what kind of box, and then check if valid or not.

		//If the box is normal
		if(pTMXTileProperties.containsTMXProperty("property", "0"))
		{
			//try all math-operations for the tile
			if(allArithmetics(1, 2, pTile.getValue()))
			{
				spriteTile.setPosition(tmxTile.getTileX(), tmxTile.getTileY());
				usedTiles[tmxTile.getTileX()/100][tmxTile.getTileY()/100] = true;
			}										
		}
		//If the box is a plus-sign
		else if(pTMXTileProperties.containsTMXProperty("property", "5"))
		{
			//try + in all four directions
			spriteTile.setPosition(tmxTile.getTileX(), tmxTile.getTileY());
			usedTiles[tmxTile.getTileX()/100][tmxTile.getTileY()/100] = true;
		}
		//if the box is a minus-sign + in all four directions
		else if(pTMXTileProperties.containsTMXProperty("property", "6"))
		{
			//try - in all four directions
			spriteTile.setPosition(tmxTile.getTileX(), tmxTile.getTileY());
			usedTiles[tmxTile.getTileX()/100][tmxTile.getTileY()/100] = true;
		}
		//if the box is a multiplication-sign
		else if(pTMXTileProperties.containsTMXProperty("property", "7"))
		{
			//try * in all four directions
			spriteTile.setPosition(tmxTile.getTileX(), tmxTile.getTileY());
			usedTiles[tmxTile.getTileX()/100][tmxTile.getTileY()/100] = true;
		}
		//if the box is a division sign
		else if(pTMXTileProperties.containsTMXProperty("property", "8"))
		{
			//try / in all four directions
			spriteTile.setPosition(tmxTile.getTileX(), tmxTile.getTileY());
			usedTiles[tmxTile.getTileX()/100][tmxTile.getTileY()/100] = true;
		}
		//If the tile is a doublet
		else if(pTMXTileProperties.containsTMXProperty("property", "9"))
		{
			//try all math-operations for the tile, in all four directions
			spriteTile.setPosition(tmxTile.getTileX(), tmxTile.getTileY());
			usedTiles[tmxTile.getTileX()/100][tmxTile.getTileY()/100] = true;
		}
		//If the tile is a triplett
		else if(pTMXTileProperties.containsTMXProperty("property", "10"))
		{
			//try all math-operations for the tile, in all four directions
			spriteTile.setPosition(tmxTile.getTileX(), tmxTile.getTileY());
			usedTiles[tmxTile.getTileX()/100][tmxTile.getTileY()/100] = true;
		}
	}

	private boolean addition(int term1, int term2, int result)
	{
		boolean correct = false;
		if((term1+term2) == result)
		{
			correct = true;
		}
		return (term1+term2) == result;
	}
	private boolean subtraction(int term1, int term2, int result)
	{
		boolean correct = false;
		if((term1-term2) == result || (term2-term1) == result)
		{
			correct = true;
		}
		return (term1-term2) == result;
	}
	private boolean multiplication(int term1, int term2, int result)
	{
		boolean correct = false;
		if((term1*term2) == result)
		{
			correct = true;
		}
		return correct;
	}
	private boolean division(int term1, int term2, int result)
	{
		boolean correct = false;
		if((term1/term2) == result || (term2/term1) == result)
		{
			correct = true;
		}
		return (term1/term2) == result;
	}

	private boolean allArithmetics(int term1, int term2, int result)
	{
		boolean correct = false;
		if((term1+term2) == result || (term1-term2) == result || (term2-term1) == result || 
				(term1*term2) == result || (term1/term2) == result || (term2/term1) == result)
		{
			correct = true;
		}
		return correct;
	}
}


/** USE FOR TEST OUTPUT! **/

//this.runOnUiThread(new Runnable() {
//	@Override
//	public void run() {
//		Toast.makeText(MainActivity.this, "Test", Toast.LENGTH_LONG).show();
//		}
//	});