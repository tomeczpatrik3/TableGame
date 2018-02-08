package tablegame;

public class Main {    
    
    public static void main(String[] args) {
        /*
            Paraméterezés:
            0. - méret
            1. - méret
            2. - első robot típusa (osztály)
            3. - második robot típusa (osztály)
            4. - maximális körszám
        */
        try {
            int n = Integer.parseInt(args[0]);
            int m = Integer.parseInt(args[1]);
            String aClassName = args[2];
            String bClassName = args[3];
            int maxRound = Integer.parseInt(args[4]);    
            
            Game game = new Game(n, m, aClassName, bClassName, maxRound);
            game.run();
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Hibás paraméterezés");
            System.exit(1);
        }
    }
}
