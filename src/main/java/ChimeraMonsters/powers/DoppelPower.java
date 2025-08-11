package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.patches.ActionCapturePatch;
import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import ChimeraMonsters.util.Wiz;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.unique.VampireDamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

import java.util.ArrayList;

public class DoppelPower extends AbstractInternalLogicPower implements IntentInterceptingPower, RenderModifierPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(DoppelPower.class.getSimpleName());

    public DoppelPower(AbstractCreature owner, int amount) {
        super(POWER_ID, owner, amount);
        priority = -5;
    }

    @Override
    public float interceptRate(EnemyMoveInfo intendedMove) {
        return intendedMove.baseDamage>-1 ? 1 : 0;
    }

    @Override
    public void setInterceptIntent(EnemyMoveInfo replacedMove) {
        int multi = replacedMove.multiplier == 0 ? 2 : replacedMove.multiplier*2;
        EnemyMoveInfo newMove = new EnemyMoveInfo(replacedMove.nextMove, replacedMove.intent, replacedMove.baseDamage,multi, true);
        setMove(owner,newMove);
    }

    @Override
    public boolean performIntercept() {
        if (owner instanceof AbstractMonster) {
            AbstractMonster ownerMon = (AbstractMonster) owner;
            ActionCapturePatch.doCapture = true;
            ownerMon.takeTurn();
            ArrayList<AbstractGameAction> captured = new ArrayList<>(ActionCapturePatch.capturedActions);
            ActionCapturePatch.clear();
            for (AbstractGameAction action : captured) {
                if (action instanceof DamageAction && action.target == AbstractDungeon.player) {
                    DamageInfo di = ReflectionHacks.getPrivate(action, DamageAction.class, "info");
                    addToBot(new DamageAction(Wiz.adp(), di, action.attackEffect));
                    addToBot(new DamageAction(Wiz.adp(), di, action.attackEffect));
                } else if (action instanceof VampireDamageAction && action.target == AbstractDungeon.player) {
                    DamageInfo di = ReflectionHacks.getPrivate(action, VampireDamageAction.class, "info");
                    addToBot(new VampireDamageAction(Wiz.adp(), di, action.attackEffect));
                    addToBot(new VampireDamageAction(Wiz.adp(), di, action.attackEffect));
                } else {
                    addToBot(action);
                }
            }
        }
        return true;
    }

    @Override
    public boolean setFollowupInterceptionIntent() {
        return false;
    }

    @Override
    public void onRender(SpriteBatch sb, TextureRegion tex) {
        Color origColor = sb.getColor();
        sb.setColor(new Color(0.85f,0.8f,0.85f,1f));
        render(sb, tex, -25, 5);
        sb.setColor(Color.WHITE);
        render(sb, tex, 25, -5);
        sb.setColor(origColor);
    }
}
