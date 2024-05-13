import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class MineSweeper {

    private final Database db;
    private final String player;
    private final String difficulty;

    private final int NUMBER_OF_ROWS;
    private final int NUMBER_OF_COLS;
    private final int TILE_SIZE = 70;
    private final int WINDOW_WIDTH;
    private final int WINDOW_HEIGHT;
    private final int BOARD_WIDTH;
    private final int BOARD_HEIGHT;

    private JFrame frame;
    private JLabel scoreLabel;
    private JLabel timerLabel;
    private JPanel textPanel;
    private JPanel boardPanel = new JPanel();


    private int score = 0;
    private int time = 0;
    private int countFlags = 0;
    private boolean gameOver = false;
    private boolean gameWon = false;

    private Tile[][] tiles;
    private ArrayList<Tile> mines;
    private final int NUMBER_OF_MINES;


    private Timer timer;

    private class Tile extends JButton {
        private final int row;
        private final int col;
        private boolean revealed = false;
        private boolean flagged = false;

        Tile (int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    MineSweeper(int numberOfRows, int numberOfMines, String player, Database db, String difficulty) {
        this.db = db;
        this.player = player;
        this.difficulty = difficulty;
        this.NUMBER_OF_ROWS = numberOfRows;
        this.NUMBER_OF_COLS = numberOfRows;
        this.NUMBER_OF_MINES = numberOfMines;
        this.WINDOW_WIDTH = NUMBER_OF_COLS * TILE_SIZE;
        this.WINDOW_HEIGHT = WINDOW_WIDTH + 50;
        this.BOARD_HEIGHT = WINDOW_HEIGHT;
        this.BOARD_WIDTH = WINDOW_WIDTH;
        this.tiles = new Tile[NUMBER_OF_ROWS][NUMBER_OF_COLS];

        this.frame = createFrame();
        this.scoreLabel = createScoreLabel();
        this.timerLabel = createTimerLabel();
        this.textPanel = createTextPanel();

        textPanel.add(scoreLabel);
        textPanel.add(timerLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        this.boardPanel = createBoard();
        frame.add(boardPanel);
        frame.setVisible(true);

        this.mines = createMines();
        //revealMines();

        timer = new Timer(1000, e -> {
            if (gameOver){
                timer.stop();
                backToMenu();
                if (!gameWon)
                    scoreLabel.setText("Game Over!");

            }
            time++;
            timerLabel.setText("Time: " + time);
        });



    }

    private JFrame createFrame() {
        JFrame frame = new JFrame("Mine Sweeper");
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        return frame;
    }

    private JLabel createScoreLabel() {
        JLabel scoreLabel = new JLabel();
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 25));
        scoreLabel.setText("Flags: " + countFlags +"/"+NUMBER_OF_MINES);
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        scoreLabel.setOpaque(false);
        return scoreLabel;
    }

    private JLabel createTimerLabel() {
        JLabel timerLabel = new JLabel();
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 25));
        timerLabel.setText("Time: " + time);
        timerLabel.setHorizontalAlignment(JLabel.CENTER);
        timerLabel.setOpaque(false);
        return timerLabel;
    }

    private JPanel createTextPanel() {
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        textPanel.setBackground(Color.WHITE);
        return textPanel;
    }

    private JPanel createBoard() {
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(NUMBER_OF_ROWS, NUMBER_OF_COLS));
        for (int r = 0; r < NUMBER_OF_ROWS; r++){
            for (int c = 0; c < NUMBER_OF_COLS; c++){
                Tile tile = new Tile(r, c);
                tiles[r][c] = tile;
                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                if (r == 0)
                    tile.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.BLACK));
                else
                    tile.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLACK));
                tile.setFont(new Font ("Arial", Font.PLAIN, 20));
                tile.setBackground(Color.WHITE);


                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (!gameOver){
                            if (!timer.isRunning()){
                                timer.start();
                            }
                            Tile tile = (Tile) e.getSource();
                            if (!tile.revealed){
                                switch (e.getButton()){
                                    case MouseEvent.BUTTON1:
                                        if (checkMine(tile)) {
                                            gameOver = true;
                                            revealMines();
                                        } else {
                                            checkTile(tile.row, tile.col);
                                        } break;
                                    case MouseEvent.BUTTON3:
                                        if (!tile.revealed){
                                            tile.setText(tile.flagged ? "" : "🚩");
                                            countFlags += tile.flagged ? -1 : 1;
                                            scoreLabel.setText("Flags: " + countFlags +"/"+NUMBER_OF_MINES);
                                            tile.flagged = !tile.flagged;
                                        }
                                }
                            }


                        }
                    }
                });
                boardPanel.add(tile);
            }
        }
        return boardPanel;
    }

    private ArrayList<Tile> createMines(){
        Random random = new Random();
        ArrayList<Tile> mines = new ArrayList<>();

        while (mines.size() < NUMBER_OF_MINES){
            Tile mine = tiles[random.nextInt(0, tiles.length)][random.nextInt(0, tiles[0].length)];
            if (!mines.contains(mine)){
                mines.add(mine);

            }
        }
        return mines;
    }

    private void revealMines(){
        for (Tile tile : mines){
            tile.setText("💣");
        }
    }

    private boolean checkMine (Tile tile){
        return mines.contains(tile);
    }

    private void revealTile(Tile tile){
        tile.setEnabled(true);
        tile.setOpaque(true);
        tile.revealed = true;
        score++;
        System.out.println("revealed");

        if (score >= NUMBER_OF_ROWS*NUMBER_OF_COLS - NUMBER_OF_MINES){
            gameWon  = true;
            gameOver = true;
            scoreLabel.setText("All Mines cleared!");
            db.addGame(player, time, difficulty);
        }
    }

    private void checkTile (int r, int c){
        if (r < 0 || r >= NUMBER_OF_ROWS || c < 0 || c >= NUMBER_OF_COLS){
            return;
        }
        if (tiles[r][c].revealed){
            return;
        }
        revealTile(tiles[r][c]);
        int mineCount = 0;

        mineCount += countMine(r-1, c); // top
        mineCount += countMine(r-1, c+1); // top right
        mineCount += countMine(r-1, c-1); // top left
        mineCount += countMine(r, c-1); // left
        mineCount += countMine(r, c+1); // right
        mineCount += countMine(r+1, c-1); // bottom left
        mineCount += countMine(r+1, c); // bottom
        mineCount += countMine(r+1, c+1); // bottom right
        System.out.println(r + "  " + c);
        if (mineCount > 0){
            tiles[r][c].setText(String.valueOf(mineCount));
        } else {
            tiles[r][c].setText(" ");
            checkTile(r-1, c);
            checkTile(r-1, c+1);
            checkTile(r-1, c-1);
            checkTile(r, c-1);
            checkTile(r, c+1);
            checkTile(r+1, c-1);
            checkTile(r+1, c);
            checkTile(r+1, c+1);
        }

    }

    private int countMine(int r, int c){
        if (r < 0 || r >= NUMBER_OF_ROWS || c < 0 || c >= NUMBER_OF_COLS)
            return 0;
        return checkMine(tiles[r][c]) ? 1 : 0;
    }

    public void backToMenu () {
        JButton returnButton = new JButton("Return");
        returnButton.setFont(new Font("Arial", Font.PLAIN, 10));
        returnButton.setOpaque(false);
        returnButton.addActionListener(e -> {
            frame.setVisible(false);
            frame.dispose();
            new MenuFrame(db).setPlayer(player);
        });

        textPanel.add(returnButton);

    }

}
