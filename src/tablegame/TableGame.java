package tablegame;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import tablegame.model.Action;
import static tablegame.model.Action.*;
import tablegame.model.Arena;
import tablegame.model.BaseEntity;
import static tablegame.model.BaseEntity.areOnTheSameField;
import tablegame.model.Direction;
import tablegame.model.Position;


public class TableGame {    
    
    public static void main(String[] args) {
        /*
            Játék "betöltése":
        */
        try {
            /*
                Paraméterezés:
                0. - méret
                1. - méret
                2. - első robot típusa (osztály)
                3. - második robot típusa (osztály)
                4. - maximális körszám
            */
            int n = Integer.parseInt(args[0]);
            int m = Integer.parseInt(args[1]);
            String aClassName = args[2];
            String bClassName = args[3];
            int maxRound = Integer.parseInt(args[4]);
            
            /*
                Scanner léterhozása:
            */
            Scanner sc = new Scanner(System.in);
            /*
                Aréna inicializálása:
            */
            Arena pvpArena = new Arena(n,m);
            
            /*
                Adott osztály kiválasztása az input alapján
            */
            Class aClass = Class.forName("tablegame.model."+aClassName);
            Class bClass = Class.forName("tablegame.model."+bClassName);
            /*
                A paramétereknek megfelelő konstruktor kiválasztása
            */
            Constructor aCtor = aClass.getConstructor(new Class[]{String.class, Arena.class, Position.class, int.class});
            Constructor bCtor = bClass.getConstructor(new Class[]{String.class, Arena.class, Position.class, int.class});
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
            Object aRobot = aCtor.newInstance("A", pvpArena, aPos, 15);
            Object bRobot = bCtor.newInstance("B", pvpArena, bPos, 10);
            
            /*
                A játék "logikája":
            */

            DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
            defaultTerminalFactory.setSwingTerminalFrameTitle("Robot Game 0.1");
            Terminal terminal = null;
            try {
                terminal = defaultTerminalFactory.createTerminal();
               
                
                //TextGraphics to write strings into console:
                final TextGraphics textGraphics = terminal.newTextGraphics();
                textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);

                //Kör számláló inicializálása:
                int roundCnt = 0;
                
                while (roundCnt <= maxRound) {
                    //Lépések, logic:
                    if (roundCnt != 0) {
                        generateRndAction(aClass, aRobot);
                        generateRndAction(bClass, bRobot);
                    }
                    
                    //Ellenorzés, hogy egy mezon allnak-e?
                    if ( areOnTheSameField((BaseEntity)aRobot, (BaseEntity)bRobot) ) {
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
                        
                        
                    }
                    
                    textGraphics.putString(1, 1, roundCnt + ". kör:", SGR.BOLD);

                    terminal.putCharacter('\n');
                    terminal.putCharacter('\n');
                    for (int i=0; i<n+2; i++) {
                        terminal.putCharacter(' ');
                        for (int j=0; j<m+2; j++) {
                            if (getPosition(aClass, aRobot).getX()+1 == i && getPosition(aClass, aRobot).getY()+1 == j)
                                terminal.putCharacter('A');
                            else if (getPosition(bClass, bRobot).getX()+1 == i && getPosition(bClass, bRobot).getY()+1 == j)
                                terminal.putCharacter('B');
                            else if ( i==0 || j==0 || i==n+1 || j==m+1)
                                terminal.putCharacter('#');
                            else
                                terminal.putCharacter('.');
                        }
                        terminal.putCharacter('\n');
                    }
                    
                    textGraphics.putString( 1 , n+6, "\"A\" robot: " + aClassName + ".class", SGR.BOLD);
                    textGraphics.putString( 1 , n+7, "Páncél: " + getActualArmor(aClass, aRobot) + "/" + getMaxArmor(aClass, aRobot), SGR.BOLD);
                    
                    textGraphics.putString( 1 , n+9, "\"B\" robot: " + bClassName + ".class", SGR.BOLD);
                    textGraphics.putString( 1 , n+10, "Páncél: " + getActualArmor(bClass, bRobot) + "/" + getMaxArmor(bClass, bRobot), SGR.BOLD);

                    terminal.flush();   

                    roundCnt++;
                    
                    //Sleep:
                    TimeUnit.SECONDS.sleep(5);
                }
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Hibás paraméterezés!");
            System.exit(1);
        }
        
    } 
    
    public static Position getPosition(Class clazz, Object obj) {
        try {
            Method getPos = clazz.getMethod("getPosition", new Class[]{});
            return (Position)getPos.invoke(obj, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static int getActualArmor(Class clazz, Object obj) {
        try {
            /*
                Egy oylan fgv-t keresünk, aminek a neve: getActualArmor
                és nincs paramétere ( new Class[]{} )
            */
            Method getActualArmor = clazz.getMethod("getActualArmor", new Class[]{});
            return (int)getActualArmor.invoke(obj, null);
        } catch (Exception e) {
            return 0;
        } 
    }
    
    public static int getMaxArmor(Class clazz, Object obj) {
        try {
            Method getMaxArmor = clazz.getMethod("getMaxArmor", new Class[]{});
            return (int)getMaxArmor.invoke(obj, null);
        } catch (Exception e) {
            return 0;
        }         
    }
    
    public static void move(Class clazz, Object obj, Direction direction) {
        try {
            Method move = clazz.getMethod("move", new Class[]{Direction.class});
            move.invoke(obj, direction);
        } catch (Exception e) {
            e.printStackTrace();
        }         
    }
    
    public static String callGetName(Class clazz, Object obj) {
        try {
            Method getName = clazz.getMethod("getName", new Class[]{});
            return (String)getName.invoke(obj, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
       
    }
    
    public static void performAction(Action action, Class clazz, Object obj) {
        /*
            Cselekvések: 
                MOVE direction - mozgás adott irányban
                ATTACK direction - támadás adott irányban
                DEFEND direction - védekezés adott irányban
                WAIT - várakozás a következő körig
        */
        switch ( action ) {
            case MOVE:
                System.out.println("MOVE");
                break;
            case ATTACK:
                System.out.println("ATTACK");
                break;
            case DEFEND:
                System.out.println("DEFEND");
                break;
            case WAIT:
                System.out.println("WAIT");
                break;
            default:
                System.err.println("Nem létező opció!\nVárakozás a következő körig");
        }
    }
    
    public static void generateRndAction(Class clazz, Object obj) {
        /*
            Rnd generátor létrehozása:
        */
        Random rnd = new Random();
        int rand = rnd.nextInt(4);
        switch (rand) {
            case 0:
                System.out.println( callGetName(clazz,obj) + " robot lépése: " );
                performAction(MOVE, clazz, obj);
                break;
            case 1:
                System.out.println( callGetName(clazz,obj) + " robot lépése: " );
                performAction(ATTACK, clazz, obj);
                break;
            case 2:
                System.out.println( callGetName(clazz,obj) + " robot lépése: " );
                performAction(DEFEND, clazz, obj);
                break;
            case 3:
                System.out.println( callGetName(clazz,obj) + " robot lépése: " );
                performAction(WAIT, clazz, obj);
                break;              
        }
        
    }
}
