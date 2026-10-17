package ChimeraMonsters.util;

import ChimeraMonsters.patches.MoveManipulationPatches;
import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.vfx.BobEffect;

import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class IntentPayload {
    public static final float SPACING = 64f * Settings.scale;
    public static final float MOVESTEP = 15f;

    public static class Fields {
        public AbstractMonster.Intent intent = AbstractMonster.Intent.DEBUG;
        public AbstractMonster.Intent tipIntent = AbstractMonster.Intent.DEBUG;
        public Texture intentImg = null;
        public Texture intentBg = null;
        public int intentDmg = -1;
        public int intentBaseDmg = -1;
        public int intentMultiAmt = 0;
        public boolean isMultiDmg = false;
        public byte nextMove = -1;
        public BobEffect bobEffect = new BobEffect();
        public EnemyMoveInfo move = null;
        public String moveName = null;
        public Hitbox intentHb = new Hitbox(64f * Settings.scale, 64f * Settings.scale);
        public PowerTip intentTip = new PowerTip();
        public Color intentColor = Color.WHITE.cpy();
        public float intentAlpha = 0f;
        public float intentAlphaTarget = 1f;
        public float intentParticleTimer = 0f;
        public float intentAngle = 0f;
    }

    public Consumer<IntentPayload> updateFunc = IntentPayload::updateCore;
    public BiConsumer<IntentPayload, SpriteBatch> renderFunc = IntentPayload::renderCore;
    public BiConsumer<IntentPayload, SpriteBatch> renderDamageFunc = IntentPayload::renderDamageCore;
    public Consumer<IntentPayload> takeTurnFunc = IntentPayload::takeTurnCore;
    public AbstractMonster owner;
    public Object handler;
    public DamageInfo damage;
    public Fields fields = new Fields();

    public void writeFields(AbstractMonster mon) {
        for (Field field : Fields.class.getFields()) {
            try {
                ReflectionHacks.setPrivate(mon, AbstractMonster.class, field.getName(), field.get(fields));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void readFields(AbstractMonster mon) {
        for (Field field : Fields.class.getFields()) {
            try {
                field.set(fields, ReflectionHacks.getPrivate(mon, AbstractMonster.class, field.getName()));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void update(AbstractMonster mon) {
        updateFunc.accept(this);
    }

    public void render(AbstractMonster mon, SpriteBatch sb) {
        renderFunc.accept(this, sb);
    }

    public void renderDamage(AbstractMonster mon, SpriteBatch sb) {
        renderDamageFunc.accept(this, sb);
    }

    public void takeTurn(AbstractMonster mon) {
        takeTurnFunc.accept(this);
    }

    public static void updateCore(IntentPayload pl) {
        pl.fields.bobEffect.update();
        if (pl.fields.intentAlpha != pl.fields.intentAlphaTarget) {
            if (pl.fields.intentAlphaTarget > pl.fields.intentAlpha) {
                pl.fields.intentAlpha = Math.min(pl.fields.intentAlphaTarget, pl.fields.intentAlpha + Gdx.graphics.getDeltaTime());
            } else {
                pl.fields.intentAlpha = Math.max(pl.fields.intentAlphaTarget, pl.fields.intentAlpha - Gdx.graphics.getDeltaTime());
            }
        }
    }

    public static void renderCore(IntentPayload pl, SpriteBatch sb) {
        IntentManager.setActivePayload(pl.owner, pl);
        IntentManager.renderIntentMethod(pl.owner).invoke(pl.owner, sb);
        IntentManager.releaseActivePayload(pl.owner, pl);
    }

    public static void renderDamageCore(IntentPayload pl, SpriteBatch sb) {
        IntentManager.setActivePayload(pl.owner, pl);
        IntentManager.renderDamageRangeMethod(pl.owner).invoke(pl.owner, sb);
        IntentManager.releaseActivePayload(pl.owner, pl);
    }

    public static void takeTurnCore(IntentPayload pl) {
        IntentManager.setActivePayload(pl.owner, pl);
        MoveManipulationPatches.handleHandler(pl);
        IntentManager.releaseActivePayload(pl.owner, pl);
    }
}
