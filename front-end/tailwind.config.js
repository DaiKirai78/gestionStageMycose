/** @type {import('tailwindcss').Config} */

import withMT from "@material-tailwind/react/utils/withMT";

export default withMT({
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    colors: {
      "orange": "#FB7813",
        "orange-dark": "#ea6f14",
      "orange-light": "#FFF8F2",
      "gris": "#A7A7A7"
    },
    extend: {
      padding: {
        '10pct': '10%',
        '20pct': '20%',
        '30pct': '30%',
      },
    },
  },
  plugins: [],
})

