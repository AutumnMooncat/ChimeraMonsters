package ChimeraMonsters.modifiers.common;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.AbstractMonsterModifier;
import ChimeraMonsters.modifiers.GroupMonsterModifier;
import ChimeraMonsters.powers.VampiricPower;
import com.evacipated.cardcrawl.mod.stslib.damagemods.BindingHelper;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class VampiricMod extends GroupMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(VampiricMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final int PERCENTAGE = 50;

    @Override
    public ModifierRarity getModRarity() {
        return ModifierRarity.COMMON;
    }

    @Override
    public String getPrefix() {
        return TEXT[0];
    }

    @Override
    public String getSuffix() {
        return TEXT[1];
    }

    @Override
    public String getModifierDescription() {
        return TEXT[2];
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return true;
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new VampiricPower(monster, PERCENTAGE));
        monster.currentHealth = monster.maxHealth * 7/10;
    }

    @Override
    public String identifier() {
        return ID;
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new VampiricMod();
    }

    @Override
    public boolean isMonsterGroupValid(MonsterGroup monsterGroup){
        for(AbstractMonster m : monsterGroup.monsters){
            if(!validMonster(m, monsterGroup)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String fightName(MonsterGroup context) {
        return "Dracula's Brood"; //TODO: localization
    }
}
