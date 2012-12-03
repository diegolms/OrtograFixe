package br.com.android.ortografixe;
/*
 * THis program is supposed to create a menu with a background and buttons that go to the game, options, and quit
 */
import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.entity.scene.menu.item.TextMenuItem;
import org.anddev.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.ui.activity.BaseGameActivity;



import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;


public class MenuActivity extends BaseGameActivity implements IOnMenuItemClickListener {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final int CAMERA_WIDTH = 600;
	private static final int CAMERA_HEIGHT = 1024;

	//Creates Id's for the buttons
	protected static final int MENU_GAME1 = 0;
	protected static final int MENU_OPTIONS = MENU_GAME1 + 3;
	protected static final int MENU_QUIT = MENU_GAME1 + 4;

	// ===========================================================
	// Fields
	// ===========================================================

	protected Camera mCamera;

	protected Scene mMainScene;

	private BitmapTextureAtlas mFontTexture, mFontTextureLogo;
	private Font mFont, mFontLogo;

	protected MenuScene mMenuScene;

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
	public Engine onLoadEngine() {
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);		
		return new Engine(new EngineOptions(true, ScreenOrientation.PORTRAIT,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
	}
	@Override
	public void onLoadResources() {

		this.mFontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFont = new Font(this.mFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 40, true, Color.BLACK);
		this.mFontTextureLogo = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFontLogo = new Font(this.mFontTextureLogo, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 80, true, Color.BLUE);
		this.mEngine.getTextureManager().loadTextures(this.mFontTexture, this.mFontTextureLogo);
		this.mEngine.getFontManager().loadFonts(this.mFont, this.mFontLogo);

	}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
				
		//Initializes the Menu
		this.mMenuScene = this.createMenuScene();

		//Creates the Scene and sets  the background
		this.mMainScene = new Scene();
		
		final Text logo = new Text(75, 5, this.mFontLogo, "OrtograFixe");
		this.mMainScene.attachChild(logo);
		this.mMainScene.setBackground(new ColorBackground(1f, 1f, 1f));
		
		//Puts the menu in the scene
		this.mMainScene.setChildScene(this.mMenuScene, false, true, true);
		
		return this.mMainScene;
	}

	@Override
	public void onLoadComplete() {

	}


	@Override
	public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {
		switch(pMenuItem.getID()) {
			case MENU_GAME1:
				/* Start Game Activity.*/
				Intent intent =new Intent(MenuActivity.this, SBIEActivity.class); 
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
			case MENU_OPTIONS:
				/* Start Game Activity. */
//				Intent intent =new Intent(MenuActivity.this, .class); 
//				startActivity(intent);
				return true;
			case MENU_QUIT:
				/* End Activity. */
				this.finish();
				return true;
			default:
				return false;
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================
	/*
	 * Creates the menu as you see it, which gets passed to on load scene
	 * You set the variable for text, then font, and then the string that is seen on the screen. You then set the color for the font
	 */
	protected MenuScene createMenuScene() {
		final MenuScene menuScene = new MenuScene(this.mCamera);
		//Does not do anything
		menuScene.setPosition(10, 10);
		final IMenuItem Game1MenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_GAME1, this.mFont, "Novo Jogo"), 1.0f,0.0f,0.0f, 0.0f,0.0f,0.0f);
		Game1MenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(Game1MenuItem);
		
//		final IMenuItem OptionsMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_OPTIONS, this.mFont, "Como Jogar"), 1.0f,0.0f,0.0f, 0.0f,0.0f,0.0f);
//		OptionsMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
//		menuScene.addMenuItem(OptionsMenuItem);
//		
		final IMenuItem quitMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_QUIT, this.mFont, "Sair"), 1.0f,0.0f,0.0f, 0.0f,0.0f,0.0f);
		quitMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(quitMenuItem);

		menuScene.buildAnimations();

		menuScene.setBackgroundEnabled(false);

		menuScene.setOnMenuItemClickListener(this);
		return menuScene;
	}


	// ==========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	//Transitions done when an activity is pushed to the background
	@Override
	protected void onPause() {
		super.onPause();
		//MenuActivity.this.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
}







