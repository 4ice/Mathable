//package se.DV1456.mathable;
//
//import org.andengine.engine.camera.ZoomCamera;
//import org.andengine.engine.options.EngineOptions;
//import org.andengine.engine.options.ScreenOrientation;
//import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
//import org.andengine.entity.scene.IOnSceneTouchListener;
//import org.andengine.entity.scene.Scene;
//import org.andengine.entity.scene.background.Background;
//import org.andengine.entity.sprite.Sprite;
//import org.andengine.entity.util.FPSLogger;
//import org.andengine.extension.tmx.TMXLayer;
//import org.andengine.extension.tmx.TMXLoader;
//import org.andengine.extension.tmx.TMXProperties;
//import org.andengine.extension.tmx.TMXTile;
//import org.andengine.extension.tmx.TMXTileProperty;
//import org.andengine.extension.tmx.TMXTiledMap;
//import org.andengine.extension.tmx.TMXLoader.ITMXTilePropertiesListener;
//import org.andengine.extension.tmx.util.exception.TMXLoadException;
//import org.andengine.input.touch.TouchEvent;
//import org.andengine.input.touch.detector.PinchZoomDetector;
//import org.andengine.input.touch.detector.ScrollDetector;
//import org.andengine.input.touch.detector.SurfaceScrollDetector;
//import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;
//import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
//import org.andengine.opengl.texture.TextureOptions;
//import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
//import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
//import org.andengine.opengl.texture.region.ITextureRegion;
//import org.andengine.ui.activity.SimpleBaseGameActivity;
//import org.andengine.util.Constants;
//import org.andengine.util.debug.Debug;
//
//import android.util.DisplayMetrics;
//import android.widget.Toast;
//
//public class GameScreen extends SimpleBaseGameActivity implements IOnSceneTouchListener, IScrollDetectorListener, IPinchZoomDetectorListener
//{
//	// ===========================================================
//	// Constants
//	// ===========================================================
//
//	final DisplayMetrics displayMetrics = new DisplayMetrics();
//
//
//
//	// ===========================================================
//	// Fields
//	// ===========================================================
//
//	//Screen resolution
//	private int CAMERA_WIDTH;
//	private int CAMERA_HEIGHT;
//
//	private ZoomCamera mZoomCamera;
//
//	//Equal to jFrame in std java.
//	private Scene mScene;
//
//	//Used for zooming and moving the camera.
//	private SurfaceScrollDetector mScrollDetector;
//	private PinchZoomDetector mPinchZoomDetector;
//	private float mPinchZoomStartedCameraZoomFactor;
//
//	//Used for loading pictures and such.
//	private BitmapTextureAtlas mBitmapTextureAtlas;
//	private ITextureRegion mFaceTextureRegion;
//
//	//Used for the board.
//	private TMXTiledMap mTMXTiledMap;
//
//	//Keeps all the tiles for the game. the tiles are hardcoded at Initialization.
//	private Tile[] theTiles;
//
//
//	// ===========================================================
//	// Constructors
//	// ===========================================================
//	// ===========================================================
//	// Getter & Setter
//	// ===========================================================
//
//	// ===========================================================
//	// Methods for/from SuperClass/Interfaces
//	// ===========================================================
//
//	@Override
//	public EngineOptions onCreateEngineOptions() {
//
//		//Gets the screen resolution and saves it in CAMERA_WIDTH and CAMERA_HEIGHT.
//		this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//		this.CAMERA_WIDTH = displayMetrics.widthPixels;
//		this.CAMERA_HEIGHT = displayMetrics.heightPixels;
//
//
//		//Init the camera
//		this.mZoomCamera = new ZoomCamera(0, 0, this.CAMERA_WIDTH, this.CAMERA_HEIGHT);
//
//
//		return new EngineOptions(true, ScreenOrientation.PORTRAIT_SENSOR, new FillResolutionPolicy(), this.mZoomCamera);
//
//	}
//	@Override
//	public void onCreateResources() {
//		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/numbers/");
//		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 100, 100, TextureOptions.BILINEAR);
//		this.mFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "2.png", 0, 0);
//		this.mBitmapTextureAtlas.load();
//
//		this.initTiles();
//	}
//
//
//
//	@Override
//	public Scene onCreateScene() {
//		this.mEngine.registerUpdateHandler(new FPSLogger());
//
//		//Initialize the Scene (Equal to jFrame)
//		this.mScene = new Scene();
//		this.mScene.setOnAreaTouchTraversalFrontToBack();
//
//		//scroll is for moving around the board
//		this.mScrollDetector = new SurfaceScrollDetector(this);
//		//pinch is for zooming
//		this.mPinchZoomDetector = new PinchZoomDetector(this);
//		//listener
//		this.mScene.setOnSceneTouchListener(this);
//		//enable touch events in the scene
//		this.mScene.setTouchAreaBindingOnActionDownEnabled(true);
//
//		//1. Load TMX (The board)
//		try {
//			final TMXLoader tmxLoader = new TMXLoader(this.getAssets(), this.mEngine.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, this.getVertexBufferObjectManager(), new ITMXTilePropertiesListener() {
//				@Override
//				public void onTMXTileWithPropertiesCreated(final TMXTiledMap pTMXTiledMap, final TMXLayer pTMXLayer, final TMXTile pTMXTile, final TMXProperties<TMXTileProperty> pTMXTileProperties) {
//				}
//			});
//			this.mTMXTiledMap = tmxLoader.loadFromAsset("tmx/gameBoard.tmx");
//
//		} catch (final TMXLoadException e) {
//			Debug.e(e);
//		}
//
//		//Config the camera
//		final TMXLayer tmxLayer = this.mTMXTiledMap.getTMXLayers().get(0);
//		this.mZoomCamera.setBounds(0f, 0f, tmxLayer.getWidth(), tmxLayer.getHeight());
//		this.mZoomCamera.setBoundsEnabled(true);
//		this.mScene.attachChild(tmxLayer);
//
//
//		//Create a tile on the screen
//		final Sprite tile = new Sprite(0, 0, this.mFaceTextureRegion, this.getVertexBufferObjectManager()) {
//			//moves the sprite
//			@Override
//			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
//				if(pSceneTouchEvent.getX() > this.getWidth() / 2)
//				{
//
//				}
//				this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
//				//If the tile is released, snap it into grid.
//				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP)
//				{		
//					final float[] tileCordinates = this.convertLocalToSceneCoordinates(25, 25);
//					/* Get the tile the feet of the player are currently waking on. */
//					final TMXTile tmxTile = tmxLayer.getTMXTileAt(tileCordinates[Constants.VERTEX_INDEX_X], tileCordinates[Constants.VERTEX_INDEX_Y]);
//					if(tmxTile != null) {
//						// tmxTile.setTextureRegion(null); <-- Rubber-style removing of tiles =D
//						this.setPosition(tmxTile.getTileX(), tmxTile.getTileY());
//					}
//				}
//
//				return true;
//			}
//		};
//		this.mScene.attachChild(tile);
//		this.mScene.registerTouchArea(tile);
//
//		//2. create a row of 7 tiles that the user can put down on the table.
//
//		this.mScene.setBackground(new Background(0, 0, 0));
//		return this.mScene;
//	}
//	@Override
//	public void onScrollStarted(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
//		this.zoomHelper(pDistanceX, pDistanceY);
//	}
//	@Override
//	public void onScroll(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
//		this.zoomHelper(pDistanceX, pDistanceY);
//	}
//
//	@Override
//	public void onScrollFinished(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
//		this.zoomHelper(pDistanceX, pDistanceY);
//	}
//	@Override
//	public void onPinchZoomStarted(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent) {
//		this.mPinchZoomStartedCameraZoomFactor = this.mZoomCamera.getZoomFactor();
//	}
//	@Override
//	public void onPinchZoom(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent, final float pZoomFactor) {
//		this.mZoomCamera.setZoomFactor(this.mPinchZoomStartedCameraZoomFactor * pZoomFactor);
//	}
//	@Override
//	public void onPinchZoomFinished(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent, final float pZoomFactor) {
//		this.mZoomCamera.setZoomFactor(this.mPinchZoomStartedCameraZoomFactor * pZoomFactor);
//	}
//	//Limit to eather zoom or scroll
//	@Override
//	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
//		this.mPinchZoomDetector.onTouchEvent(pSceneTouchEvent);
//		if(this.mPinchZoomDetector.isZooming()) {
//			this.mScrollDetector.setEnabled(false);
//		} else {
//			if(pSceneTouchEvent.isActionDown()) {
//				this.mScrollDetector.setEnabled(true);
//			}
//			this.mScrollDetector.onTouchEvent(pSceneTouchEvent);
//		}
//		return true;
//	}
//	// ===========================================================
//	// Methods
//	// ===========================================================
//	public void zoomHelper(final float pDistanceX, final float pDistanceY)
//	{
//		final float zoomFactor = this.mZoomCamera.getZoomFactor();
//		this.mZoomCamera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);
//	}
//	public void initTiles()
//	{
//		this.theTiles = new Tile[] {new Tile(0), new Tile(1), new Tile(1), new Tile(1), new Tile(1), new Tile(1), new Tile(1), new Tile(1), new Tile(2), new Tile(2), new Tile(2), new Tile(2), new Tile(2), new Tile(2), new Tile(2), new Tile(3), new Tile(3), new Tile(3), new Tile(3), new Tile(3), new Tile(3), new Tile(3), new Tile(4), new Tile(4), new Tile(4), new Tile(4), new Tile(4), new Tile(4), new Tile(4), new Tile(5), new Tile(5), new Tile(5), new Tile(5), new Tile(5), new Tile(5), new Tile(5), new Tile(6), new Tile(6), new Tile(6), new Tile(6), new Tile(6), new Tile(6), new Tile(6), new Tile(7), new Tile(7), new Tile(7), new Tile(7), new Tile(7), new Tile(7), new Tile(7), new Tile(8), new Tile(8), new Tile(8), new Tile(8), new Tile(8), new Tile(8), new Tile(8), new Tile(9), new Tile(9), new Tile(9), new Tile(9), new Tile(9), new Tile(9), new Tile(9), new Tile(10), new Tile(10), new Tile(10), new Tile(10), new Tile(10), new Tile(10), new Tile(10), new Tile(11), new Tile(12), new Tile(13), new Tile(14), new Tile(15), new Tile(16), new Tile(17), new Tile(18), new Tile(19), new Tile(20), new Tile(21), new Tile(24), new Tile(25), new Tile(27), new Tile(28), new Tile(30), new Tile(32), new Tile(35), new Tile(36), new Tile(40), new Tile(42), new Tile(45), new Tile(48), new Tile(49), new Tile(50), new Tile(54), new Tile(56), new Tile(60), new Tile(63), new Tile(64), new Tile(70), new Tile(72), new Tile(80), new Tile(81), new Tile(90) };
//
//	}
//
//	// ===========================================================
//	// Inner and Anonymous Classes
//	// ===========================================================
//}
