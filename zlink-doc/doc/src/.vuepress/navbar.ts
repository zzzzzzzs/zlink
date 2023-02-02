import {navbar} from 'vuepress-theme-hope';

export const zhNavbarConfig = navbar([
    {text: "主页", link: "/"},
    {text: "文档", link: "/guide/get-started/"},
]);

export const enNavbarConfig = navbar([
    {text: "home", icon: "home", link: "/"},
    '/en/guide/get-started/',
]);
