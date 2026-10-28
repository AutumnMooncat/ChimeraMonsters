package ChimeraMonsters.util;

import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
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

public class IntentPayload implements Comparable<IntentPayload> {
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
        public float intentAlphaTarget = 0f;
        public float intentParticleTimer = 0f;
        public float intentAngle = 0f;
    }

    public Consumer<IntentPayload> updateFunc = IntentPayload::updateCore;
    public BiConsumer<IntentPayload, SpriteBatch> renderFunc = IntentPayload::renderCore;
    public BiConsumer<IntentPayload, SpriteBatch> renderDamageFunc = IntentPayload::renderDamageCore;
    public Consumer<IntentPayload> takeTurnFunc = IntentPayload::takeTurnCore;
    public AbstractMonster owner;
    public IntentInterceptingPower interceptor;
    public EnemyMoveInfo replacedIntendedMove;
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

    public void update() {
        updateFunc.accept(this);
    }

    public void render(SpriteBatch sb) {
        renderFunc.accept(this, sb);
    }

    public void renderDamage(SpriteBatch sb) {
        renderDamageFunc.accept(this, sb);
    }

    public void takeTurn() {
        takeTurnFunc.accept(this);
    }

    @Override
    public int compareTo(IntentPayload other) {
        return weight() - other.weight();
    }

    private int weight() {
        int base = 0;
        if (fields.intentBaseDmg > -1) {
            base -= 1000;
        }
        if (fields.intent.name().toLowerCase().contains(AbstractMonster.Intent.DEFEND.name().toLowerCase())) {
            base -= 100;
        }
        if (fields.intent.name().toLowerCase().contains(AbstractMonster.Intent.DEBUFF.name().toLowerCase())) {
            base += 10;
        } else if (fields.intent.name().toLowerCase().contains(AbstractMonster.Intent.BUFF.name().toLowerCase())) {
            base += 1;
        }
        return base;
    }

    public static void updateCore(IntentPayload pl) {
        ReflectionHacks.<BobEffect>getPrivate(pl.owner, AbstractMonster.class, "bobEffect").update();
        if (pl.owner.intentAlpha != pl.owner.intentAlphaTarget) {
            if (pl.owner.intentAlphaTarget > pl.owner.intentAlpha) {
                pl.owner.intentAlpha = Math.min(pl.owner.intentAlphaTarget, pl.owner.intentAlpha + Gdx.graphics.getDeltaTime());
            } else {
                pl.owner.intentAlpha = Math.max(pl.owner.intentAlphaTarget, pl.owner.intentAlpha - Gdx.graphics.getDeltaTime());
            }
        }
    }

    public static void renderCore(IntentPayload pl, SpriteBatch sb) {
        IntentManager.renderIntentMethod(pl.owner).invoke(pl.owner, sb);
    }

    public static void renderDamageCore(IntentPayload pl, SpriteBatch sb) {
        IntentManager.renderDamageRangeMethod(pl.owner).invoke(pl.owner, sb);
    }

    public static void takeTurnCore(IntentPayload pl) {
        pl.owner.takeTurn();
    }
}
