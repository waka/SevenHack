# ![](app/src/main/res/mipmap-xxxhdpi/ic_launcher.png) 78.9 (7hack)

78.9 (7hack) is the podcast player for android.

## Screenshot

![](artwork/screenshot_1.png)

* Search podcasts
* Show episodes of podcast
* Show `Show notes` of episode
* Listen mp3 file by downloaded or streaming

![](artwork/7hack_1.gif)

## Development

This app uses [retrolambda](https://github.com/orfjackal/retrolambda), and so required Java8.

### Layers in app.

This app has adopted a simple mvc(not mvc2) architecture.

```
src/main/java/io/github/waka/sevenhack
|
|--activities (view controller)
|--data
| |--dao (access to sqlite)
| |--dxo (exchange model to entity)
| |--entities (sqlite table row)
| |--models (mapping from API)
| |--network (access to external api)
|--events (event bus)
|--internal
| |--constants
| |--di
|--logics (business logic)
|--media (mp3 player)
|--services (background service)
|--utils
|--views
  |--adapters (recycler view)
  |--components (custome view)
  |--dialogs (dialog builder)
  |--fragments (view with lifecycle)
  |--notifications (notification builder)
```

## Libraries
