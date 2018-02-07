package tablegame.model;

public class Position {
    private int x;  //X koord.
    private int y;  //Y koord.
    
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    /*
        X koordináta beállítása
    */
    public void setX(int x) {
        this.x = x;
    }
    
    /*
        Y koordináta beállítása
    */
    public void setY(int y) {
        this.y = y;
    }
    
    /*
        X koordináta lekérdezése
    */
    public int getX() {
        return this.x;
    }
    
    /*
        Y koordináta lekérdezése
    */
    public int getY() {
        return this.y;
    }
    
    /*
        A default equals metódus felüldefiniálása:
        Két Position típusú objektum összehasonlítása az X
        és az Y koordináták segítségével
    */
    @Override
    public boolean equals(Object obj) {
        if (obj!=null && obj instanceof Position &&
                this.x == ((Position)obj).getX() && this.y == ((Position)obj).getY()) {
            return true;
        }
        else {
            return false;
        }
    }

    /*
        A default hashCode metódus felüldefiniálása:
    */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.x;
        hash = 79 * hash + this.y;
        return hash;
    }
}
