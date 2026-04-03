---
description: "Update the mod wiki (wiki/index.html) to match the latest mod version. Use when: updating wiki, wiki outdated, new enchantment added, version changed, sync wiki with mod."
name: "Update Mod Wiki"
argument-hint: "Optional: brief description of what changed (e.g. 'added Frost Blade enchantment')"
agent: "agent"
---

You are updating the **Mentalitys | Custom Enchantments** wiki site located at `wiki/index.html`.

## Step 1 — Read source files in parallel

Read ALL of the following files before making any changes:

- [gradle.properties](../../gradle.properties) — current `mod_version`
- [README.md](../../README.md) — full enchantment list with stats (English)
- [README_RU.md](../../README_RU.md) — full enchantment list with stats (Russian)
- [CHANGELOG.md](../../CHANGELOG.md) — version history (Russian)
- [CHANGELOG_EN.md](../../CHANGELOG_EN.md) — version history (English)
- [wiki/index.html](../index.html) — current wiki to update

## Step 2 — Diff: find what is missing or outdated

Compare source files against the current wiki and build a checklist:

- [ ] **Version** — does `v{mod_version}` appear in: header `.site-subtitle`, footer `.footer-copy`, badge `.badge-version`?
- [ ] **New enchantments** — are all enchantments from README present as `<article class="enchant-card">` blocks?
  - Each card must have `data-category` set to one of: `weapon`, `tool`, `armor`, `ranged`
  - Each card must have **both** `data-en` and `data-ru hidden` text variants for: title, tags, description, slot line, table headers/cells, and notes
  - The `.card-sub` span (EN name shown in RU mode) must have `data-ru` attribute
- [ ] **Enchantment stats** — do existing card tables match README values (levels, durations, chances, radii)?
- [ ] **Incompatibilities** — do card notes match current README incompatibility rules?
- [ ] **Villager trades table** (`#view-trades`) — are all enchantment levels present with correct tier and price from README?
- [ ] **Changelog** — does `#changelog-section` contain an entry for the latest version? Are new entries added at the top (inside `<details open>`)?
- [ ] **Nav filter buttons** — if a new category was introduced, is a filter button present?

## Step 3 — Apply all changes

Edit `wiki/index.html` directly. Follow these rules strictly:

### Enchantment card template
Every new card must follow this structure exactly:

```html
<article class="enchant-card" data-category="CATEGORY">
  <div class="card-header">
    <span class="card-emoji">EMOJI</span>
    <div>
      <h3 class="card-title">
        <span data-en>English Name</span><span data-ru hidden>Русское название</span>
        <span class="card-sub" data-ru>English Name</span>
      </h3>
      <div class="card-tags">
        <span class="tag tag-CATEGORY" data-en>Category EN</span><span class="tag tag-CATEGORY" data-ru hidden>Категория RU</span>
        <span class="tag tag-RARITY"   data-en>Rarity EN</span>  <span class="tag tag-RARITY"   data-ru hidden>Редкость RU</span>
        <span class="tag tag-lvl"      data-en>Max level: N</span><span class="tag tag-lvl"      data-ru hidden>Макс. уровень: N</span>
      </div>
    </div>
  </div>
  <p class="card-desc">
    <span data-en>English description.</span>
    <span data-ru hidden>Русское описание.</span>
  </p>
  <div class="card-slots">
    <span data-en>📦 Slot EN</span><span data-ru hidden>📦 Слот RU</span>
  </div>
  <!-- Add <table class="stat-table"> if the enchantment has per-level stats -->
  <ul class="card-notes">
    <li><span data-en>Note EN</span><span data-ru hidden>Заметка RU</span></li>
  </ul>
</article>
```

### Trades table row template
```html
<tr>
  <td><span data-en>English Name</span><span data-ru hidden>Русское название</span></td>
  <td>LEVEL</td>
  <td><span data-en>Tier (N)</span><span data-ru hidden>Тир RU (N)</span></td>
  <td><span data-en>N Emeralds</span><span data-ru hidden>N изумрудов</span></td>
</tr>
```

### Changelog entry template (insert BEFORE the first `<details>` inside `.changelog-list`, set `open`):
```html
<details class="cl-entry" open>
  <summary class="cl-summary">
    <span class="cl-ver">vX.Y.Z</span>
    <span class="cl-date">YYYY-MM-DD</span>
    <span class="cl-new" data-en>+N enchantment(s)</span><span class="cl-new" data-ru hidden>+N зачарование(й)</span>
  </summary>
  <div class="cl-body">
    <p><span data-en>EN description of changes.</span><span data-ru hidden>RU описание изменений.</span></p>
  </div>
</details>
```
Remove `open` from the previously-open entry.

### CSS tag classes
| Rarity       | `data-en` text | CSS class      |
|--------------|----------------|----------------|
| Rare         | Rare           | `tag-rare`     |
| Very Rare    | Very Rare      | `tag-vrare`    |
| Uncommon     | Uncommon       | `tag-uncommon` |

| Category | CSS class    |
|----------|--------------|
| weapon   | `tag-weapon` |
| tool     | `tag-tool`   |
| armor    | `tag-armor`  |
| ranged   | `tag-ranged` |

## Step 4 — Verify

After all edits, confirm in your response:

- ✅ Version updated to `vX.Y.Z` in all 3 locations
- ✅ N new enchantment card(s) added: [list names]
- ✅ N updated stats/incompatibilities in existing cards: [list]
- ✅ N trades rows added/updated
- ✅ Changelog entry added for vX.Y.Z
- ✅ Both EN and RU variants present for all new/changed content
