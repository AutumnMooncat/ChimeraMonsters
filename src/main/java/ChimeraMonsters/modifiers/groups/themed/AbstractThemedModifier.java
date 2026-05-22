package ChimeraMonsters.modifiers.groups.themed;

import ChimeraMonsters.ChimeraMonstersController;
import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.groups.AbstractMonsterGroupModifier;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.util.FormatHelper;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public abstract class AbstractThemedModifier extends AbstractMonsterGroupModifier {
    private static final String[] BASE_TEXT = CardCrawlGame.languagePack.getUIString(ChimeraMonstersMod.makeID(AbstractThemedModifier.class.getSimpleName())).TEXT;
    protected AbstractMonsterModifier monsterMod;

    public AbstractThemedModifier(String ID, AbstractMonsterModifier monsterMod) {
        super(ID);
        this.monsterMod = monsterMod;
    }

    @Override
    public ModifierRarity getModRarity() {
        return monsterMod.getModRarity();
    }

    @Override
    public String getModifierDescription() {
        return String.format(BASE_TEXT[0], FormatHelper.prefixWords(monsterMod.getModifierName(), "#y"));
    }

    @Override
    protected boolean validGroup(MonsterGroup group) {
        return allCanAccept(group, monsterMod);
    }

    @Override
    public void applyTo(MonsterGroup group) {
        for (AbstractMonster monster : group.monsters) {
            ChimeraMonstersController.applyModifier(monster, monsterMod);
        }
    }
}
