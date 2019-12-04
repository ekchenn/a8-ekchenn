package a8;

import java.awt.*;

import java.awt.event.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.util.*;

import java.awt.event.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.util.*;

public class GameOfLife extends JFrame implements ActionListener {
	private static final Dimension DEFAULT_WINDOW_SIZE = new Dimension(5000, 5000);
	private static final Dimension MINIMUM_WINDOW_SIZE = new Dimension(100, 100);
	private static final int BLOCK_SIZE = 10;

	private JMenuBar menu;
	private JMenu file, gameMenu;
	private JMenuItem fileOptions, fileExit, fileRules;
	private JMenuItem gameAutofill, gamePlay, gameStop, gameReset, gameSize, gameTorus;
	private int iMovesPerSecond = 3;
	private GameBoard gameBoard;
	private Thread game;
	private int lowBirth = 2;
	private int highBirth = 3;
	private int lowSurvive = 3;
	private int highSurvive = 3;
	private int s;
	private boolean torus = false;

	public static void main(String[] args) {
		// Set up the swing specifics
		JFrame game = new GameOfLife();
		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.setTitle("Conway's Game of Life");
		game.setMaximumSize(DEFAULT_WINDOW_SIZE);
		game.setMinimumSize(MINIMUM_WINDOW_SIZE);
		game.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {
				Dimension size = game.getSize();
				Dimension min = game.getMinimumSize();
				if (size.getWidth() < min.getWidth()) {
					game.setSize((int) min.getWidth(), (int) size.getHeight());
				}
				if (size.getHeight() < min.getHeight()) {
					game.setSize((int) size.getWidth(), (int) min.getHeight());
				}
			}
		});
		game.setSize(DEFAULT_WINDOW_SIZE);

		game.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - game.getWidth()),
				(Toolkit.getDefaultToolkit().getScreenSize().height - game.getHeight()));
		game.setVisible(true);
	}

	public GameOfLife() {
		// Set up menu bar + drop downs
		menu = new JMenuBar();
		setJMenuBar(menu);
		file = new JMenu("File");
		menu.add(file);
		gameMenu = new JMenu("Game");
		menu.add(gameMenu);
		fileOptions = new JMenuItem("Options");
		fileOptions.addActionListener(this);
		fileRules = new JMenuItem("Rules");
		fileRules.addActionListener(this);
		fileExit = new JMenuItem("Exit");
		fileExit.addActionListener(this);
		file.add(fileOptions);
		file.add(fileRules);
		file.add(new JSeparator());
		file.add(fileExit);
		gameSize = new JMenuItem("Size");
		gameSize.addActionListener(this);
		gameTorus = new JMenuItem("Torus");
		gameTorus.addActionListener(this);
		gameAutofill = new JMenuItem("Autofill");
		gameAutofill.addActionListener(this);
		gamePlay = new JMenuItem("Play");
		gamePlay.addActionListener(this);
		gameStop = new JMenuItem("Stop");
		gameStop.setEnabled(false);
		gameStop.addActionListener(this);
		gameReset = new JMenuItem("Reset");
		gameReset.addActionListener(this);
		gameMenu.add(gameAutofill);
		gameMenu.add(gameSize);
		gameMenu.add(gameTorus);
		gameMenu.add(new JSeparator());
		gameMenu.add(gamePlay);
		gameMenu.add(gameStop);
		gameMenu.add(gameReset);
		
		
		// Set up game board
		gameBoard = new GameBoard();
		add(gameBoard);
	}

	public void setGameBeingPlayed(boolean isBeingPlayed) {
		if (isBeingPlayed) {
			gamePlay.setEnabled(false);
			gameStop.setEnabled(true);
			game = new Thread(gameBoard);
			game.start();
		} else {
			gamePlay.setEnabled(true);
			gameStop.setEnabled(false);
			game.interrupt();
		}
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource().equals(fileExit)) {
			// Exit the game
			System.exit(0);
		} else if (ae.getSource().equals(fileOptions)) {
			// Put up an options panel to change the number of moves per second
			final JFrame Options = new JFrame();
			Options.setTitle("Options");
			Options.setSize(300, 60);
			Options.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - Options.getWidth()) / 2,
					(Toolkit.getDefaultToolkit().getScreenSize().height - Options.getHeight()) / 2);
			Options.setResizable(false);
			JPanel pOptions = new JPanel();
			pOptions.setOpaque(false);
			Options.add(pOptions);
			pOptions.add(new JLabel("Number of moves per second:"));
			Integer[] secondOptions = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
					23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47,
					48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72,
					73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97,
					98, 99, 100 };
			final JComboBox cbSeconds = new JComboBox(secondOptions);
			pOptions.add(cbSeconds);
			cbSeconds.setSelectedItem(iMovesPerSecond);
			cbSeconds.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					iMovesPerSecond = (Integer) cbSeconds.getSelectedItem();
					Options.dispose();
				}
			});
			Options.setVisible(true);
		} else if (ae.getSource().equals(gameSize)) {
			JFrame Size = new JFrame();
			Size.setTitle("Size");
			Size.setSize(300, 60);
			Size.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - Size.getWidth()) / 2,
					(Toolkit.getDefaultToolkit().getScreenSize().height - Size.getHeight()) / 2);
			Size.setResizable(false);
			Size.setResizable(false);
			JPanel pSize = new JPanel();
			JSlider slider = new JSlider(10, 500);
			slider.setMinorTickSpacing(10);
			slider.setMajorTickSpacing(100);
			gameBoard = new GameBoard();
			s = slider.getValue();
			slider.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent arg0) {
					gameBoard.setSize(slider.getValue(), slider.getValue());
				}
			});
			pSize.add(slider);
			pSize.setOpaque(false);
			Size.add(pSize);
			Size.setVisible(true);
		} else if (ae.getSource().equals(fileRules)) {
			JFrame Rules = new JFrame();
			Rules.setTitle("Rules");
			Rules.setSize(300, 60);
			Rules.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - Rules.getWidth()) / 2,
					(Toolkit.getDefaultToolkit().getScreenSize().height - Rules.getHeight()) / 2);
			Rules.setResizable(false);
			JPanel pRules = new JPanel();
			pRules.setOpaque(false);
			Rules.add(pRules);
			pRules.add(new JLabel("Low Birth Threshold:"));
			JTextField textField1 = new JTextField(10);
			pRules.add(textField1);
			// low_birth = Integer.parseInt(textField1.getText());
			pRules.add(new JLabel("High Birth Threshold:"));
			JTextField textField2 = new JTextField(10);
			pRules.add(textField2);
			// high_birth = Integer.parseInt(textField2.getText());
			pRules.add(new JLabel("Low Survive Threshold:"));
			JTextField textField3 = new JTextField(10);
			pRules.add(textField3);
			// low_survive = Integer.parseInt(textField3.getText());
			pRules.add(new JLabel("High Survive Threshold:"));
			JButton apply = new JButton(String.valueOf("Apply"));
			pRules.add(apply);
			apply.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					lowBirth = Integer.parseInt(textField1.getText());
					highBirth = Integer.parseInt(textField2.getText());
					lowSurvive = Integer.parseInt(textField3.getText());
					lowSurvive = Integer.parseInt(textField3.getText());
					Rules.dispose();
				}
			});
			Object[] options = { apply };
			JTextField textField = new JTextField(10);
			pRules.add(textField);
			// high_survive = Integer.parseInt(textField.getText());

			int result = JOptionPane.showOptionDialog(null, pRules, "Thresholds:", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, options, null);

			if (result == JOptionPane.YES_OPTION) {
				JOptionPane.showMessageDialog(null, textField.getText());
			}
			Rules.dispose();

		} else if (ae.getSource().equals(gameAutofill)) {
			int max = 100;
			int min = 1;
			gameBoard.resetBoard();
			gameBoard.randomlyFillBoard((int) (Math.random() * ((max - min)) + min));
		} else if (ae.getSource().equals(gameReset)) {
			gameBoard.resetBoard();
			gameBoard.repaint();
		} else if (ae.getSource().equals(gamePlay)) {
			setGameBeingPlayed(true);
		} else if (ae.getSource().equals(gameStop)) {
			setGameBeingPlayed(false);
		} else if (ae.getSource().equals(gameTorus)) {
			if (gameTorus.getBackground().equals(Color.BLACK)) {
				gameTorus.setBackground(Color.WHITE);
				gameTorus.setForeground(Color.BLACK);
				torus = true;
			} else {
				gameTorus.setBackground(Color.BLACK);
				gameTorus.setForeground(Color.WHITE);
			}
		}

	}

	private class GameBoard extends JPanel implements ComponentListener, MouseListener, MouseMotionListener, Runnable {
		private Dimension GameBoardSize = null;
		private ArrayList<Point> point = new ArrayList<Point>(0);

		public GameBoard() {
			// Add resizing listener
			addComponentListener(this);
			addMouseListener(this);
			addMouseMotionListener(this);
		}

		private void updateArraySize() {
			ArrayList<Point> removeList = new ArrayList<Point>(0);
			for (Point current : point) {
				if ((current.x > GameBoardSize.width - 1) || (current.y > GameBoardSize.height - 1)) {
					removeList.add(current);
				}
			}
			point.removeAll(removeList);
			repaint();
		}

		public void addPoint(int x, int y) {
			if (!point.contains(new Point(x, y))) {
				point.add(new Point(x, y));
			}
			repaint();
		}

		public void addPoint(MouseEvent me) {
			int x = me.getPoint().x / BLOCK_SIZE - 1;
			int y = me.getPoint().y / BLOCK_SIZE - 1;
			if ((x >= 0) && (x < GameBoardSize.width) && (y >= 0) && (y < GameBoardSize.height)) {
				addPoint(x, y);
			}
		}

		public void removePoint(int x, int y) {
			point.remove(new Point(x, y));
		}

		public void resetBoard() {
			point.clear();
		}

		public void randomlyFillBoard(int percent) {
			for (int i = 0; i < GameBoardSize.width; i++) {
				for (int j = 0; j < GameBoardSize.height; j++) {
					if (Math.random() * 100 < percent) {
						addPoint(i, j);
					}
				}
			}
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			try {
				for (Point newPoint : point) {
					// Draw new point
					g.setColor(Color.green);
					g.fillRect(BLOCK_SIZE + (BLOCK_SIZE * newPoint.x), BLOCK_SIZE + (BLOCK_SIZE * newPoint.y),
							BLOCK_SIZE, BLOCK_SIZE);
				}
			} catch (ConcurrentModificationException cme) {
			}
			// Setup grid
			g.setColor(Color.BLACK);
			for (int i = 0; i <= GameBoardSize.width; i++) {
				g.drawLine(((i * BLOCK_SIZE) + BLOCK_SIZE), BLOCK_SIZE, (i * BLOCK_SIZE) + BLOCK_SIZE,
						BLOCK_SIZE + (BLOCK_SIZE * GameBoardSize.height));
			}
			for (int i = 0; i <= GameBoardSize.height; i++) {
				g.drawLine(BLOCK_SIZE, ((i * BLOCK_SIZE) + BLOCK_SIZE), BLOCK_SIZE * (GameBoardSize.width + 1),
						((i * BLOCK_SIZE) + BLOCK_SIZE));
			}
		}

		@Override
		public void componentResized(ComponentEvent e) {
			// Setup the game board size with proper boundaries
			GameBoardSize = new Dimension(getWidth() / BLOCK_SIZE - 2, getHeight() / BLOCK_SIZE - 2);
			updateArraySize();
		}

		@Override
		public void componentMoved(ComponentEvent e) {
		}

		@Override
		public void componentShown(ComponentEvent e) {
		}

		@Override
		public void componentHidden(ComponentEvent e) {
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// Mouse was released (user clicked)
			addPoint(e);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			// Mouse is being dragged, user wants multiple selections
			addPoint(e);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
		}

		@Override
		public void run() {
			boolean[][] gameBoard = new boolean[GameBoardSize.width + 2][GameBoardSize.height + 2];
			for (Point current : point) {
				if (torus && current.x == gameBoard[0].length - 3) {
					current.x = 0;
				} else if (torus && current.y == gameBoard[0].length - 3) {
					current.y = 0;
				} else if (torus && current.x == 0) {
					current.x = gameBoard.length - 3;
				} else if (torus && current.y == 0) {
					current.y = gameBoard[0].length - 3;
				}
				gameBoard[current.x + 1][current.y + 1] = true;
			}
			ArrayList<Point> survivingCells = new ArrayList<Point>(0);
			// Iterate through the array, follow game of life rules
			for (int i = 1; i < (gameBoard.length - 1); i++) {
				for (int j = 1; j < (gameBoard[0].length - 1); j++) {
					int surrounding = 0;
					if (gameBoard[i - 1][j - 1]) {
						surrounding++;
					}
					if (gameBoard[i - 1][j]) {
						surrounding++;
					}
					if (gameBoard[i - 1][j + 1]) {
						surrounding++;
					}
					if (gameBoard[i][j - 1]) {
						surrounding++;
					}
					if (gameBoard[i][j + 1]) {
						surrounding++;
					}
					if (gameBoard[i + 1][j - 1]) {
						surrounding++;
					}
					if (gameBoard[i + 1][j]) {
						surrounding++;
					}
					if (gameBoard[i + 1][j + 1]) {
						surrounding++;
					}
					if (gameBoard[i][j]) {
						// Cell is alive, only can live if 2-3
						if (surrounding >= lowBirth && surrounding <= highBirth) {
							survivingCells.add(new Point(i - 1, j - 1));
						}
					} else {
						// Cell is dead, can come back again at 3
						if (surrounding >= lowSurvive && surrounding <= highSurvive) {
							survivingCells.add(new Point(i - 1, j - 1));
						}
					}

				}
			}
			resetBoard();
			point.addAll(survivingCells);
			repaint();
		}
	}
}