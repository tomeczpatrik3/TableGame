package tablegame.models;

import tablegame.utils.Position;
import java.util.Random;

public class Arena {
    private int n; //Sorok száma
    private int m; //Oszlopok száma
    
    public Arena (int n, int m) {
        this.n = n;
        this.m = m;
    }
    
    /*
        Az aréna méretének lekérdezése:
    */
    public int[] getSize(){
        return new int[] {n,m};
    }
    
    /*
        Az aréna méretének beállítása:
    */
    public void setSize(int n, int m) {
        this.n = n;
        this.m = m;
    }
    
    /*
        Egy véletlen pozíció generálása, mely
        az aréna területére esik (pl spawn esetén hasznos)
    */
    public Position getRndSpawn() {
        Random rand = new Random();
        return new Position( rand.nextInt(n),  rand.nextInt(m));
    }
   
    /*
        Egy pozíció helyességének ellenőrzése, bele esik-e
        az aréna területébe?
    */
    public boolean isValidPosition(Position pos) {
        return ( pos.getX()<this.n && pos.getX()>=0 && pos.getY()<this.m && pos.getY()>=0 );
    }
}
