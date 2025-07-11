package ChimeraMonsters.commands;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.AbstractMonsterModifier;
import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import basemod.helpers.ConvertHelper;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class MonsterApply extends ConsoleCommand {
    public MonsterApply() {
        requiresPlayer = true;
        minExtraTokens = 2;
        maxExtraTokens = 3;
    }

    @Override
    public void execute(String[] tokens, int depth) {
        if (ChimeraMonstersMod.modMap.containsKey(tokens[depth+1])) {
            String monsterID = tokens[depth];
            AbstractMonsterModifier mod = ChimeraMonstersMod.modMap.get(tokens[depth+1]);
            int index = 0;
            if (tokens.length > depth + 2 && ConvertHelper.tryParseInt(tokens[depth + 2]) != null) {
                index = ConvertHelper.tryParseInt(tokens[depth + 2], 0);
            }
            AbstractMonster found = Monster.getMonsterFromRoom(monsterID, index);
            if (found != null) {
                if (mod.canApplyTo(found, AbstractDungeon.getMonsters())) {
                    DevConsole.log("adding " + mod.getClass().getSimpleName() + " to " + monsterID);
                    ChimeraMonstersMod.applyModifier(found, mod.makeCopy());
                } else {
                    DevConsole.log(mod.getClass().getSimpleName() + " cannot be applied to " + monsterID);
                }
            } else {
                if (tokens.length > depth + 2 && ConvertHelper.tryParseInt(tokens[depth + 2]) != null) {
                    DevConsole.log("could not find monster " + monsterID + " at index " + ConvertHelper.tryParseInt(tokens[depth + 2]));
                } else {
                    DevConsole.log("could not find monster " + monsterID);
                }
            }
        }
        else {
            DevConsole.log("could not find modifier " + tokens[depth+1]);
        }
    }

    @Override
    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> options = Monster.getMonsterOptionsFromRoom();
        if (options.contains(tokens[depth])) { // Input monsterID is correct
            options.clear();
            String monsterID = tokens[depth];
            AbstractMonster found = Monster.getMonsterFromRoom(monsterID, 0);
            if (found != null) {
                options = Monster.getValidMods(found, AbstractDungeon.getMonsters());
                if (tokens.length > depth + 1) {
                    if (options.contains(tokens[depth + 1])) {
                        if (tokens.length > depth + 2) {
                            if (tokens[depth + 2].matches("\\d+")) {
                                ConsoleCommand.complete = true;
                            } else if (!tokens[depth + 2].isEmpty()) {
                                tooManyTokensError();
                            }
                        }
                        return ConsoleCommand.smallNumbers();
                    }
                }
            } else {
                tooManyTokensError();
            }
        } else if (tokens.length > depth + 1) {
            tooManyTokensError();
        }
        return options;
    }

    @Override
    public void errorMsg() {
        Monster.cmdMonsterHelp();
    }
}