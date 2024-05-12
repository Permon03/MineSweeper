import org.postgresql.util.PGInterval;

import java.sql.*;

public class Database {
    String url = "jdbc:postgresql://localhost:5432/minesweeperdb";

    // Database user to access database;
    String user = "java";
    Connection connection;
    public Database (){

        try {
            this.connection = DriverManager.getConnection(url, user, user);
            System.out.println("Connected to Database successfully");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean searchPlayer (String name){
        boolean bool = false;
        try {
            /*Statement st1 = connection.createStatement();
            ResultSet rs = st1.executeQuery("SELECT * FROM players");

            while(rs.next()){
                System.out.printf("Name: %s, Games: %d", rs.getString(1), rs.getInt(2));
            }*/

            PreparedStatement st = connection.prepareStatement("SELECT 1 FROM players WHERE name=?");
            st.setString(1, name);

            ResultSet result = st.executeQuery();

            while (result.next()){
                 bool = result.getBoolean(1);
                System.out.println(bool);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return bool;
    }

    public void insertNewPlayer(String name){
        try {
            PreparedStatement st = connection.prepareStatement("INSERT INTO players (name, number_of_games) VALUES (?, 0)");
            st.setString(1, name);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addGame(String player, int time, String difficulty){
        try {
            PGInterval interval = new PGInterval();
            interval.setMinutes(time/60);
            interval.setSeconds(time%60);





            int numberOfGames = 1;
            PreparedStatement getNumberOfGames = connection.prepareStatement("SELECT number_of_games FROM players WHERE name=?");
            getNumberOfGames.setString(1, player);
            ResultSet numberOfGamesRS = getNumberOfGames.executeQuery();

            while(numberOfGamesRS.next()){
                numberOfGames = numberOfGamesRS.getInt(1) + 1;
                System.out.println(numberOfGames);
            }

            PreparedStatement setNewGame = connection.prepareStatement("INSERT INTO games (name, game_number, time, difficulty) VALUES (?, ?, '"+interval+"', ?); UPDATE players SET number_of_games=? WHERE name=?");
            setNewGame.setString(1, player);
            setNewGame.setInt(2, numberOfGames);
            setNewGame.setString(3, difficulty);
            setNewGame.setInt(4, numberOfGames);
            setNewGame.setString(5, player);

            setNewGame.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
