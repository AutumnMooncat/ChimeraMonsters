package ChimeraMonsters.commands;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.AbstractMonsterModifier;
import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;
import java.util.Collections;

public class MonsterPoll extends ConsoleCommand {
    public MonsterPoll() {
        requiresPlayer = true;
        minExtraTokens = 1;
        maxExtraTokens = 1;
    }

    @Override
    public void execute(String[] tokens, int depth) {
        if (Monster.getAllMonsters().contains(tokens[depth])) {
            AbstractMonster dummy = Monster.getDummy(tokens[depth]);
            if (dummy != null) {
                ArrayList<String> validMods = Monster.getValidMods(dummy, null);
                Collections.sort(validMods);
                DevConsole.log(validMods.size()+" valid modifiers found. Dumping to logger.");
                ChimeraMonstersMod.logger.info("{} valid modifiers found for monster {}", validMods.size(), tokens[depth]);
                ChimeraMonstersMod.logger.info(validMods);
            } else {
                DevConsole.log("Failed to get monster " + tokens[depth]);
            }
        } else if (ChimeraMonstersMod.modMap.containsKey(tokens[depth])) {
            AbstractMonsterModifier mod = ChimeraMonstersMod.modMap.get(tokens[depth]);
            ArrayList<String> validMonsters = Monster.getAllValidMonsters(mod, null);
            Collections.sort(validMonsters);
            DevConsole.log(validMonsters.size()+" valid monsters found. Dumping to logger.");
            ChimeraMonstersMod.logger.info("{} valid monsters found for modifier {}", validMonsters.size(), mod.identifier());
            ChimeraMonstersMod.logger.info(validMonsters);
        } else {
            DevConsole.log("could not find monster or modifier " + tokens[depth]);
        }
    }

    @Override
    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> options = Monster.getAllMods();
        if (options.contains(tokens[depth])) {
            options.clear();
        } else if(tokens.length > depth + 1) {
            tooManyTokensError();
        }
        return options;
    }
}