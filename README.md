# Soul Link
Silly little Minecraft paper plugin for linking player's xp, potion effects, inventories, and hp.<br />
### Commands <br />
/link "player1" "player2"<br />
to link two players.<br />
(they must be online, only accessible by opped players)<br />

/unlink "player1" "player2"<br />
to unlink two players.<br />
(online or offline, only accessible by opped players)<br />
 
/linkinfo "player"<br />
to see who that player is linked to.<br />
(online or offline, accessible by any player)<br />

/setlinklevel<br />
to set the level, or how many "kills" the linked players have.<br />
(online or offline, only accessible by opped players)v

/killvalue<br />
to check how many kills until you are freed<br />

/deathvalue<br />
to check how many deaths until you are banned<br />

To have permanent linking, just turn off kills to be freed in the config!

### Configuration<br />
```
#Link choices
  #If dying too much bans you
Death Ban On: true
  #How many deaths make a ban
Death Limit: 5

  #If they can be freed by kills
Freed by kills: true
  #How many kills they need to get to be freed
Kills to freedom: 4

  #If players will have their xp linked
XP Linking On: true
  #How many kills to unlink levels
Kills to xp: 1

  #If players will have their food linked
Food Linking On: true
  #How many kills to unlink food
Kills to food: 1

  #If players will have their potion effects linked
Potion Linking On: true
  #How many kills to unlink potion effects
Kills to potion: 2

  #If player inventories are linked
Inventory Linking On: true
  #How many kills to unlink inventories
Kills to inventory: 3

  #If damage taken is linked
Damage Linking On: true
  #How many kills to unlink damage taken
Kills to hp: 4
    #Made by WizarTheGreat

```

If you have any suggestions or bugs that need to be fixed, please put it in the issues tab at the top left with the proper Label.

