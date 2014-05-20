//package se.DV1456.mathable;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//
//public class MainActivity extends Activity 
//{
//	@Override
//	protected void onCreate(Bundle savedInstanceState) 
//	{
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);		
//		
//		StartFragment fragment = (StartFragment) getFragmentManager().
//				findFragmentById(R.id.start_frag);
//		
//		if(fragment == null || ! fragment.isInLayout())
//		{
//			//start new activity
//		}
//		else
//		{
//			//Update?
//		}
//		
//		if (savedInstanceState == null) 
//		{
//			getFragmentManager().beginTransaction()
//					.add(R.id.container, new StartFragment()).commit();
//		}
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) 
//	{
//		// Skapar top-meny
//		MenuInflater inflater = getMenuInflater();
//	    inflater.inflate(R.menu.main, menu);
//		return super.onCreateOptionsMenu(menu);
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) 
//	{
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		
//		//Hanterar om man klickar på något i top-meny
//		switch (item.getItemId()) 
//		{
//		case R.id.action_settings:
//			//Gå till inställningar
//			return true;
//		case R.id.action_newGame:
//			//skapa ett nytt spel!
//			return true;
//		default:
//			return super.onOptionsItemSelected(item);
//		}
//	}
//}


package se.DV1456.mathable;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.entity.util.ScreenCapture;
import org.andengine.entity.util.ScreenCapture.IScreenCaptureCallback;
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
import org.andengine.util.FileUtils;
import org.andengine.util.color.Color;
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
	private static int CAMERA_WIDTH;
	private static int CAMERA_HEIGHT;
	
	// ===========================================================
	// Fields
	// ===========================================================

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private ITextureRegion mFaceTextureRegion;
	private Entity theTable;
	private TMXTiledMap mTMXTiledMap;

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
		
		//1. Create a rectangle with the grid that has the gameboard
		try {
			final TMXLoader tmxLoader = new TMXLoader(this.getAssets(), this.mEngine.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, this.getVertexBufferObjectManager(), new ITMXTilePropertiesListener() {
				@Override
				public void onTMXTileWithPropertiesCreated(final TMXTiledMap pTMXTiledMap, final TMXLayer pTMXLayer, final TMXTile pTMXTile, final TMXProperties<TMXTileProperty> pTMXTileProperties) {
					/* We are going to count the tiles that have the property "cactus=true" set. */
					if(pTMXTileProperties.containsTMXProperty("cactus", "true")) {
						//Cactus found!!
					}
				}
			});
			this.mTMXTiledMap = tmxLoader.loadFromAsset("tmx/desert.tmx");

			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					//Cactus found!!
				}
			});
		} catch (final TMXLoadException e) {
			Debug.e(e);
		}
		
		//2. create a row of 7 tiles that the user can put down on the table.
		
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
		
		// Create the rectangles
		//final Rectangle rect1 = this.makeColoredRectangle(-180, -180, 1, 0, 0);
		final Rectangle rect1 = new Rectangle(0,0, 100, 100, this.getVertexBufferObjectManager());
		final Rectangle rect2 = new Rectangle(100,0, 100, 100, this.getVertexBufferObjectManager());
		
		final Entity rectangleGroup = new Entity(0,0);
		
		rectangleGroup.attachChild(rect1);
		rectangleGroup.attachChild(rect2);
		
		scene.attachChild(rectangleGroup);
		
		scene.setBackground(new Background(0, 0, 0));
		scene.attachChild(face);
		scene.registerTouchArea(face);
		scene.setTouchAreaBindingOnActionDownEnabled(true);

		return scene;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	
	private Rectangle makeRectangleFromFile(final float pX, final float pY, String fileName) {
		final Rectangle tile = new Rectangle(pX, pY, 100, 100, this.getVertexBufferObjectManager());
		tile.setColor(Color.RED);
		return tile;
	}
	private void createTable()
	{
		
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
