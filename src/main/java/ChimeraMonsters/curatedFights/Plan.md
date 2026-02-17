# Curated Fights
### Overview Requirements
 - [ ] Setup Monsters
   - [ ] Class
   - [ ] Modifiers (optional)
     - [ ] Specific
     - [ ] Roll
       - [ ] With whitelist
   - [ ] Positioning (optional)
   - [ ] Rendering/Shaders (optional)
   - [ ] ?
 - [ ] FightName
 - [ ] Tooltip (optional)
 - [ ] Conditions
   - [ ] Chance: see below


1) CuratedFights should be a set of setup instructions executed when the CuratedFight is chosen as opposed to a ready to go fight

2) The condition of a CuratedFight that replaces sth is solely the Monstergroup being exactly as expected

3) I should make sth for CuratedFights that don't replace anything (i think i'd want to make them solely based on roomtype (i.e normal/elite/boss) + act for now)

4) Should probably only pull CuratedFights that dont replace anything (and then refresh the pool on act transition or sth)

5) for CuratedFights that replace a fight we do not empty the MonsterGroup but instead modify the existing one

# Modifier Rolling
### Configurations
- Number of Rolls 
- Chance per roll 
- Rarity Weights
- Curated Chance
- Thematic Chance
- Curated Consumes Roll (default yes)
- Curated bans extra Rolls (default yes)
- Thematic Consumes Roll (default yes)
- Monster Type Rarity Bias

## Math
2 Rolls assuming Thematic and Curated consume Roll

Curated 50%

Thematic 30%

Rolling 80%

Curated and Thematic are Indepedent Rolls
Curated -> Thematic -> Roll

1st Roll:
- 50% for curated
- 50% x 30% thematic
- 50% x 70% x 80% rolling

2nd roll onwards:

- 80% rolling
