package qwop_ai;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import static java.awt.event.KeyEvent.*;

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
		forest.run();
	}

	public QwopRunner() {
		super("https://www.foddy.net/athletics.swf", "chrome");
	}
	
	public void run() {

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

		File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try{
			FileUtils.copyFile(screenshot, new File("./screenshot.png"));
            ImageParser.getDistanceSubImage(ImageParser.getBufferedImage(screenshot));
		} catch (Exception e){
			e.printStackTrace();
		}

		int i = 0;

		while (true) {
			try{
                milliSleep(10);
				executeControl(controls.get(i), 10); // a bit shaky.....
			} catch (AWTException e){
				System.out.println("AWT EXCEPT");
			} finally {
				i++;
				i %= 4;
			}
		}
	}

	private void executeControl(String controls, Integer duration) throws AWTException {
		char [] keys = controls.toCharArray();

		Robot robot = new Robot();

		for (char key : keys){
			robot.keyPress(getKeyEvent(key));
		}
		milliSleep(duration);
		for (char key : keys){
			robot.keyRelease(getKeyEvent(key));
		}

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