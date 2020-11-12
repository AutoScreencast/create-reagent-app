<img src="https://raw.githubusercontent.com/cljs/logo/master/cljs.svg" height="120">

# Create Reagent App

⚠️ WARNING! This is a very early version of `create-reagent-app`, which will be undergoing breaking changes until this warning is removed. Please use it with this in mind. ⚠️

A simple way to bootstrap a ClojureScript (CLJS) web-app using:

- [Shadow-CLJS](http://shadow-cljs.org/) as the build tool / compiler

- [Reagent](https://github.com/reagent-project/reagent) (CLJS wrapper around [React](https://reactjs.org/)) for building your user interface

---

## Creating an App

### Clone the Repo

Clone the repo from GitHub using git in your CLI into your project folder called, for example, `my-app`:

```
git clone https://github.com/AutoScreencast/create-reagent-app.git my-app
```

### Rename Namespaces

After cloning, please replace `my-app` with your project name in the following places:

- inside the file `shadow-cljs.edn`, on the line with `:modules {:main {:init-fn my-app.app.core/main}}}}}`

- inside the file `src/my_app/app/core.cljs`, on the first line `(ns my-app.app.core`

- the folder name `my_app` under the `src` folder (NOTE: Clojure requires underscores instead of hyphens in folder and file names!)

Your project name (`my-app` here) used in this way ensures proper namespacing, which will save you potential headaches later on.

### Change Directory into Project Folder

Assuming you called your project `my-app`:

```
cd my-app
```

### Install Dependencies

Note: This step creates a `node_modules` folder with all the dependencies in your project folder.

#### Yarn

Note: Creates a `yarn.lock` file in your project folder.

```
yarn install
```

#### npm

Note: Creates a `package-lock.json` file in your project folder.

```
npm install
```

---

## Scripts

### Start App

This will compile the app in development mode, and watch for any changes in your code.
Open [http://localhost:3000](http://localhost:3000) to view the app in the browser.

This operation creates a `.shadow-cljs` folder in your project folder.

#### Yarn

```
yarn start
```

#### npm

```
npm start
```

### Build Release Version of App

This will compile the app in production mode. The finished build will be in the `public` folder, which can be deployed.

This operation creates a `.shadow-cljs` folder in your project folder.

#### Yarn

```
yarn build
```

#### npm

```
npm run build
```

### Show Detailed Build Report of Release Version of App

A detailed build report will show in your CLI, and also creates a `report.html` page in your project folder.

#### Yarn

```
yarn report
```

#### npm

```
npm run report
```

### Serve Release Version of App Locally

This will serve your finished build (from doing `yarn build` or `npm run build` on [http://localhost:5000](http://localhost:5000)) in your browser.

#### Yarn

```
yarn serve
```

#### npm

```
npm run serve
```

### Remove Generated JS Code

Remove (“clean”) the `public/js` folder and contents generated during compilation.

#### Yarn

```
yarn clean
```

#### npm

```
npm run clean
```
