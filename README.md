# JCV plugin
IntelliJ IDEA plugin for an enhanced coding experience on JCV based projects.

[![Release](https://img.shields.io/github/release/ekino/jcv-idea-plugin/all.svg)](https://github.com/ekino/jcv-idea-plugin/releases)
[![License](https://img.shields.io/github/license/ekino/jcv-idea-plugin.svg)](https://github.com/ekino/jcv-idea-plugin/blob/master/LICENSE.md)
[![Jetbrains plugin](https://img.shields.io/jetbrains/plugin/v/13916-jcv.svg)](https://plugins.jetbrains.com/plugin/13916-jcv)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/13916-jcv.svg)](https://plugins.jetbrains.com/plugin/13916-jcv)

## Table of contents

* [Syntax Highlighting](#syntax-highlighting)
* [Validator auto-completion](#validator-auto-completion)
* [Replacement suggestions](#replacement-suggestions)

## Syntax Highlighting

It provides template colors and information about known JCV validators:

![](./screenshots/jcv-syntax_highlighting.png)

It will also detect invalid usages and give quick-fixes when possible:

![](./screenshots/jcv-validation-unexpected_whitespaces.png)

![](./screenshots/jcv-validation-unexpected_param.png)

![](./screenshots/jcv-validation-empty_param.png)

## Validator auto-completion

It provides autocompletion for jcv validators in json files.

Just start typing "{#" or any jcv validator id and press ctrl+space to see all the suggested validators:

![](./screenshots/jcv-autocomplete_all.png)

It also works for templated validators with suggested values:

![](./screenshots/jcv-autocomplete_param.png)

### Validators covered
[JCV](https://github.com/ekino/jcv) is a library allowing you to compare JSON contents with embedded validation.
It comes with plenty of pre-defined validators (listed [here](https://github.com/ekino/jcv/wiki/Predefined-validators))
designed to cover the most common needs when validating data with non-predictable values.

[JCV-DB](https://github.com/ekino/jcv-db) reuses these validators and also defines a list of its own 
(listed [here](https://github.com/ekino/jcv-db/wiki/Validators)) that are specific to a database-oriented usage.

This plugin offers autocompletion on all the validators of these two projects to date.

## Replacement suggestions

It will suggest smart replacements of json value to matching validators:

![](./screenshots/jcv-suggestion-date_time_format.png)
