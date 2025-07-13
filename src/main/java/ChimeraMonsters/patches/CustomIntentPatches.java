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

    private static boolean didInitIntents;
    private static boolean didInitTips;
    private static String[] INTENT_TEXT;
    private static String WARNING_NAME;
    private static String WARNING_TEXT;
    private static String INTERCEPT_NAME;
    private static String INTERCEPT_TEXT;
    private static String FERAL_NAME;
    private static String ATTACK_FOR;
    private static String ATTACK_BUFF_FOR;
    private static String ATTACK_DEBUFF_FOR;
    private static String ATTACK_BLOCK_FOR;
    private static String DAMAGE;
    private static String DAMAGE_N;
    private static String TIMES;
    private static Texture warningIntentTexture;
    private static Texture warningTipTexture;
    private static Texture interceptIntentTexture;
    private static Texture interceptTipTexture;
    private static Texture sweepingIntentTexture;
    private static Texture sweepingTipTexture;

    private static void initIntents() {
        didInitIntents = true;
        warningIntentTexture = TextureLoader.getTexture(ChimeraMonstersMod.makeImagePath("ui/IntentFlinchedL.png"));
        interceptIntentTexture = TextureLoader.getTexture(ChimeraMonstersMod.makeImagePath("ui/IntentInterceptingL.png"));
        sweepingIntentTexture = TextureLoader.getTexture(ChimeraMonstersMod.makeImagePath("ui/IntentClawsL.png"));
    }

    private static void initTips() {
        didInitTips = true;
        warningTipTexture = TextureLoader.getTexture(ChimeraMonstersMod.makeImagePath("ui/IntentFlinched.png"));
        interceptTipTexture = TextureLoader.getTexture(ChimeraMonstersMod.makeImagePath("ui/IntentIntercepting.png"));
        sweepingTipTexture = TextureLoader.getTexture(ChimeraMonstersMod.makeImagePath("ui/IntentClaws.png"));
        INTENT_TEXT = CardCrawlGame.languagePack.getUIString(ChimeraMonstersMod.makeID("IntentPatches")).TEXT;
        WARNING_NAME = INTENT_TEXT[0];
        WARNING_TEXT = INTENT_TEXT[1];
        INTERCEPT_NAME = INTENT_TEXT[2];
        INTERCEPT_TEXT = INTENT_TEXT[3];
        FERAL_NAME = INTENT_TEXT[4];
        ATTACK_FOR = INTENT_TEXT[5];
        ATTACK_BUFF_FOR = INTENT_TEXT[6];
        ATTACK_DEBUFF_FOR = INTENT_TEXT[7];
        ATTACK_BLOCK_FOR = INTENT_TEXT[8];
        DAMAGE = INTENT_TEXT[9];
        DAMAGE_N = INTENT_TEXT[10];
        TIMES = INTENT_TEXT[11];
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
        public static SpireReturn<Texture> plz(AbstractMonster __instance) {
            if (!didInitIntents) {
                initIntents();
            }
            if (__instance.intent == CHIMERA_MONSTERS_WARNING) {
                return SpireReturn.Return(warningIntentTexture);
            } else if (__instance.intent == CHIMERA_MONSTERS_INTERCEPTING) {
                return SpireReturn.Return(interceptIntentTexture);
            } else if (__instance.intent == CHIMERA_MONSTERS_SWEEPING_ATTACK || __instance.intent == CHIMERA_MONSTERS_SWEEPING_ATTACK_BUFF || __instance.intent == CHIMERA_MONSTERS_SWEEPING_ATTACK_DEBUFF || __instance.intent == CHIMERA_MONSTERS_SWEEPING_ATTACK_BLOCK) {
                return SpireReturn.Return(sweepingIntentTexture);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = AbstractMonster.class, method = "updateIntentTip")
    public static class HaveTipImage {
        @SpirePrefixPatch
        public static SpireReturn<Void> plz(AbstractMonster __instance, PowerTip ___intentTip, int ___intentDmg, boolean ___isMultiDmg, int ___intentMultiAmt) {
            if (!didInitTips) {
                initTips();
            }
            if (__instance.intent == CHIMERA_MONSTERS_WARNING) {
                ___intentTip.header = WARNING_NAME;
                ___intentTip.body = WARNING_TEXT;
                ___intentTip.img = warningTipTexture;
                return SpireReturn.Return();
            } else if (__instance.intent == CHIMERA_MONSTERS_INTERCEPTING) {
                ___intentTip.header = INTERCEPT_NAME;
                ___intentTip.body = INTERCEPT_TEXT;
                ___intentTip.img = interceptTipTexture;
                return SpireReturn.Return();
            } else if (__instance.intent == CHIMERA_MONSTERS_SWEEPING_ATTACK || __instance.intent == CHIMERA_MONSTERS_SWEEPING_ATTACK_BUFF || __instance.intent == CHIMERA_MONSTERS_SWEEPING_ATTACK_DEBUFF || __instance.intent == CHIMERA_MONSTERS_SWEEPING_ATTACK_BLOCK) {
                String text;
                if (__instance.intent == CHIMERA_MONSTERS_SWEEPING_ATTACK) {
                    text = ATTACK_FOR;
                } else if (__instance.intent == CHIMERA_MONSTERS_SWEEPING_ATTACK_BUFF) {
                    text = ATTACK_BUFF_FOR;
                } else if (__instance.intent == CHIMERA_MONSTERS_SWEEPING_ATTACK_DEBUFF) {
                    text = ATTACK_DEBUFF_FOR;
                } else  {
                    text = ATTACK_BLOCK_FOR;
                }
                if (___isMultiDmg) {
                    text += ___intentDmg + DAMAGE_N + ___intentMultiAmt + TIMES;
                } else {
                    text += ___intentDmg + DAMAGE;
                }
                ___intentTip.header = FERAL_NAME;
                ___intentTip.body = text;
                ___intentTip.img = sweepingTipTexture;
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = AbstractMonster.class, method = "updateIntentVFX")
    public static class HaveIntentVFX {
        @SpirePostfixPatch
        public static void plz(AbstractMonster __instance, @ByRef float[] ___intentParticleTimer, ArrayList<AbstractGameEffect> ___intentVfx) {
            if (__instance.intent == CHIMERA_MONSTERS_SWEEPING_ATTACK_BUFF) {
                ___intentParticleTimer[0] -= Gdx.graphics.getDeltaTime();
                if (___intentParticleTimer[0] < 0.0F) {
                    ___intentParticleTimer[0] = 0.1F;
                    ___intentVfx.add(new BuffParticleEffect(__instance.intentHb.cX, __instance.intentHb.cY));
                }
            } else if (__instance.intent == CHIMERA_MONSTERS_SWEEPING_ATTACK_DEBUFF) {
                ___intentParticleTimer[0] -= Gdx.graphics.getDeltaTime();
                if (___intentParticleTimer[0] < 0.0F) {
                    ___intentParticleTimer[0] = 1.0F;
                    ___intentVfx.add(new DebuffParticleEffect(__instance.intentHb.cX, __instance.intentHb.cY));
                }
            } else if (__instance.intent == CHIMERA_MONSTERS_SWEEPING_ATTACK_BLOCK) {
                ___intentParticleTimer[0] -= Gdx.graphics.getDeltaTime();
                if (___intentParticleTimer[0] < 0.0F) {
                    ___intentParticleTimer[0] = 0.5F;
                    ___intentVfx.add(new ShieldParticleEffect(__instance.intentHb.cX, __instance.intentHb.cY));
                }
            }
        }
    }
}
