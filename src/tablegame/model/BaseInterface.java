package tablegame.model;

public abstract interface BaseInterface {
    
    public abstract String getName();
    
    public abstract void setName(String name);
    
    public abstract Position getPosition();
    
    public abstract void setPosition(int x, int y);
    
    public abstract int getMaxArmor();
    
    public abstract void setMaxArmor(int armor);
    
    public abstract int getActualArmor();
    
    public abstract void setActualArmor(int armor);
    
    public abstract int getDamage();
    
    public abstract void setDamage(int dmg);
    
    public abstract void attack();
    
    public abstract void defend();
    
    public abstract void waitNextRound();
    
    public abstract void move(Direction direction);
    
    public abstract int[] getArenaSize();
    
    public abstract void whoAmI();
}
