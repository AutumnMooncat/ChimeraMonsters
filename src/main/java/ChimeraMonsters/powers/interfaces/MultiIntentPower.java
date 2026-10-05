package ChimeraMonsters.powers.interfaces;

import ChimeraMonsters.patches.MoveManipulationPatches;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

public interface MultiIntentPower {
    void preIntentRender(SpriteBatch sb);
    void postIntentRender(SpriteBatch sb);

    default Hitbox intentHitbox(AbstractCreature creature) {
        if (creature instanceof AbstractMonster) {
            return ((AbstractMonster) creature).intentHb;
        }
        return null;
    }

    default void offsetIntentHitbox(AbstractCreature creature, float dx, float dy) {
        Hitbox hb = intentHitbox(creature);
        if (hb == null) {
            return;
        }
        hb.move(hb.cX + dx * Settings.xScale, hb.cY + dy * Settings.yScale);
    }

    default void createIntent(AbstractCreature creature) {
        if (creature instanceof AbstractMonster) {
            float origAlpha = ((AbstractMonster) creature).intentAlpha;
            float origAlphaTarget = ((AbstractMonster) creature).intentAlphaTarget;
            float origTimer = ReflectionHacks.getPrivate(creature, AbstractMonster.class, "intentParticleTimer");
            ((AbstractMonster) creature).createIntent();
            ((AbstractMonster) creature).intentAlpha = origAlpha;
            ((AbstractMonster) creature).intentAlphaTarget = origAlphaTarget;
            ReflectionHacks.setPrivate(creature, AbstractMonster.class, "intentParticleTimer", origTimer);
        }
    }

    default float angleCheck(EnemyMoveInfo info, float angle) {
        if (info.intent == AbstractMonster.Intent.DEBUFF || info.intent == AbstractMonster.Intent.STRONG_DEBUFF) {
            return angle;
        }
        return 0f;
    }

    default void renderFullIntent(AbstractCreature creature, SpriteBatch sb, EnemyMoveInfo info) {
        renderFullIntent(creature, sb, info, 0);
    }

    // TODO - Refactor extra intents to not require interceptor logic
    default void renderFullIntent(AbstractCreature creature, SpriteBatch sb, EnemyMoveInfo info, float angle) {
        if (!(creature instanceof AbstractMonster)) return;
        EnemyMoveInfo curr = MoveManipulationPatches.getMove(creature);
        MoveManipulationPatches.setMove(creature, info);
        createIntent(creature);
        float origAngle = ReflectionHacks.getPrivate(creature, AbstractMonster.class, "intentAngle");
        ReflectionHacks.setPrivate(creature, AbstractMonster.class, "intentAngle", angle);
        renderFullIntent(creature, sb);
        ReflectionHacks.setPrivate(creature, AbstractMonster.class, "intentAngle", origAngle);
        MoveManipulationPatches.setMove(creature, curr);
        createIntent(creature);
    }

    default void renderFullIntent(AbstractCreature creature, SpriteBatch sb) {
        renderIntentVfxBehind(creature, sb);
        renderIntentImage(creature, sb);
        renderIntentVfxAfter(creature, sb);
        renderDamageRange(creature, sb);
    }

    default void renderIntentVfxBehind(AbstractCreature creature, SpriteBatch sb) {
        if (creature instanceof AbstractMonster) {
            ReflectionHacks.privateMethod(AbstractMonster.class, "renderIntentVfxBehind", SpriteBatch.class).invoke(creature, sb);
        }
    }

    default void renderIntentImage(AbstractCreature creature, SpriteBatch sb) {
        if (creature instanceof AbstractMonster) {
            ReflectionHacks.privateMethod(AbstractMonster.class, "renderIntent", SpriteBatch.class).invoke(creature, sb);
        }
    }

    default void renderIntentVfxAfter(AbstractCreature creature, SpriteBatch sb) {
        if (creature instanceof AbstractMonster) {
            ReflectionHacks.privateMethod(AbstractMonster.class, "renderIntentVfxAfter", SpriteBatch.class).invoke(creature, sb);
        }
    }

    default void renderDamageRange(AbstractCreature creature, SpriteBatch sb) {
        if (creature instanceof AbstractMonster) {
            ReflectionHacks.privateMethod(AbstractMonster.class, "renderDamageRange", SpriteBatch.class).invoke(creature, sb);
        }
    }
}
