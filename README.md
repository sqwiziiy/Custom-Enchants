# Mentalitys | Custom Enchantments

> [Русская версия](README_RU.md)

[![CI](https://github.com/sqwiziiy/Custom-Enchants/actions/workflows/ci.yml/badge.svg?branch=3.1%2F1.20.1)](https://github.com/sqwiziiy/Custom-Enchants/actions/workflows/ci.yml)

Version `3.1.1` · Minecraft `1.20.1` · Fabric · Java `17` · [Releases](https://github.com/sqwiziiy/Custom-Enchants/releases) · [Documentation](https://sqwiziiy.github.io/Custom-Enchants/) · [Issues](https://github.com/sqwiziiy/Custom-Enchants/issues)

Minecraft 1.20.1 Fabric mod with 19 configurable gameplay enchantments. The
server owns gameplay state; Cloth Config and Mod Menu provide configuration UI
when installed on the client.

## Contents

- [Enchantment reference](ENCHANTMENTS.md)
- [Configuration reference](CONFIGURATION.md)
- [Changelog](CHANGELOG_EN.md)
- [Source repository](https://github.com/sqwiziiy/Custom-Enchants)
- [Issues](https://github.com/sqwiziiy/Custom-Enchants/issues)

## Compatibility

- Minecraft `~1.20.1`
- Java `>=17`
- Fabric Loader `>=0.18.4`
- Fabric API `>=0.92.7+1.20.1`
- Cloth Config `>=11.1.118`
- Mod Menu `>=7.2.2` (optional)

All 19 enchantments, their supported items, levels, conflicts, librarian
trades, configuration fields, and known limitations are documented in the
reference files above. The project is licensed under CC0-1.0.

## Installation

Download `custom-enchants-3.1.1.jar` from the [3.1.1 release](https://github.com/sqwiziiy/Custom-Enchants/releases/tag/v3.1.1) and place it in the Fabric `mods/` directory. Required dependencies are Fabric Loader `>=0.18.4`, Fabric API `>=0.92.7+1.20.1`, Cloth Config `>=11.1.118`, and Java `>=17`; Mod Menu `>=7.2.2` is optional.

## Known limitation

On Minecraft 1.20.1, Magnet + Lumberjack generally collects linked drops, but complex real-tree layouts can leave some secondary log drops on nearby ground. The release contract preserves all drops: no item loss or duplication is accepted.
