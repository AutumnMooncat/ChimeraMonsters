package ChimeraMonsters.util;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.patches.MonsterFields;
import ChimeraMonsters.patches.MonsterGroupFields;
import basemod.BaseMod;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MonsterSpawnHelper {
    public static Class<?> unbox(Class<?> clazz) {
        if (Boolean.class.equals(clazz)) return boolean.class;
        if (Byte.class.equals(clazz)) return byte.class;
        if (Character.class.equals(clazz)) return char.class;
        if (Short.class.equals(clazz)) return short.class;
        if (Integer.class.equals(clazz)) return int.class;
        if (Long.class.equals(clazz)) return long.class;
        if (Float.class.equals(clazz)) return float.class;
        if (Double.class.equals(clazz)) return double.class;
        if (Void.class.equals(clazz)) return void.class;
        return clazz;
    }

    public static List<AbstractMonster> getSplitMonsters(AbstractMonster original) {
        return getSplitMonsters(original, MonsterGroupFields.encounterID.get(AbstractDungeon.getCurrRoom().monsters));
    }

    public static List<AbstractMonster> getSplitMonsters(AbstractMonster original, String encounterID) {
        ArrayList<AbstractMonster> splitMonsters = new ArrayList<>();
        Object[] args = MonsterFields.rawCtorArgs.get(original);
        if (args != null) {
            Class<?>[] gotTypes = Arrays.stream(args).map(o -> unbox(o.getClass())).toArray(Class<?>[]::new);
            for (Constructor<?> ctor : original.getClass().getDeclaredConstructors()) {
                Class<?>[] neededTypes = ctor.getParameterTypes();
                if (Arrays.deepEquals(gotTypes, neededTypes)) {
                    try {
                        AbstractMonster clone1 = (AbstractMonster) ctor.newInstance(args);
                        AbstractMonster clone2 = (AbstractMonster) ctor.newInstance(args);
                        clone1.maxHealth = clone1.currentHealth = original.currentHealth;
                        clone1.drawX = original.drawX - 100f * Settings.xScale;
                        clone1.drawY = original.drawY;
                        clone2.maxHealth = clone2.currentHealth = original.currentHealth;
                        clone2.drawX = original.drawX + 100f * Settings.xScale;
                        clone2.drawY = original.drawY;
                        splitMonsters.add(clone1);
                        splitMonsters.add(clone2);
                        return splitMonsters;
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignored) {}
                }
            }
        }

        int tries=0;
        do {
            MonsterGroup group = BaseMod.getMonster(encounterID);
            if(group==null){
                group = MonsterHelper.getEncounter(encounterID);
            }

            tries++;
            for (AbstractMonster monster : group.monsters) {
                if (monster == null) {
                    ChimeraMonstersMod.logger.warn("Got null monster from {} -> {}", original.name, encounterID);
                    continue;
                }
                if (original.getClass().equals(monster.getClass()) && splitMonsters.size() < 2) {
                    monster.maxHealth=original.currentHealth;
                    monster.currentHealth=monster.maxHealth;
                    monster.drawX= (float) (original.drawX+(Math.pow(-1,(splitMonsters.size()%2+1))*100f*Settings.xScale));
                    monster.drawY=original.drawY;
                    splitMonsters.add(monster);
                }
            }
        } while (splitMonsters.size()<2 && tries < 100);
        if(splitMonsters.size()<2){
            ChimeraMonstersMod.logger.warn("Could not find split monsters");
        }
        return splitMonsters;
    }
}
