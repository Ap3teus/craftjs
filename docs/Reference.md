# CraftJS API reference

## RegisterEvent

```ts
registerEvent(event, callback, priority?): Function
```

### Parameters

|Name|Type|
|-|-|
|event|`typeof` [Event](https://papermc.io/javadocs/paper/1.16/org/bukkit/event/Event.html)|
|callback|`(e: typeof event) => void`|
|priority|[EventPriority](https://papermc.io/javadocs/paper/1.16/org/bukkit/event/EventPriority.html)` | undefined`|

### Description

Registers Spigot event of type `event` with default priority of `HIGHEST`, and calls the provided callback function with the event when the event is triggered. Returns a function that when called unregisters the handler.

### Examples

```ts
import { PlayerInteractEvent } from 'org.bukkit.event.player';

registerEvent(PlayerInteractEvent, (event) => {
  console.log(event.player.name);
});
```

---

## RegisterCommand

```ts
registerCommand(name, callback): void
```

### Parameters

|Name|Type|
|-|-|
|name|`string`|
|callback|`(sender: ` [CommandSender](https://papermc.io/javadocs/paper/1.16/org/bukkit/command/CommandSender.html) `, label: string, args: string[]) => void | boolean`

### Description

Registers command with `name`, and calls callback when the command is executed. The command is considered to have succeeded if the callback function returns either `undefined` (i.e. doesn't return anything) or `true`. If the callback function returns `false`, the command is considered to have failed.

### Examples

```ts
registerCommand('hello', (sender, label, args) => {
  sender.sendMessage(`Hello ${args[0]} !`);
});

// Output:
// > /hello dude
// < Hello dude !
```