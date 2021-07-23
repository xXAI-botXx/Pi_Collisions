package Tobia.Ippolito.collision;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import Images.Sprite;


public class RectangleObject implements PhysicalObject{
	
	private double x = 400, y = 275, xBegin = x;	
	private int width = 50, height = 50;
	private int grenzwertR = 100;	
	
	private long collisionCount = 0;
	private double m = 1.0;
	private double v = -80.0;
	private double v2;
	
	private boolean arrowOn = false;
	private Color color = Color.BLACK;
	
	private int pic = 0;
	private BufferedImage arrowL, arrowR;
	
	public RectangleObject() {
		try {	//Bilder laden
			arrowL = Sprite.getSprite("left-arrow.png");
			arrowR = Sprite.getSprite("right-arrow.png");
			}catch(Exception e) {
				System.err.println(e.getMessage());
				System.exit(1);
			}
	}
	
	public long getCollision() {
		return collisionCount;
	}
	
	public void setM(double m) {
		this.m = m;
	}
	
	public double getM() {
		return m;
	}
	
	public void setM(String m) {
		this.m = Double.parseDouble(m);
	}
	
	public void setV(double v) {	//für den Start
		this.v = v;
	}
	
	public void setV(String v) {
		this.v = Double.parseDouble(v);
	}
	
	public void setGrenzwertR(int r) {
		grenzwertR = r;
	}
	
	public double getV() {
		return v;
	}
	
	public void setArrowOn() {
		if(arrowOn == true) {
			arrowOn = false;
		}else {
			arrowOn = true;
		}
	}
	
	public void setArrowTrue() {
		arrowOn = true;
	}
	
	public void setArrowFalse() {
		arrowOn = false;
	}
	
	public boolean getArrow() {
		return arrowOn;
	}

	public void setX(float x) {
		this.x = x;
		xBegin = x;
	}
	
	public double getX() {
		return x;
	}
	
	public float getLength() {
		return width;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public void setPic(int i) {
		pic = i;
	}
	
	public Color getColor(int i) {
		switch(i) {
		case 0:	return color;
			
		case 1:	return Color.BLACK;
			
		case 2:	return Color.MAGENTA;
			
		case 3:
			
		}
		return color;	//falls ( unnötig)
	}
	
	public void reset() {
		x = xBegin;
		collisionCount = 0;
	}
	
	public void collisionCounter() {
		collisionCount++;
	}
	
	public void calculate(RectangleObject r1) {//vlt erst wall-collision überprüfen
		v2 = 2*( ((m * v)+(r1.getV()*r1.getM())) / (r1.getM() + m)) -v;  //2*(((m*v)+(m*v))/(m+m))
		r1.setV(2* ((m * v)+(r1.getV()*r1.getM())) / (m + r1.getM()) -r1.getV());	//für das andere Objekt
		v = v2;	
	}
	
	public void update() {
		x = x + (v/100);
	}
	
	public void render(Graphics2D g) {
		if(x >= grenzwertR) {	//Normale Grafik
			switch(pic) {
			case 0:	g.setColor(color);
					g.fillRect((int)x, (int)y, width, height);
					break;
			case 1:	g.setColor(Color.BLACK);
					g.fillRect((int)x, (int)y, width, height);
					break;
			case 2:	g.setColor(Color.MAGENTA);
					g.fillRect((int)x, (int)y, width, height);
					break;
			}
			
			if(arrowOn) {
				if(v < 0) {
					g.drawImage(arrowL,(int) x,(int) y, null);
				}else if(v > 0) {
					g.drawImage(arrowR, (int) x, (int) y, null);
				}
			}
		}else {		//Grafik-Manipulation
			switch(pic) {
			case 0:	g.setColor(color);
					g.fillRect((int)grenzwertR, (int)y, width, height);
					break;
			case 1:	g.setColor(Color.BLACK);
					g.fillRect((int)grenzwertR, (int)y, width, height);
					break;
			case 2:	g.setColor(Color.MAGENTA);
					g.fillRect((int)grenzwertR, (int)y, width, height);
					break;
			}
			
			if(arrowOn) {
				if(v < 0) {
					g.drawImage(arrowL,(int) grenzwertR,(int) y, null);
				}else if(v > 0) {
					g.drawImage(arrowR, (int) grenzwertR, (int) y, null);
				}
			}
		}
	}
}
