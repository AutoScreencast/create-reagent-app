# Create Reagent App

A simple way to bootstrap a ClojureScript (CLJS) web-app using:

- Shadow-CLJS as a build tool
- Reagent (CLJS wrapper around React) for the UI

## Creating an App

- TODO: `npx create-reagent-app my-app`
  This will create `my-app` folder in current folder, and install dependencies.
  Then just `cd my-app`, and use one of the scripts below.

  This step creates a `node_modules` folder with contents in your project folder.
  It also creates a `yarn.lock` file if you use Yarn as your build runner, or a `package-lock.json` file if you use npm.

## Scripts

### Start App

#### Yarn

```
yarn start
```

#### npm

```
npm start
```

This will compile the app in development mode, and watch for any changes in your code.
Open [http://localhost:3000](http://localhost:3000) to view the app in the browser.

This operation creates a `.shadow-cljs` folder in your project folder.

### Build Release Version of App

#### Yarn

```
yarn build
```

#### npm

```
npm run build
```

This will compile the app in production mode. The finished build will be in the `public` folder, which can be deployed.

This operation creates a `.shadow-cljs` folder in your project folder.

### Show Detailed Build Report of Release Version of App

#### Yarn

```
yarn report
```

#### npm

```
npm run report
```

A detailed build report will show in your CLI, and also creates a `report.html` page in your project folder.

### Serve Release Version of App Locally

#### Yarn

```
yarn serve
```

#### npm

```
npm run serve
```

This will serve your finished build (from doing `yarn build` or `npm run build` on [http://localhost:5000](http://localhost:5000) in your browser.
