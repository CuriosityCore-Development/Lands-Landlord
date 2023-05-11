# Landlord Plugin
<div align="center">
  <img src="https://github.com/CuriosityCore-Development/Lands-Landlord/assets/117315226/7f7372bf-2e9a-4638-a896-b30885da0871" alt="CuriosityCore Logo Final" width="300">
</div>

The Landlord plugin is an addon to the Lands Plugin, which gives functionality for checking the upkeep costs of land claims, checking player activity and auto deleting as part of inactivity checks, and allows for ownership limits to be put into place in a more QoL manner than the base Lands Plugin. 

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
  - [Commands](#commands)
- [License](#LICENCE)

## Usage
### Automation
The Landlord plugin provides automated member activity checking and enforcement of land ownership limits, allowing server owners to:

- Seamlessly monitor and enforce Land Claim member activity automatically, based on defined criteria within the configuration file.
- Enforce Land ownership limits automatically, promoting a more balanced gameplay experience.
Enjoy a more streamlined and efficient management experience with Landlord.

### Commands

The following commands are available:

- `/landlord activitycheck <Land-Name>`: Checks the activity of each land based off of session logs from the CoreProtect plugin. 
- `/landlord ownercheck`: Performs a server-wide ownercheck to ensure no owner is above the Land Ownership limit. Any changes made (either owner transfer or land deletion) will be notified to the command sender and the console.
- `/landlord upkeeptop`: Performs a check to see the Lands with the top 10 upkeep amounts.

## Installation

Simply copy the compiled JAR into your plugin folder.

### Prerequisites

This Plugin is dependant on both the Lands Plugin and the Coreprotect Plugin. 
