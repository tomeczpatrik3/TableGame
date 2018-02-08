package tablegame;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static tablegame.enums.Action.*;
import static tablegame.enums.Direction.*;
import static tablegame.models.BaseEntity.areOnTheSameField;

import tablegame.models.Arena;
import tablegame.models.BaseEntity;
import tablegame.utils.Position;
import tablegame.enums.Action;

public class Game {
    private Arena pvpArena;
    private Class aRobotClass;
    private Class bRobotClass;
    private Constructor aRobotCtor;
    private Constructor bRobotCtor;
    private Object aRobot;
    private Object bRobot;
    private int maxRound;
    
    public Game(int n, int m, String aClassName, String bClassName, int maxRound) {
        try {
            this.maxRound = maxRound;
            
            /*
                Aréna inicializálása:
            */
            this.pvpArena = new Arena(n,m);
            
            /*
                Adott osztály kiválasztása az input alapján
            */
            this.aRobotClass = Class.forName("tablegame.models."+aClassName);
            this.bRobotClass = Class.forName("tablegame.models."+bClassName);
            /*
                A paramétereknek megfelelő konstruktor kiválasztása
            */
            this.aRobotCtor = aRobotClass.getConstructor(new Class[]{String.class, Arena.class, Position.class, int.class});
            this.bRobotCtor = bRobotClass.getConstructor(new Class[]{String.class, Arena.class, Position.class, int.class}); 
            
            this.initRobots("A", "B");
            
        } catch (ClassNotFoundException ex) {
            System.err.println("Nem létező osztály");
            ex.printStackTrace();
            System.exit(1);
        } catch (NoSuchMethodException ex) {
            System.err.println("Nem létező metódus");
            ex.printStackTrace();
            System.exit(2);
        } catch (SecurityException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            System.exit(3);
        }
    }
    
    /*
        Robotok inicializálása
    */
    private void initRobots(String aName, String bName) {
        /*
            Random pozíciók generálása a robotoknak:
        */
        Position aPos = pvpArena.getRndSpawn();
        Position bPos = pvpArena.getRndSpawn();
        while (aPos.getX()==bPos.getX() || aPos.getY()==bPos.getY()) {
            bPos = pvpArena.getRndSpawn();
        }
        
        /*
            Objektum inicalizálása:
        */
        try {
            this.aRobot = aRobotCtor.newInstance(aName, this.pvpArena, aPos, 5);
            this.bRobot = aRobotCtor.newInstance(bName, this.pvpArena, bPos, 3);             
        } catch (Exception ex) {
            System.err.println("Hiba az objektumok (robotok) inicializálása közben");
            ex.printStackTrace();
            System.exit(4);
        }
    }
        
