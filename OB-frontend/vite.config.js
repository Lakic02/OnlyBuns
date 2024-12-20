import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import { Buffer } from 'buffer';

import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  define: {
    'global': 'globalThis',  // Definišemo global objekat
    'process.env': {},       // Dodajemo polje za proces
  },
  optimizeDeps: {
    include: ['process']  // Uključujemo 'buffer' i 'process' u optimizaciju zavisnosti
  }
})
