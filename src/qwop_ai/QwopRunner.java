package qwop_ai;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import static java.awt.event.KeyEvent.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.interactions.Actions;
import selenium.utils.Automato;

public class QwopRunner extends Automato {
	
	public static void main(String[] args) {
		QwopRunner forest = new QwopRunner();

		forest.driver.manage().window().setSize(new Dimension(1024, 720));
		try{
			forest.run();
		} catch (AWTException e){
			e.printStackTrace();
		}
	}

	public QwopRunner() {
		super("https://www.foddy.net/athletics.swf", "chrome");
	}
	
	public void run() throws AWTException{

		refresh(); // game doesn't load first time ?

		milliSleep(1000); // wait for refresh

		WebElement game = getWebElement("id=plugin"); // get the game frame

		game.click();

		ArrayList<String> controls = new ArrayList<String>();
		controls.add("q");
		controls.add("w");
		controls.add("o");
		controls.add("p");

		controls.add("qo");
		controls.add("qp");
		controls.add("wo");
		controls.add("wp");

		controls.add("r");

		int i = 0;

		Robot robot = new Robot();

		while (i < 1) {
			char[] keys = controls.get(i % 8).toCharArray();

			long millis = System.currentTimeMillis();

			for (char key : keys){
				robot.keyPress(getKeyEvent(key));
			}

			File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			try{
				FileUtils.copyFile(screenshot, new File("./screenshot.png"));
				ImageParser.getDistanceSubImage(ImageParser.getBufferedImage(screenshot));
				BufferedImage distance_read = ImageParser.getBufferedImage(new File("clipped.png"));

				ImageParser.readDistance(distance_read, ImageParser.findMReference(distance_read));

			} catch (Exception e){
				e.printStackTrace();
			}

			while (System.currentTimeMillis() < millis + 100){}

			for (char key : keys){
				robot.keyRelease(getKeyEvent(key));
			}
			i++;
		}

		quitDriver();
	}

	private int getKeyEvent(char key){
		switch (key) {
			case 'q': return(VK_Q);
			case 'w': return(VK_W);
			case 'o': return(VK_O);
			case 'p': return(VK_P);
			case 'r': return(VK_P);
			default: return(VK_F); // does nothing for game
		}
	}
}