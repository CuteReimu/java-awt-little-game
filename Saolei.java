package self.frame;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;

public class Saolei extends JFrame {
	private int width = 16;
	private int height = 16;
	private int bombNum = 40;

	private JButton[] button;
	private int[] visible;
	private int[] value;
	private boolean isEnd = false;
	private boolean isBegin = false;
	private long nanoTime;
	
	public Saolei(int w, int h, int bombNum) {
		if (w * h < bombNum)
			throw new RuntimeException(w * h + "个格子，" + bombNum + "个雷");
		width = w;
		height = h;
		this.bombNum = bombNum;
		initWindow();
		pack();
		initData();
		setBounds(100, 100, width * 30, height * 30);
	}

	public Saolei() { // 程序界面
		initWindow();
		pack();
		initData();
		setBounds(100, 100, width * 30, height * 30);
	}

	private void initData() {
		visible = new int[width * height];
		value = new int[width * height];
		Random rand = new Random();
		for (int k = 0; k < bombNum;) {
			int loc = rand.nextInt(width * height);
			if (value[loc] != 9) {
				value[loc] = 9;
				k++;
				int i = loc / width;
				int j = loc % width;
				for (int ii = i - 1; ii <= i + 1; ii++)
					for (int jj = j - 1; jj <= j + 1; jj++)
						if (ii >= 0 && jj >= 0 && ii < height && jj < width && getValue(ii, jj) != 9)
							value[ii * width + jj]++;
			}
		}
	}
	
	private void display() {
		int plus1count = 0;
		int minus1count = 0;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (getVisible(i, j) == 1)
					plus1count++;
				if (getVisible(i, j) == -1)
					minus1count++;
			}
		}
		setTitle((bombNum - minus1count) + " bombs left, "
				+ ((int)Math.floor((System.nanoTime() - nanoTime) / 1e9) + 1)
				+ " seconds passed");
		if (plus1count == height * width - bombNum) {
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					if (getValue(i, j) == 9)
					setVisible(i, j, -1);
				}
			}
			isEnd = true;
			isBegin = false;
		}
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (getVisible(i, j) == 1) {
					button[i * width + j].setBackground(Color.gray);
					switch (getValue(i, j)) {
					case 9:
						button[i * width + j].setText("☆");
						break;
					case 0:
						break;
					default:
						button[i * width + j].setText(String.valueOf(getValue(i, j)));
					}
				} else if (getVisible(i, j) == 0) {
					button[i * width + j].setText(null);
					button[i * width + j].setBackground(null);
				} else {
					button[i * width + j].setText("☆");
					button[i * width + j].setBackground(null);
				}
			}
		}
	}

	private void onClick(int i, int j) {
		if (!isBegin) {
			isBegin = true;
			nanoTime = System.nanoTime();
			new Thread(new Runnable(){
				public void run() {
					while(!isEnd) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						int minus1count = 0;
						for (int i = 0; i < height; i++) {
							for (int j = 0; j < width; j++) {
								if (getVisible(i, j) == -1)
									minus1count++;
							}
						}
						setTitle((bombNum - minus1count) + " bombs left, "
								+ ((int)Math.floor((System.nanoTime() - nanoTime) / 1e9) + 1)
								+ " seconds passed");
					}
				}
			}).start();
		}
		if (getVisible(i, j) == 1){
			int minus1count = 0;
			for (int ii = i - 1; ii <= i + 1; ii++) {
				for (int jj = j - 1; jj <= j + 1; jj++) {
					if (ii >= 0 && ii < height && jj >= 0 && jj < width) {
						if (getVisible(ii, jj) == -1)
							minus1count++;
					}
				}
			}
			if (minus1count == getValue(i, j)) {
				for (int ii = i - 1; ii <= i + 1; ii++) {
					for (int jj = j - 1; jj <= j + 1; jj++) {
						if (ii >= 0 && ii < height && jj >= 0 && jj < width) {
							if (getVisible(ii, jj) == 0)
								onClick(ii, jj);
						}
					}
				}
			}
		} else if (getVisible(i, j) == 0) {
			if (getValue(i, j) == 9) {
				for (int ii = 0; ii < height; ii++) {
					for (int jj = 0; jj < width; jj++) {
						if (getValue(ii, jj) == 9)
							setVisible(ii, jj, 1);
					}
				}
				isEnd = true;
				isBegin = false;
			} else if (getValue(i, j) == 0) {
				setVisible(i, j, 1);
				if (i > 0) {
					if (j > 0)
						onClick(i - 1, j - 1);
					onClick(i - 1, j);
					if (j < width - 1)
						onClick(i - 1, j + 1);
				}
				if (j > 0)
					onClick(i, j - 1);
				onClick(i, j);
				if (j < width - 1)
					onClick(i, j + 1);
				if (i < height - 1) {
					if (j > 0)
						onClick(i + 1, j - 1);
					onClick(i + 1, j);
					if (j < width - 1)
						onClick(i + 1, j + 1);
				}
			} else {
				setVisible(i, j, 1);
			}
		}
	}

	private void initWindow() {
		setVisible(true);
		setTitle("sweeping bomb");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridLayout(height, width));
		button = new JButton[width * height];
		for (int i = 0; i < height; i++) {
			final int ii = i;
			for (int j = 0; j < width; j++) {
				final int jj = j;
				button[i * width + j] = new JButton();
				button[i * width + j].setMargin(new Insets(0, 0, 0, 0));
				getContentPane().add(button[i * width + j]);
				button[i * width + j].addMouseListener(new MouseListener() {
					public void mouseReleased(MouseEvent e) {}
					public void mousePressed(MouseEvent e) {}
					public void mouseExited(MouseEvent e) {}
					public void mouseEntered(MouseEvent e) {}
					@Override
					public void mouseClicked(MouseEvent e) {
						if (Saolei.this.isEnd && e.getButton() == MouseEvent.BUTTON1) {
							Saolei.this.isEnd = false;
							initData();
							setTitle("sweeping bomb");
						} else {
							switch (e.getButton()) {
							case MouseEvent.BUTTON1:
								Saolei.this.onClick(ii, jj);
								Saolei.this.display();
								break;
							case MouseEvent.BUTTON3:
								if (getVisible(ii, jj) == 0)
									setVisible(ii, jj, -1);
								else if (getVisible(ii, jj) == -1)
									setVisible(ii, jj, 0);
							}
						}
						display();
					}
				});
			}
		}
	}

	public static void main(String[] args) {
		new Saolei();
	}

	private int getValue(int i, int j) {
		return value[i * width + j];
	}

	private void setVisible(int i, int j, int visible) {
		this.visible[i * width + j] = visible;
	}

	private int getVisible(int i, int j) {
		return visible[i * width + j];
	}
}
