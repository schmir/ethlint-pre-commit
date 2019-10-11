# ethlint-pre-commit

This repository contains an
[ethlint/solium](https://github.com/duaraghav8/Ethlint) hook for the
[pre-commit framework](https://pre-commit.com/).


## Usage

Add the following to `.pre-commit-config.yaml`:

```yaml
- repo: https://github.com/schmir/ethlint-pre-commit.git
  rev: 0.3.0
  hooks:
  - id: ethlint
```

### Configuration file

The hook reads ethlint's `soliumrc.json` file as configuration
file. If the file doesn't live at the repositories root directory, the
path can be specified with the `--config` argument.

The hook does not read the `.soliumignore` file. Please use
pre-commit's `exclude` key instead.

The following snippet shows how to exclude files and how to specify
a different location of ethlint's config file:


```yaml
- repo: https://github.com/schmir/ethlint-pre-commit.git
  rev: 0.3.0
  hooks:
  - id: ethlint
    exclude: ^contracts/contracts/lib/
    args: ["--config", "contracts/.soliumrc.json"]
```

