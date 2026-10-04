# PlanetEarth Market Fabric Mod

Minecraft Fabric 1.20.1 client mod for the PlanetEarth Portal trade board.

## Current first slice

- Press `M` to open the in-game trade board.
- Search listing names with Korean/English aliases such as `금`, `금괴`, `금 블럭`, and `gold`.
- Open a registration screen and enter a catalog item or an item name that is not in the portal catalog.
- Keep the API boundary separate from the Minecraft UI so HTTP requests can run asynchronously.

The API client currently returns demo listings. Before release, replace the placeholder methods in `TradeApiClient` with an authenticated Supabase Edge Function flow. Do not put a Supabase service-role key, Discord secret, or other server secret in this client mod.

## Build

This module uses Java 17 and Fabric Loom. Generate or add the Gradle wrapper, then run:

```powershell
./gradlew build
```

The target is Minecraft 1.20.1 with Fabric Loader and Fabric API.
