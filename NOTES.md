# Notes

## Publishing to NPM Registry

1. Update version number in `/package.json`
2. Create new build with `npm run build`
3. Test new build with `node bin/index.js abc-123-project` (with additional options if needed)
4. Inspect `abc-123-project` project folder to see if updates correctly applied
5. Delete `abc-123-project` project folder
6. Upload to NPM Registry with `npm publish`

Don’t forget to push to Git as well.
