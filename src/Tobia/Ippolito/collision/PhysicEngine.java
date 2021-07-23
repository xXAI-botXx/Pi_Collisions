package Tobia.Ippolito.collision;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import javax.swing.*;


public class PhysicEngine {
	//Variablen 
	private final int width = 800, height = 600;	
	private boolean run = false;
	private ArrayList<PhysicalObject> updating = new ArrayList<PhysicalObject>();
	private ArrayList<PhysicalObject> rendering = new ArrayList<PhysicalObject>();
	
	//Swing-Komponenten
	private JFrame window;
	private Canvas renderer;		//ich zeichne mit Canvas
	private JButton start;
	private JLabel label_start_warning;
	private JButton btnBreak;
	private JButton btnRefresh;
	private JTextField weightR2;
	private JLabel weightLabelR2;
	private JTextField vR2;
	private JLabel vLabelR2;
	private JSlider vR2Slider;
	private JSlider weightR2Slider;
	
	private JTextField weightR1;
	private JLabel weightLabelR1;
	private JTextField vR1;
	private JLabel vLabelR1;
	private JSlider vR1Slider;
	private JSlider weightR1Slider;
	
	private RectangleObject r1;
	private RectangleObject r2;
	private Wall bottomW;
	private Wall leftW;
	private boolean firstStart = true;
	private boolean inputMode = true;
	
	private double lastTime = 900;
	private int limit = 3500; 	
	
	private String x2 = "x2: ";
	private String x1 = "x1: ";
	private String v2 = "v2: ";
	private String v1 = "v1: ";
	private Font vFont = new Font("Arial", Font.BOLD, 15);
	private String collisionC = "collisions: ";
	private Font collisionFont = new Font("Arial", Font.BOLD, 20);
	private Font collisionFont2 = new Font("Arial", Font.BOLD, 60);
	private Font collisionFont2_2 = new Font("Arial", Font.PLAIN, 20);
	
	private MusicLoader click0;
	private MusicLoader click1;
	private MusicLoader click2;
	private MusicLoader click3;
	private MusicLoader click4;
	private String songName0 = "13119__looppool__bell-blip";
	private String songName1 = "2174__spexis__cloche2-spexis";
	private String songName2 = "254239__kwahmah-02__y";
	private String songName3 = "448080__breviceps__wet-click";
	private String songName4 = "113634__edgardedition__click4";
	
	private int sound = 0;
	
	private boolean velocity = false;
	private boolean x = false;
	private boolean collision = false;
	private boolean normalMode = true;
	private boolean soundOn = true;
	private boolean abbruch = false;
	
	private Thread thread1;
	private CollisionProcess cp;
	
	//Variablen für den Update-Loop
	private final int TICKS_PER_SECOND = 60;	//Update =60 Bilder pro Sekunde, legt das
	private final int TIME_PER_TICK = 1000/TICKS_PER_SECOND;
	
	private long nextGameTick = System.currentTimeMillis();	//grundlegende Zeit 
	private int loops;	//zählt wie oft es geupdatet hat und es soll nicht über MAX_FRAMESKIPS gehen, da das Game sonst nicht mehr Korrekt läuft (man ist zahlen technisch woanders, als das man sieht wo man ist), aber trotzdem soll es öfters Updaten als Rendern, da das Spiel so genauer und somit auch flüssiger läuft. Man kann MAX_FRAMESKIPS ändern und herausfinden, wo es am flüssigigsten läuft ohne grafische fehlanzeigen zu machen(die Problematik habe ich gerade eben beschrieben).
	
	//für die Darstellung der fps
	private long timeAtLastFPSCheck = 0;
	private int ticks = 0;	//jeder tick ein Update		//ticks und loops scheinen gleich zu sein, aber loops wird bei jedem durchgang auf 0 gesetzt und wird dafür verwendet zu überprüfen wie oft das Game geupdatet hat (darf nicht zu oft, sonst stimmt das gezeichnete nicht mehr, (da updaten schneller funktioniert))
	
	//Konstruktor
	public PhysicEngine(RectangleObject r1, RectangleObject r2, Wall bottomW, Wall leftW, CollisionProcess cp) {
		this.r1 = r1;
		this.r2 = r2;
		this.bottomW = bottomW;
		this.leftW = leftW;
		this.cp = cp;
	}
	
