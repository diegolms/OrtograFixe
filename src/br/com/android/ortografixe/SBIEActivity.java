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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import br.com.android.ortografixe.informacoes.Valor;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;


public class SBIEActivity extends BaseGameActivity implements IOnAreaTouchListener {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 600;
	private static final int CAMERA_HEIGHT = 1024;

	private static final int PROXIMO_DESAFIO = 0;
	private static final int SAIR = 1;


	private PhysicsWorld mPhysicsWorld;
	private BitmapTextureAtlas mFontTexture;
	private Font mFont;
	private int cont = 3;
	private PhysicsHandler physicsHandler;
	private Scene scene;
	int[] coordenadas = new int[9];
	float[] duracao = new float[3];
	private BitmapTextureAtlas mFontAcertosTexture,mFontErrosTexture;
	private Font mFontAcertos, mFontErros;
	private int acertos, erros,aux = 0;
	private int time = 20;
	private ChangeableText textoErros,textoAcertos;
	private AlertDialog.Builder alert;
	private Map<String, List<Valor>> mapa;
	private Set<String> chaves;
	private String chaveDaVez= "";
	private String regraDaVez= "";
	private List<String> chavesChamadas;
	private LinkedList<Valor> valores;
	private ChangeableText texto;
	private LinkedList<ChangeableText> palavras;
	private BitmapTextureAtlas mFontTempoTexture;
	private Font mFontTempo;
	private ChangeableText textoTempo;
	private TimerHandler timer;



