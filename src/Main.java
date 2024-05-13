public class Main {
    public static void main(String[] args) {
        Database db = new Database();
       //db.addGame("Jan", 60, "easy");
        System.out.print(db.showScoreboard("easy"));
        MenuFrame menu = new MenuFrame(db);
        //MineSweeper mineSweeper = new MineSweeper();
    }
}