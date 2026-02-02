package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {
    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        if (attackUnit == null || targetUnit == null || !attackUnit.isAlive() || !targetUnit.isAlive()) {
            return new ArrayList<>();
        }

        // Получаем координаты
        int startX = attackUnit.getX();
        int startY = attackUnit.getY();
        int targetX = targetUnit.getX();
        int targetY = targetUnit.getY();

        // Создаем сет занятых клеток
        Set<String> occupiedCells = new HashSet<>();
        for (Unit unit : existingUnitList) {
            if (unit.isAlive() && unit != attackUnit) {
                occupiedCells.add(unit.getX() + "," + unit.getY());
            }
        }

        // Алгоритм A* для поиска пути
        return aStarSearch(startX, startY, targetX, targetY, occupiedCells);
    }

    private List<Edge> aStarSearch(int startX, int startY, int targetX, int targetY, Set<String> obstacles) {
        // Приоритетная очередь для открытых узлов
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.fCost));

        // Мапы для отслеживания узлов
        Map<String, Node> allNodes = new HashMap<>();

        // Начальный узел
        Node startNode = new Node(startX, startY);
        startNode.gCost = 0;
        startNode.hCost = heuristic(startX, startY, targetX, targetY);
        startNode.fCost = startNode.gCost + startNode.hCost;

        openSet.add(startNode);
        allNodes.put(startX + "," + startY, startNode);

        // Возможные направления движения (включая диагонали)
        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1},           {0, 1},
                {1, -1},  {1, 0},  {1, 1}
        };

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            // Если достигли цели
            if (current.x == targetX && current.y == targetY) {
                return reconstructPath(current);
            }

            // Исследуем соседей
            for (int[] dir : directions) {
                int newX = current.x + dir[0];
                int newY = current.y + dir[1];

                // Проверяем границы поля (предполагаем 27x15 по описанию координат)
                if (newX < 0 || newX >= 27 || newY < 0 || newY >= 15) {
                    continue;
                }

                // Проверяем препятствия
                if (obstacles.contains(newX + "," + newY)) {
                    continue;
                }

                String key = newX + "," + newY;
                Node neighbor = allNodes.get(key);
                if (neighbor == null) {
                    neighbor = new Node(newX, newY);
                    allNodes.put(key, neighbor);
                }

                // Стоимость перехода: 1 для ортогональных, 1.4 для диагональных
                int moveCost = (Math.abs(dir[0]) == 1 && Math.abs(dir[1]) == 1) ? 14 : 10;
                int tentativeGCost = current.gCost + moveCost;

                if (tentativeGCost < neighbor.gCost) {
                    neighbor.cameFrom = current;
                    neighbor.gCost = tentativeGCost;
                    neighbor.hCost = heuristic(newX, newY, targetX, targetY);
                    neighbor.fCost = neighbor.gCost + neighbor.hCost;

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        // Путь не найден
        return new ArrayList<>();
    }

    private int heuristic(int x1, int y1, int x2, int y2) {
        // Манхэттенское расстояние
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    private List<Edge> reconstructPath(Node endNode) {
        List<Edge> path = new ArrayList<>();
        Node current = endNode;

        while (current != null) {
            path.add(0, new Edge(current.x, current.y));
            current = current.cameFrom;
        }

        return path;
    }

    // Вспомогательный класс для узлов A*
    private static class Node {
        int x, y;
        int gCost = Integer.MAX_VALUE; // Стоимость от старта
        int hCost; // Эвристическая стоимость до цели
        int fCost; // Общая стоимость
        Node cameFrom;

        Node(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}