	//Methoden
	public void addUpdating(PhysicalObject phyObj) {
		updating.add(phyObj);
	}
	
	public void addRendering(PhysicalObject phyObj) {
		rendering.add(phyObj);
	}
	
	
	public void init() {
		click0 = new MusicLoader();
		click0.loadPackage(songName0);
		click1 = new MusicLoader();
		click1.loadPackage(songName1);
		click2 = new MusicLoader();
		click2.loadPackage(songName2);
		click3 = new MusicLoader();
		click3.loadPackage(songName3);
		click4 = new MusicLoader();
		click4.loadPackage(songName4);
		
		
		EventQueue.invokeLater(() -> {		//regelt die Zugriffe
			//Fenster und Canvas wird realisiert
			window = new JFrame("Pi Collision");
			
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window.setResizable(false);
			window.setSize(width, height);
			
			window.setLocationRelativeTo(null);
			window.setVisible(true);
	
		
			//Container für die komponenten
			Container contentPane = window.getContentPane();
			
			
			//renderer
			renderer = new Canvas();
			renderer.setSize(width, height);	
			renderer.setLocation(0, 0);
			
			
			
			start = new JButton(new ImageIcon("src/images/start-png-44882.png")); 
			start.setSize(100, 100);	
			start.setLocation(600, 250);	//75
			start.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(!run) {	//nur wenn die while-Schleife aktuell nicht läuft
						window.getJMenuBar().updateUI();
						
						lastTime = System.currentTimeMillis();
					
						run = true;
						thread1 = new Thread(cp);
						thread1.start();
					
						firstStart = false;
					}else {
						warning_show();
					}
				}
			});
			
			label_start_warning = new JLabel("Warte bis der aktuelle Durchlauf zuende ist!");	
			label_start_warning.setSize(250, 50);
			label_start_warning.setLocation(520, 375);	
			label_start_warning.setVisible(false);
			
