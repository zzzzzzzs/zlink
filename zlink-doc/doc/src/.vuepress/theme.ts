import {hopeTheme} from 'vuepress-theme-hope';

import {enNavbarConfig, zhNavbarConfig} from './navbar';
import {enSidebarConfig, zhSidebarConfig} from './sidebar';

export default hopeTheme({
    hostname: 'https://zzzzzzzs.github.io/zlink',

    logo: '/logo.svg',
    repo: 'zzzzzzzs/zlink',
    docsDir: 'docs/src',
    docsBranch: 'main',
    // pageInfo: ['ReadingTime', 'PageView'],
    pageInfo: false,
    contributors: false,

    locales: {
        '/': {
            navbar: zhNavbarConfig,
            sidebar: zhSidebarConfig,

            footer: 'GPL-2.0 协议 | Copyright © 2023-present zzzzzzzs',
            displayFooter: true,
        },

        '/en/': {
            navbar: enNavbarConfig,
            sidebar: enSidebarConfig,

            footer: 'GPL-2.0 LICENSE | Copyright © 2023-present zzzzzzzs',
            displayFooter: true,
        },
    },

    iconAssets: '//at.alicdn.com/t/c/font_3180165_u7vein90ekb.css',

    plugins: {
        blog: false,
        mdEnhance: {
            codetabs: true,
            figure: true,
            mathjax: true,
            sub: true,
            sup: true,
            tasklist: true,
        },
    },
});
