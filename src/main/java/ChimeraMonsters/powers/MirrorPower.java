package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.actions.DoAction;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import ChimeraMonsters.util.IntentManager;
import ChimeraMonsters.util.IntentPayload;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

public class MirrorPower extends AbstractEasyPower implements RenderModifierPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(MirrorPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private boolean primed;

    public MirrorPower(AbstractCreature owner) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner,-1);
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public void atStartOfTurn() {
        primed = true;
    }

    @Override
    public void atEndOfRound() {
        primed = false;
        stopFlashing();
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (info.owner != null && info.owner != owner && info.type == DamageInfo.DamageType.NORMAL && !primed && owner instanceof AbstractMonster) {
            primed = true;
            flash();
            startFlashing();
            addToBot(new DoAction(() -> {
                IntentPayload pl = new IntentPayload();
                pl.owner = (AbstractMonster) owner;
                pl.handler = owner;
                pl.fields.move = new EnemyMoveInfo((byte) -1, AbstractMonster.Intent.ATTACK, info.output, 0, false);
                pl.damage = new DamageInfo(owner, info.output);
                pl.takeTurnFunc = thiz -> {
                    addToBot(new AnimateSlowAttackAction(thiz.owner));
                    addToBot(new DamageAction(AbstractDungeon.player, thiz.damage, thiz.damage.base > 10 ? AbstractGameAction.AttackEffect.BLUNT_HEAVY : AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                    addToBot(new DoAction(() -> IntentManager.removeExtraIntent(thiz.owner, thiz)));
                };
                IntentManager.addExtraIntent((AbstractMonster) owner, pl);
            }));
        }
        return damageAmount;
    }

    @Override
    public void onRender(SpriteBatch sb, TextureRegion tex) {
        render(sb, tex);
    }
}
