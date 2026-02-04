package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.*;

public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        List<Unit> suitableUnits = new ArrayList<>();

        if (unitsByRow == null || unitsByRow.isEmpty()) {
            return suitableUnits;
        }

        // Для каждого ряда находим подходящие цели
        for (List<Unit> row : unitsByRow) {
            if (row == null || row.isEmpty()) continue;

            // Фильтруем живых юнитов
            List<Unit> aliveUnits = new ArrayList<>();
            for (Unit unit : row) {
                if (unit != null && unit.isAlive()) {
                    aliveUnits.add(unit);
                }
            }

            if (aliveUnits.isEmpty()) continue;

            // Если цель слева - берем самого левого
            // Если цель справа - берем самого правого
            if (isLeftArmyTarget) {
                // Атакуем левую армию - берем первого живого слева
                suitableUnits.add(aliveUnits.get(0));
            } else {
                // Атакуем правую армию - берем последнего живого справа
                suitableUnits.add(aliveUnits.get(aliveUnits.size() - 1));
            }
        }

        return suitableUnits;
    }
}