package ChimeraMonsters.patches;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.util.TextureLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BobEffect;
import com.megacrit.cardcrawl.vfx.DebuffParticleEffect;
import com.megacrit.cardcrawl.vfx.ShieldParticleEffect;
import com.megacrit.cardcrawl.vfx.combat.BuffParticleEffect;

import java.util.ArrayList;

public class CustomIntentPatches {
    @SpireEnum
    public static AbstractMonster.Intent CHIMERA_MONSTERS_WARNING;
    @SpireEnum
    public static AbstractMonster.Intent CHIMERA_MONSTERS_SWEEPING_ATTACK;
    @SpireEnum
    public static AbstractMonster.Intent CHIMERA_MONSTERS_SWEEPING_ATTACK_BUFF;
    @SpireEnum
    public static AbstractMonster.Intent CHIMERA_MONSTERS_SWEEPING_ATTACK_DEBUFF;
    @SpireEnum
    public static AbstractMonster.Intent CHIMERA_MONSTERS_SWEEPING_ATTACK_BLOCK;
    @SpireEnum
    public static AbstractMonster.Intent CHIMERA_MONSTERS_INTERCEPTING;
    @SpireEnum
    public static AbstractMonster.Intent CHIMERA_MONSTERS_METEOR_ATTACK;

    private static boolean didInitHandlers;
    private static final ArrayList<CustomHandler> handlers = new ArrayList<>();

    private static void initHandlers() {
        didInitHandlers = true;
        handlers.add(new WarningHandler());
        handlers.add(new InterceptingHandler());
        handlers.add(new FeralHandler());
        handlers.add(new MeteorHandler());
    }

    @SpirePatch2(clz = AbstractMonster.class, method = "renderDamageRange")
    public static class RenderDamage {
        @SpirePostfixPatch
        public static void plz(AbstractMonster __instance, SpriteBatch sb, int ___intentDmg, BobEffect ___bobEffect, Color ___intentColor) {
            // We might still want this patch later, but not needed for now
        }
    }

