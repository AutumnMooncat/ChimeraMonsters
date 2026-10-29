package ChimeraMonsters.modifiers.groups.curated;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.monsters.OrbMaster;
import ChimeraMonsters.patches.MonsterFields;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.beyond.OrbWalker;

public class TheOrbMaster extends AbstractCuratedModifier {
    public static final String ID = ChimeraMonstersMod.makeID(TheOrbMaster.class.getSimpleName());

    public TheOrbMaster() {
        super(ID);
    }

    @Override
    public ModifierRarity getModRarity() {
        return ModifierRarity.COMMON;
    }

    @Override
    protected boolean validGroup(MonsterGroup group) {
        return group.monsters.size() == 1 && group.monsters.get(0) instanceof OrbWalker;
    }

    @Override
    public void applyTo(MonsterGroup group) {
        AbstractMonster mon = group.monsters.get(0);
        float x = MonsterFields.rawOffsetX.get(mon);
        float y = MonsterFields.rawOffsetY.get(mon);
        group.monsters.clear();
        group.monsters.add(new OrbMaster(x, y));
    }
}
