package tablegame.model;

import static tablegame.model.Action.*;

public class BaseEntity implements BaseInterface {
    //A robot nevének tárolására:
    private String name;
    //A robot utolsó végrehajtott akciója:
    private Action lastAction;
    //A robot utolsó pozíciója (a mostani előtti):
    private Position lastPos;
    //A robot jelenlegi pozíciója:
    private Position pos;
    //Az aréna, amiben a robot szerepel:
    private Arena arena;
    //A robot maximális páncélja:
    private int maxArmor;
    //A robot aktuális páncélja:
    private int actualArmor;
    //A robot sebzése:
    private int damage;
    
    public BaseEntity(String name, Arena arena, Position pos, int armor) {
        this.name = name;
        this.arena = arena;
        this.maxArmor = armor;
        this.actualArmor = armor;
        this.pos = pos;
    }
    
    public BaseEntity(String name, Arena arena, int x, int y, int armor) {
        this.name = name;
        this.arena = arena;
        this.maxArmor = armor;
        this.actualArmor = armor;
        this.pos = new Position(x,y);
    }
    
    /*
        Név lekérdezése
    */
    @Override
    public String getName() {
        return this.name;
    }
    
    /*
        Név beállítása
    */
    @Override
    public void setName(String name) {
        this.name = name;
    }
    
    /*
        Aktuális pozíció lekérdezése
    */
    @Override
    public Position getActualPosition() {
        return this.pos;
    }
    
    /*
        Aktuális pozíció beállítása
    */
    @Override
    public void setActualPosition(int x, int y) {
        this.pos.setX(x);
        this.pos.setY(y);
    }
    
    /*
        Utolsó pozíció lekérdezése
    */
    @Override
    public Position getLastPosition() {
        return this.lastPos;
    }
    
    /*
        Utolsó pozíció beállítása
    */
    @Override
    public void setLastPosition(int x, int y) {
        this.lastPos.setX(x);
        this.lastPos.setY(y);
    }
    
    /*
        Utolsó akció lekérdezése
    */
    @Override
    public Action getLastAction() {
        return this.lastAction;
    }
    
    /*
        Utolsó akció beállítása
    */
    @Override
    public void setLastAction(Action act) {
        this.lastAction = act;
    }
    
    /*
        Maximum páncél lekérdezése
    */
    @Override
    public int getMaxArmor() {
        return this.maxArmor;
    }
     
    /*
        Maximum páncél beállítása
    */
    @Override
    public void setMaxArmor(int armor) {
        this.maxArmor = armor;
    }
    
    /*
        Jelenlegi páncél lekérdezése
    */
    @Override
    public int getActualArmor() {
        return this.actualArmor;
    }
    
    /*
        Jelenlegi páncél beállítása, feltéve ha
        nem haladja meg a maximális páncélt
    */
    @Override
    public void setActualArmor(int armor) {
        if (armor <= this.maxArmor )
            this.actualArmor = armor;
    }
    
    /*
        Sebzés lekérdezése
    */
    @Override
    public int getDamage(){
        return this.damage;
    }
    
    /*
        Sebzés beállítása
    */
    @Override
    public void setDamage(int dmg){
        this.damage = dmg;
    }
    
    /*
        Aréna méretének lekérdezése
    */
    @Override
    public int[] getArenaSize() {
        return this.arena.getSize();
    }
    
    /*
        Támadás egy meghatározott irányba
    */
    @Override
    public void attack(Direction direction){
        performMove(direction);
        this.lastAction = ATTACK;
    }
    
    /*
        Védekezés egy meghatározott irányba
    */
    @Override
    public void defend(Direction direction){
        performMove(direction);
        this.lastAction = DEFEND;
    }
    
    /*
        Várakozás az adott körben
    */
    @Override
    public void waitNextRound(){
        this.lastPos = this.pos;
        this.lastAction = WAIT;
    }
    
    /*
        Lépés egy meghatározott irányba
    */
    @Override
    public void move(Direction direction) {
        performMove(direction);
        this.lastAction = MOVE;
    }
    
    /*
        Az attack, move és defend metódusok kódismétlésének elkerülése végett
        Előző pozíció eltárolása, majd lépés vágrehajtása
        Ha a lépés helytelen (nem esik bele az arénába), akkor pozíció visszaállítása
        a "lastPos" adattag segítségével.
    */
    @Override
    public void performMove(Direction direction) {
        this.lastPos = this.pos;
        
        switch (direction) {
            case EAST: 
                this.pos = new Position(this.getActualPosition().getX(), this.getActualPosition().getY()+1);
                break;
            case WEST: 
                this.pos = new Position(this.getActualPosition().getX(), this.getActualPosition().getY()-1);
                break;
            case NORTH:
                this.pos = new Position(this.getActualPosition().getX()-1, this.getActualPosition().getY());
                break;
            case SOUTH:
                this.pos = new Position(this.getActualPosition().getX()+1, this.getActualPosition().getY());
                break;
        }
       
        if ( !this.arena.isValidPosition(this.pos) ) {
            this.pos = this.lastPos;
        } 
    }
    
    @Override
    public void whoAmI(){
        System.out.println("I am an animal!");
    }
    
    //-------
    //Osztály szintű metódusok:
    
    /*
        Egy adott robot pozíciójának lekérdezése:
    */
    public static Position getEnemysPostion(BaseEntity entity) {
        return entity.getActualPosition();
    }
    
    /*
        Adott robothoz tartozó aktuális páncél lekérdezése:
    */
    public static int getEnemysArmor(BaseEntity entity) {
        return entity.getActualArmor();
    }
    
    /*
        Robotok pozíciójának ellenőrzése:
    */
    public static boolean areOnTheSameField(BaseEntity a, BaseEntity b) {
        return ( a.getActualPosition().equals(b.getActualPosition()) );
    }
}
