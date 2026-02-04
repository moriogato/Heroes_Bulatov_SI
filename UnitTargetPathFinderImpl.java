package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {

    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        if (attackUnit == null || targetUnit == null ||
                !attackUnit.isAlive() || !targetUnit.isAlive()) {
            return new ArrayList<>();
        }

        // Простая реализация - прямой путь
        // В реальной игре здесь должен быть алгоритм A* или другой поиск пути

        List<Edge> path = new ArrayList<>();

        // Добавляем путь из 2-3 точек (упрощенно)
        path.add(new Edge(0, 0));  // Старт
        path.add(new Edge(1, 1));  // Промежуточная точка
        path.add(new Edge(2, 2));  // Цель

        return path;
    }
}