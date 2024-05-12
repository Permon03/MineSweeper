import javax.swing.*;
import javax.swing.border.Border;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.SimpleTimeZone;

public class MenuFrame {
    private final Database db;

    private final int WINDOW_WIDTH = 500;
    private final int WINDOW_HEIGHT = 500;
    private final int TOP_LABEL_HEIGHT = 35;

    private String player;
    private MineSweeper game;


    private JFrame frame;

    private JTextField playerNameField;
    private JButton selectButton;
    private JButton createNewPlayerButton;
    private JLabel playerLabel;
    private JLabel topLabel;

    private JButton easyButton;
    private JButton mediumButton;
    private JButton hardButton;
    private JLabel bottomLabel;

    public MenuFrame (Database db){
        this.db = db;
        // Creating Frame
        this.frame = new JFrame("Minesweeper");
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        createTop();
        createBottom();

        frame.add(topLabel, BorderLayout.NORTH);
        frame.add(bottomLabel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }


    public void createTop(){
        // Text field to put player name
        this.playerNameField = new JTextField();
        playerNameField.setPreferredSize(new Dimension(100, 20));

        // Button to select Player
        this.selectButton = new JButton("Select Player");
        selectButton.setFont(new Font("Arial", Font.PLAIN, 15));
        selectButton.setOpaque(false);
        selectButton.addActionListener(e -> {
            if (textFieldEmpty()){
                JOptionPane.showMessageDialog(frame, "Please enter a Player name!");
            } else {
                if(db.searchPlayer(playerNameField.getText())){
                    setPlayer(playerNameField.getText());
                } else {
                    JOptionPane.showMessageDialog(frame, "Player not found!");
                }
            }
        });


        // Button to create new player
        this.createNewPlayerButton = new JButton("Create new Player");
        createNewPlayerButton.setFont(new Font("Arial", Font.PLAIN, 15));
        createNewPlayerButton.setOpaque(false);
        createNewPlayerButton.addActionListener(e -> {
            if (textFieldEmpty()){
                JOptionPane.showMessageDialog(frame, "Please enter a Player name!");
            } else {
                if (playerNameField.getText().length() > 80){
                    JOptionPane.showMessageDialog(frame, "Name to long!");
                }
                else if (db.searchPlayer(playerNameField.getText())){
                    JOptionPane.showMessageDialog(frame, "Player already exists!");
                } else {
                    db.insertNewPlayer(playerNameField.getText());
                    setPlayer(playerNameField.getText());
                }
            }
        });

        this.playerLabel = new JLabel();

        // Creating Top Label that holds text field for player name and two buttons:
        // checking if player already exists and select player; create new player
        this.topLabel = new JLabel();
        topLabel.setPreferredSize(new Dimension(WINDOW_WIDTH, TOP_LABEL_HEIGHT));
        topLabel.setLayout(new FlowLayout(FlowLayout.CENTER, 4, 2));
        topLabel.setOpaque(false);
        topLabel.setBackground(Color.BLACK);


        // Adding all the different Labels and Panels to one another
        topLabel.add(playerNameField);
        topLabel.add(selectButton);
        topLabel.add(createNewPlayerButton);
        topLabel.add(playerLabel);
    }

    public void createBottom(){
        this.easyButton = new JButton("Easy");
        easyButton.setFont(new Font("Arial", Font.PLAIN, 15));
        easyButton.setOpaque(false);
        easyButton.addActionListener(e -> startGame(1));

        this.mediumButton = new JButton("Medium");
        mediumButton.setFont(new Font("Arial", Font.PLAIN, 15));
        mediumButton.setOpaque(false);
        mediumButton.addActionListener(e -> startGame(2));

        this.hardButton = new JButton("Hard");
        hardButton.setFont(new Font("Arial", Font.PLAIN, 15));
        hardButton.setOpaque(false);
        hardButton.addActionListener(e -> startGame(3));

        this.bottomLabel = new JLabel();
        bottomLabel.setPreferredSize(new Dimension(WINDOW_WIDTH, TOP_LABEL_HEIGHT));
        bottomLabel.setLayout(new FlowLayout(FlowLayout.CENTER, 4, 2));
        bottomLabel.setOpaque(false);

        bottomLabel.add(easyButton);
        bottomLabel.add(mediumButton);
        bottomLabel.add(hardButton);

    }

    public boolean textFieldEmpty (){
        return playerNameField.getText().equals("");
    }

    public void setPlayer (String player){
        this.player = player;
        this.playerLabel.setText(player);
    }

    public void startGame (int diff){
        if (playerChosen()){
            frame.setVisible(false);
            frame.dispose();
            switch (diff){
                case 1 -> game = new MineSweeper(8, 6, player, db, "easy");
                case 2 -> game = new MineSweeper(10, 15, player, db, "medium");
                case 3 -> game = new MineSweeper(14, 35, player, db, "hard");
            }
        }

    }

    public boolean playerChosen(){
        if (player == null || player.isBlank()){
            JOptionPane.showMessageDialog(frame, "Enter valid Player!");
            return false;
        }
        return true;
    }

}
