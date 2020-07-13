# Getting Started

When you first boot up your server with CraftJS installed, the plugin will copy it's core Javascript library to `your-server/js`. The file structure will look something like this:

```
your-server
├── plugins
├── bukkit.yml
├── ...
├── js
|   ├── internal
|   ├── plugins
|   ├── types
```

`internal` contains core CraftJS code. Ideally, there should not be any need to modify code there. `types` contains Typescript-declarations for Spigot's packages and also for some of Java's. This folder should be in the `include` array of your project's `tsconfig.json`.

`plugins` folder contains user code. Each file or directory in the folder is treated as a module, but the recommended approach is to create a new directory there, execute `npm init -y` to initialize an empty npm project and go from there. The default file CraftJS looks for as the entrypoint is `index.js` but this can be changed by editing `package.json`'s `main`-property. A working plugin structure would look something like this:

```
├── js
|   ├── ...
|   ├── plugins
|   |   ├── my-plugin
|   |   |   ├── index.js
|   |   |   ├── package.json
```

## Typescript

Usage of Typescript is heavily recommended, even though it requires some additional setup. Create a `tsconfig.json` file and copy and paste the following.

```json
{
  "compilerOptions": {
    "target": "es2017",
    "module": "commonjs",
    "outDir": "./dist",
    "strict": true,
    "esModuleInterop": false,
    "forceConsistentCasingInFileNames": true
  },
  "include": [
    "src/**/*",
    "../../types/**/*",
  ]
}
```

This will assume your source code resides at `my-plugin/src` and compiled javascript is emitted to `my-plugin/dist`. Also note the inclusion of the `types`-folder. However, by default CraftJS looks for `my-plugin/index.js`, so you will need to edit your `package.json` to something like

```json
{
  "name": "my-plugin",
  "version": "1.0.0",
  "description": "",
  "main": "dist/index.js",
  "dependencies": {}
}
```
The key being the `"main"`-field. This will treat `my-plugin/src/index.ts` (which compiles to `my-plugin/dist/index.js`) as your plugin's entrypoint and execute that when your plugin loads. At this point you are essentially set. `tsc --watch` can be used for development time automatic watch compilation.