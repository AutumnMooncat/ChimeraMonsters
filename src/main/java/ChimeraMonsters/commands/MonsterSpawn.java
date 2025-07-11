package ChimeraMonsters.commands;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.AbstractMonsterModifier;
import ChimeraMonsters.patches.MonsterModifierFieldPatches;
import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class MonsterSpawn extends ConsoleCommand {
    public MonsterSpawn() {
        requiresPlayer = true;
        minExtraTokens = 2;
        maxExtraTokens = 2;
    }

    @Override
    public void execute(String[] tokens, int depth) {
        if (ChimeraMonstersMod.modMap.containsKey(tokens[depth+1])) {
            String monsterID = tokens[depth];
            AbstractMonster monster = Monster.createMonster(monsterID);
            if (monster != null) {
                AbstractMonsterModifier mod = ChimeraMonstersMod.modMap.get(tokens[depth+1]);
                if (mod.canApplyTo(monster, null)) {
                    DevConsole.log("spawning " + monsterID + " with " + mod.getClass().getSimpleName());
                    ChimeraMonstersMod.applyModifier(monster, mod.makeCopy());
                    MonsterModifierFieldPatches.ModifierFields.rolled.set(monster, true);
                    addAndPositionMonster(monster);
                } else {
                    DevConsole.log(mod.getClass().getSimpleName() + " cannot be applied to " + monsterID);
                }
            } else {
                DevConsole.log("Failed to instantiate " + monsterID);
            }
        }
        else {
            Monster.cmdMonsterHelp();
        }
    }

    private void addAndPositionMonster(AbstractMonster monster) {
        AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(monster, false));
    }

    @Override
    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> options = Monster.getAllMonsters();
        if(options.contains(tokens[depth])) { // Input monsterID is correct
            options.clear();
            String monsterID = tokens[depth];
            AbstractMonster dummy = Monster.getDummy(monsterID);
            if (dummy != null) {
                options = Monster.getValidMods(dummy, null);
                if (tokens.length > depth + 1) {
                    if (options.contains(tokens[depth+1])) {
                        ConsoleCommand.complete = true;
                    }
                } else if (tokens.length > depth + 2) {
                    tooManyTokensError();
                }
            } else {
                tooManyTokensError();
            }
        } else if(tokens.length > depth + 1) {
            tooManyTokensError();
        }
        return options;
    }
}