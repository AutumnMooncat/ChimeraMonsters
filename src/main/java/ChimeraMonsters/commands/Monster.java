package ChimeraMonsters.commands;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.AbstractMonsterModifier;
import basemod.BaseMod;
import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import basemod.patches.whatmod.WhatMod;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import javassist.Modifier;
import org.clapper.util.classutil.*;

import java.io.File;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

public class Monster extends ConsoleCommand {

    public Monster() {
        followup.put("apply", MonsterApply.class);
        followup.put("spawn", MonsterSpawn.class);
        followup.put("poll", MonsterPoll.class);
        requiresPlayer = true;
        simpleCheck = true;
    }

    @Override
    public void execute(String[] tokens, int depth) {
        cmdMonsterHelp();
    }

    @Override
    public void errorMsg() {
        cmdMonsterHelp();
    }

    public static ArrayList<String> getAllMonsters() {
        return new ArrayList<>(ChimeraMonstersMod.dummyMonsterMap.keySet());
    }

    public static AbstractMonster getDummy(String id) {
        return ChimeraMonstersMod.dummyMonsterMap.get(id);
    }

    public static Class<?> getClass(String id) {
        return ChimeraMonstersMod.dummyMonsterMap.get(id).getClass();
    }

    public static ArrayList<String> getValidMods(AbstractMonster monster, MonsterGroup context) {
        return ChimeraMonstersMod.modMap.keySet().stream().filter(s -> ChimeraMonstersMod.modMap.get(s).canApplyTo(monster, context)).collect(Collectors.toCollection(ArrayList::new));
    }

    public static ArrayList<String> getAllMods() {
        return new ArrayList<>(ChimeraMonstersMod.modMap.keySet());
    }

    public static ArrayList<String> getMonsterOptionsFromRoom() {
        ArrayList<String> result = new ArrayList<>();
        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            if (!monster.isDeadOrEscaped()) {
                for (Map.Entry<String, AbstractMonster> entry : ChimeraMonstersMod.dummyMonsterMap.entrySet()) {
                    if (monster.getClass() == entry.getValue().getClass() && !result.contains(entry.getKey())) {
                        result.add(entry.getKey());
                    }
                }
            }
        }
        return result;
    }

    public static AbstractMonster getMonsterFromRoom(String id, int index) {
        int match = 0;
        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            if (monster.getClass() == Monster.getClass(id) && !monster.isDeadOrEscaped()) {
                if (match == index) {
                    return monster;
                } else {
                    match++;
                }
            }
        }
        return null;
    }

    public static AbstractMonster createMonster(String id) {
        if (id == null) {
            ChimeraMonstersMod.logger.warn("Tried to create monster with null id");
            return null;
        }
        String encounterID = ChimeraMonstersMod.encounterMap.get(id);
        if (encounterID == null) {
            ChimeraMonstersMod.logger.warn("Got null encounter from {}", id);
            return null;
        }
        MonsterGroup group = BaseMod.getMonster(encounterID);
        if (group == null) {
            group = MonsterHelper.getEncounter(encounterID);
        }
        for (AbstractMonster monster : group.monsters) {
            if (monster == null) {
                ChimeraMonstersMod.logger.warn("Got null monster from {} -> {}", id, encounterID);
                continue;
            }
            if (id.equals(ChimeraMonstersMod.idMap.get(monster.getClass()))) {
                return monster;
            }
        }
        return null;
    }

    public static ArrayList<String> getAllValidMonsters(AbstractMonsterModifier modifier, MonsterGroup context) {
        ArrayList<String> monsterIDs = new ArrayList<>();
        for (String monsterName : ChimeraMonstersMod.dummyMonsterMap.keySet()) {
            if (modifier.canApplyTo(ChimeraMonstersMod.dummyMonsterMap.get(monsterName), context)) {
                monsterIDs.add(monsterName);
            }
        }
        return monsterIDs;
    }

    public static void cmdMonsterHelp() {
        DevConsole.couldNotParse();
        DevConsole.log("options are:");
        DevConsole.log("* apply [monster id] [modifier id] {index}");
        DevConsole.log("* spawn [monster id] [modifier id]");
        DevConsole.log("* poll [monster id]");
        DevConsole.log("* poll [modifier id]");
    }
}