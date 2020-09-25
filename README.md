# ChronoPay

ChronoPay is a Bukkit plugin for rewarding players with money for online time.
Originally made a number of years ago for a smaller Minecraft server I play on,
I'm now open sourcing it and making it available publicly. 🎉

The plugin depends on [Vault](https://github.com/MilkBowl/Vault) for economy features,
and optionally [Essentials](https://github.com/EssentialsX/Essentials) to prevent players from earning money while AFK.

The plugin also includes an IP address check to prevent players from logging in on multiple accounts to earn money.

## Versioning

ChronoPay is built for Java 8 using Gradle. It should work on pretty much any version of CraftBukkit, Spigot, Paper, etc. that you're running, but feel free to get in contact if you run into any problems.

Version numbers roughly follow the [Semantic Versioning Guidelines](https://semver.org).

## Configuration

`payout-interval` - The number of seconds a player must be online before a payout. *Default: 300*  
`payout-amount` - The amount of money to give to each player on every payout. *Default: 0.10*  
`payout-cap` - The maximum amount of money a player can earn each day if they don't have the
`chronopay.bypass.cap` permission node. Set to 0 to disable. *Default: 5.0*

You can also configure the messages that are sent to players upon certain conditions being met.

## Commands

Currently, the plugin only registers one command.

`/cpreload` - Reloads the plugin configuration from the disk.

## Permissions

- `chronopay.*`  
  Provides access to all plugin features.
  - `chronopay.reload`  
    Provides access to the reload command, `/cpreload`.
  - `chronopay.bypass.*`  
    Provides access to all bypasses.
    - `chronopay.bypass.cap`  
      Allows a player to bypass the money cap check.
    - `chronopay.bypass.afk`  
      Allows a player to bypass the AFK check.
    - `chronopay.bypass.ip`  
      Allows a player to bypass the multi-account IP check.