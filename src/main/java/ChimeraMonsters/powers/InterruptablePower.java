package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.actions.DoAction;
import ChimeraMonsters.patches.EnemyMoveInfoPatches;
import ChimeraMonsters.patches.MonsterModifierFieldPatches;
import ChimeraMonsters.patches.MoveManipulationPatches;
import com.evacipated.cardcrawl.mod.stslib.patches.NeutralPowertypePatch;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

public class InterruptablePower extends AbstractEasyPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(InterruptablePower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private final EnemyMoveInfo moveInfo;

    public InterruptablePower(AbstractCreature owner, EnemyMoveInfo moveInfo) {
        super(POWER_ID, NAME, NeutralPowertypePatch.NEUTRAL, true, owner, -1);
        this.moveInfo = moveInfo;
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (owner instanceof AbstractMonster && info.owner != null && damageAmount > 0 && info.type == DamageInfo.DamageType.NORMAL) {
            AbstractMonster ownerMon = (AbstractMonster) owner;
            addToTop(new DoAction(() -> {
                MonsterModifierFieldPatches.ModifierFields.interceptor.set(ownerMon, null);
                MoveManipulationPatches.setMove(ownerMon, moveInfo, true);
            }));
            addToTop(new RemoveSpecificPowerAction(owner, owner, this));
            flash();
        }

        return damageAmount;
    }

    @Override
    public void atEndOfRound() {
        addToTop(new RemoveSpecificPowerAction(owner, owner, this));
    }
}
