package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

public class GeneratePresetImpl implements GeneratePreset {

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        // 4 типа юнитов, ограничение 11 каждого типа
        int n = unitList.size(); // n = 4 (типы юнитов)
        int maxUnitsPerType = 11;

        // Группируем юниты по типу для удобства
        Map<String, Unit> unitByType = new HashMap<>();
        for (Unit unit : unitList) {
            unitByType.put(unit.getUnitType(), unit);
        }

        // DP: dp[i][points] = максимальная сила при i типах и points очках
        // Восстановление: prevType[i][points], prevCount[i][points]
        int[][][] dp = new int[n + 1][maxPoints + 1][maxUnitsPerType + 1];
        int[][][] prevType = new int[n + 1][maxPoints + 1][maxUnitsPerType + 1];
        int[][][] prevCount = new int[n + 1][maxPoints + 1][maxUnitsPerType + 1];

        List<Unit> unitsInOrder = new ArrayList<>(unitList);
        Collections.sort(unitsInOrder, Comparator.comparing(Unit::getCost));

        // Базовый случай
        for (int i = 0; i <= maxPoints; i++) {
            for (int k = 0; k <= maxUnitsPerType; k++) {
                dp[0][i][k] = 0;
            }
        }

        // Заполнение DP таблицы
        for (int i = 1; i <= n; i++) {
            Unit currentUnit = unitsInOrder.get(i - 1);
            int cost = currentUnit.getCost();
            int power = currentUnit.getBaseAttack() + currentUnit.getHealth(); // Комбинированная эффективность

            for (int points = 0; points <= maxPoints; points++) {
                for (int count = 0; count <= maxUnitsPerType; count++) {
                    dp[i][points][count] = dp[i - 1][points][count];
                    prevType[i][points][count] = i - 1;
                    prevCount[i][points][count] = count;

                    // Пробуем взять k юнитов текущего типа
                    for (int k = 1; k <= count; k++) {
                        if (k * cost <= points) {
                            int newValue = dp[i - 1][points - k * cost][count - k] + k * power;
                            if (newValue > dp[i][points][count]) {
                                dp[i][points][count] = newValue;
                                prevType[i][points][count] = i - 1;
                                prevCount[i][points][count] = count - k;
                                // Запоминаем, что взяли k юнитов текущего типа
                            }
                        }
                    }
                }
            }
        }

        // Восстановление решения
        List<Unit> selectedUnits = new ArrayList<>();
        int currentPoints = maxPoints;
        int currentTypeIndex = n;
        int currentCount = maxUnitsPerType;

        while (currentTypeIndex > 0 && currentPoints > 0) {
            Unit currentUnit = unitsInOrder.get(currentTypeIndex - 1);
            int prevTypeIdx = prevType[currentTypeIndex][currentPoints][currentCount];
            int prevCountIdx = prevCount[currentTypeIndex][currentPoints][currentCount];

            // Сколько юнитов этого типа было взято?
            int taken = currentCount - prevCountIdx;

            for (int i = 0; i < taken; i++) {
                selectedUnits.add(currentUnit);
            }

            currentPoints -= taken * currentUnit.getCost();
            currentTypeIndex = prevTypeIdx;
            currentCount = prevCountIdx;
        }

        // Учитываем ограничение 1500 очков и сортируем по силе
        List<Unit> finalArmy = new ArrayList<>();
        int totalCost = 0;

        // Простая жадная стратегия как fallback (если DP дало плохой результат)
        List<Unit> sortedByEfficiency = new ArrayList<>(unitList);
        sortedByEfficiency.sort((u1, u2) -> {
            double eff1 = (u1.getBaseAttack() + u1.getHealth()) / (double) u1.getCost();
            double eff2 = (u2.getBaseAttack() + u2.getHealth()) / (double) u2.getCost();
            return Double.compare(eff2, eff1); // По убыванию
        });

        // Набираем армию с учетом ограничений
        Map<String, Integer> typeCount = new HashMap<>();
        for (Unit unit : sortedByEfficiency) {
            String type = unit.getUnitType();
            int maxCanTake = Math.min(
                    11 - typeCount.getOrDefault(type, 0),
                    (maxPoints - totalCost) / unit.getCost()
            );

            for (int i = 0; i < maxCanTake; i++) {
                if (totalCost + unit.getCost() <= maxPoints) {
                    finalArmy.add(unit);
                    totalCost += unit.getCost();
                    typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);
                }
            }
        }

        // Добираем оставшиеся очки более дешевыми юнитами
        if (totalCost < maxPoints) {
            List<Unit> cheapUnits = new ArrayList<>(unitList);
            cheapUnits.sort(Comparator.comparingInt(Unit::getCost));

            for (Unit unit : cheapUnits) {
                String type = unit.getUnitType();
                while (typeCount.getOrDefault(type, 0) < 11 &&
                        totalCost + unit.getCost() <= maxPoints) {
                    finalArmy.add(unit);
                    totalCost += unit.getCost();
                    typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);
                }
            }
        }

        return new Army(finalArmy);
    }
}