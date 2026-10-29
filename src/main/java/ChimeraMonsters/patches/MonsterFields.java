package ChimeraMonsters.patches;

import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import ChimeraMonsters.ui.HoveringCardManager;
import ChimeraMonsters.util.IntentPayload;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.stances.AbstractStance;

import java.util.ArrayList;
import java.util.List;

@SpirePatch(clz = AbstractMonster.class, method = "<class>")
public class MonsterFields {
    // Modifiers
    public static SpireField<Boolean> rolledModifiers = new SpireField<>(() -> false);
    public static SpireField<List<AbstractMonsterModifier>> receivedModifiers = new SpireField<>(ArrayList::new);
    // Stats
    public static SpireField<String> originalName = new SpireField<>(() -> null);
    public static SpireField<Integer> baseMaxHP = new SpireField<>(() -> -1);
    public static SpireField<Float> blockMulti = new SpireField<>(() -> 1f);
    // Intents
    public static SpireField<IntentInterceptingPower> interceptor = new SpireField<>(() -> null);
    public static SpireField<EnemyMoveInfo> replacedIntendedMove = new SpireField<>(() -> null);
    public static SpireField<Boolean> successfullyRolledMove = new SpireField<>(() -> false);
    public static SpireField<IntentPayload> mainIntent = new SpireField<>(() -> null);
    public static SpireField<IntentPayload> activeIntent = new SpireField<>(() -> null);
    public static SpireField<ArrayList<IntentPayload>> extraIntents = new SpireField<>(ArrayList::new);
    // Features
    public static SpireField<HoveringCardManager> cardManager = new SpireField<>(() -> null);
    public static SpireField<ArrayList<AbstractOrb>> orbs = new SpireField<>(() -> null);
    public static SpireField<AbstractStance> stance = new SpireField<>(() -> null); // TODO stances not implemented
    public static SpireField<Integer> maxOrbs = new SpireField<>(() -> 1);
    // Misc
    public static SpireField<Boolean> stunOnArmorBreak = new SpireField<>(() -> false);
    public static SpireField<Float> rawOffsetX = new SpireField<>(() -> 0f);
    public static SpireField<Float> rawOffsetY = new SpireField<>(() -> 0f);
}
