package Tobia.Ippolito.collision;

import java.io.File;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class MusicLoader{
	
	private File sound;
	private Clip clip;
	
	public MusicLoader() {
		
	}

	public void loadPackage(String songName) {
		sound = new File("src/Sound/"+songName+".wav");
	}
	
	public void play() {	
		try {
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(sound));
			clip.start();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}	
}
