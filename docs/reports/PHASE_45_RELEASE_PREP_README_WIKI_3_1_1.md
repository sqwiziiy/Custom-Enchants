# Phase 45 — 3.1.1 release preparation

- Recovered site source: existing `wiki` branch (`index.html`, `style.css`), not a replacement site. Pages deploy method remains branch `wiki`, path `/`.
- Old website URL: `https://sapexzzz.github.io/Custom-Enchants/` (HTTP 404). Canonical URL: `https://sqwiziiy.github.io/Custom-Enchants/` (HTTP 200 before update); website commit `97c9721c3039f043593bc82c4c72d7ab61e239df` updates it for 3.1.1 and all three maintained versions.
- READMEs: EN/RU links replace empty GitHub Wiki on 1.20.1, 1.21.1, and 1.21.11. Stale current-release 3.1.0/port-in-progress language is removed.
- Release payloads and Modrinth drafts are under `docs/releases/3.1.1/`; exact CI asset evidence is in `ASSET_MANIFEST.md`. Only 1.20.1 carries the accepted complex-tree Magnet + Lumberjack limitation.
- Repository metadata recommendation (do not apply during prep): `Mentalitys | Custom Enchantments — configurable Fabric enchantments for Minecraft 1.20.1, 1.21.1 and 1.21.11.`
- No tags, GitHub Releases, Modrinth versions, default-branch changes, or publication actions were created.
- README commits change final release SHAs, so re-CI is required on each final documentation SHA before tags/publication. Planned tags remain `v3.1.1`, `v3.1.1-mc1.21.1`, and `v3.1.1-mc1.21.11`.
