# Elytra Time
A minecraft mod which calculates and displays the flying time remaining for an elytra as part of its tooltip or in chat when a key is pressed. The calculation uses the elytra's durability and its level of unbreaking.

## Port status
This branch (`port/minecraft-26.3`) carries the port to Minecraft 26.3 (Fabric, Java 25):
- Same tooltip format, chat keybind behaviour and config options as v2.2.0.
- Flight-equipment detection is now evidence based (`FlightEquipment`): the vanilla elytra, any item that carries the `minecraft:glider` component while being equippable in the chest slot, or detectors registered via `FlightEquipment.registerDetector(...)`.
- A fully broken elytra now shows `0m 0s` instead of the old `0m -1s`.

## Roadmap
- Server side only (if there's enough interest)

## License
Elytra Time is licensed under GNU-LGPL v3
