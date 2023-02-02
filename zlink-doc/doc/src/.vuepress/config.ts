import { docsearchPlugin } from '@vuepress/plugin-docsearch';
import { path } from '@vuepress/utils';
import { defineUserConfig } from 'vuepress';
import { redirectPlugin } from 'vuepress-plugin-redirect';
import theme from './theme';

export default defineUserConfig({
  base: '/zlink/',
  dest: 'dist',
  locales: {
    '/': {
      lang: 'zh-CN',
      title: 'zlink',
      description: '一款简洁、安全的评论系统。',
    },
    '/en/': {
      lang: 'en-US',
      title: 'zlink',
      description: 'A Simple, Safe Comment System.',
    },
  },

  markdown: {
    code: {
      lineNumbers: 20,
    },
  },

  theme,

  plugins: [
    redirectPlugin(),
  ],

  alias: {
    '@MigrationTool': path.resolve(__dirname, './components/MigrationTool.vue'),
    '@theme-hope/components/HomePage': path.resolve(
      __dirname,
      './components/HomePage'
    ),
    '@theme-hope/components/NormalPage': path.resolve(
      __dirname,
      './components/NormalPage'
    ),
  },

  shouldPrefetch: false,
});
