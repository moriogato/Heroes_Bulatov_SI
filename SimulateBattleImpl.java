package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.programs.SimulateBattle;

public class SimulateBattleImpl implements SimulateBattle {

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        // Игра сама проводит симуляцию, этот метод вызывается для логирования
        // или дополнительной логики если нужно

        System.out.println("Battle simulation started");
        System.out.println("Player units: " + playerArmy.getUnits().size());
        System.out.println("Computer units: " + computerArmy.getUnits().size());
    }
}