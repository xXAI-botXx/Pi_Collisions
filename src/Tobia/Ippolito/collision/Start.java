package Tobia.Ippolito.collision;

import java.awt.Color;

public class Start {

	public static void main(String[] bvsus) {
		
		//2 Objekte
		RectangleObject r1 = new RectangleObject();
		RectangleObject r2 = new RectangleObject();

		r2.setX(200);
		r2.setV("0");
		r2.setColor(Color.CYAN);
		r1.setColor(Color.BLUE);
		r1.setM(100);
		r1.setGrenzwertR(150);
		
		//zwei Mauern
		Wall leftW = new Wall();
		leftW.setY(75);	//25		
		leftW.setX(95);	
		Wall bottomW = new Wall();
		bottomW.setHeight(5);
		bottomW.setWidth(300);
		bottomW.setY(325);	//275
		
		//Thread-Klasse, braucht das Objekt von PhysicEngine
		CollisionProcess cp = new CollisionProcess(); //damit es als Thread laufen kann
		
		//physicEngine
		PhysicEngine engine = new PhysicEngine(r1, r2, bottomW, leftW, cp);
		engine.addRendering(r1);
		engine.addUpdating(r1);
		engine.addRendering(r2);
		engine.addUpdating(r2);
		engine.addRendering(leftW);
		engine.addRendering(bottomW);
		
		cp.init(engine);	//PhyscEngine wird hinzugefügt
		
		engine.init();
	}
	
}
