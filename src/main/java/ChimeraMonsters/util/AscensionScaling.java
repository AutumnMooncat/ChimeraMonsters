package ChimeraMonsters.util;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public interface AscensionScaling {
    default <T> T scaleDeadlier(AbstractMonster monster, T base, T boosted) {
        switch (monster.type) {
            case BOSS:
                return AbstractDungeon.ascensionLevel >= 4 ? boosted : base;
            case ELITE:
                return AbstractDungeon.ascensionLevel >= 3 ? boosted : base;
            default:
                return AbstractDungeon.ascensionLevel >= 2 ? boosted : base;
        }
    }

    default <T extends Number> Number scaleDeadlier(AbstractMonster monster, T baseMin, T baseMax, T boostedMin, T boostedMax) {
        T min, max;
        switch (monster.type) {
            case BOSS:
                min = AbstractDungeon.ascensionLevel >= 4 ? boostedMin : baseMin;
                max = AbstractDungeon.ascensionLevel >= 4 ? boostedMax : baseMax;
                break;
            case ELITE:
                min = AbstractDungeon.ascensionLevel >= 3 ? boostedMin : baseMin;
                max = AbstractDungeon.ascensionLevel >= 3 ? boostedMax : baseMax;
                break;
            default:
                min = AbstractDungeon.ascensionLevel >= 2 ? boostedMin : baseMin;
                max = AbstractDungeon.ascensionLevel >= 2 ? boostedMax : baseMax;
        }
        if (min instanceof Float || min instanceof Double || max instanceof Float || max instanceof Double) {
            return AbstractDungeon.monsterHpRng.random(min.floatValue(), max.floatValue());
        }
        return AbstractDungeon.monsterHpRng.random(min.longValue(), max.longValue());
    }

    default <T> T scaleTougher(AbstractMonster monster, T base, T boosted) {
        switch (monster.type) {
            case BOSS:
                return AbstractDungeon.ascensionLevel >= 9 ? boosted : base;
            case ELITE:
                return AbstractDungeon.ascensionLevel >= 8 ? boosted : base;
            default:
                return AbstractDungeon.ascensionLevel >= 7 ? boosted : base;
        }
    }

    default <T extends Number> Number scaleTougher(AbstractMonster monster, T baseMin, T baseMax, T boostedMin, T boostedMax) {
        T min, max;
        switch (monster.type) {
            case BOSS:
                min = AbstractDungeon.ascensionLevel >= 9 ? boostedMin : baseMin;
                max = AbstractDungeon.ascensionLevel >= 9 ? boostedMax : baseMax;
                break;
            case ELITE:
                min = AbstractDungeon.ascensionLevel >= 8 ? boostedMin : baseMin;
                max = AbstractDungeon.ascensionLevel >= 8 ? boostedMax : baseMax;
                break;
            default:
                min = AbstractDungeon.ascensionLevel >= 7 ? boostedMin : baseMin;
                max = AbstractDungeon.ascensionLevel >= 7 ? boostedMax : baseMax;
        }
        if (min instanceof Float || min instanceof Double || max instanceof Float || max instanceof Double) {
            return AbstractDungeon.monsterHpRng.random(min.floatValue(), max.floatValue());
        }
        return AbstractDungeon.monsterHpRng.random(min.longValue(), max.longValue());
    }

    default <T> T scaleEvents(T base, T boosted) {
        return AbstractDungeon.ascensionLevel >= 15 ? boosted : base;
    }

    default <T> T scaleAbilities(AbstractMonster monster, T base, T boosted) {
        switch (monster.type) {
            case BOSS:
                return AbstractDungeon.ascensionLevel >= 19 ? boosted : base;
            case ELITE:
                return AbstractDungeon.ascensionLevel >= 18 ? boosted : base;
            default:
                return AbstractDungeon.ascensionLevel >= 17 ? boosted : base;
        }
    }

    default <T extends Number> Number scaleAbilities(AbstractMonster monster, T baseMin, T baseMax, T boostedMin, T boostedMax) {
        T min, max;
        switch (monster.type) {
            case BOSS:
                min = AbstractDungeon.ascensionLevel >= 19 ? boostedMin : baseMin;
                max = AbstractDungeon.ascensionLevel >= 19 ? boostedMax : baseMax;
                break;
            case ELITE:
                min = AbstractDungeon.ascensionLevel >= 18 ? boostedMin : baseMin;
                max = AbstractDungeon.ascensionLevel >= 18 ? boostedMax : baseMax;
                break;
            default:
                min = AbstractDungeon.ascensionLevel >= 17 ? boostedMin : baseMin;
                max = AbstractDungeon.ascensionLevel >= 17 ? boostedMax : baseMax;
        }
        if (min instanceof Float || min instanceof Double || max instanceof Float || max instanceof Double) {
            return AbstractDungeon.monsterHpRng.random(min.floatValue(), max.floatValue());
        }
        return AbstractDungeon.monsterHpRng.random(min.longValue(), max.longValue());
    }
}
