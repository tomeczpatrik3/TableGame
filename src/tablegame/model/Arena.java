package tablegame.model;

import java.util.Random;

public class Arena {
    int n; //Rows 
    int m; //Columns
    
    public Arena (int n, int m) {
        this.n = n;
        this.m = m;
    }
        
    public int[] getSize(){
        return new int[] {n,m};
    }
    
    public void setSize(int n, int m) {
        this.n = n;
        this.m = m;
    }
    
    public Position getRndSpawn() {
        Random rand = new Random();
        return new Position( rand.nextInt(n),  rand.nextInt(m));
    }
   
}