			btnBreak = new JButton("Break"); 
			btnBreak.setSize(100, 30);	
			btnBreak.setLocation(600, 350);	//75
			btnBreak.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(run) abbruch = true;
				}
			});
			
			btnRefresh = new JButton("Refresh the Menubar");
			btnRefresh.setSize(150,  35);	
			btnRefresh.setLocation(575, 30);
			btnRefresh.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					window.getJMenuBar().updateUI();
				}
			});
			btnRefresh.addMouseListener(new MouseListener() {

					@Override
					public void mouseClicked(MouseEvent arg0) {}
				
					@Override
					public void mouseEntered(MouseEvent arg0) {
						btnRefresh.setBackground(new Color(123, 210, 110));
					}
				
					@Override
					public void mouseExited(MouseEvent arg0) {
						btnRefresh.setBackground(new Color(123, 230, 98));
					}
				
					@Override
					public void mousePressed(MouseEvent arg0) {}
				
					@Override
					public void mouseReleased(MouseEvent arg0) {}
				});
				
			btnRefresh.setBorder(null);
			btnRefresh.setBackground(new Color(123, 230, 98));
			btnRefresh.setFocusable(false);
			
			
			//Konfiguration von Komponenten zum Rechteck 2 (links)
			weightR2 = new JTextField(String.valueOf(r2.getM()));	
			weightR2.setSize(100, 35);
			weightR2.setLocation(110, 400);	
			
			weightLabelR2 = new JLabel("weight: ");
			weightLabelR2.setSize(50, 35);
			weightLabelR2.setLocation(50, 400);	
			
			vR2 = new JTextField(String.valueOf(r2.getV()));
			vR2.setSize(100,  35);
			vR2.setLocation(110, 450);
			
			vLabelR2 = new JLabel("velocity: ");
			vLabelR2.setSize(50, 35);
			vLabelR2.setLocation(50, 450);	
			
			vR2Slider = new JSlider(-10000, 10000);
			vR2Slider.setSize(100,  35);
			vR2Slider.setLocation(110, 450);
			vR2Slider.setVisible(false);
			vR2Slider.createStandardLabels(500);
			vR2Slider.setPaintLabels(true);
			vR2Slider.setValue(0);
		
			weightR2Slider = new JSlider(0, 10000);
			weightR2Slider.setSize(100, 35);
			weightR2Slider.setLocation(110, 400);
			weightR2Slider.setVisible(false);
			weightR2Slider.createStandardLabels(500);
			weightR2Slider.setPaintLabels(true);
			weightR2Slider.setValue(1);
			
			
			//Konfiguration von Komponenten zum Rechteck 1 (rechts)
			weightR1 = new JTextField(String.valueOf(r1.getM()));	
			weightR1.setSize(100, 35);
			weightR1.setLocation(360, 400);	
			
			
			weightLabelR1 = new JLabel("weight: ");
			weightLabelR1.setSize(50, 35);
			weightLabelR1.setLocation(300, 400);	
			
			vR1 = new JTextField(String.valueOf(r1.getV()));
			vR1.setSize(100,  35);
			vR1.setLocation(360, 450);	
			
			vLabelR1 = new JLabel("velocity: ");
			vLabelR1.setSize(50, 35);
			vLabelR1.setLocation(300, 450);	
			
			vR1Slider = new JSlider(-10000, 10000);
			vR1Slider.setSize(100,  35);
			vR1Slider.setLocation(360, 450);
			vR1Slider.setVisible(false);
			vR1Slider.createStandardLabels(100);
			vR1Slider.setPaintLabels(true);
			vR1Slider.setValue(-80);
			
			weightR1Slider = new JSlider(0, 100000);
			weightR1Slider.setSize(100, 35);
			weightR1Slider.setLocation(360, 400);
			weightR1Slider.setVisible(false);
			weightR1Slider.createStandardLabels(500);
			weightR1Slider.setPaintLabels(true);
			weightR1Slider.setValue(100);
			
		
			//Menü-Bar-Init
			//Menü: JmenuBar -> Jmenu -> JMenuItem
			
			JMenuBar jMenuBar = new JMenuBar();		//das ist die Menü-Leiste
			
			//Punkte der Menü-Leiste
			
			
			//erster Menüpunkt-Pictures für das linke Objekte
			JMenu p2Menu = new JMenu("Picture-Object 2");
			jMenuBar.add(p2Menu);
			
			//Item 1
			JMenuItem pic1R2= new JMenuItem("Version 1", new ImageIcon("src/Images/ctPic1.png"));
			pic1R2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					r2.setPic(0);
				}
			});
			p2Menu.add(pic1R2);
			
			//Item 2
			JMenuItem pic2R2= new JMenuItem("Version 2", new ImageIcon("src/Images/ctPic3.png"));
			pic2R2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					r2.setPic(1);
				}
			});
			p2Menu.add(pic2R2);
			
			//Item 3
			JMenuItem pic3R2= new JMenuItem("Version 3", new ImageIcon("src/Images/ctPic4.png"));
			pic3R2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					r2.setPic(2);
				}
			});
			p2Menu.add(pic3R2);
			
			//zweiter Menüpunkt-Pictures für das rechte Objekte
			JMenu p1Menu = new JMenu("Picture-Object 1");
			jMenuBar.add(p1Menu);
			
			//item 1
			JMenuItem pic1R1 = new JMenuItem("Version 1", new ImageIcon("src/Images/ctPic2.png"));	//+bild(ImageIcon)
			pic1R1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					r1.setPic(0);
				}
			});
			p1Menu.add(pic1R1);	//adden	
			
			//Item 2
			JMenuItem pic2R1 = new JMenuItem("Version 2", new ImageIcon("src/Images/ctPic3.png"));	
			pic2R1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					r1.setPic(1);
				}
			});
			p1Menu.add(pic2R1);
			
			//Item 3
			JMenuItem pic3R1 = new JMenuItem("Version 3", new ImageIcon("src/Images/ctPic4.png"));	
			pic3R1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					r1.setPic(2);
				}
			});
			p1Menu.add(pic3R1);
			
			
			
			//dritter Menüpunkt-Sound
			JMenu soundMenu = new JMenu("Sounds");
			jMenuBar.add(soundMenu);
			
			//Item 1
			JMenuItem sound1 = new JMenuItem("Sound 1");
			sound1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sound = 0;
				}
			});
			soundMenu.add(sound1);
			
			//Item 2
			JMenuItem sound2 = new JMenuItem("Sound 2");
			sound2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sound = 1;
				}
			});
			soundMenu.add(sound2);
			
			//Item 3
			JMenuItem sound3 = new JMenuItem("Sound 3");
			sound3.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sound = 2;
				}
			});
			soundMenu.add(sound3);
			
			//Item 4
			JMenuItem sound4 = new JMenuItem("Sound 4");
			sound4.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sound = 3;
				}
			});
			soundMenu.add(sound4);
			
			//Item 5
			JMenuItem sound5 = new JMenuItem("Sound 5");
			sound5.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sound = 4;
				}
			});
			soundMenu.add(sound5);
			
			//item 6
			JMenuItem soundOff = new JMenuItem("Sound Off/On");
			soundOff.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(soundOn) {
						soundOn = false;
					}else {
						soundOn = true;
					}
				}
			});
			soundMenu.add(soundOff);
			
			
			//4 Punkt - Anzeigen an/aus
			JMenu values = new JMenu("Apperance");	
			jMenuBar.add(values);
			
			//JRadioButtonMenuItem 1
			JRadioButtonMenuItem velocityJR = new JRadioButtonMenuItem("velocity");
			velocityJR.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(velocity) {
						velocity = false;
					}else {
						velocity = true;
					}
				}
			});
			values.add(velocityJR);
			
			//JRadioButtonMenuItem 2
			JRadioButtonMenuItem xJR = new JRadioButtonMenuItem("x-values");
			xJR.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(x) {
						x = false;
					}else {
						x = true;
					}
				}
			});
			values.add(xJR);
			
			//JRadioButtonMenuItem 3
			JRadioButtonMenuItem collisionJR = new JRadioButtonMenuItem("collision");
			collisionJR.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(collision) {
						collision = false;
					}else {
						collision = true;
					}
				}
			});
			values.add(collisionJR);
			
			
			//JRadioButtonMenuItem 4
			JMenuItem arrow = new JMenuItem("arrows");
			arrow.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					r1.setArrowOn();
					r2.setArrowOn();
				}
			});
			values.add(arrow);
			
			
			//JRadioButtonMenuItem 5
			JMenuItem all = new JMenuItem("all");
			all.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(velocity && collision && x && r1.getArrow() && r2.getArrow()) {
						velocity = false;
						x = false;
						collision = false;
						r1.setArrowFalse();
						r2.setArrowFalse();
					}else {
						velocity = true;
						x = true;
						collision = true;
						r1.setArrowTrue();
						r2.setArrowTrue();
					}
				}
			});
			values.add(all);
			
			//SubMenu und eine Abtrennung
			JSeparator sep = new JSeparator();
			values.add(sep);
			//Neues Menu -> Submenu -> für die regulierung der Werte
			 JMenu submenu=new JMenu("Values-Regulation");
			 
			 JMenuItem regulation1 = new JMenuItem("Slider");
			 regulation1.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						weightR2Slider.setVisible(true);
						vR2Slider.setVisible(true);
						weightR1Slider.setVisible(true);
						vR1Slider.setVisible(true);
						
						vR2.setVisible(false);
						weightR2.setVisible(false);
						weightR1.setVisible(false);
						vR1.setVisible(false);
						
						inputMode = false;
					}
			 });
			 submenu.add(regulation1);
			 
			 
			 JMenuItem regulation2 = new JMenuItem("Input");
			 regulation2.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						weightR2Slider.setVisible(false);
						vR2Slider.setVisible(false);
						weightR1Slider.setVisible(false);
						vR1Slider.setVisible(false);
						
						vR2.setVisible(true);
						weightR2.setVisible(true);
						weightR1.setVisible(true);
						vR1.setVisible(true);
						
						inputMode = true;
					}
			 });
			 submenu.add(regulation2);
			 //das neue Menu wird hinzugefügt
			 values.add(submenu);
			
			
			
			//5er Punkt - das komplette Design soll sich hier ändern (die Walls sollen sich ändern und auch die hintergrundfarbe)
			/*JMenu design = new JMenu("Design");
			jMenuBar.add(design);
	
			//design 1
			JMenuItem design1 = new JMenuItem("Design 1");
			design1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					renderer.setBackground(Color.red);
					bottomW.setColor(Color.black);
					leftW.setColor(Color.black);
				}
			});
			design.add(design1);
			
			//design 2
			JMenuItem design2 = new JMenuItem("Design 2");
			design2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					renderer.setBackground(Color.blue);
					bottomW.setColor(Color.black);
					leftW.setColor(Color.black);
				}
			});
			design.add(design2);*/
			
			
			//6 Menüpunkt-Pictures für das linke Objekte
			JMenu modeMenu = new JMenu("modes");
			jMenuBar.add(modeMenu);
			
			//Mode 1
			JMenuItem mode1 = new JMenuItem("normal");
			//mode1.setSelected(true);
			mode1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(!normalMode & run != true) {
						normalMode = true;
					}
				}
			});
			modeMenu.add(mode1);
			
			//Mode 2
			JMenuItem mode2 = new JMenuItem("dont draw");
			mode2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(normalMode  & run != true) {
						normalMode = false;
						collision = true;
						soundOn = false;	//to loud -> makes no sense
					}
				}
			});
			modeMenu.add(mode2);
			
			
			window.setJMenuBar(jMenuBar);	//komplette MenuBar adden
			//Menü Ende
			
			//Komponeten hinzufügen (dem Container des Fensters)       
			contentPane.add(vLabelR1);
			contentPane.add(vR1);
			contentPane.add(vR1Slider);
			contentPane.add(vLabelR2);
			contentPane.add(vR2);
			contentPane.add(vR2Slider);
			contentPane.add(weightLabelR2);
			contentPane.add(weightR2);
			contentPane.add(weightR2Slider);
			contentPane.add(weightLabelR1);
			contentPane.add(weightR1);
			contentPane.add(weightR1Slider);
			contentPane.add(start);
			contentPane.add(label_start_warning);
			contentPane.add(btnBreak);
			contentPane.add(btnRefresh);
			contentPane.add(renderer);
		});	
	}
	
	public void setInstances() {
		if(inputMode) {
			r2.setM(weightR2.getText());
			r2.setV(vR2.getText());
			
			r1.setM(weightR1.getText());
			r1.setV(vR1.getText());
		}else {
			r2.setM(weightR2Slider.getValue());
			r2.setV(vR2Slider.getValue());
			
			r1.setM(weightR1Slider.getValue());
			r1.setV(vR1Slider.getValue());
		}
		//wird nur für den update-loop benötigt
		nextGameTick = System.currentTimeMillis();
	}
	
	private void warning_show() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
				label_start_warning.setVisible(true);
				Thread.sleep(5000);
				label_start_warning.setVisible(false);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}
	
	
	public void fps() {	
		if(System.currentTimeMillis() - timeAtLastFPSCheck >= 1000 && normalMode) {	//jede Sekunde: falls der letzte FPScheck länger als 1 Sekunde her ist, dann:			//System.currentTimeMillis() - LastFPSCheck = die Zeit von dem letzten FPSCheck bis jetzt
			//System.out.println("Ticks: "+ticks+"\nLoops: "+loops);
			window.setTitle("Pi Collision - FPS: "+ticks);
			ticks = 0;
			timeAtLastFPSCheck = System.currentTimeMillis();
		}else if(!normalMode) {
			window.setTitle("Pi Collision");
		}
	}
	
	public void timeCheck() {
		if(System.currentTimeMillis()-lastTime > limit) {
			run = false;
			if(normalMode == false) {
				drawEnd();
			}
		}
	}
	
	public void soundPlay() {
		if(soundOn && normalMode) {
			switch(sound) {
				case 0: click0.play();
				break;
				case 1: click1.play();
				break;
				case 2: click2.play();
				break;
				case 3: click3.play();
				break;
				case 4: click4.play();
				break;
			}
		}
	}
	
	private double think_positiv(double input) {
		if(input < 0) {
			return input = input * -1;
		}else {
			return input;
		}
	}
	
	
	public void start() {		
		r1.reset();		
		r2.reset();
			
		setInstances();	
		
		while(run) {	//hier kommen wir nicht mehr raus, das darf nicht sein. Arbeite mit System.timeMillis und schau wann die letzte Kollision war und mach es davon abhängig
			
			if(normalMode) {	
				loops = 0;
					
				while(System.currentTimeMillis() > nextGameTick /*|| r2.getX() <= 100 || r1.getX() <= 150*/) {	//wenn das Update länger als erwartet dauert, soll weiterhin geupdatet werden, bis die Anzahl an akzeptierbaren Rundenanzahlen errreicht ist
					update();
					ticks++;	//ticks werden nur jede Sekunde zurückgesetzt, also gibt der Wert an wie oft man in einer Sekunde geupdatet hat
					nextGameTick += TIME_PER_TICK;	//nextGameTick zeigt nun wann das nächste Update sein sollte, verglichen mit der aktuellen zeit. Falls das Game nun länger für das GameUpdate brauchen würde, updatet es weiterhin. Sonst würde das Game in echt leggen. 
					loops++;	//wie oft man in einem Gameloop durchgang geupdatet hat
				}
				render();
			}else {		//andere Modus (ohne Zeichnen)
				loops = 0;
				/*double dimension_velocity = think_positiv(r1.getV() + r2.getV());
				double dimension_weight = think_positiv(r1.getM() - r2.getM());
				double dimension = dimension_velocity + dimension_weight;
				System.out.println(dimension);*/
				
				while(loops < 10000) {	//10000 //wenn das Update länger als erwartet dauert, soll weiterhin geupdatet werden, bis die Anzahl an akzeptierbaren Rundenanzahlen errreicht ist
					update();
					if(run != true) break;
					ticks++;	//ticks werden nur jede Sekunde zurückgesetzt, also gibt der Wert an wie oft man in einer Sekunde geupdatet hat
					loops++;	//wie oft man in einem Gameloop durchgang geupdatet hat
				}
				if(run != true) break;
				renderMode2();	//hier ird das zeichnen runtergestufft, jedoch könnt man auch eine start2() Methode schreiben! vlt sogar sinnvoll, wenn ich update auch ändere
				}
			fps(); //mit dieser Methode stelle ich die FPS dar
			
			//falls man den Abbruch-button drückt
			if(abbruch) {
				abbruch = false;
				run = false;
				
				//render
				BufferStrategy preRender = renderer.getBufferStrategy();	//mein oben initialisiertes Canvas besitzt einen BufferStartegy, nun nutzen ich diesen um das nächste Bild schoneinmal vor zu laden, dadurch gibt es weniger Grafik Fehler und die Grafik wird dadurch auch weicher
				if(preRender == null) {	//falls unser Canvas keine Buffer hat, sollen zwei Stück erstellt werden
					renderer.createBufferStrategy(2);
					return;
				}
				
				Graphics2D g = (Graphics2D) preRender.getDrawGraphics();
				g.clearRect(0, 0, renderer.getWidth(), renderer.getHeight());	//löscht alle Grafiken/Gezeichnetes in dem Rechteck(im kompletten Screen). Aber davon sieht der Benutzer nichts. Und zwar wegen der BufferStartegy. Der Buffer zeigt einem nur relevante Dinge. Und das alles gelöscht wurde ist in diesem Fall nicht relevant. Man sieht also erst wieder, wenn alles neu gezeichnet wurde.
				
				g.setFont(collisionFont);
				g.drawString("Der Durchgang wurde abgebrochen!", 100, 200);
				
				g.dispose();
				preRender.show();
				
				break;
			}
		}
	}
	
	public void update() {	
		for(PhysicalObject i:updating) {
				i.update();
		}
		//Kollision-Prüfung
		if( r2.getX() <= leftW.getX()+5) {	
			soundPlay();
			lastTime = System.currentTimeMillis();	//Zeit von der letzten Kollision
			r2.setV(-(r2.getV()));
			r2.collisionCounter();
		} else if(r2.getX()+50 >= r1.getX()) {		
			soundPlay();
			lastTime = System.currentTimeMillis();	//Zeit von der letzten Kollision
			r2.calculate(r1);
			r2.collisionCounter();	
		}
		
		x2 = "x2: "+String.valueOf((int) r2.getX());
		x1 = "x1: "+String.valueOf((int) r1.getX());
		v2 = "v2: "+String.valueOf((int)r2.getV());
		v1 = "v1: "+String.valueOf((int)r1.getV());
		collisionC = "collisons: "+String.valueOf(r2.getCollision());
		
		if(r2.getCollision() > 0) {
			timeCheck();
		}
	}
	
	public void render() {
		BufferStrategy preRender = renderer.getBufferStrategy();	//mein oben initialisiertes Canvas besitzt einen BufferStartegy, nun nutzen ich diesen um das nächste Bild schoneinmal vor zu laden, dadurch gibt es weniger Grafik Fehler und die Grafik wird dadurch auch weicher
		if(preRender == null) {	//falls unser Canvas keine Buffer hat, sollen zwei Stück erstellt werden
			renderer.createBufferStrategy(2);
			return;
		}
		
		Graphics2D g = (Graphics2D) preRender.getDrawGraphics();
		g.clearRect(0, 0, renderer.getWidth(), renderer.getHeight());	//löscht alle Grafiken/Gezeichnetes in dem Rechteck(im kompletten Screen). Aber davon sieht der Benutzer nichts. Und zwar wegen der BufferStartegy. Der Buffer zeigt einem nur relevante Dinge. Und das alles gelöscht wurde ist in diesem Fall nicht relevant. Man sieht also erst wieder, wenn alles neu gezeichnet wurde.
		
		g.setFont(collisionFont);
		if(collision) {
			g.drawString(collisionC, 400, 150);	
		}
		g.setFont(vFont);
		
		if(velocity) {
			g.drawString(v2, 150, 200);	
			g.drawString(v1, 250, 200);	
		}
		if(x) {
			g.drawString(x2, 150, 225);	
			g.drawString(x1, 250, 225);	
		}
		
		
		for(PhysicalObject i:rendering) {
			i.render(g);
		}
		g.dispose();
		preRender.show();
	}
	
	public void renderMode2() {//oder in update
		BufferStrategy preRender = renderer.getBufferStrategy();	//mein oben initialisiertes Canvas besitzt einen BufferStartegy, nun nutzen ich diesen um das nächste Bild schoneinmal vor zu laden, dadurch gibt es weniger Grafik Fehler und die Grafik wird dadurch auch weicher
		if(preRender == null) {	//falls unser Canvas keine Buffer hat, sollen zwei Stück erstellt werden
			renderer.createBufferStrategy(2);
			return;
		}
		
		Graphics2D g = (Graphics2D) preRender.getDrawGraphics();
		g.clearRect(0, 0, renderer.getWidth(), renderer.getHeight());	//löscht alle Grafiken/Gezeichnetes in dem Rechteck(im kompletten Screen). Aber davon sieht der Benutzer nichts. Und zwar wegen der BufferStartegy. Der Buffer zeigt einem nur relevante Dinge. Und das alles gelöscht wurde ist in diesem Fall nicht relevant. Man sieht also erst wieder, wenn alles neu gezeichnet wurde.
		
		g.setFont(collisionFont2);
		g.drawString(collisionC, 100, 200);	
		
		g.setFont(collisionFont2_2);
		g.drawString("Berechnungen laufen...", 120, 235);	
		
		
		g.dispose();
		preRender.show();
	}
	
	public void drawEnd() {//oder in update
		BufferStrategy preRender = renderer.getBufferStrategy();	//mein oben initialisiertes Canvas besitzt einen BufferStartegy, nun nutzen ich diesen um das nächste Bild schoneinmal vor zu laden, dadurch gibt es weniger Grafik Fehler und die Grafik wird dadurch auch weicher
		if(preRender == null) {	//falls unser Canvas keine Buffer hat, sollen zwei Stück erstellt werden
			renderer.createBufferStrategy(2);
			return;
		}
		
		Graphics2D g = (Graphics2D) preRender.getDrawGraphics();
		g.clearRect(0, 0, renderer.getWidth(), renderer.getHeight());	//löscht alle Grafiken/Gezeichnetes in dem Rechteck(im kompletten Screen). Aber davon sieht der Benutzer nichts. Und zwar wegen der BufferStartegy. Der Buffer zeigt einem nur relevante Dinge. Und das alles gelöscht wurde ist in diesem Fall nicht relevant. Man sieht also erst wieder, wenn alles neu gezeichnet wurde.
		
		g.setFont(collisionFont2);
		g.drawString(collisionC, 100, 200);	
		
		g.setFont(collisionFont2_2);
		g.drawString("Ergebnis:", 90, 140);	
		
		
		g.dispose();
		preRender.show();
	}
	
}
