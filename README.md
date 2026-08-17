# Consumables

![Build](https://github.com/YOUR_USERNAME/Consumables/actions/workflows/build.yml/badge.svg)

Minecraft plugin for **Purpur 1.21.4** that adds configurable consumables:

- 🍺 Alcohol
- 💊 Vitamins
- 🚬 Smoking items

Effects, particles, sounds, item names and lore are configured in `config.yml`.

## Requirements

- Minecraft/Purpur 1.21.4
- Java 21
- Maven 3.9+

## Build locally

```bash
mvn clean package
```

The plugin JAR will be created in:

```text
target/consumables-1.0.0.jar
```

## Install

1. Build the project.
2. Copy `target/consumables-1.0.0.jar` to your server's `plugins/` directory.
3. Start/restart Purpur 1.21.4.
4. Edit `plugins/Consumables/config.yml` if needed.
5. Use `/consumables reload` after configuration changes.

## Commands

```text
/consumables
/consumables menu
/consumables reload
/consumables give <player> <alcohol|vitamins|smoking> <id> [amount]
```

Permission:

```text
consumables.admin
```

Default permission is `op`.

## GitHub

After creating a repository on GitHub:

```bash
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/Consumables.git
git push -u origin main
```

The included GitHub Actions workflow automatically builds the plugin with Java 21 whenever you push changes.

## Project structure

```text
Consumables/
├── .github/
│   └── workflows/
│       └── build.yml
├── src/
│   └── main/
│       ├── java/
│       │   └── ru/example/consumables/
│       └── resources/
│           ├── config.yml
│           └── plugin.yml
├── .gitignore
├── CHANGELOG.md
├── LICENSE
├── README.md
└── pom.xml
```

## Configuration

New consumables can be added to `src/main/resources/config.yml` without changing Java code.

Effect format:

```text
EFFECT:LEVEL:SECONDS
```

Example:

```yaml
effects:
  - "SPEED:2:30"
  - "REGENERATION:1:10"
```

Particle format:

```text
PARTICLE:COUNT:X:Y:Z
```

Example:

```yaml
particles:
  - "CAMPFIRE_COSY_SMOKE:30:0.3:0.8:0.3"
```

Sound example:

```yaml
sound: "ENTITY_GENERIC_DRINK"
```

## License

MIT.
