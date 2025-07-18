// generate-env.js
const fs = require("fs");
const path = require("path");

const env = {
  VITE_BROWSING_BASE_URL: process.env.VITE_BROWSING_BASE_URL,
  VITE_NOTIFICATIONS_BASE_URL: process.env.VITE_NOTIFICATIONS_BASE_URL,
};

const content = `window.RUNTIME_ENVIRONMENT_VARIABLES = ${JSON.stringify(
  env,
  null,
  2
)};\n`;
const outPath = path.join(__dirname, "dist", "env.js");

fs.writeFileSync(outPath, content);
console.log("✅ Generated dist/env.js");
