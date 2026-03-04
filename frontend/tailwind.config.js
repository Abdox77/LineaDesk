/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{js,jsx,ts,tsx}",
    "./public/index.html",
  ],
  darkMode: "class",
  theme: {
    extend: {
      colors: {
        "primary": "#0b84c1",
        "background-light": "#fafafa",
        "background-dark": "#22262a",
        "card-light": "#ffffff",
        "card-dark": "#1e2227",
        "border-light": "#e5e7eb",
        "border-dark": "#374151",
        // Heatmap levels
        "git-level-0": "#ebedf0",
        "git-level-1": "#9be9a8",
        "git-level-2": "#40c463",
        "git-level-3": "#30a14e",
        "git-level-4": "#216e39",
        "dark-git-level-0": "#2d333b",
        "dark-git-level-1": "#0e4429",
        "dark-git-level-2": "#006d32",
        "dark-git-level-3": "#26a641",
        "dark-git-level-4": "#39d353",
      },
      fontFamily: {
        "display": ["Manrope", "sans-serif"],
        "mono": ["JetBrains Mono", "monospace"],
      },
      borderRadius: {
        "DEFAULT": "0.25rem",
        "lg": "0.5rem",
        "xl": "0.75rem",
        "full": "9999px"
      },
    },
  },
  plugins: [],
}
