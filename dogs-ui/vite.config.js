import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  // Local dev only (npm run dev): send /api calls to Spring Boot on port 8080.
  // Docker uses Caddy for this, and the production build ignores this block.
  server: {
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})
