package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.actions.DoAction;
import ChimeraMonsters.patches.CustomIntentPatches;
import ChimeraMonsters.util.CalcHelper;
import ChimeraMonsters.util.IntentManager;
import ChimeraMonsters.util.IntentPayload;
import ChimeraMonsters.util.Wiz;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.NonStackablePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

public class HarbingerPower extends AbstractEasyPower implements NonStackablePower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(HarbingerPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private final DamageInfo info;
    private final int baseDamage;

    public HarbingerPower(AbstractCreature owner, int baseDamage) {
        super(POWER_ID, NAME, PowerType.BUFF, true, owner, 2);
        this.baseDamage = baseDamage;
        this.info = new DamageInfo(owner, baseDamage);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        if (info == null) {
            description = "???";
            return;
        }
        if (amount > 1) {
            description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
        } else if (amount == 1) {
            description = DESCRIPTIONS[2];
        } else {
            description = DESCRIPTIONS[3];
        }
        description += info.base + DESCRIPTIONS[4] + baseDamage + DESCRIPTIONS[5];
    }

    @Override
    public void onInitialApplication() {
        if (!(owner instanceof AbstractMonster)) return;
        IntentPayload pl = new IntentPayload();
        pl.owner = (AbstractMonster) owner;
        pl.fields.move = new EnemyMoveInfo((byte) -1, AbstractMonster.Intent.UNKNOWN, -1, 0, false);
        pl.damage = info;
        pl.takeTurnFunc = thiz -> {
            if (HarbingerPower.this.amount == -1) {
                addToBot(new AnimateSlowAttackAction(owner));
                addToBot(new DamageAction(AbstractDungeon.player, thiz.damage, AbstractGameAction.AttackEffect.FIRE));
                Wiz.forAllMonstersLiving(monster -> {
                    if (monster == thiz.owner) return;
                    DamageInfo temp = new DamageInfo(thiz.owner, CalcHelper.monsterToMonsterDamage(thiz.owner, monster, thiz.damage.base));
                    addToBot(new DamageAction(monster, temp, AbstractGameAction.AttackEffect.FIRE));
                });
                DamageInfo temp = new DamageInfo(thiz.owner, CalcHelper.monsterToMonsterDamage(thiz.owner, thiz.owner, thiz.damage.base));
                addToBot(new DamageAction(thiz.owner, temp, AbstractGameAction.AttackEffect.FIRE));
            }
            addToBot(new DoAction(() -> {
                if (HarbingerPower.this.amount > 0) {
                    HarbingerPower.this.amount--;
                    if (HarbingerPower.this.amount == 0) {
                        HarbingerPower.this.amount = -1;
                        HarbingerPower.this.startFlashing();
                        thiz.fields.move.intent = CustomIntentPatches.CHIMERA_MONSTERS_METEOR_ATTACK;
                        thiz.fields.move.baseDamage = thiz.damage.base;
                    }
                } else {
                    thiz.damage.base += HarbingerPower.this.baseDamage;
                    thiz.fields.move.baseDamage = thiz.damage.base;
                }
                HarbingerPower.this.updateDescription();
            }));
        };
        IntentManager.addExtraIntent((AbstractMonster) owner, pl);
    }
}
