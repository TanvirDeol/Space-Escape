package application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.image.*;
import javafx.scene.transform.Rotate;
/**
 * Class for a spaceship object
 * @author Tanvir
 *
 */
public class SpaceShip {
	long passengers;
	long capacity;
	double speed;
	double x;
	double y;
	FileInputStream imgDir;
	Image spriteFile;
	ImageView sprite;
	/**
	 * Constructor for a spaceship. 
	 * It gets the image used for the sprite and initializes other fields.
	 * @param pas - No of Passengers
	 * @param cap - Spaceship capacity
	 * @throws FileNotFoundException
	 */
	public SpaceShip(long pas, long cap) throws FileNotFoundException {
		imgDir = new FileInputStream(Main.directory+"\\rocket.png");
		spriteFile = new Image(imgDir);
		sprite = new ImageView(spriteFile);
		Rotate rot = new Rotate();
        rot.setPivotX(sprite.getFitWidth()/2);
        rot.setPivotY(sprite.getFitHeight()/2);
        sprite.getTransforms().add(rot);
        this.passengers = pas;
        this.capacity = cap;
	}
}