	@Override
	public Engine onLoadEngine() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		engineOptions.getTouchOptions().setRunOnUpdateThread(true);
		return new Engine(engineOptions);
	}

	@Override
	public void onLoadResources() {

		this.mFontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFontAcertosTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFontErrosTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFontTempoTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFont = new Font(this.mFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 40, true, Color.BLACK);
		this.mFontAcertos = new Font(this.mFontAcertosTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 30, true, Color.BLUE);
		this.mFontErros = new Font(this.mFontErrosTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 30, true, Color.RED);
		this.mFontTempo = new Font(this.mFontTempoTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 30, true, Color.BLACK);
		this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
		this.mEngine.getFontManager().loadFonts(this.mFontAcertos,this.mFontErros,this.mFont, this.mFontTempo);
		this.mEngine.getTextureManager().loadTextures(this.mFontAcertosTexture,this.mFontErrosTexture,this.mFontTexture, this.mFontTempoTexture);
		chavesChamadas = new LinkedList<String>();
		this.carregarTodasAsPalavras();
		chaves = mapa.keySet();

	}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.scene = new Scene();
		this.scene.setBackground(new ColorBackground(255, 255, 255));
		this.preencherCoordenadaseDuracao();
		this.mPhysicsWorld = new PhysicsWorld(new Vector2(), false);
		this.scene.registerUpdateHandler(this.mPhysicsWorld);
		this.scene.setOnAreaTouchListener(this);

		novoDesafio();

	

		return this.scene;
	}

	private void tempo() {
		this.scene.registerUpdateHandler(timer = new TimerHandler(1 / 20.0f, true, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				int tempo = Math.round(SBIEActivity.this.mEngine.getSecondsElapsedTotal());
				aux = time-tempo;
				if(aux>9){
					textoTempo.setText("0:"+(aux));
				}else{
					textoTempo.setText("0:0"+(aux));
				}
				 
				if(aux<=0){
					time += 20;
					novoDesafio();
					scene.unregisterUpdateHandler(timer);
					
					
				}
			
				if(tempo==cont){
					int i = 1 + (int)(Math.random() * 2);
					cont+=i;
					gerarPalavra();

				}
			}
		}));
	}

	private void novaRegra(String descricao) {
		palavras = new LinkedList<ChangeableText>();
		alert = new AlertDialog.Builder(SBIEActivity.this);
		alert.setTitle("Regra");
		alert.setMessage(descricao);
		alert.setPositiveButton("Começar", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				gerarPalavra();
				tempo();
			}
		});

		alert.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent keyEvent) {
				if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {

					return true;
				}
				else if (keyCode == KeyEvent.KEYCODE_SEARCH && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
					return true;
				}
				else{
					return false;	
				}

			}
		});

		alert.show();
	}

	private void gerarPalavra(){
		int k = 0 + (int)(Math.random() * coordenadas.length);
		addPalavra(coordenadas[k], 10, chaveDaVez);  	  
	}

	private void preencherCoordenadaseDuracao(){
		coordenadas[0] = 280;
		coordenadas[1] = 300;
		coordenadas[2] = 320;
		coordenadas[3] = 350;
		coordenadas[4] = 380;
		coordenadas[5] = 400;
		coordenadas[6] = 480;
		coordenadas[7] = 500;
		coordenadas[8] = 580;
		duracao[0] = 100.0f;
		duracao[1] = 200.0f;
		duracao[2] = 300.0f;
		textoAcertos = new ChangeableText(20, 5, mFontAcertos, "Acertos: " +acertos, "Acertos: XXXX".length());
		textoErros = new ChangeableText(450, 5, mFontErros, "Erros: " +erros, "Erros: XXXX".length());
		textoTempo = new ChangeableText(280, 5, mFontTempo, "0:"+time, "Erros: XXXX".length());
		this.scene.attachChild(textoAcertos);
		this.scene.attachChild(textoErros);
		this.scene.attachChild(textoTempo);
		
	}

	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		if(pSceneTouchEvent.isActionDown()) {
			this.removeFace((Text)pTouchArea);
			this.pontuar((Text)pTouchArea, chaveDaVez);
			return true;
		}

		return false;
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


	private void addPalavra(final float pX, final float pY, String chave) {
		//final ChangeableText texto;
		final Body body;

		final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 0);
		int k = 0 + (int)(Math.random() * mapa.get(chave).size());
		String pergunta = mapa.get(chave).get(k).getValor();
		texto = new ChangeableText(pX, pY, this.mFont,"",HorizontalAlign.LEFT,20);
		texto.setText(pergunta);
		texto.setPosition(pX - this.mFont.getStringWidth(texto.getText()), pY);
		palavras.add(texto);
		body = PhysicsFactory.createBoxBody(this.mPhysicsWorld, texto, BodyType.DynamicBody, objectFixtureDef);

		int j = 0 + (int)(Math.random() * duracao.length);
		physicsHandler = new PhysicsHandler(texto);
		texto.registerUpdateHandler(physicsHandler);
		physicsHandler.setVelocityY(duracao[j]);
		this.scene.attachChild(texto);
		this.scene.registerTouchArea(texto);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(texto, body, false, false));
	}

	private void removeFace(Text texto) {
		final PhysicsConnector facePhysicsConnector = this.mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(texto);

		this.mPhysicsWorld.unregisterPhysicsConnector(facePhysicsConnector);
		this.mPhysicsWorld.destroyBody(facePhysicsConnector.getBody());

		this.scene.unregisterTouchArea(texto);
		this.scene.detachChild(texto);

		System.gc();
	}

	private void pontuar(final Text texto, String chave){
		for (final Valor valor: mapa.get(chave)){
			if(valor.getValor().equals(texto.getText())){
				if(valor.isAcerto()){
					acertos++;
				}
				else{
					erros++;
					SBIEActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							alert = new AlertDialog.Builder(SBIEActivity.this);
							alert.setTitle("CUIDADO!!!");
							alert.setMessage(valor.getRegra());
							alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0, int arg1) {

								}
							});

							alert.show();		

						}
					});

				}
			}

			textoAcertos.setText("Acertos: " +acertos);
			textoErros.setText("Erros: " +erros);

		}


	}

	private String sortearChaves(){
		String chave = "";
		for (Iterator<String> iterator = chaves.iterator(); iterator.hasNext();){
			chave = iterator.next();
			if((chave != null) && (!chavesChamadas.contains(chave))){  
				chaveDaVez = chave;
				chavesChamadas.add(chave);
				regraDaVez = mapa.get(chaveDaVez).get(1).getDescricao();
				break;
			}
			else if(chavesChamadas.size()==3){
				Intent i = new Intent(SBIEActivity.this, TelaFinalActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.putExtra("acertos", acertos);
				i.putExtra("erros", erros);
				startActivity(i);
			}
		}
		return chave; 
	}

	
	private void novoDesafio() {
		
		SBIEActivity.this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				apagarPalavras();
				sortearChaves();
				novaRegra(regraDaVez);
				
			}
		});
		
	}

	private void apagarPalavras() {
		if(palavras != null){
			for(int i=0; i<palavras.size(); i++){
				this.scene.detachChild(palavras.get(i));
			}
		}

	}

	private void carregarTodasAsPalavras(){
		mapa = new HashMap<String, List<Valor>>();
		valores = new LinkedList<Valor>();
		valores.add(new Valor("idéia", false, "Os ditongos abertos tônicos éi e ói perdem o acento agudo quando caem na penúltima sílaba (portanto, de palavras paroxítonas","Mudanças na Acentuação e no uso do trema"));
		valores.add(new Valor("jóia", false,"Os ditongos abertos tônicos éi e ói perdem o acento agudo quando caem na penúltima sílaba (portanto, de palavras paroxítonas","Mudanças na Acentuação e no uso do trema"));
		valores.add(new Valor("geléia", false,"Os ditongos abertos tônicos éi e ói perdem o acento agudo quando caem na penúltima sílaba (portanto, de palavras paroxítonas","Mudanças na Acentuação e no uso do trema"));
		valores.add(new Valor("tramóia", false,"Os ditongos abertos tônicos éi e ói perdem o acento agudo quando caem na penúltima sílaba (portanto, de palavras paroxítonas","Mudanças na Acentuação e no uso do trema"));
		valores.add(new Valor("epopéia", false,"Os ditongos abertos tônicos éi e ói perdem o acento agudo quando caem na penúltima sílaba (portanto, de palavras paroxítonas","Mudanças na Acentuação e no uso do trema"));
		valores.add(new Valor("apóia", false,"Os ditongos abertos tônicos éi e ói perdem o acento agudo quando caem na penúltima sílaba (portanto, de palavras paroxítonas","Mudanças na Acentuação e no uso do trema"));
		valores.add(new Valor("diarréico", false,"Os ditongos abertos tônicos éi e ói perdem o acento agudo quando caem na penúltima sílaba (portanto, de palavras paroxítonas","Mudanças na Acentuação e no uso do trema"));
		valores.add(new Valor("heróico", false,"Os ditongos abertos tônicos éi e ói perdem o acento agudo quando caem na penúltima sílaba (portanto, de palavras paroxítonas","Mudanças na Acentuação e no uso do trema"));
		valores.add(new Valor("hebréia", false,"Os ditongos abertos tônicos éi e ói perdem o acento agudo quando caem na penúltima sílaba (portanto, de palavras paroxítonas","Mudanças na Acentuação e no uso do trema"));
		valores.add(new Valor("debilóide", false,"Os ditongos abertos tônicos éi e ói perdem o acento agudo quando caem na penúltima sílaba (portanto, de palavras paroxítonas","Mudanças na Acentuação e no uso do trema"));
		valores.add(new Valor("ideia", true,"",""));
		valores.add(new Valor("joia", true,"",""));
		valores.add(new Valor("geleia", true,"",""));
		valores.add(new Valor("tramoia", true,"",""));
		valores.add(new Valor("epopeia", true,"",""));
		valores.add(new Valor("apoia", true,"",""));
		valores.add(new Valor("diarreico", true,"",""));
		valores.add(new Valor("heroico", true,"",""));
		valores.add(new Valor("hebreia", true,"",""));
		valores.add(new Valor("debiloide", true,"",""));
		valores.add(new Valor("vôo", false,"Cai o acento circunflexo de palavras paroxítonas terminadas em ôo e em êem","Mudanças na Acentuação e no uso do trema"));
		valores.add(new Valor("dêem", false,"Cai o acento circunflexo de palavras paroxítonas terminadas em ôo e em êem","Mudanças na Acentuação e no uso do trema"));
		valores.add(new Valor("enjôo", false,"Cai o acento circunflexo de palavras paroxítonas terminadas em ôo e em êem","Mudanças na Acentuação e no uso do trema"));
		valores.add(new Valor("vêem", false,"Cai o acento circunflexo de palavras paroxítonas terminadas em ôo e em êem","Mudanças na Acentuação e no uso do trema"));
		valores.add(new Valor("acôo", false,"Cai o acento circunflexo de palavras paroxítonas terminadas em ôo e em êem","Mudanças na Acentuação e no uso do trema"));
		valores.add(new Valor("crêem", false,"Cai o acento circunflexo de palavras paroxítonas terminadas em ôo e em êem","Mudanças na Acentuação e no uso do trema"));
		valores.add(new Valor("abençôo", false,"Cai o acento circunflexo de palavras paroxítonas terminadas em ôo e em êem","Mudanças na Acentuação e no uso do trema"));
		valores.add(new Valor("lêem", false,"Cai o acento circunflexo de palavras paroxítonas terminadas em ôo e em êem","Mudanças na Acentuação e no uso do trema"));
		valores.add(new Valor("voo", true,"",""));
		valores.add(new Valor("deem", true,"",""));
		valores.add(new Valor("enjoo", true,"",""));
		valores.add(new Valor("veem", true,"",""));
		valores.add(new Valor("acoo", true,"",""));
		valores.add(new Valor("creem", true,"",""));
		valores.add(new Valor("abençoo", true,"",""));
		valores.add(new Valor("leem", true,"",""));
		valores.add(new Valor("boiúno", false,"Perdem o acento agudo as vogais tônicas i e u de palavras paroxítonas, quando antecedidas de ditongo","Mudanças na Acentuação e no uso do trema"));
		valores.add(new Valor("feiúra", false,"Perdem o acento agudo as vogais tônicas i e u de palavras paroxítonas, quando antecedidas de ditongo","Mudanças na Acentuação e no uso do trema"));
		valores.add(new Valor("baiúca", false,"Perdem o acento agudo as vogais tônicas i e u de palavras paroxítonas, quando antecedidas de ditongo","Mudanças na Acentuação e no uso do trema"));
		valores.add(new Valor("alauíta", false,"Perdem o acento agudo as vogais tônicas i e u de palavras paroxítonas, quando antecedidas de ditongo","Mudanças na Acentuação e no uso do trema"));
		valores.add(new Valor("boiuno", true,"",""));
		valores.add(new Valor("feiura", true,"",""));
		valores.add(new Valor("baiuca", true,"",""));
		valores.add(new Valor("alauita", true,"",""));
		valores.add(new Valor("agüentar", false,"O trema deixa de ser usado para assinalar a pronúncia do u* em sílabas como güe, güi, qüe, qüi. Permanece em palavras estrangeiras e suas derivadas","Mudanças na Acentuação e no uso do trema"));
		valores.add(new Valor("sagüi", false,"O trema deixa de ser usado para assinalar a pronúncia do u* em sílabas como güe, güi, qüe, qüi. Permanece em palavras estrangeiras e suas derivadas","Mudanças na Acentuação e no uso do trema"));
		valores.add(new Valor("freqüenciar", false,"O trema deixa de ser usado para assinalar a pronúncia do u* em sílabas como güe, güi, qüe, qüi. Permanece em palavras estrangeiras e suas derivadas","Mudanças na Acentuação e no uso do trema"));
		valores.add(new Valor("tranqüilo", false,"O trema deixa de ser usado para assinalar a pronúncia do u* em sílabas como güe, güi, qüe, qüi. Permanece em palavras estrangeiras e suas derivadas","Mudanças na Acentuação e no uso do trema"));
		valores.add(new Valor("aguentar", true,"",""));
		valores.add(new Valor("sagui", true,"",""));
		valores.add(new Valor("frequenciar", true,"",""));
		valores.add(new Valor("tranquilo", true,"",""));
		mapa.put("Acentuação", valores);
		valores = new LinkedList<Valor>();
		valores.add(new Valor("água-de-coco", false,"Não se usa hífen em locuções nominais","Locuções"));
		valores.add(new Valor("café-da-manhã", false,"Não se usa hífen em locuções nominais","Locuções"));
		valores.add(new Valor("água de coco", true,"","Locuções"));
		valores.add(new Valor("café da manhã", true,"","Locuções"));
		valores.add(new Valor("cor-de-abóbora", false,"Não se usa hífen em locuções adjetivas","Locuções"));
		valores.add(new Valor("cor-de-açafrão", false,"Não se usa hífen em locuções adjetivas","Locuções"));
		valores.add(new Valor("cor-de-abóbora", true,"","Locuções"));
		valores.add(new Valor("cor-de-açafrão", true,"","Locuções"));
		valores.add(new Valor("à-vontade", false,"Não se usa hífen em locuções adverbiais","Locuções"));
		valores.add(new Valor("antes-de-ontem", false,"Não se usa hífen em locuções adverbiais","Locuções"));
		valores.add(new Valor("à vontade", true,"","Locuções"));
		valores.add(new Valor("antes de ontem", true,"","Locuções"));
		mapa.put("Locuções", valores);
		valores = new LinkedList<Valor>();
		valores.add(new Valor("ultra-som", false,"Quando o prefixo ou falso prefixo termina em vogal e o segundo elemento começa por r ou s não se usa mais o hífen e a consoante r ou s é duplicada","Mudanças no uso do hífen em palavras compostas por prefixação e recomposição"));
		valores.add(new Valor("ante-semita", false,"Quando o prefixo ou falso prefixo termina em vogal e o segundo elemento começa por r ou s não se usa mais o hífen e a consoante r ou s é duplicada","Mudanças no uso do hífen em palavras compostas por prefixação e recomposição"));
		valores.add(new Valor("eco-sistema", false,"Quando o prefixo ou falso prefixo termina em vogal e o segundo elemento começa por r ou s não se usa mais o hífen e a consoante r ou s é duplicada","Mudanças no uso do hífen em palavras compostas por prefixação e recomposição"));
		valores.add(new Valor("mini-saia", false,"Quando o prefixo ou falso prefixo termina em vogal e o segundo elemento começa por r ou s não se usa mais o hífen e a consoante r ou s é duplicada","Mudanças no uso do hífen em palavras compostas por prefixação e recomposição"));
		valores.add(new Valor("maxi-resultado", false,"Quando o prefixo ou falso prefixo termina em vogal e o segundo elemento começa por r ou s não se usa mais o hífen e a consoante r ou s é duplicada","Mudanças no uso do hífen em palavras compostas por prefixação e recomposição"));
		valores.add(new Valor("contra-regra", false,"Quando o prefixo ou falso prefixo termina em vogal e o segundo elemento começa por r ou s não se usa mais o hífen e a consoante r ou s é duplicada","Mudanças no uso do hífen em palavras compostas por prefixação e recomposição"));
		valores.add(new Valor("co-seno", false,"Quando o prefixo ou falso prefixo termina em vogal e o segundo elemento começa por r ou s não se usa mais o hífen e a consoante r ou s é duplicada","Mudanças no uso do hífen em palavras compostas por prefixação e recomposição"));
		valores.add(new Valor("semi-reta", false,"Quando o prefixo ou falso prefixo termina em vogal e o segundo elemento começa por r ou s não se usa mais o hífen e a consoante r ou s é duplicada","Mudanças no uso do hífen em palavras compostas por prefixação e recomposição"));
		valores.add(new Valor("ultrassom", true,"","Mudanças no uso do hífen em palavras compostas por prefixação e recomposição"));
		valores.add(new Valor("antessemita", true,"","Mudanças no uso do hífen em palavras compostas por prefixação e recomposição"));
		valores.add(new Valor("ecossistema", true,"","Mudanças no uso do hífen em palavras compostas por prefixação e recomposição"));
		valores.add(new Valor("minissaia", true,"","Mudanças no uso do hífen em palavras compostas por prefixação e recomposição"));
		valores.add(new Valor("maxirresultado", true,"","Mudanças no uso do hífen em palavras compostas por prefixação e recomposição"));
		valores.add(new Valor("contrarregra", true,"","Mudanças no uso do hífen em palavras compostas por prefixação e recomposição"));
		valores.add(new Valor("cosseno", true,"","Mudanças no uso do hífen em palavras compostas por prefixação e recomposição"));
		valores.add(new Valor("semirreta", true,"","Mudanças no uso do hífen em palavras compostas por prefixação e recomposição"));
		mapa.put("hífen", valores);

	}
}
