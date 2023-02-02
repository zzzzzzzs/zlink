import {sidebar} from 'vuepress-theme-hope';


export const zhSidebarConfig = sidebar({
    '/guide/': [
        {
            text: 'zlink',
            prefix: 'get-started/',
            children: [
                '',
                'install-deploy',
                'table-design',
                'use-guide',
                'dev-guide',
                'upcoming-plans',
            ],
        },
    ],
});


export const enSidebarConfig = sidebar({
    '/en/cookbook/': [
        {
            text: 'Import',
            icon: 'import',
            prefix: 'import/',
            children: ['cdn', 'project'],
        },
        'syntax',
        {
            text: 'Customize',
            icon: 'customize',
            prefix: 'customize/',
            children: [
                'emoji',
                'locale',
                'highlighter',
                'image-preview',
                'search',
                'tex-renderer',
                'upload-image',
            ],
        },
        'reactivity',
        {
            text: 'Compatibility',
            icon: 'history',
            children: ['legacy', 'emoji-compact'],
        },
    ],
    '/en/guide/': [
        {
            text: 'Get Started',
            icon: 'creative',
            prefix: 'get-started/',
            children: ['', 'client', 'server'],
        },
        {
            text: 'Features',
            icon: 'feature',
            prefix: 'features/',
            children: [
                'syntax',
                'emoji',
                'search',
                'reaction',
                'label',
                'i18n',
                'pageview',
                'comment',
                'safety',
                'notification',
                'style',
                'widget/',
            ],
        },
        'database',
        {
            text: 'Deploy',
            icon: 'deploy',
            prefix: 'deploy/',
            children: ['vercel', 'deta', 'railway', 'vps'],
        },
    ],
    '/en/': [
        {
            text: 'Guide',
            icon: 'creative',
            prefix: 'guide/',
            children: [
                'get-started/',
                {
                    text: 'Features',
                    icon: 'feature',
                    prefix: 'features/',
                    children: [
                        'syntax',
                        'emoji',
                        'search',
                        'reaction',
                        'label',
                        'i18n',
                        'pageview',
                        'comment',
                        'safety',
                        'notification',
                        'style',
                        'widget/',
                    ],
                },
                'database',
                'deploy/',
            ],
        },
        'cookbook/',
        {
            text: 'Reference',
            icon: 'reference',
            prefix: 'reference/',
            children: [
                {
                    text: 'Client',
                    icon: 'client',
                    prefix: 'client/',
                    children: ['api', 'props', 'file', 'style'],
                },
                {
                    text: 'server',
                    icon: 'server',
                    prefix: 'server/',
                    children: ['api', 'config', 'env'],
                },
            ],
        },
        {
            text: 'Migration',
            icon: 'migration',
            prefix: 'migration/',
            collapsible: true,
            children: ['client', 'valine', 'tool'],
        },
        {
            text: 'Advanced',
            icon: 'advanced',
            prefix: '/advanced/',
            collapsible: true,
            children: ['intro', 'design', 'ecosystem', 'faq', 'contribution'],
        },
    ],
});
