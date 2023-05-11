# Landlord Plugin
<div align="center">
  <img src="https://github.com/CuriosityCore-Development/Lands-Landlord/assets/117315226/7f7372bf-2e9a-4638-a896-b30885da0871" alt="CuriosityCore Logo Final" width="300">
</div>

The Landlord plugin is an addon to the Lands Plugin, which gives functionality for checking the upkeep costs of land claims, checking player activity and auto deleting as part of inactivity checks, and allows for ownership limits to be put into place in a more QoL manner than the base Lands Plugin. 

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
  - [Commands](#commands)
- [License](https://github.com/CuriosityCore-Development/Lands-Landlord/blob/master/LICENCE)


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

To use this plugin, you will need to have the <a href="https://www.spigotmc.org/resources/lands-%E2%AD%95-land-claim-plugin-%E2%9C%85-grief-prevention-protection-gui-management-nations-wars-1-19-support.53313/">Lands</a> and <a href="https://www.spigotmc.org/resources/coreprotect.8631/">Coreprotect</a> plugins installed on your server.
