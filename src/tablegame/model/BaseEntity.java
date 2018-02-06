package tablegame.model;

import static tablegame.model.Action.*;

public class BaseEntity implements BaseInterface {
    private String name;
    private Action lastAction;
    private Position lastPos;
    private Position pos;
    private Arena arena;
    private int maxArmor;
    private int actualArmor;
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
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public Position getPosition() {
        return this.pos;
    }
    
    @Override
    public void setPosition(int x, int y) {
        this.pos.setX(x);
        this.pos.setY(y);
    }
    
    @Override
    public int getMaxArmor() {
        return this.maxArmor;
    }
            
    @Override
    public void setMaxArmor(int armor) {
        this.maxArmor = armor;
    }
    
    @Override
    public int getActualArmor() {
        return this.actualArmor;
    }
            
    @Override
    public void setActualArmor(int armor) {
        this.actualArmor = armor;
    }
    
    @Override
    public int getDamage(){
        return this.damage;
    }
    
    @Override
    public void setDamage(int dmg){
        this.damage = dmg;
    }
    
    @Override
    public int[] getArenaSize() {
        return this.arena.getSize();
    }
    
    @Override
    public void attack(){
        this.lastAction = ATTACK;
    }
    
    @Override
    public void defend(){
        this.lastAction = DEFEND;
    }
    
    @Override
    public void waitNextRound(){
        this.lastAction = WAIT;
    }
    
    /*
        X koord.: vízszintes mozgatás
        Y koord.: függőleges mozgatás
    */
    @Override
    public void move(Direction direction) {
        this.lastAction = MOVE;
        
        switch (direction) {
            case EAST: 
                if (this.getPosition().getY()+1 < this.arena.getSize()[1])
                    this.pos = new Position(this.getPosition().getX(), this.getPosition().getY()+1);
                break;
            case WEST: 
                if (this.getPosition().getY()-1 >= 0)
                    this.pos = new Position(this.getPosition().getX(), this.getPosition().getY()-1);
                break;
            case NORTH:
                if (this.getPosition().getX()-1 >= 0)
                    this.pos = new Position(this.getPosition().getX()-1, this.getPosition().getY());
                break;
            case SOUTH:
                if (this.getPosition().getX()+1 < this.arena.getSize()[0])
                    this.pos = new Position(this.getPosition().getX()+1, this.getPosition().getY());
                break;
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
        return entity.getPosition();
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
        return ( a.getPosition().equals(b.getPosition()) );
    }
}
