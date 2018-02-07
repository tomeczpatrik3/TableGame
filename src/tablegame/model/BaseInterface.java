package tablegame.model;

/*
    A megvalósításra váró függvények.
    A függvények leírása megtalálható a BaseEntity osztályban
*/
public abstract interface BaseInterface {
    public abstract String getName();
    
    public abstract void setName(String name);
    
    public abstract Position getActualPosition();
    
    public abstract void setActualPosition(int x, int y);
    
    public abstract Position getLastPosition();
    
    public abstract void setLastPosition(int x, int y);
    
    public abstract Action getLastAction();
    
    public abstract void setLastAction(Action act);
    
    public abstract int getMaxArmor();
    
    public abstract void setMaxArmor(int armor);
    
    public abstract int getActualArmor();
    
    public abstract void setActualArmor(int armor);
    
    public abstract int getDamage();
    
    public abstract void setDamage(int dmg);
    
    public abstract void attack(Direction direction);
    
    public abstract void defend(Direction direction);
    
    public abstract void waitNextRound();
    
    public abstract void move(Direction direction);
    
    public abstract void performMove(Direction direction);
    
    public abstract int[] getArenaSize();
    
    public abstract void restorePosition();
    
    public abstract void sufferDmg(int dmg);
    
    public abstract boolean getStatus();
    
    public abstract void whoAmI();
}
