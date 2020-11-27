# JCV plugin
IntelliJ IDEA plugin for an enhanced coding experience on JCV based projects.

[![Release](https://img.shields.io/github/release/ekino/jcv-idea-plugin/all.svg)](https://github.com/ekino/jcv-idea-plugin/releases)
[![License](https://img.shields.io/github/license/ekino/jcv-idea-plugin.svg)](https://github.com/ekino/jcv-idea-plugin/blob/master/LICENSE.md)
[![Jetbrains plugin](https://img.shields.io/jetbrains/plugin/v/13916-jcv.svg)](https://plugins.jetbrains.com/plugin/13916-jcv)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/13916-jcv.svg)](https://plugins.jetbrains.com/plugin/13916-jcv)

This plugin brings the JCV language support injected in JSON language.

## Auto-completion and documentation

Any identified validator will get a quick documentation and even external links to official documentation for official Jcv validators:

![](./screenshots/jcv-demo-validator_documentation.png)

As you start typing in the Jcv template pattern, a list of available validators will be suggested to you:

![](./screenshots/jcv-demo-validator_completion_list.png)

You will also get parameter values completion if available, for example with the [date_time_format](https://ekino.github.io/jcv/documentation/validators.html#date_time_format) from [JCV](https://github.com/ekino/jcv):

![](./screenshots/jcv-demo-auto_completion.gif)

## Supported validators

### Official ones

[JCV](https://github.com/ekino/jcv) is a library allowing you to compare JSON contents with embedded validation.
It comes with plenty of pre-defined validators (listed [here](https://github.com/ekino/jcv/wiki/Predefined-validators))
designed to cover the most common needs when validating data with non-predictable values.

[JCV-DB](https://github.com/ekino/jcv-db) reuses these validators and also defines a list of its own
(listed [here](https://github.com/ekino/jcv-db/wiki/Validators)) that are specific to a database-oriented usage.

This plugin offers autocompletion on all the validators of these two projects to date if detected in your project external libraries.

Using an official validator without the actual library linked to the current project module will give you a warning and can guide you to the Maven Central library page to get it:

![](./screenshots/jcv-demo-missing_library.gif)

### Your custom ones

You can define your own custom validator definitions by adding a `.jcvdefinitions.json` file in the project root directory or just let the plugin do it for you:

![](./screenshots/jcv-demo-custom_validator_definition.gif)

Here is and example definition you can customize:

`ROOT_PROJECT_DIRECTORY/.jcvdefinitions.json`
```json
{
  "validators" : [ {
    "id" : "my_custom_validator",
    "parameters" : [ {
      "description" : "My first param",
      "required" : true,
      "suggested_values" : [ "Value 1", "Value 2" ]
    }, {
      "description" : "Another parameter",
      "required" : false,
      "suggested_values" : [ "Another value to suggest" ]
    } ]
  } ]
}
```

## Replacements suggestions

The plugin can find raw JSON value that can be replaced by official matching validators.

![](./screenshots/jcv-demo-replacements_suggestions.png)

Note that it also support multi-caret replacement with matching validators for all JSON values find at each caret.

![](./screenshots/jcv-demo-replacements_suggestions.gif)

## Settings

### Color scheme

You can configure the color scheme settings:

![](./screenshots/jcv-demo-color_settings.png)

### Inspections

Here are the available inspections that you can customize:

![](./screenshots/jcv-demo-inspections.png)

### Intentions

You can find the Jcv replacement suggestions on JSON values in the intentions:

![](./screenshots/jcv-demo-intentions.png)
