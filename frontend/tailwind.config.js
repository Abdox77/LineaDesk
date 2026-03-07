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
        "primary": "#38bdf8",
        "primary-dark": "#0284c7",
        "accent-pop": "#10b981",
        "background-light": "#fafafa",
        "background-dark": "#0d1117",
        "surface-light": "#ffffff",
        "surface-dark": "#161b22",
        "surface-dark-alt": "#21262d",
        "border-light": "#e2e8f0",
        "border-dark": "#30363d",
        "text-main": "#c9d1d9",
        "text-secondary": "#8b949e",
        // Legacy aliases for existing components
        "card-light": "#ffffff",
        "card-dark": "#161b22",
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
        "mono": ["JetBrains Mono", "ui-monospace", "SFMono-Regular", "monospace"],
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
