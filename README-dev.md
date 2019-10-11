# without a REPL
Run
```
   npm install
   npx shadow-cljs watch script
```

And start the script with
```
node bin/ethlint-pre-commit.js
```

# Emacs/cider

Run `cider-jack-in-cljs`, wait for build to finish, run
`./connect-to-shadow-cljs` in a terminal

`bin/ethlint-pre-commit.js` will automatically be rebuild.

# Merge into master

The `master` branch contains the compiled script.

Run the following to merge the development branch:
```
rm bin/ethlint-pre-commit.js
git merge --no-commit development
npx shadow-cljs release script
git add bin/ethlint-pre-commit.js
git commit
```
