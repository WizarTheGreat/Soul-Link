# Soul-Link
Silly little Minecraft paper plugin for linking player's xp, potion effects, inventories, and hp.

/link "player1" "player2"
  to link two players.
  (they must be online, only accessible by opped players)
/unlink "player1" "player2"
  to unlink two players.
  (online or offline, only accessible by opped players)
/linkinfo "player"
  to see who that player is linked to.
  (online or offline, accessible by any player)
/setlinklevel
  to set the level, or how many "kills" the linked players have.
  (online or offline, only accessible by opped players)

All below values can be changed in the config.yml -

1st player kill - Experience is no longer linked.
2nd player kill - Potion effects are no longer linked.
3rd player kill - Inventories are no longer linked.
4th(final) player kill - Players become unlinked, and therefore do not have linked damage intakes.
If players die 5 times while linked, they are banned from the server.

If you have any suggestions or bugs that need to be fixed, please put it in the issues tab at the top left with the proper Label.