    public void run() {
        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
        defaultTerminalFactory.setSwingTerminalFrameTitle("Table Game 0.1");
        
        Terminal terminal = null;
        
        try {
            terminal = defaultTerminalFactory.createTerminal();

            terminal.enterPrivateMode();
            terminal.setCursorVisible(false);

            TextGraphics textGraphics = terminal.newTextGraphics();
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
            textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);

            //Kör számláló és logikai változó inicializálása:
            int roundCnt = 0;
            boolean onlyOneIsAlive = false;

            /*
                A játék addig tart amíg el nem jutunk egy adott körig (maxRound)
                vagy valaki nem győz
            */
            while (!onlyOneIsAlive && roundCnt <= this.maxRound) {                    
                System.out.println("----- " + roundCnt + ". kör: -----");

                //Véletlen lépések generálása:
                if (roundCnt != 0) {
                    generateRndAction(aRobot);
                    generateRndAction(bRobot);
                }
                    
                //Ellenorzés, egy mezőn állnak-e a robotok?
                if ( areOnTheSameField((BaseEntity)aRobot, (BaseEntity)bRobot) ) {
                    resolveCollision();
                }

                textGraphics.putString(1, 1, roundCnt + ". kör:", SGR.BOLD);

                //Aréna "felépítése" a terminálon belül:
                for (int i=0; i<this.pvpArena.getSize()[0]+2; i++) {
                    for (int j=0; j<this.pvpArena.getSize()[1]+2; j++) {
                        if ( ((BaseEntity)aRobot).getActualPosition().getX()+1 == i && ((BaseEntity)aRobot).getActualPosition().getY()+1 == j)
                            textGraphics.putString(i+1, j+3, "A", SGR.BOLD);
                        else if (((BaseEntity)bRobot).getActualPosition().getX()+1 == i && ((BaseEntity)bRobot).getActualPosition().getY()+1 == j)
                            textGraphics.putString(i+1, j+3, "B", SGR.BOLD);
                        else if ( i==0 || j==0 || i==this.pvpArena.getSize()[0]+1 || j==this.pvpArena.getSize()[1]+1)
                            textGraphics.putString(i+1, j+3, "#", SGR.BOLD); 
                        else
                            textGraphics.putString(i+1, j+3, ".", SGR.BOLD); 
                    }
                }
                
                //Robotok statisztikáinak megjelenítése:
                textGraphics.putString( 1 , this.pvpArena.getSize()[0]+6, "\""+ ((BaseEntity)aRobot).getName() +"\" robot: " + this.aRobotClass.getSimpleName() + ".class", SGR.BOLD);
                textGraphics.putString( 1 , this.pvpArena.getSize()[0]+7, "Páncél: " + ((BaseEntity)aRobot).getActualArmor() + "/" + ((BaseEntity)aRobot).getMaxArmor() + "   ", SGR.BOLD);
                textGraphics.putString( 1 , this.pvpArena.getSize()[0]+9, "\""+ ((BaseEntity)bRobot).getName() + "\" robot: " + this.bRobotClass.getSimpleName() + ".class", SGR.BOLD);
                textGraphics.putString( 1 , this.pvpArena.getSize()[0]+10, "Páncél: " + ((BaseEntity)bRobot).getActualArmor() + "/" + ((BaseEntity)bRobot).getMaxArmor() + "   ", SGR.BOLD);

                terminal.flush();   

                if (!areTheRobotsAlive()) {
                    onlyOneIsAlive = true;
                }
                else {
                    roundCnt++;
                }
                
                //Sleep:
                TimeUnit.SECONDS.sleep(5);
                System.out.println();
            }
            
            announceTheResult(textGraphics, onlyOneIsAlive);
            
            textGraphics.putString( 1 , this.pvpArena.getSize()[0]+14, "A terminál 10 másodpercen belül bezáródik...", SGR.BOLD);
            TimeUnit.SECONDS.sleep(10);
            terminal.exitPrivateMode();
            
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Hibás paraméterezés!");
            System.exit(1);
        }
    }        
    
    /*
        Ütközés feloldása, attól függően, hogy melyik robot
        milyen cselekvést hajtott végre utoljára
    */
    private void resolveCollision() {
        /*
            Lehetésges esetek:
            A:              B:              Eredmény:

            Lépés           Lépés           Mindkettő visszakerül a helyére
            Lépés           Védekezés       Mindkettő visszakerül a helyére
            Lépés           Várakozás       A visszakerül a helyére
            Lépés           Támadás         Mindkettő visszakerül a helyére, páncél levonás A-nál

            Támadás         Lépés           Mindkettő visszakerül a helyére, páncél levonás B-nél
            Támadás         Védekezés       Mindkettő visszakerül a helyére, nincs páncél levonás
            Támadás         Várakozás       A visszakerül a helyére, páncél levonás B-nél
            Támadás         Támadás         Mindkettő visszakerül a helyére, páncél levonás mindkettőnél

            Védekezés        Lépés           Mindkettő visszakerül a helyére
            Védekezés        Védekezés       Mindkettő visszakerül a helyére
            Védekezés        Várakozás       A visszakerül a helyére
            Védekezés        Támadás         Mindkettő visszakerül a helyére, nincs páncél levonás

            Várakozás        Lépés           B visszakerül a helyére
            Várakozás        Védekezés       B visszakerül a helyére
            Várakozás        Támadás         B visszakerül a helyére,  páncél levonás A-nál                           
        */

        System.out.println("A két robot azonos mezőre került!");

        if ( ((BaseEntity)aRobot).getLastAction() == MOVE && ((BaseEntity)aRobot).getLastAction() == MOVE ) {
            ((BaseEntity)aRobot).restorePosition();
            ((BaseEntity)bRobot).restorePosition();
        }
        else if ( (((BaseEntity)aRobot).getLastAction() == MOVE && ((BaseEntity)aRobot).getLastAction() == DEFEND ) 
            || (((BaseEntity)aRobot).getLastAction() == DEFEND && ((BaseEntity)aRobot).getLastAction() == MOVE )){
            ((BaseEntity)aRobot).restorePosition();
            ((BaseEntity)bRobot).restorePosition();
        }
        else if ( ((BaseEntity)aRobot).getLastAction() == MOVE && ((BaseEntity)aRobot).getLastAction() == WAIT ) {
            ((BaseEntity)aRobot).restorePosition();
        }
        else if ( ((BaseEntity)aRobot).getLastAction() == MOVE && ((BaseEntity)aRobot).getLastAction() == ATTACK ) {
            ((BaseEntity)aRobot).restorePosition();
            ((BaseEntity)bRobot).restorePosition();
            ((BaseEntity)aRobot).sufferDmg(1);
        }
        else if ( ((BaseEntity)aRobot).getLastAction() == ATTACK && ((BaseEntity)aRobot).getLastAction() == MOVE ) {
            ((BaseEntity)aRobot).restorePosition();
            ((BaseEntity)bRobot).restorePosition();
            ((BaseEntity)bRobot).sufferDmg(1);
        }
        else if ( ((BaseEntity)aRobot).getLastAction() == ATTACK && ((BaseEntity)aRobot).getLastAction() == WAIT ) {
            ((BaseEntity)aRobot).restorePosition();
            ((BaseEntity)bRobot).sufferDmg(1);
        }     
        else if ( ((BaseEntity)aRobot).getLastAction() == ATTACK && ((BaseEntity)aRobot).getLastAction() == ATTACK ) {
            ((BaseEntity)aRobot).restorePosition();
            ((BaseEntity)aRobot).sufferDmg(1);
            ((BaseEntity)bRobot).restorePosition();
            ((BaseEntity)bRobot).sufferDmg(1);
        }  
        else if ( ((BaseEntity)aRobot).getLastAction() == DEFEND && ((BaseEntity)aRobot).getLastAction() == DEFEND ) {
            ((BaseEntity)aRobot).restorePosition();
            ((BaseEntity)bRobot).restorePosition();
        }  
        else if ( ((BaseEntity)aRobot).getLastAction() == DEFEND && ((BaseEntity)aRobot).getLastAction() == WAIT ) {
            ((BaseEntity)aRobot).restorePosition();
        }  
        else if ( (((BaseEntity)aRobot).getLastAction() == DEFEND && ((BaseEntity)aRobot).getLastAction() == ATTACK )
            || (((BaseEntity)aRobot).getLastAction() == ATTACK && ((BaseEntity)aRobot).getLastAction() == DEFEND )){
            ((BaseEntity)aRobot).restorePosition();
            ((BaseEntity)bRobot).restorePosition();
        }
        else if ( ((BaseEntity)aRobot).getLastAction() == WAIT && ((BaseEntity)aRobot).getLastAction() == MOVE ) {
            ((BaseEntity)bRobot).restorePosition();
        }  
        else if ( ((BaseEntity)aRobot).getLastAction() == WAIT && ((BaseEntity)aRobot).getLastAction() == DEFEND ) {
            ((BaseEntity)bRobot).restorePosition();
        }  
        else if ( ((BaseEntity)aRobot).getLastAction() == WAIT && ((BaseEntity)aRobot).getLastAction() == ATTACK ) {
            ((BaseEntity)bRobot).restorePosition();
            ((BaseEntity)aRobot).sufferDmg(1);
        }          
    }
    
    private boolean areTheRobotsAlive() {
        return ( ((BaseEntity)aRobot).isAlive() == true ) && ( ((BaseEntity)bRobot).isAlive() == true );
    }
    
    /*
        Eredmény kihirdetése:
        -Ha az egyik robot már nem él, akkor a másik a győztes
        -Ha mindkettő él, akkor a páncéljaikat hasonlítjuk össze
    */
    private void announceTheResult(TextGraphics tg, boolean onlyOneIsAlive) {
        if (onlyOneIsAlive) {
            if ( ((BaseEntity)aRobot).isAlive() == false ) {
                tg.putString( 1 , this.pvpArena.getSize()[0]+12, "--- A játék véget ért, " + ((BaseEntity)aRobot).getName() + " robot páncélja elfogyott ---", SGR.BOLD);
                tg.putString( 1 , this.pvpArena.getSize()[0]+13, "A győztes: " + ((BaseEntity)bRobot).getName() + " robot", SGR.BOLD);
            }
            else if ( ((BaseEntity)bRobot).isAlive() == false ) {
                tg.putString( 1 , this.pvpArena.getSize()[0]+12, "--- A játék véget ért, " + ((BaseEntity)bRobot).getName() + " robot páncélja elfogyott ---", SGR.BOLD);
                tg.putString( 1 , this.pvpArena.getSize()[0]+13, "A győztes: " + ((BaseEntity)aRobot).getName() + " robot", SGR.BOLD);
            }       
        }
        else {
            if (((BaseEntity)aRobot).getActualArmor() > ((BaseEntity)bRobot).getActualArmor() ) {
                tg.putString( 1 , this.pvpArena.getSize()[0]+12, "--- A játék véget ért, " + ((BaseEntity)aRobot).getName() + " robotnak maradt több páncélja ---", SGR.BOLD);
                tg.putString( 1 , this.pvpArena.getSize()[0]+13, "A győztes: " + ((BaseEntity)aRobot).getName() + " robot", SGR.BOLD);
            }
            else if (((BaseEntity)bRobot).getActualArmor() > ((BaseEntity)aRobot).getActualArmor()) {
                tg.putString( 1 , this.pvpArena.getSize()[0]+12, "--- A játék véget ért, " + ((BaseEntity)bRobot).getName() + " robotnak maradt több páncélja ---", SGR.BOLD);
                tg.putString( 1 , this.pvpArena.getSize()[0]+13, "A győztes: " + ((BaseEntity)bRobot).getName() + " robot", SGR.BOLD);
            }
            else {
                tg.putString( 1 , this.pvpArena.getSize()[0]+12, "--- A játék döntetlen eredménnyel ért! ---", SGR.BOLD);
            }             
        }
    }
    
    /*
        A paraméterben megadott akció végrehajtása az adott robottal
        (mj.: Az akció iránya véletlenszerűen generált)
    */
    private void performAction(Action action, Object robot) {
        /*
            Cselekvések: 
                MOVE direction - mozgás adott irányban
                ATTACK direction - támadás adott irányban
                DEFEND direction - védekezés adott irányban
                WAIT - várakozás a következő körig
        */
        /*
            Rnd generátor létrehozása:
        */
        Random rnd = new Random();
        int rand = rnd.nextInt(4);
        switch ( action ) {
            case MOVE:
                switch ( rand ) {
                    case 0:
                        System.out.println("\tLépés felfelé (ha lehetséges)");
                        ((BaseEntity)robot).move(NORTH);
                        break;
                    case 1:
                        System.out.println("\tLépés lefelé (ha lehetséges)");
                        ((BaseEntity)robot).move(SOUTH);                       
                        break;
                    case 2:
                        System.out.println("\tLépés jobbra (ha lehetséges)");
                        ((BaseEntity)robot).move(EAST);
                        break;
                    case 3:
                        System.out.println("\tLépés balra (ha lehetséges)");
                        ((BaseEntity)robot).move(WEST);                       
                        break;
                }
                break;
            case ATTACK:
                switch ( rand ) {
                    case 0:
                        System.out.println("\tTámadás felfelé (ha lehetséges)");
                        ((BaseEntity)robot).attack(NORTH);   
                        break;
                    case 1:
                        System.out.println("\tTámadás lefelé (ha lehetséges)");
                        ((BaseEntity)robot).attack(SOUTH);                         
                        break;
                    case 2:
                        System.out.println("\tTámadás jobbra (ha lehetséges)");
                        ((BaseEntity)robot).attack(EAST);  
                        break;
                    case 3:
                        System.out.println("\tTámadás balra (ha lehetséges)");
                        ((BaseEntity)robot).attack(WEST);                       
                        break;
                }
                break;
            case DEFEND:
                switch ( rand ) {
                    case 0:
                        System.out.println("\tVédekezés felfelé (ha lehetséges)");
                        ((BaseEntity)robot).defend(NORTH);  
                        break;
                    case 1:
                        System.out.println("\tVédekezés lefelé (ha lehetséges)");
                        ((BaseEntity)robot).defend(SOUTH);                        
                        break;
                    case 2:
                        System.out.println("\tVédekezés jobbra (ha lehetséges)");
                        ((BaseEntity)robot).defend(EAST);  
                        break;
                    case 3:
                        System.out.println("\tVédekezés balra (ha lehetséges)");
                        ((BaseEntity)robot).defend(WEST);                       
                        break;
                }
                break;
            case WAIT:
                System.out.println("\tVárakozás");
                ((BaseEntity)robot).waitNextRound();
                break;
            default:
                System.err.println("Nem létező opció!\nVárakozás a következő körig");
                ((BaseEntity)robot).waitNextRound();
        }
    }
    
    /*
        A paraméterben megadott robothoz véletlen akció generálása:
    */
    private void generateRndAction(Object robot) {
        /*
            Rnd generátor létrehozása:
        */
        Random rnd = new Random();
        int rand = rnd.nextInt(4);
        System.out.println( ((BaseEntity)robot).getName() + " robot lépése: " );
        switch (rand) {
            case 0:
                performAction(MOVE, robot);
                break;
            case 1:
                performAction(ATTACK, robot);
                break;
            case 2:
                performAction(DEFEND, robot);
                break;
            case 3:
                performAction(WAIT, robot);
                break;              
        }
        
    }
}
