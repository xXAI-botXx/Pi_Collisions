package Images;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Sprite {
	
	public static BufferedImage getSprite(String fileName){
		try {
			return ImageIO.read(Sprite.class.getResourceAsStream(fileName));	//Dateipfad relativ zu der Klasse Sprite, damit ist es einfacher/Kombatibler
	
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
