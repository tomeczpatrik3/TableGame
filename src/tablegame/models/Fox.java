package tablegame.models;

import tablegame.utils.Position;

public class Fox extends BaseEntity{
    public Fox(String name, Arena arena, Position pos, int armor) {
       super(name, arena, pos, armor);
       super.setDamage(2);
    }
    
    @Override
    public void whoAmI() {
        System.out.println("I am a fox!");
    }
}
