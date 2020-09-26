# ChronoPay

ChronoPay is a Bukkit plugin for rewarding players with money for online time.
Originally made a number of years ago for a smaller Minecraft server I play on,
I'm now open sourcing it, making it available publicly, and continuing development. 🎉

The plugin depends on [Vault](https://github.com/MilkBowl/Vault) for economy features,
and optionally [Essentials](https://github.com/EssentialsX/Essentials) to prevent players from earning money while AFK.

The plugin also includes an IP address check to prevent players from logging in on multiple accounts to earn money.

## Versioning

ChronoPay is built for Java 8 using Gradle.
It should work on pretty much any version of CraftBukkit, Spigot, Paper, etc. that you're running,
but feel free to get in contact if you run into any problems.

Version numbers roughly follow the [Semantic Versioning Guidelines](https://semver.org).

Version 2.0.0 is a massive improvement over version 1.0.0, and it's the version I recommend everybody uses.

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
    - `chronopay.bypass.address`  
      Allows a player to bypass the multi-account IP address check.
    - `chronopay.bypass.cap`  
      Allows a player to bypass the money cap check.
    - `chronopay.bypass.afk`  
      Allows a player to bypass the AFK check.