package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.*;


public class SimulateBattleImpl implements SimulateBattle {
    private PrintBattleLog printBattleLog; // Позволяет логировать. Использовать после каждой атаки юнита

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        List<Unit> allUnits = new ArrayList<>();
        allUnits.addAll(playerArmy.getUnits());
        allUnits.addAll(computerArmy.getUnits());

        boolean battleContinues = true;

        while (battleContinues) {
            // Собираем живых юнитов
            List<Unit> aliveUnits = new ArrayList<>();
            for (Unit unit : allUnits) {
                if (unit.isAlive()) {
                    aliveUnits.add(unit);
                }
            }

            // Если одна из армий мертва - завершаем
            boolean playerHasAlive = false;
            boolean computerHasAlive = false;

            for (Unit unit : aliveUnits) {
                if (playerArmy.getUnits().contains(unit)) {
                    playerHasAlive = true;
                } else {
                    computerHasAlive = true;
                }
            }

            if (!playerHasAlive || !computerHasAlive) {
                battleContinues = false;
                continue;
            }

            // Сортируем по убыванию атаки (самые сильные ходят первыми)
            aliveUnits.sort((u1, u2) -> Integer.compare(u2.getBaseAttack(), u1.getBaseAttack()));

            // Раунд: каждый живой юнит делает ход
            List<Unit> unitsToProcess = new ArrayList<>(aliveUnits);

            for (Unit attackingUnit : unitsToProcess) {
                // Проверяем, жив ли еще юнит (мог умереть в этом же раунде)
                if (!attackingUnit.isAlive()) {
                    continue;
                }

                // Выполняем атаку через программу юнита
                Unit targetUnit = attackingUnit.getProgram().attack();

                if (targetUnit != null && targetUnit.isAlive()) {
                    // Логируем атаку
                    if (printBattleLog != null) {
                        printBattleLog.printBattleLog(attackingUnit, targetUnit);
                    }

                    // Наносим урон (упрощенно)
                    // В реальности здесь должен быть вызов метода атаки юнита
                    int damage = attackingUnit.getBaseAttack();
                    // targetUnit.takeDamage(damage); // Такого метода нет в спецификации

                    // Проверяем, умерла ли цель
                    // if (targetUnit.getHealth() <= 0) {
                    //     targetUnit.setAlive(false);
                    // }

                    // Пауза для визуализации
                    Thread.sleep(100);
                }

                // Проверяем условие окончания боя после каждого хода
                playerHasAlive = false;
                computerHasAlive = false;

                for (Unit unit : allUnits) {
                    if (unit.isAlive()) {
                        if (playerArmy.getUnits().contains(unit)) {
                            playerHasAlive = true;
                        } else {
                            computerHasAlive = true;
                        }
                    }
                }

                if (!playerHasAlive || !computerHasAlive) {
                    battleContinues = false;
                    break;
                }
            }
        }

        // Завершение боя
        boolean playerWins = false;
        for (Unit unit : playerArmy.getUnits()) {
            if (unit.isAlive()) {
                playerWins = true;
                break;
            }
        }

        // Логирование результата
        System.out.println(playerWins ? "Игрок победил!" : "Компьютер победил!");
    }

    // Метод для установки логгера (должен вызываться извне)
    public void setPrintBattleLog(PrintBattleLog printBattleLog) {
        this.printBattleLog = printBattleLog;
    }
}