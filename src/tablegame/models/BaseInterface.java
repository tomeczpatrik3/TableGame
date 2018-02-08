package tablegame.models;

import tablegame.enums.Direction;
import tablegame.enums.Action;
import tablegame.utils.Position;

/*
    A megvalósításra váró függvények.
    A függvények leírása megtalálható a BaseEntity osztályban
*/
public abstract interface BaseInterface {
    String getName();
    
    void setName(String name);
    
    Position getActualPosition();
    
    void setActualPosition(int x, int y);
    
    Position getLastPosition();
    
    void setLastPosition(int x, int y);
    
    Action getLastAction();
    
    void setLastAction(Action act);
    
    int getMaxArmor();
    
    void setMaxArmor(int armor);
    
    int getActualArmor();
    
    void setActualArmor(int armor);
    
    int getDamage();
    
    void setDamage(int dmg);
    
    void attack(Direction direction);
    
    void defend(Direction direction);
    
    void waitNextRound();
    
    void move(Direction direction);
    
    void performMove(Direction direction);
    
    int[] getArenaSize();
    
    void restorePosition();
    
    void sufferDmg(int dmg);
    
    boolean isAlive();
    
    void whoAmI();
}
