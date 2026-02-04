package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

public class GeneratePresetImpl implements GeneratePreset {

    private static final int MAX_POINTS = 1500;
    private static final int MAX_UNITS_PER_TYPE = 11;

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        if (unitList == null || unitList.isEmpty()) {
            return new Army(new ArrayList<>());
        }

        // Оптимальный алгоритм с учетом ограничений
        List<Unit> armyUnits = createOptimalArmy(unitList, maxPoints);

        return new Army(armyUnits);
    }

    private List<Unit> createOptimalArmy(List<Unit> unitList, int maxPoints) {
        List<Unit> army = new ArrayList<>();
        int remainingPoints = maxPoints;

        // Сортируем юниты по эффективности (атака+здоровье на стоимость)
        List<Unit> sortedUnits = new ArrayList<>(unitList);
        sortedUnits.sort((u1, u2) -> {
            double eff1 = (u1.getBaseAttack() + u1.getHealth()) / (double) u1.getCost();
            double eff2 = (u2.getBaseAttack() + u2.getHealth()) / (double) u2.getCost();
            return Double.compare(eff2, eff1);
        });

        Map<String, Integer> typeCount = new HashMap<>();

        // Шаг 1: Берем самых эффективных юнитов
        for (Unit unit : sortedUnits) {
            if (remainingPoints <= 0) break;

            String type = unit.getUnitType();
            int currentCount = typeCount.getOrDefault(type, 0);

            if (currentCount >= MAX_UNITS_PER_TYPE) continue;

            int unitCost = unit.getCost();
            if (unitCost > remainingPoints) continue;

            // Сколько можем взять этого типа
            int maxByType = MAX_UNITS_PER_TYPE - currentCount;
            int maxByBudget = remainingPoints / unitCost;
            int toTake = Math.min(maxByType, maxByBudget);

            for (int i = 0; i < toTake; i++) {
                army.add(unit);
                remainingPoints -= unitCost;
                typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);

                System.out.println("Added " + army.size() + " unit");

                if (remainingPoints <= 0) break;
            }
        }

        // Шаг 2: Добираем дешевыми юнитами если остались очки
        if (remainingPoints > 0) {
            sortedUnits.sort(Comparator.comparingInt(Unit::getCost));

            for (Unit unit : sortedUnits) {
                if (remainingPoints <= 0) break;

                String type = unit.getUnitType();
                int currentCount = typeCount.getOrDefault(type, 0);

                while (currentCount < MAX_UNITS_PER_TYPE &&
                        remainingPoints >= unit.getCost()) {
                    army.add(unit);
                    remainingPoints -= unit.getCost();
                    currentCount++;
                    typeCount.put(type, currentCount);

                    System.out.println("Added " + army.size() + " unit");
                }
            }
        }

        System.out.println("Used points: " + (maxPoints - remainingPoints));
        return army;
    }
}