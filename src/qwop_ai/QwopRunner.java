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

	QLearning brain = new QLearning();

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

		game.click(); //init game, get focus

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
		int failures = 0;
		double last_distance = 0;

		Robot robot = new Robot();

		while (failures < 10) {
			String decision = brain.getDecision(last_distance);
			//System.out.println(decision);
			//char[] keys = decision.toCharArray();
			char[] keys = controls.get(i).toCharArray();

			for (char key : keys){
				robot.keyPress(getKeyEvent(key));
			}

			File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			try{
				FileUtils.copyFile(screenshot, new File("screenshot.png"));
				BufferedImage game_img = ImageParser.getBufferedImage(screenshot);
				ImageParser.getDistanceSubImage(game_img);
				BufferedImage distance_read = ImageParser.getBufferedImage(new File("clipped.png"));

				double new_distance = ImageParser.readDistance(distance_read, ImageParser.findMReference(distance_read));

				//brain.recordOutcome(last_distance, last_distance-new_distance, decision);

				last_distance = new_distance;

				for (char key : keys){
					robot.keyRelease(getKeyEvent(key));
				}

				if(ImageParser.gameOver(game_img)){ //restart
					System.out.println("GAME OVER");
					sendKeys(game, "r");
					milliSleep(50);
					failures++;
				}

			} catch (Exception e){
				e.printStackTrace();
			}

			i++;
			i %= 8;
		}
		brain.saveMatrixFile();
		quitDriver();
	}

	private int getKeyEvent(char key){
		switch (key) {
			case 'q': return(VK_Q);
			case 'w': return(VK_W);
			case 'o': return(VK_O);
			case 'p': return(VK_P);
			case 'r': return(VK_R);
			default: return(VK_F); // does nothing for game
		}
	}
}