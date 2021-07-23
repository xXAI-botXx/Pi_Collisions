package Tobia.Ippolito.collision;

import java.awt.Color;
import java.awt.Graphics2D;

public class Wall implements PhysicalObject{

	private int width = 5, height = 275, x = 75, y = 0;
	private Color color = Color.gray;
	
	public Wall() {
		
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getX() {
		return x;
	}
	
	public void setColor(Color c) {
		color = c;
	}
	
	public void update() {		//wird eh nicht ausgeführt, da die Wall nicht bei Update[] geadded wird
		
	}
	
	public void render(Graphics2D g) {
		g.setColor(color);
		g.fillRect(x, y, width, height);
		
	}
}
