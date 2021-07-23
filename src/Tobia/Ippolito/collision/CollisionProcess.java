package Tobia.Ippolito.collision;

public class CollisionProcess implements Runnable{

	//Variablen
	private PhysicEngine engine;
	
	public CollisionProcess() {	//thread class
		
	}
	
	public void init(PhysicEngine pe) {	//braucht das Objekt PhysicEngine
		engine = pe;
	}
	
	public void run() {
		//Thread thread = Thread.currentThread();
		engine.start();
	}
}