    @SpirePatch2(clz = AbstractMonster.class, method = "getIntentImg")
    public static class HaveImage {
        @SpirePrefixPatch
        public static SpireReturn<Texture> plz(AbstractMonster __instance, int ___intentDmg, boolean ___isMultiDmg, int ___intentMultiAmt) {
            if (!didInitHandlers) {
                initHandlers();
            }
            for (CustomHandler handler : handlers) {
                if (handler.handles(__instance, __instance.intent)) {
                    return SpireReturn.Return(handler.handleMainTex(__instance, __instance.intent, ___intentDmg, ___isMultiDmg ? ___intentMultiAmt : 0));
                }
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = AbstractMonster.class, method = "updateIntentTip")
    public static class HaveTipImage {
        @SpirePrefixPatch
        public static SpireReturn<Void> plz(AbstractMonster __instance, PowerTip ___intentTip, int ___intentDmg, boolean ___isMultiDmg, int ___intentMultiAmt) {
            if (!didInitHandlers) {
                initHandlers();
            }
            for (CustomHandler handler : handlers) {
                if (handler.handles(__instance, __instance.intent)) {
                    handler.handleTip(__instance, __instance.intent, ___intentTip, ___intentDmg, ___isMultiDmg ? ___intentMultiAmt : 0);
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = AbstractMonster.class, method = "updateIntentVFX")
    public static class HaveIntentVFX {
        @SpirePostfixPatch
        public static void plz(AbstractMonster __instance, @ByRef float[] ___intentParticleTimer, ArrayList<AbstractGameEffect> ___intentVfx) {
            if (!didInitHandlers) {
                initHandlers();
            }
            for (CustomHandler handler : handlers) {
                if (handler.handles(__instance, __instance.intent)) {
                    handler.handleVfx(__instance, __instance.intent, ___intentVfx, ___intentParticleTimer);
                }
            }
        }
    }

    public static abstract class CustomHandler {
        public Texture mainTex;
        public Texture tipTex;

        public CustomHandler(String mainTex, String tipTex) {
            this(TextureLoader.getTexture(ChimeraMonstersMod.makeImagePath(mainTex)), TextureLoader.getTexture(ChimeraMonstersMod.makeImagePath(tipTex)));
        }

        public CustomHandler(Texture mainTex, Texture tipTex) {
            this.mainTex = mainTex;
            this.tipTex = tipTex;
        }

        public abstract boolean handles(AbstractMonster mon, AbstractMonster.Intent intent);

        public abstract void handleTip(AbstractMonster mon, AbstractMonster.Intent intent, PowerTip tip, int damage, int multi);

        public Texture handleMainTex(AbstractMonster mon, AbstractMonster.Intent intent, int damage, int multi) {
            return mainTex;
        }

        public void handleVfx(AbstractMonster mon, AbstractMonster.Intent intent, ArrayList<AbstractGameEffect> effects, float[] timer) {}
    }

    public static class WarningHandler extends CustomHandler {
        public static String[] TEXT = CardCrawlGame.languagePack.getUIString(ChimeraMonstersMod.makeID(WarningHandler.class.getSimpleName())).TEXT;

        public WarningHandler() {
            super("ui/IntentFlinchedL.png", "ui/IntentFlinched.png");
        }

        @Override
        public boolean handles(AbstractMonster mon, AbstractMonster.Intent intent) {
            return intent == CHIMERA_MONSTERS_WARNING;
        }

        @Override
        public void handleTip(AbstractMonster mon, AbstractMonster.Intent intent, PowerTip tip, int damage, int multi) {
            tip.header = TEXT[0];
            tip.body = TEXT[1];
            tip.img = tipTex;
        }
    }

    public static class InterceptingHandler extends CustomHandler {
        public static String[] TEXT = CardCrawlGame.languagePack.getUIString(ChimeraMonstersMod.makeID(InterceptingHandler.class.getSimpleName())).TEXT;

        public InterceptingHandler() {
            super("ui/IntentInterceptingL.png", "ui/IntentIntercepting.png");
        }

        @Override
        public boolean handles(AbstractMonster mon, AbstractMonster.Intent intent) {
            return intent == CHIMERA_MONSTERS_INTERCEPTING;
        }

        @Override
        public void handleTip(AbstractMonster mon, AbstractMonster.Intent intent, PowerTip tip, int damage, int multi) {
            tip.header = TEXT[0];
            tip.body = TEXT[1];
            tip.img = tipTex;
        }
    }

    public static class FeralHandler extends CustomHandler {
        public static String[] TEXT = CardCrawlGame.languagePack.getUIString(ChimeraMonstersMod.makeID(FeralHandler.class.getSimpleName())).TEXT;

        public FeralHandler() {
            super("ui/IntentClawsL.png", "ui/IntentClaws.png");
        }

        @Override
        public boolean handles(AbstractMonster mon, AbstractMonster.Intent intent) {
            return intent == CHIMERA_MONSTERS_SWEEPING_ATTACK ||
                    intent == CHIMERA_MONSTERS_SWEEPING_ATTACK_BUFF ||
                    intent == CHIMERA_MONSTERS_SWEEPING_ATTACK_DEBUFF ||
                    intent == CHIMERA_MONSTERS_SWEEPING_ATTACK_BLOCK;
        }

        @Override
        public void handleTip(AbstractMonster mon, AbstractMonster.Intent intent, PowerTip tip, int damage, int multi) {
            String text;
            if (intent == CHIMERA_MONSTERS_SWEEPING_ATTACK) {
                text = TEXT[1];
            } else if (intent == CHIMERA_MONSTERS_SWEEPING_ATTACK_BUFF) {
                text = TEXT[2];
            } else if (intent == CHIMERA_MONSTERS_SWEEPING_ATTACK_DEBUFF) {
                text = TEXT[3];
            } else  {
                text = TEXT[4];
            }
            if (multi > 0) {
                text += damage + TEXT[6] + multi + TEXT[7];
            } else {
                text += damage + TEXT[5];
            }

            tip.header = TEXT[0];
            tip.body = text;
            tip.img = tipTex;
        }

        @Override
        public void handleVfx(AbstractMonster mon, AbstractMonster.Intent intent, ArrayList<AbstractGameEffect> effects, float[] timer) {
            if (intent == CHIMERA_MONSTERS_SWEEPING_ATTACK_BUFF) {
                timer[0] -= Gdx.graphics.getDeltaTime();
                if (timer[0] < 0.0F) {
                    timer[0] = 0.1F;
                    effects.add(new BuffParticleEffect(mon.intentHb.cX, mon.intentHb.cY));
                }
            } else if (intent == CHIMERA_MONSTERS_SWEEPING_ATTACK_DEBUFF) {
                timer[0] -= Gdx.graphics.getDeltaTime();
                if (timer[0] < 0.0F) {
                    timer[0] = 1.0F;
                    effects.add(new DebuffParticleEffect(mon.intentHb.cX, mon.intentHb.cY));
                }
            } else if (intent == CHIMERA_MONSTERS_SWEEPING_ATTACK_BLOCK) {
                timer[0] -= Gdx.graphics.getDeltaTime();
                if (timer[0] < 0.0F) {
                    timer[0] = 0.5F;
                    effects.add(new ShieldParticleEffect(mon.intentHb.cX, mon.intentHb.cY));
                }
            }
        }
    }

    public static class MeteorHandler extends CustomHandler {
        public static String[] TEXT = CardCrawlGame.languagePack.getUIString(ChimeraMonstersMod.makeID(MeteorHandler.class.getSimpleName())).TEXT;

        public MeteorHandler() {
            super("ui/IntentMeteorL.png", "ui/IntentMeteor.png");
        }

        @Override
        public boolean handles(AbstractMonster mon, AbstractMonster.Intent intent) {
            return intent == CHIMERA_MONSTERS_METEOR_ATTACK;
        }

        @Override
        public void handleTip(AbstractMonster mon, AbstractMonster.Intent intent, PowerTip tip, int damage, int multi) {
            String text = TEXT[1];
            if (multi > 0) {
                text += damage + TEXT[3] + multi + TEXT[4];
            } else {
                text += damage + TEXT[2];
            }

            tip.header = TEXT[0];
            tip.body = text;
            tip.img = tipTex;
        }
    }
}
