# Soul-Link
Silly little Minecraft paper plugin for linking player's xp, potion effects, inventories, and hp.<br />

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

This is the config.yml

#Link choices<br />
#If dying too much bans you<br />
Death Ban On: true<br />
#How many deaths make a ban<br />
Death Limit: 5<br />

#If they can be freed by kills<br />
Freed by kills: true<br />
#How many kills they need to get to be freed<br />
Kills to freedom: 4<br />

#If players will have their xp linked<br />
XP Linking On: true<br />
#How many kills to unlink levels<br />
Kills to xp: 1<br />

#If players will have their potion effects linked<br />
Potion Linking On: true<br />
#How many kills to unlink potion effects<br />
Kills to potion: 2<br />

#If player inventories are linked<br />
Inventory Linking On: true<br />
#How many kills to unlink inventories<br />
Kills to inventory: 3<br />

#If damage taken is linked<br />
Damage Linking On: true<br />
#How many kills to unlink damage taken<br />
Kills to hp: 4<br />
#Made by WizarTheGreat<br />

If you have any suggestions or bugs that need to be fixed, please put it in the issues tab at the top left with the proper Label.

