package self.frame;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;

class SnakePoint {
	public int x;
	public int y;
	public SnakePoint(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int hashCode() {
		return y * Snake.width + x;
	}
	
	public boolean equals(Object anObject) {
		if (anObject == null || !(anObject instanceof SnakePoint)) 
			return false;
		if (x == ((SnakePoint) anObject).x && y == ((SnakePoint) anObject).y)
			return true;
		return false;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append(x);
		sb.append(", ");
		sb.append(y);
		sb.append("]");
		return sb.toString();
	}
}

public class Snake extends JFrame {
	private static int initLen = 5;
	public static int width = 15;
	public static int height = 20;
	private int status;
	private JButton[] button;
	private SnakePoint food;
	private List<SnakePoint> snakePoint;
	private Random rand;
	private int period = 500;
	private long lastFoodTime;
	private int score;
	private int lastOperationTime;
	/**
	 * <ol>
	 * <li>up</li>
	 * <li>down</li>
	 * <li>left</li>
	 * <li>right</li>
	 * </ol>
	 */
	private int direction = 1;

	public Snake() { // 程序界面
		initWindow();
		setBounds(100, 100, width * 15 + 1, height * 15 + 38);
		display();
		addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					if (snakePoint.get(0).y - 1 != snakePoint.get(1).y) {
						direction = 1;
						move();
					}
					break;
				case KeyEvent.VK_DOWN:
					if (snakePoint.get(0).y + 1 != snakePoint.get(1).y) {
						direction = 2;
						move();
					}
					break;
				case KeyEvent.VK_LEFT:
					if (snakePoint.get(0).x - 1 != snakePoint.get(1).x) {
						direction = 3;
						move();
					}
					break;
				case KeyEvent.VK_RIGHT:
					if (snakePoint.get(0).x + 1 != snakePoint.get(1).x) {
						direction = 4;
						move();
					}
				}
			}
		});
		while (status == 0) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			lastOperationTime++;
			if (lastOperationTime >= period)
				move();
		}
	}
	
	private void initWindow() {
		setVisible(true);
		setTitle("Score: 0");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		snakePoint = new LinkedList<SnakePoint>();
		button = new JButton[width * height];
		setLayout(new GridLayout(height, width));
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				button[i * width + j] = new JButton();
				add(button[i * width + j]);
			}
		}
		for (int i = 0; i < initLen; i++) {
			int j = width / 2;
			snakePoint.add(new SnakePoint(j, (height * 3 / 4 - initLen) / 2 + height / 4 + i));
		}
		newFood();
		lastFoodTime = System.nanoTime();
	}
	
	private void move() {
		if (status != 0)
			return;
		SnakePoint firstPoint = snakePoint.get(0);
		SnakePoint nextPoint;
		switch (direction) {
		case 1:
			nextPoint = new SnakePoint(firstPoint.x, firstPoint.y - 1);
			break;
		case 2:
			nextPoint = new SnakePoint(firstPoint.x, firstPoint.y + 1);
			break;
		case 3:
			nextPoint = new SnakePoint(firstPoint.x - 1, firstPoint.y);
			break;
		case 4:
			nextPoint = new SnakePoint(firstPoint.x + 1, firstPoint.y);
			break;
		default:
			throw new RuntimeException(String.valueOf(direction));
		}
		int lastFoodPeriod = (int) ((System.nanoTime() - lastFoodTime) / 1000000 / period);
		if (!nextPoint.equals(food)) {
			snakePoint.remove(snakePoint.size() - 1);
		} else {
			do {
				newFood();
			} while(food.equals(nextPoint));
			score += 20 * Math.pow(0.5, (lastFoodPeriod / 20.0));
			period = (int) (1000 * Math.pow(0.5, Math.log10(score)));
			if (period > 500)
				period = 500;
			lastFoodTime = System.nanoTime();
			setTitle("Score: " + score);
		}
		if (nextPoint.x < 0 || nextPoint.x >= width || nextPoint.y < 0 || nextPoint.y >= height || snakePoint.contains(nextPoint))
			status = 1;
		else
			snakePoint.add(0, nextPoint);
		display();
		lastOperationTime = 0;
		if (status != 0) {
			if (!snakePoint.contains(nextPoint)){
				if (nextPoint.x < 0)
					nextPoint.x = 0;
				if (nextPoint.x >= width)
					nextPoint.x = width - 1;
				if (nextPoint.y < 0)
					nextPoint.y = 0;
				if (nextPoint.y >= height)
					nextPoint.y = height - 1;
			}
			button[nextPoint.hashCode()].setBackground(Color.RED);
		}
	}
	
	private void newFood() {
		if (rand == null) {
			synchronized (this) {
				if (rand == null)
					rand = new Random();
			}
		}
		do {
			food = new SnakePoint(rand.nextInt(width), rand.nextInt(height));
		} while (snakePoint.contains(food));
	}
	
	private void display() {
		for (int i = 0; i < button.length; i++)
			button[i].setBackground(null);
		for (int i = 0; i < snakePoint.size(); i++)
			button[snakePoint.get(i).hashCode()].setBackground(Color.BLACK);
		button[food.hashCode()].setBackground(Color.BLUE);
	}
	
	public static void main(String[] args) {
		new Snake();
	}
}
