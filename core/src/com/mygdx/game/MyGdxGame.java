package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.backends.lwjgl.audio.Mp3.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.*;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.Arrays;
import java.util.Timer;


import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class MyGdxGame extends ApplicationAdapter {

	
	//Textures
	Texture anzeige;
	Texture felsen_unter_wasser;
	Texture hai_1;
	Texture hai_2;
	Texture herz_leer_tex;
	Texture herz_voll_tex;
	Texture hindernis_felsen;
	Texture rennschwan;
	Texture schwimmer_aufsicht_body;
	Texture schwimmer_aufsicht_linker_arm;
	Texture schwimmer_aufsicht_rechter_arm;
	Texture schwimmer_seitlich_body;
	Texture schwimmer_seitlich_rechtes_bein;
	Texture schwimmer_seitlich_linkes_bein;
	Texture luftblasen;
	Texture seerosen;
	Texture seerosen_mund;
	Texture seerose_zaehne;
	Texture ufer;
	Texture unter_wasser_textur_1;
	Texture unter_wasser_textur_2;
	Texture unter_wasser_textur_3;
	Texture unter_wasser_textur_4;
	Texture wellen;

	
	private GameState state;

	// Hintergrund Schwimmwelt
	private Sprite wellen1;
	private Sprite wellen2;
	private Sprite wellen3;
	private Sprite wellen4;
	private Sprite ufer_links;
	private Sprite ufer_rechts;

	// Hintergrund Tauchwelt
	private Sprite hintergrund1;
	private Sprite hintergrund2;
	private Sprite hintergrund3;
	private Sprite hintergrund4;

	private Sprite herz_leer;
	private Sprite herz_voll;
	private Sprite swimmer;
	private Sprite swimmer_rechter_arm;
	private Sprite swimmer_linker_arm;

	private Sprite tauchersprite;
	private Sprite taucher_rechtes_bein;
	private Sprite taucher_linkes_bein;
	private Sprite taucher_luftblasen;
	
	//Hindernis unter Wasser
	private Sprite hindernis_lowerworld_up;
	private Sprite hindernis_lowerworld_low;
	
	private SpriteBatch batch;

	private World world;
	public Body body;

	// Schrift
	private BitmapFont font;
	private BitmapFont gameover; 


	// Graphics Updates -> Variables to update positions
	private float wellen_y_pos;
	private float loop;
	private float unter_wasser_textur_pos;
	private float zeit_unter_wasser;

	// Hindernis-Array-Swim
	private Obstacle[] hindernis = new Obstacle[40];
	// Positionen aktiver Hindernisse in array
	private boolean[] hindernis_aktiv = new boolean[40];

	// Hindernis Dive
	private Obstacle hindernis_lowerworld_upper;
	private Obstacle hindernis_lowerworld_lower;
	private float[] wand_punkte = new float[2 * 10];

	// Hilfsvariable für den Hindernisgenerator
	// Bei Aufruf von Hindernis-Generator wird h auf 0 gesetzt
	// Bei jedem Aufruf von render wird geschwindigkeit auf h addiert
	// Bis h größer gleich der Länge eines Hindernisses ist, dann starte
	// Hindernisgenerator
	private float h;
	
	//Hindernis-Generator
	//maximale Anzahl unterschiedlicher Hindernisse
	private int n_obstacles = 4;
	//Schwierigkeit einzelner Typen von Hindernissen
	//Hindernis x kann ab Level difficulty[x] generiert werden
	private int[] difficulty = new int[n_obstacles];
	//Start-Wahrscheinlichkeit eines Hindernisses x in lvl difficulty[x]
	private double[] first_probability = new double[n_obstacles];
	//Nach so vielen Leveln ist probability des Hindernisses auf 0.1
	private int obstacle_ausdauer = 10;
	//Wahrscheinlichkeits-Verteilung des gemeinen Hindernisses: [Hindernis,lvl]
	private double[][] obstacle_probability = new double[n_obstacles][obstacle_ausdauer];
	//Erwartungswert Anzahl Hindernisse pro Zeile
	private double generation_probability;
	//Poisson-Verteilung für Anzahl Hindernisse einer Zeile
	private double[] p = new double[8];

	// Variablen für Schwimmer, Hintergrund, Hindernis
	private float geschwindigkeit;
	private float max_speed = 5.0f;
	private float hindernis_geschwindigkeit = 1.0f;
	// Aenderung der Geschwindigkeit
	private float beschleunigung;
	//Hilfsvariable, welche die Echtzeit messen soll
	//Geschwindigkeit kann dafür nicht verwendet werden, da sich diese erhöht
	private int realtime;

	// swimmer variables
	// Bahn des Schwimmers
	private int swimmer_position_swim;
	// swimmer Groesse
	private float swimmer_width;

	// Verwundbarkeit
	private boolean invulnerable;
	
	//Schwan_Geschwindigkeit
	private int schwan_speed = 20;

//Abstand zur Bahn
	private float swimmer_offset;
	//Position der Arme
	private float arm_pos;
	private float arm_pos_x = 0.0f;
	private float arm_pos_y = 0.0f;
	
	//taucher variables
	//taucher Groesse
	private float taucher_width;
	private float taucher_body_width;
	private float luftblasen_x_pos;
	private float luftblasen_y_pos;

	// game variables
	private long score;
	private long level;
	private int health;
	private boolean game_over;
	
	// highscore-management
	Highscore highscore;

	// Zählt wie viel weiter geschwommen wurde, in Länge eines Hindernisses
	private long Zeile;

	//Hilfsvariable: bei Kollision
	private boolean freeze;
	
	// Musik & Sound
	private Sound sound;
	private Music music;
	private Music bewegungmusic;
	private Music shark;

	// shortcuts for graphics fields
	private int width, height;
	private float ppiX, ppiY;

	private float width2; 


	// input
	private boolean paused;
	private InputMultiplexer multiplexer;

	private Menu menu;
	private EventListener steuerung;

	private FreeTypeFontGenerator generator;

	//Luftanzeige
	private Sprite luftanzeige;


	@Override
	public void create() {

		// init Sounds
		music = Gdx.audio.newMusic(Gdx.files.internal("super-mario-bros.mp3"));
		music.setLooping(true);
		music.setVolume(0.3f);
		shark = Gdx.audio.newMusic(Gdx.files.internal("shark_bite.mp3"));
		shark.setVolume(0.3f);
			

		// init state
		state = GameState.MAINMENU;

		// Infos Screen;
		readGraphics();

		// New Sprite Batch
		batch = new SpriteBatch();
		
		//Textures laden
		anzeige = new Texture("anzeige.png");
		felsen_unter_wasser = new Texture("felsen_unter_wasser.png");
		hai_1 = new Texture("hai_1.png");
		hai_2 = new Texture("hai_2.png");
		herz_leer_tex = new Texture("herz_leer.png");
		herz_voll_tex = new Texture("herz_voll.png");
		hindernis_felsen = new Texture("hindernis_felsen.png");
		rennschwan = new Texture("rennschwan.png");
		schwimmer_aufsicht_body = new Texture("schwimmer_aufsicht_body.png");
		schwimmer_aufsicht_linker_arm = new Texture("schwimmer_aufsicht_linker_arm.png");
		schwimmer_aufsicht_rechter_arm = new Texture("schwimmer_aufsicht_rechter_arm.png");
		schwimmer_seitlich_body = new Texture("schwimmer_seitlich_body.png");
		schwimmer_seitlich_rechtes_bein = new Texture("schwimmer_seitlich_rechtes_bein.png");
		schwimmer_seitlich_linkes_bein = new Texture("schwimmer_seitlich_linkes_bein.png");
		luftblasen = new Texture("luftblasen.png");
		seerosen = new Texture("seerosen.png");
		seerosen_mund = new Texture("seerosen_mund.png");
		seerose_zaehne = new Texture("seerosen_zaehne.png");
		ufer = new Texture("ufer.png");
		unter_wasser_textur_1 = new Texture("unter_wasser_textur_1.png");
		unter_wasser_textur_2 = new Texture("unter_wasser_textur_2.png");
		unter_wasser_textur_3 = new Texture("unter_wasser_textur_3.png");
		unter_wasser_textur_4 = new Texture("unter_wasser_textur_4.png");
		wellen = new Texture("wellen.png");

		// init Wellentextur
		wellen1 = new Sprite(wellen);
		wellen1.setSize(width, height);
		wellen2 = new Sprite(wellen);
		wellen2.setSize(width, height);
		wellen_y_pos = 0;

		// init Unterwasserwelt Hintergrund

		hintergrund1 = new Sprite(new Texture("unter_wasser_textur_1.png"));
		hintergrund1.setSize(width, height);
		hintergrund2 = new Sprite(new Texture("unter_wasser_textur_2.png"));
		hintergrund2.setSize(width, height);
		hintergrund3 = new Sprite(new Texture("unter_wasser_textur_3.png"));
		hintergrund3.setSize(width, height);
		hintergrund4 = new Sprite(new Texture("unter_wasser_textur_4.png"));
		hintergrund4.setSize(width, height);
		unter_wasser_textur_pos = 0.0f;
		zeit_unter_wasser = 0.0f;

		//Luftanzeige
		luftanzeige = new Sprite(new Texture("image.png"));
		luftanzeige.setSize(width/18, height/18);
		loop = 0; 
		
		//init Taucher
		tauchersprite = new Sprite(schwimmer_seitlich_body);
		taucher_rechtes_bein = new Sprite(schwimmer_seitlich_rechtes_bein);
		taucher_linkes_bein = new Sprite(schwimmer_seitlich_linkes_bein);
		taucher_width = width/9;
		taucher_body_width = width/12;
		luftblasen_x_pos = 0.0f-(taucher_width);
		luftblasen_y_pos = 0.0f;
		
		world = new World(new Vector2(0, -1), true);
		BodyDef diver = new BodyDef();
		diver.type = BodyDef.BodyType.DynamicBody;
		
		// TODO Anfangsposition bzw. Anfangsimpuls bei changeDiveState
		
		diver.position.set(0, 0);
		body = world.createBody(diver);

		CircleShape circle = new CircleShape();
		circle.setRadius(6f);

		FixtureDef diverfixture = new FixtureDef();
		diverfixture.shape = circle;
		diverfixture.density = 0.1f;
		diverfixture.friction = 0.4f;
		diverfixture.restitution = 0.6f;

		body.createFixture(diverfixture);

		circle.dispose();

		//Anzeigen
		//init Lebens-Anzeige
		herz_leer = new Sprite(herz_leer_tex);
		herz_voll = new Sprite(herz_voll_tex);		
		herz_voll.setSize(width/18, height/18);
		herz_leer.setSize(width/18, height/18);
		
		health = 5;
		
		//init Schrift für alle Anzeigen
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Mecha_Bold.ttf"));
		FreeTypeFontParameter parameter1 = new FreeTypeFontParameter();
		FreeTypeFontParameter parameter2 = new FreeTypeFontParameter();
		parameter1.size = 27;
		parameter2.size = 50;
		parameter1.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.!'()>?: ";
		font = generator.generateFont(parameter1);
		gameover = generator.generateFont(parameter2);
		
		//init Swimmer_Grafik
		swimmer = new Sprite(schwimmer_aufsicht_body);
		swimmer_linker_arm = new Sprite(schwimmer_aufsicht_linker_arm);
		swimmer_rechter_arm = new Sprite(schwimmer_aufsicht_rechter_arm);
		
		swimmer_offset = ((width-2) / 9) * 1/8;
		swimmer_width = ((width-2) / 9) * 3/4;

		//init Ufertextur
		ufer_links = new Sprite(ufer);
		ufer_links.setSize(width/9, height);
		ufer_rechts = new Sprite(ufer);
		ufer_rechts.setSize(width/9, height);
		ufer_rechts.flip(true, false);
		ufer_rechts.setOrigin(width - ufer_rechts.getWidth(), 0);
		
		
			
	
		//init geschwindigkeit
		geschwindigkeit = 1.0f;
		
		//init swimmer_position
		swimmer_position_swim = 4;
		
		//init score
		score = 0;
		level = 1;
		
		//init Hindernisgenerator
		difficulty[0] = 1;
		difficulty[1] = 1;
		difficulty[2] = 1;
		difficulty[3] = 2;
		first_probability[0] = 0.8;
		first_probability[1] = 0.8;
		first_probability[2] = 0.8;
		first_probability[3] = 0.8;
		for (int k=0;k<n_obstacles;k++){
		for (int i=0; i<obstacle_ausdauer;i++){
			double b = Math.log(first_probability[k]);
			double a = Math.log(first_probability[k]*100);
			obstacle_probability[k][i] = Math.exp((-1/obstacle_ausdauer)*a*i+b);
		}
		}
		generation_probability = 2;
		p[0]=0;
		for (int i=1; i<8;i++){
			p[i] = Math.exp(-generation_probability)*Math.pow(generation_probability,i-1)/fact(i-1);
		}
		
		// init Highscore
		highscore = new Highscore(font, "highscore.txt");
		highscore.load();
		
		//input
		paused = false;
		multiplexer = new InputMultiplexer();
		// erstelle menu
		menu = new Menu(multiplexer, this, highscore, font);
		menu.loadMainMenu();
		

		// erstelle und registriere Steuerung
		steuerung = new EventListener();
		steuerung.setGame(this);
		steuerung.setMenu(menu);
		multiplexer.addProcessor(steuerung);
		Gdx.input.setInputProcessor(multiplexer);

		// initialisiere Spielvariablen
		resetGameVariables();
	}

	@Override
	public void render() {
		
		// Spielgrafik rendern
		if (state == GameState.UPPERWORLD){
			if(!freeze){
				render_upperworld();
			}
			if(freeze){
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				update_variables_swim();
				// Graphik-Variablen updaten
				update_graphics();
				freeze = false;
			}
		}

		if (state == GameState.LOWERWORLD)
			render_lowerworld();

		// Spiellogik updaten
		if (!(paused || game_over)) {

			if (state == GameState.UPPERWORLD) {
				update_variables_swim();
				// Graphik-Variablen updaten
				update_graphics();
			} else if (state == GameState.LOWERWORLD) {
				update_variables_dive();
				// Graphik-Variablen updaten
				update_graphics();
			}

		} else {
			if (game_over) {
				render_gameover();
			}
		}
		
		menu.render();


	}

	// setzt alle Variablen für den Spielstart
	public void resetGameVariables() {
		geschwindigkeit = 1.0f;
		beschleunigung = 0.05f;

		swimmer_position_swim = 4;

		score = 0;
		level = 1;
		health = 5;

		paused = false;
		game_over = false;
		
		Arrays.fill(hindernis_aktiv, false);
		Arrays.fill(wand_punkte, 0);
	}

	// Methode um die Schwimmwelt zu rendern
	private void render_upperworld() {
		// Musik
		music.play();

		// Hintergrundfarbe
		Gdx.gl.glClearColor(0, 0.6f, 0.9f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();

		// Hintergrund
		batch.draw(wellen1, 0, wellen_y_pos % height, width, height);
		batch.draw(wellen2, 0, (wellen_y_pos % (height)) + height, width, height);
		batch.draw(ufer_links, 0, 0, width / 9, height);
		batch.draw(ufer_rechts, ufer_rechts.getOriginX(),
				ufer_rechts.getOriginY(), width / 9, height);

		//Animation Schwimmer
		batch.draw(swimmer_rechter_arm, (width-2*width/9) / 7 * (swimmer_position_swim-1) + swimmer_offset + width/9 + swimmer_width/5.0f + (swimmer_width/4.0f) - arm_pos_x*swimmer_width/70, (swimmer_width/6.0f - arm_pos_y*swimmer_width/80), swimmer_width/2, swimmer_width);
		batch.draw(swimmer_linker_arm, (width-2*width/9) / 7 * (swimmer_position_swim-1) + swimmer_offset + width/9 + swimmer_width/5.0f - (swimmer_width/4.0f) + arm_pos_x*swimmer_width/70, (swimmer_width/6.0f - arm_pos_y*swimmer_width/80), swimmer_width/2, swimmer_width);
		batch.draw(swimmer, (width - 2 * width / 9) / 7
				* (swimmer_position_swim - 1) + swimmer_offset + width / 9, 0,
				swimmer_width, swimmer_width);


		// Hindernisse
		for (int i = 0; i < 40; i++) {
			if (hindernis_aktiv[i]) {

				Obstacle aktiv = hindernis[i];
				int aktiv_type = aktiv.getType();
				switch (aktiv_type) {
				case 0:
					batch.draw(aktiv.getSprite(),
							(width / 9) * aktiv.getBahn(),
							height - aktiv.getY(), width / 9, width / 9);
					break;
				case 1:
					batch.draw(aktiv.getSprite(),
							(width / 9) * aktiv.getBahn(),
							height - aktiv.getY(), width / 9, width / 9);
					batch.draw(aktiv.getSpritesAnim()[0], (width / 9) * aktiv.getBahn() + width/34, height - aktiv.getY() + width / 30, width / 18, width /25 + (float)(5*(Math.sin(0.3*realtime))));
					batch.draw(aktiv.getSpritesAnim()[1], (width / 9) * aktiv.getBahn() + width/25, height - aktiv.getY() + width / 20 +(float)(2.5*(Math.sin(0.3*realtime))), width / 30, width/60);
					batch.draw(aktiv.getSpritesAnim()[2], (width / 9) * aktiv.getBahn() + width/25, height - aktiv.getY() + width / 28 -(float)(2.5*(Math.sin(0.3*realtime))), width / 30, width/60);
					break;
				case 2:
					batch.draw(aktiv.getSprite(),
							(width / 9) * aktiv.getBahn(),
							height - aktiv.getY(), width / 9, width / 9);
					batch.draw(aktiv.getSpritesAnim()[0],
							(width / 9) * aktiv.getBahn() + (width /17.5f)
									+ (realtime % 50*0.3f),
							height - (aktiv.getY()+width/500),
							width / 25, width / 18);
					break;
				case 3:
					batch.draw(aktiv.getSprite(),
							(width / 9) * aktiv.getBahn(),
							height - aktiv.getY(), width / 9, width / 9);
					break;
				default:
					batch.draw(aktiv.getSprite(),
							(width / 9) * aktiv.getBahn(),
							height - aktiv.getY(), width / 9, width / 9);
					break;
				}
			}
		}

		// Score-Anzeige
		font.setColor(Color.BLACK);
		font.draw(batch, "Score: " + score, 470, 465);

		// Level-Anzeigen
		if (score % 30 < 2) {
			gameover.draw(batch, "Level " + level, width / 2, height / 2);
		}


		
		// Herzen update
		if (health == 5) {
			batch.draw(herz_voll, 19, 440, width / 18, height / 18);
			batch.draw(herz_voll, 55, 440, width / 18, height / 18);
			batch.draw(herz_voll, 90, 440, width / 18, height / 18);
			batch.draw(herz_voll, 125, 440, width / 18, height / 18);
			batch.draw(herz_voll, 160, 440, width / 18, height / 18);

		} else if (health == 4) {
			batch.draw(herz_voll, 19, 440, width / 18, height / 18);
			batch.draw(herz_voll, 55, 440, width / 18, height / 18);
			batch.draw(herz_voll, 90, 440, width / 18, height / 18);
			batch.draw(herz_voll, 125, 440, width / 18, height / 18);
			batch.draw(herz_leer, 160, 440, width / 18, height / 18);

		} else if (health == 3) {
			batch.draw(herz_voll, 19, 440, width / 18, height / 18);
			batch.draw(herz_voll, 55, 440, width / 18, height / 18);
			batch.draw(herz_voll, 90, 440, width / 18, height / 18);
			batch.draw(herz_leer, 125, 440, width / 18, height / 18);
			batch.draw(herz_leer, 160, 440, width / 18, height / 18);

		} else if (health == 2) {
			batch.draw(herz_voll, 19, 440, width / 18, height / 18);
			batch.draw(herz_voll, 55, 440, width / 18, height / 18);
			batch.draw(herz_leer, 90, 440, width / 18, height / 18);
			batch.draw(herz_leer, 125, 440, width / 18, height / 18);
			batch.draw(herz_leer, 160, 440, width / 18, height / 18);

		} else if (health == 1) {
			batch.draw(herz_voll, 19, 440, width / 18, height / 18);
			batch.draw(herz_leer, 55, 440, width / 18, height / 18);
			batch.draw(herz_leer, 90, 440, width / 18, height / 18);
			batch.draw(herz_leer, 125, 440, width / 18, height / 18);
			batch.draw(herz_leer, 160, 440, width / 18, height / 18);
		}

		else if (health == 0) {
			batch.draw(herz_leer, 19, 440, width / 18, height / 18);
			batch.draw(herz_leer, 55, 440, width / 18, height / 18);
			batch.draw(herz_leer, 90, 440, width / 18, height / 18);
			batch.draw(herz_leer, 125, 440, width / 18, height / 18);
			batch.draw(herz_leer, 160, 440, width / 18, height / 18);
		}

		else {
			batch.draw(herz_leer, 19, 440, width / 18, height / 18);
			batch.draw(herz_leer, 55, 440, width / 18, height / 18);
			batch.draw(herz_leer, 90, 440, width / 18, height / 18);
			batch.draw(herz_leer, 125, 440, width / 18, height / 18);
			batch.draw(herz_leer, 160, 440, width / 18, height / 18);
			geschwindigkeit = 0;
		}

		batch.end();
	}

	// Methode um die Tauchwelt zu rendern
	private void render_lowerworld() {
		// Hintergrundfarbe
		Gdx.gl.glClearColor(0.6f, 0.6f, 0.9f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		world.step(Gdx.graphics.getDeltaTime(), 6, 2);
		tauchersprite.setPosition(body.getPosition().x, body.getPosition().y);
		
		if(body.getPosition().y < 0){
			body.setLinearVelocity(0, 0);
			body.setTransform(0, 0, 0);
		}
		
        batch.begin(); 	
        
        
        //Hintergrundanimation
  		batch.draw(hintergrund1, 0, -10 - unter_wasser_textur_pos, width, height - height/8);
  		batch.draw(hintergrund2, 0, -10 - 3*unter_wasser_textur_pos, width, height - height/8);
  		batch.draw(hintergrund3, 0, -10 - 7*unter_wasser_textur_pos, width, height - height/8);
  		batch.draw(hintergrund4, 0, -10 - 10*unter_wasser_textur_pos, width, height - height/8);
  		
        // Taucher
  		//Animation
        batch.draw(taucher_linkes_bein, tauchersprite.getX()-taucher_body_width/3 +width/10, tauchersprite.getY() + taucher_body_width/2 + 3.5f*(float) Math.sin(8*unter_wasser_textur_pos), taucher_body_width/2, taucher_body_width/4);
        batch.draw(taucher_rechtes_bein, tauchersprite.getX()-taucher_body_width/3 +width/10, tauchersprite.getY() + taucher_body_width/2.5f - 3.5f*(float) Math.sin(8*unter_wasser_textur_pos), taucher_body_width/2, taucher_body_width/4);             
        batch.draw(tauchersprite, tauchersprite.getX()+width/10, tauchersprite.getY(), taucher_body_width, taucher_body_width);
        
        //Luftblasen
        if(luftblasen_x_pos<(0-luftblasen.getWidth()) || luftblasen_y_pos>height) init_luftblasen();
        batch.draw(luftblasen, luftblasen_x_pos, luftblasen_y_pos, taucher_width/2, taucher_width);
        
        //Hindernisse
        //TODO: Typ von Unterwasserhindernis haengt von Typ des tauchbaren Hindernisses ab!
        for(int i = 0; i<20; i=i+2){
        	batch.draw(hindernis_lowerworld_lower.getSprite(), hindernis_lowerworld_lower.getX() + i/2*(width/8), hindernis_lowerworld_lower.getY() - wand_punkte[i],width/8, height);
        	batch.draw(hindernis_lowerworld_upper.getSprite(), hindernis_lowerworld_upper.getX() + i/2*(width/8), hindernis_lowerworld_upper.getY() + wand_punkte[i+1],width/8, height);

        }
        
        
        
		// Score-Anzeige
		font.setColor(Color.BLACK);
		font.draw(batch, "Score: " + score, 470, 465);
		
		// Herzen update
				if (health == 5) {
					batch.draw(herz_voll, 19, 440, width / 18, height / 18);
					batch.draw(herz_voll, 55, 440, width / 18, height / 18);
					batch.draw(herz_voll, 90, 440, width / 18, height / 18);
					batch.draw(herz_voll, 125, 440, width / 18, height / 18);
					batch.draw(herz_voll, 160, 440, width / 18, height / 18);

				} else if (health == 4) {
					batch.draw(herz_voll, 19, 440, width / 18, height / 18);
					batch.draw(herz_voll, 55, 440, width / 18, height / 18);
					batch.draw(herz_voll, 90, 440, width / 18, height / 18);
					batch.draw(herz_voll, 125, 440, width / 18, height / 18);
					batch.draw(herz_leer, 160, 440, width / 18, height / 18);

				} else if (health == 3) {
					batch.draw(herz_voll, 19, 440, width / 18, height / 18);
					batch.draw(herz_voll, 55, 440, width / 18, height / 18);
					batch.draw(herz_voll, 90, 440, width / 18, height / 18);
					batch.draw(herz_leer, 125, 440, width / 18, height / 18);
					batch.draw(herz_leer, 160, 440, width / 18, height / 18);

				} else if (health == 2) {
					batch.draw(herz_voll, 19, 440, width / 18, height / 18);
					batch.draw(herz_voll, 55, 440, width / 18, height / 18);
					batch.draw(herz_leer, 90, 440, width / 18, height / 18);
					batch.draw(herz_leer, 125, 440, width / 18, height / 18);
					batch.draw(herz_leer, 160, 440, width / 18, height / 18);

				} else if (health == 1) {
					batch.draw(herz_voll, 19, 440, width / 18, height / 18);
					batch.draw(herz_leer, 55, 440, width / 18, height / 18);
					batch.draw(herz_leer, 90, 440, width / 18, height / 18);
					batch.draw(herz_leer, 125, 440, width / 18, height / 18);
					batch.draw(herz_leer, 160, 440, width / 18, height / 18);
				}

				else if (health == 0) {
					batch.draw(herz_leer, 19, 440, width / 18, height / 18);
					batch.draw(herz_leer, 55, 440, width / 18, height / 18);
					batch.draw(herz_leer, 90, 440, width / 18, height / 18);
					batch.draw(herz_leer, 125, 440, width / 18, height / 18);
					batch.draw(herz_leer, 160, 440, width / 18, height / 18);
				}

				else {
					batch.draw(herz_leer, 19, 440, width / 18, height / 18);
					batch.draw(herz_leer, 55, 440, width / 18, height / 18);
					batch.draw(herz_leer, 90, 440, width / 18, height / 18);
					batch.draw(herz_leer, 125, 440, width / 18, height / 18);
					batch.draw(herz_leer, 160, 440, width / 18, height / 18);
					geschwindigkeit = 0;
				}
 

		//Luft-Anzeige
        
		/*width2 = width/2 + (loop*0.5f);
		if (width2 > 0){
			batch.draw(luftanzeige, 40, 40, width2, height/18);
			}
/*		else {setGameOver();
			music.stop();
		}*/
		
        batch.end();

	}
	
	//Helpermethods
	
	private void hindernis_Generator(){
		h = 0;
		//erste einfache Version des Hindernisgenerators
		//erstellt ein zufälliges Hindernis von Typ 1-3 auf einer zufälligen Bahn mit 50%iger Wahrscheinlichkeit
		
		/*if (Math.random()<0.5){
		int random_bahn = (int)(Math.random()*7+1);
		int random_hindernis = (int)(Math.random()*3);
		int i = 0;
		while (hindernis_aktiv[i]){
			i++;
		}
		hindernis[i] = init_obstacle(random_hindernis,random_bahn);
		hindernis_aktiv[i]=true;
		}*/
		
		//zweite Version des Hindernisgenerators
		//erstellt ein zufälliges Hindernis von Typ 0 bis n_obstacles-1 auf einer zufälligen Bahn
		//Auswahl des Typen des Hindernisses erfolgt über Exponentialverteilung
		//Auswahl der Anzahl Hindernisse in einer Zeile erfolgt über Poisson-Verteilung
	
		//Auswahl Anzahl Bahnen wo ein Hindernis generiert wird
		//sei p array mit Poissonverteilung bereits initialisiert
		//init p[0]=0;
		int[] counts = new int[]{6,21,35};
		int n=choice(p,7,1)-1;
		if (n==0){
			return;
		}
		//Auswahl Bahnen konkret
		//wird in array bahnen gespeichert
		int[] bahnen = new int[n];
		int[] bahnen_final = new int[n];
		int count = counts[(int)(-Math.abs(n-3.5)+3.5)-1];
		//m ist der Index der Liste aller Teilmengen der Mächtigkeit n von {1,..7}
		int m = (int)(Math.random()*count);
		bahnen = get_bahnen(m,(int)(-Math.abs(n-3.5)+3.5));
		//falls es 4,5,6 Bahnen sind, müssen die ausgewählten/nicht ausgewählten Bahnen invertiert werden
		if (n>3){
			int j = 0;
			for (int i=1;i<8;i++){
				boolean in_bahnen = false;
				for (int k=0;k<7-n;k++){
					if (bahnen[k]==i){
						in_bahnen = true;
					}
				}
				if (!in_bahnen){
					bahnen_final[j] = i;
					j++;
				}
			}
		}
		else{
			for (int i=0;i<n;i++){
				bahnen_final[i] = bahnen[i];
			}
		}
		//erzeuge Wahrscheinlichkeit-Verteilung zur Auswahl des Typen des Hindernisses
		double[] p_typ = new double[n_obstacles+1];
		p_typ[0] = 0;
		for (int i=1;i<n_obstacles+1;i++){
			if (level<difficulty[i-1]){
				p_typ[i] = 0;
			}
			else if (level>=difficulty[i-1]+obstacle_ausdauer){
				p_typ[i] = 0.01;
			}
			else {
				p_typ[i] = obstacle_probability[i-1][(int)(level)-difficulty[i-1]];
			}
		}
		double sum = 0;
		for (int i=1;i<n_obstacles+1;i++){
			sum += p_typ[i];
		}
		for (int i=1;i<n_obstacles+1;i++){
			p_typ[i] /= sum;
		}
		//iteriere i über jede ausgewählte Bahn
		for (int i=0;i<n;i++){			
			gen_obstacle(choice(p_typ,n_obstacles,1)-1,bahnen_final[i]);
		}
		for (int i=0;i<40;i++){
			//wenn ein Schwan generiert wurde, entferne alle anderen Hindernisse dieser Zeile
			if (hindernis_aktiv[i]&&(hindernis[i].getType()==3)&&(hindernis[i].getLine()==score)){
				for (int k=0;k<40;k++){
					if (hindernis_aktiv[k]&&(hindernis[k].getLine()==score)&&(i!=k)){
						hindernis_aktiv[k] = false;
					}
				}
			}
			//teste, ob es einen path für den swimmer gibt, falls nicht, lösche ausgewählte Hindernisse
			else if (hindernis_aktiv[i]&&(hindernis[i].getType()!=3)&&(hindernis[i].getLine()==score)){
				for (int k=0;k<40;k++){
					if(hindernis_aktiv[k]&&(hindernis[k].getLine()==score-1)&&((hindernis[i].getBahn()==hindernis[k].getBahn()+1)||(hindernis[i].getBahn()==hindernis[k].getBahn()-1))){
						hindernis_aktiv[i] = false;
					}
				}
			}
		}
	}
	
	//ein neu generiertes Hindernis erzeugen
	private void gen_obstacle(int type,int bahn){
		int i = 0;
		while (hindernis_aktiv[i]){
			i++;
		}
		if (i<40){
			hindernis[i] = init_obstacle(type,bahn);
			hindernis[i].setLine(score);
			hindernis_aktiv[i]=true;
		}
	}
	
	//Hilfsfunktion für den Hindernisgenerator
	private int[] get_bahnen(int m,int n){
		int help = -1;
		int[] a = new int[n];
		int[] b = new int[3];
		if (n==3){		
			for (b[0]=1;b[0]<8;b[0]++){
				for (b[1]=b[0]+1;b[1]<8;b[1]++){
					for (b[2]=b[1]+1;b[2]<8;b[2]++){
						help++;
						if (help==m){
							for (int i=0;i<n;i++){
								a[i] = b[2-i];
							}
							return a;
						}
					}
				}
			}
		}
		else if (n==2){
			for (b[0]=1;b[0]<8;b[0]++){
				for (b[1]=b[0]+1;b[1]<8;b[1]++){
						help++;
						if (help==m){
							for (int i=0;i<n;i++){
								a[i] = b[1-i];
							}
							return a;
						}
				}
			}
		}
		else {
			for (b[0]=1;b[0]<8;b[0]++){
						help++;
						if (help==m){
								a[0] = b[0];
							return a;
						}
			}
		}
		return a;
	}
	
	//choice wählt zufällig einen Index des arrays d, welches die W-Verteilung dieser Auswahl darstellt,
	//Wertebereich 1-L, gibt a aus, falls nichts ausgewählt wurde
	//init ar[0]=0;
	private int choice(double[] ar, int L, int a){
		double help=1;
		for (int i=1;i<L+1;i++){
			double r = Math.random();
			help*=1-ar[i-1];
			if (r<ar[i]/help){
				return i;
			}
		}
		return a;
	}
	
	//Fakultätsfunktion für die Poissonverteilung
	private int fact(int n){
        int fact = 1;
        for (int i=1;i<=n;i++){
            fact *= i;
        }
        return fact;
    }
	
	private void reset_obstacles(){
		for(int i=0;i<40;i++){
		    hindernis_aktiv[i]=false;
		}
	}

	public void render_gameover() {
		String gameoverstring = "GAME OVER";
		batch.begin();
		GlyphLayout gl = new GlyphLayout(gameover, gameoverstring);
		float left = (Gdx.graphics.getWidth() - gl.width) / 2;
		float bottom = Gdx.graphics.getHeight() - (gl.height + 10);
		gameover.draw(batch, gameoverstring, left, bottom);
		gameover.setColor(Color.WHITE);
		batch.end();
	}

	public GameState getState() {
		return state;
	}

	private void hindernis_Generator_dive_init(){
		//Die beiden ersten Hindernisse generieren
		
		float w0 = height * (float) Math.random();
		float w1 = height * (float) Math.random();

		while ((w1 - (hindernis_lowerworld_lower.getSprite().getHeight() - w0) < 5/4*taucher_width)){

			w0 = height* (float) Math.random();
			w1 = height* (float) Math.random();

		}
		
		wand_punkte[16] = w0;
		wand_punkte[17] = w1;
		
		while ((w1 - (hindernis_lowerworld_lower.getSprite().getHeight() - w0) < 5/4*taucher_width) || (hindernis_lowerworld_lower.getSprite().getHeight()- w0 +  5/4*taucher_width > wand_punkte[17]) || (hindernis_lowerworld_lower.getSprite().getHeight()-wand_punkte[16]+ 5/4*taucher_width > w1)) {

			w0 = height* (float) Math.random();
			w1 = height* (float) Math.random();

		}
		
		wand_punkte[18] = w0;
		wand_punkte[19] = w1;
		
		for(int i = 0; i < 16; i++){
			hindernis_Generator_dive();
		}
	}

	
	private void hindernis_Generator_dive() {


		for (int i = 0; i < 18; i++) {

			//if(wand_punkte[])
			wand_punkte[i] = wand_punkte[i + 2];

		}

		float w0 = height * (float) Math.random();
		float w1 = height * (float) Math.random();

		//Letzte beide generierte Hindernisse abfragen -> entsteht ein machbares Labyrinth?
		while ((w1 - (hindernis_lowerworld_lower.getSprite().getHeight() - w0) < 5/4*taucher_width) || (hindernis_lowerworld_lower.getSprite().getHeight()- w0 +  5/4*taucher_width > wand_punkte[17]) || (hindernis_lowerworld_lower.getSprite().getHeight()-wand_punkte[16]+ 5/4*taucher_width > w1) ||
				(hindernis_lowerworld_lower.getSprite().getHeight()- w0 +  5/4*taucher_width > wand_punkte[15]) || (hindernis_lowerworld_lower.getSprite().getHeight()-wand_punkte[14]+ 5/4*taucher_width > w1)) {

			w0 = height* (float) Math.random();
			w1 = height* (float) Math.random();

		}

		wand_punkte[18] = w0;
		wand_punkte[19] = w1;

	}

	public void startGame() {
		System.out.println("in startGame()");
		resetGameVariables();
		state = GameState.UPPERWORLD;
		menu.unloadMenu();
	}

	public void pauseGame(boolean p) {
		if (p && p != paused) {
			menu.loadPauseMenu();
		}
		else if(!p){
			menu.unloadMenu();
		}
		paused = p;
	}

	public void setGameOver() {
		game_over = true;
		music.stop();
		if(highscore.isHighscore(score)){
			menu.loadHighscoreInput(score);
		}
		else{
			menu.loadGameOverMenu();
		}
	}

	public boolean isPaused() {
		return paused;
	}
	
	public boolean isGameOver() {
		return game_over;
	}
	
	public void returnToMainMenu() {
		paused = false;
		menu.loadMainMenu();
		state = GameState.MAINMENU;
		Arrays.fill(hindernis_aktiv, false);
		Arrays.fill(wand_punkte, 0);
	}

	public void endApplication() {
		menu.unloadMenu();
		Gdx.app.exit();
	}

	public void changeDiveState() {

		if (state == GameState.UPPERWORLD) {
			Arrays.fill(wand_punkte, 0);
			state = GameState.LOWERWORLD;
			body.setLinearVelocity(0, 0);
			body.setTransform(0, 100, 0);

			//Unterwasser-Hindernis initialisieren
			//TODO: -> Dynamisch annpassen -> Obstacle ueber init_Obstacle_lowerworld-Methode erzeugen
			hindernis_lowerworld_low = new Sprite(felsen_unter_wasser);
			hindernis_lowerworld_up = new Sprite(felsen_unter_wasser);
			hindernis_lowerworld_up.flip(true, false);

			hindernis_lowerworld_lower  = new Obstacle(hindernis_lowerworld_low, 0, (float)2*width/3, 0.0f, 50);
			hindernis_lowerworld_upper  = new Obstacle(hindernis_lowerworld_up, 0, (float)2*width/3, 0.0f, 50);
			hindernis_lowerworld_upper.getSprite().flip(false, true);

			//Hindernis-Generator anwerfen
			hindernis_Generator_dive_init();
			
			// TODO Dispose einfügen
		} else {
			Arrays.fill(hindernis_aktiv, false);
			state = GameState.UPPERWORLD;
		}

	}

	protected void changeSwimmerPosition_swim(int change) {
		swimmer_position_swim += change;
		if (swimmer_position_swim < 1) {
			swimmer_position_swim = 1;
		}
		if (swimmer_position_swim > 7) {
			swimmer_position_swim = 7;
		}
	}

	protected void changeSwimmerPosition_dive(int change) {

		body.applyForceToCenter(0, 20000*change, true);

	}
	
	public boolean meetObstacle(Obstacle obs, Sprite swimmer){
		if(swimmer_position_swim == obs.getBahn()){
		if(width*8/9-obs.getY()<2.5*swimmer_width){
				return true;
			}
		}
		return false;
	}
	
	public boolean collision_dive(){
		
		if((body.getPosition().y + 0.25*taucher_width > wand_punkte[1]) && (body.getPosition().y + 0.25*taucher_width > wand_punkte[3])){
			
			return true;
			
		}
		
		if((body.getPosition().y + 0.75*taucher_width > wand_punkte[0]) && (body.getPosition().y + 0.75*taucher_width > wand_punkte[2])){
			
			return true;
			
		}
		
		/*if((body.getPosition().y + 0.25*taucher_width > wand_punkte[5]) && (body.getPosition().y + 0.75*taucher_width > wand_punkte[4])){
			
			return true;
			
		}*/
		
		return false;
	}

	private void update_graphics() {				
			
		if (state == GameState.UPPERWORLD) {
			wellen_y_pos = (wellen_y_pos - geschwindigkeit) % height;
			arm_pos += 10 % 314;
			arm_pos_x = swimmer_width/8*(float) Math.sin(0.01*arm_pos -1.5);
			arm_pos_y = swimmer_width/8*(float) Math.sin(0.01*arm_pos);
			
			
			// Update Hindernisse
			for (int i = 0; i < 40; i++) {
				if (hindernis_aktiv[i]) {

					Obstacle aktiv = hindernis[i];
					int aktiv_type = aktiv.getType();
					switch (aktiv_type) {
					case 0:
					case 1:
					case 2:
						aktiv.setY(aktiv.getY() + geschwindigkeit);
						break;
					case 3:
						// Bahn wechseln -> nach rechts oder nach links?
						if (/*aktiv.getY()*/realtime % schwan_speed == 0 && aktiv.getRichtung() == 1) {
							// Richtungswechsel
							if (aktiv.getBahn() == 7) {
								aktiv.setRichtung(2);
								Sprite temp = aktiv.getSprite();
								temp.flip(true, false);
								aktiv.setSprite(temp);
							} else
								aktiv.setBahn(aktiv.getBahn() + 1);
						} else if (/*aktiv.getY()*/realtime % schwan_speed == 0
								&& aktiv.getRichtung() == 2) {
							// Richtungswechsel
							if (aktiv.getBahn() == 1) {
								aktiv.setRichtung(1);
								Sprite temp = aktiv.getSprite();
								temp.flip(true, false);
								aktiv.setSprite(temp);
							} else
								aktiv.setBahn(aktiv.getBahn() - 1);
						}
						aktiv.setY(aktiv.getY() + geschwindigkeit);
						break;
					default:
						aktiv.setY(aktiv.getY() + geschwindigkeit);
						break;
					}
					//Hindernisse auf false setzen (= loeschen), wenn aus Fenster
					if(aktiv.getY() > aktiv.getSprite().getHeight() + height) hindernis_aktiv[i] = false;
				}
			}

		} else if (state == GameState.LOWERWORLD) {
			// Bewegung Hintergrundtextur
			unter_wasser_textur_pos = ((float) Math.sin((double) 0.05f
					* zeit_unter_wasser));
			zeit_unter_wasser = (zeit_unter_wasser + 1) % 200;
			taucher_width = width/9;
			taucher_body_width = width/12;

			//Luftblasen
			luftblasen_x_pos -= hindernis_geschwindigkeit;
			luftblasen_y_pos += (hindernis_geschwindigkeit/2 + Math.sin(0.2*luftblasen_x_pos));
			
			// Bewegung Hindernisse
			hindernis_lowerworld_upper.setX(hindernis_lowerworld_upper.getX()-hindernis_geschwindigkeit);
			hindernis_lowerworld_lower.setX(hindernis_lowerworld_lower.getX()-hindernis_geschwindigkeit);
			if(hindernis_lowerworld_lower.getX() < 0-width/8 && ((hindernis_lowerworld_upper.getX()*(-1))>width/8*hindernis_lowerworld_upper.getLaenge())) {
				hindernis_Generator_dive();
				hindernis_lowerworld_lower.setX(hindernis_lowerworld_lower.getX()+width/8);
				hindernis_lowerworld_upper.setX(hindernis_lowerworld_upper.getX()+width/8);
			}

			loop = (loop - hindernis_geschwindigkeit);
		}

	}

	private void readGraphics() {
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		ppiX = Gdx.graphics.getPpiX();
		ppiY = Gdx.graphics.getPpiY();

	}

	private void update_variables_swim() {
		//Hindernis-Generator-Aufruf
		if (h >= width / 9) {
			hindernis_Generator();
			score++;
			geschwindigkeit += beschleunigung;
			if (geschwindigkeit>max_speed){
				geschwindigkeit = max_speed;
			}
		}
		h += geschwindigkeit;
		realtime++;
		if (realtime==schwan_speed){
			realtime = 0;
		}
		//Andere Game-Variablen
		level = (score/30)+1;
		swimmer_offset = ((width-2) / 9) * 1/8;
		swimmer_width = ((width-2) / 9) * 3/4;
		width2 = luftanzeige.getHeight ();		

		// Kollisionsabfrage
		for (int i = 0; i < 40; i++) {
			if (hindernis_aktiv[i]) {
				if (meetObstacle(hindernis[i], swimmer)) {
					health--;
					shark.play ();
				    hindernis_aktiv[i]=false;
				    freeze = true;
				}
			}
		}		
		
		// GameOver check
		if (health <= 0) {
			setGameOver();

		}
	}

	private void update_variables_dive() {
		
		// TODO festlegen, ab wann der taucher wieder kollidieren kann
		
		invulnerable = false;
		
		tauchersprite.setPosition(body.getPosition().x, body.getPosition().y);

		if (body.getPosition().y > height-taucher_width/2) {
			changeDiveState();
		}
		if (body.getPosition().y < 0) {
			body.setLinearVelocity(0, 0);
			body.setTransform(0, 0, 0);
		}
		

		// Kollisionsabfrage
		
		if(invulnerable == false){
			
			if(collision_dive()){
				
				health--;
				freeze = true;
				invulnerable = true;
				
			}
		}
		

		//gameover check (luftanzeige)
		if(width2 <= 0){
			setGameOver();
		}

	}

	// init Klasse, um Obstacle-Objekte zu erzeugen
	private Obstacle init_obstacle(int type, int bahn) {
		Obstacle new_obstacle;
		
		switch(type){
			case 0: 
				Sprite felsen_sprite = new Sprite(hindernis_felsen);
				felsen_sprite.setSize(width/9, height/9);
				new_obstacle = new Obstacle(felsen_sprite, 0, bahn, 0.0f);
				break;
			case 1:
				Sprite seerosen_sprite = new Sprite(seerosen);
				seerosen_sprite.setSize(width/9, height/9);
				Sprite seerosen_mund_sprite = new Sprite(seerosen_mund);
				seerosen_mund_sprite.setOriginCenter();
				Sprite zaehne_oben = new Sprite(seerose_zaehne);
				Sprite zaehne_unten = new Sprite(seerose_zaehne);
				zaehne_unten.flip(false, true);
				Sprite[] sprites_anim = new Sprite[3];
				sprites_anim[0] = seerosen_mund_sprite;
				sprites_anim[1] = zaehne_oben;
				sprites_anim[2] = zaehne_unten;
				new_obstacle = new Obstacle(seerosen_sprite, 1, bahn, 0.0f, 3, sprites_anim);
				break;
			case 2:
				Sprite hai_sprite = new Sprite(hai_1);
				hai_sprite.setSize(width/9, height/9);
				Sprite haikinn = new Sprite(hai_2); 
				Sprite[] sprites_anim_2 = new Sprite[1];
				sprites_anim_2[0] = haikinn;
				new_obstacle = new Obstacle(hai_sprite, 2, bahn, 0.0f, 1, sprites_anim_2);
				break;
			case 3:
				Sprite schwan_sprite = new Sprite(rennschwan);
				schwan_sprite.setSize(width/9, height/9);
				new_obstacle = new Obstacle(schwan_sprite, 3, bahn, 0.0f);
				//Richtung auf links setzen
				new_obstacle.setRichtung(2);
				break;
			default: 
				Sprite default_sprite = new Sprite(hindernis_felsen);
				default_sprite.setSize(width/9, height/9);
				new_obstacle = new Obstacle(default_sprite, 0, bahn, 0.0f);
				break;
		}
		return new_obstacle;

	}
	
	private void init_luftblasen(){
		luftblasen_x_pos = tauchersprite.getX()+taucher_width+taucher_width/8;
		luftblasen_y_pos = tauchersprite.getY()+taucher_width;
	}

	@Override
	public void dispose() {
		music.dispose();
		batch.dispose();

	}

}