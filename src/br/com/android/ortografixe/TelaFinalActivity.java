package br.com.android.ortografixe;


import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnAreaTouchListener;
import org.anddev.andengine.entity.scene.Scene.ITouchArea;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.HorizontalAlign;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import br.com.android.ortografixe.informacoes.Valor;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga
 *
 * @author Nicolas Gramlich
 * @since 18:47:08 - 19.03.2010
 */
public class TelaFinalActivity extends BaseGameActivity{
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 600;
	private static final int CAMERA_HEIGHT = 1024;

	
	private Scene scene;
	private BitmapTextureAtlas mFontDesempenhoTexture,mFontAcertosTexture,mFontErrosTexture;
	private Font mFontDesempenho, mFontAcertos, mFontErros;
	private ChangeableText textoErros,textoAcertos, textoDesempenho;
	

	@Override
	public Engine onLoadEngine() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		engineOptions.getTouchOptions().setRunOnUpdateThread(true);
		return new Engine(engineOptions);
	}

	@Override
	public void onLoadResources() {

		this.mFontDesempenhoTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFontAcertosTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFontErrosTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFontDesempenho = new Font(this.mFontDesempenhoTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 70, true, Color.BLACK);
		this.mFontAcertos = new Font(this.mFontAcertosTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 50, true, Color.BLUE);
		this.mFontErros = new Font(this.mFontErrosTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 50, true, Color.RED);
		this.mEngine.getFontManager().loadFonts(this.mFontAcertos,this.mFontErros,this.mFontDesempenho);
		this.mEngine.getTextureManager().loadTextures(this.mFontAcertosTexture,this.mFontErrosTexture,this.mFontDesempenhoTexture);
	

	}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.scene = new Scene();
		this.scene.setBackground(new ColorBackground(255, 255, 255));
		
		Bundle bundle = getIntent().getExtras();
		int acertos = bundle.getInt("acertos");
		int erros = bundle.getInt("erros");
		textoAcertos = new ChangeableText(20, 350, mFontAcertos, "Acertos: " +acertos, "Acertos: XXXX".length());
		textoErros = new ChangeableText(20, 450, mFontErros, "Erros: " +erros, "Erros: XXXX".length());
		textoDesempenho = new ChangeableText(90, 5, mFontDesempenho, "Desempenho", "Erros: XXXX".length());
		this.scene.attachChild(textoAcertos);
		this.scene.attachChild(textoErros);
		this.scene.attachChild(textoDesempenho);
		
		return this.scene;
	}

	
	
	
	@Override
	public void onLoadComplete() {

	}

	@Override
	public void onResumeGame() {

		super.onResumeGame();


	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();

		this.disableAccelerometerSensor();
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			Intent intent = new Intent(TelaFinalActivity.this, MenuActivity.class); 
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


}
