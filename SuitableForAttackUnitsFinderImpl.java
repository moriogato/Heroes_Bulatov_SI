package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.*;

public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        List<Unit> suitableUnits = new ArrayList<>();

        // Обрабатываем каждый ряд (3 ряда всего)
        for (List<Unit> row : unitsByRow) {
            if (row == null || row.isEmpty()) {
                continue;
            }

            // Фильтруем только живых юнитов
            List<Unit> aliveUnitsInRow = new ArrayList<>();
            for (Unit unit : row) {
                if (unit != null && unit.isAlive()) {
                    aliveUnitsInRow.add(unit);
                }
            }

            if (aliveUnitsInRow.isEmpty()) {
                continue;
            }

            if (isLeftArmyTarget) {
                // Атакуем левую армию (компьютер)
                // Берем самого левого юнита (первого в списке)
                // Проверяем, не закрыт ли он слева
                Unit leftmost = aliveUnitsInRow.get(0);
                suitableUnits.add(leftmost);
            } else {
                // Атакуем правую армию (игрок)
                // Берем самого правого юнита (последнего в списке)
                // Проверяем, не закрыт ли он справа
                Unit rightmost = aliveUnitsInRow.get(aliveUnitsInRow.size() - 1);
                suitableUnits.add(rightmost);
            }
        }

        return suitableUnits;
    }

    // Вспомогательный метод для определения, закрыт ли юнит
    private boolean isUnitBlocked(Unit unit, List<Unit> row, boolean checkLeft) {
        if (row == null || row.isEmpty()) return false;

        int unitIndex = row.indexOf(unit);
        if (unitIndex == -1) return false;

        if (checkLeft) {
            // Проверяем, есть ли живой юнит слева
            for (int i = unitIndex - 1; i >= 0; i--) {
                Unit leftUnit = row.get(i);
                if (leftUnit != null && leftUnit.isAlive()) {
                    return true; // Закрыт слева
                }
            }
        } else {
            // Проверяем, есть ли живой юнит справа
            for (int i = unitIndex + 1; i < row.size(); i++) {
                Unit rightUnit = row.get(i);
                if (rightUnit != null && rightUnit.isAlive()) {
                    return true; // Закрыт справа
                }
            }
        }

        return false; // Не закрыт
    }
}
