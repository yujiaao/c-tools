import { defineConfig } from 'dumi';

export default defineConfig({
  mode: 'site',
  base: '/tablegear/',
  publicPath: '/tablegear/',
  locales: [
    ['zh-CN', '中文'],
    ['en-US', 'English'],
  ],
  title: 'C-Tools',
  favicon: 'logo.svg',
  styles: [
    '.__dumi-default-layout[data-route^="/"] .__dumi-default-navbar-logo { background-image:url("logo.svg"); background-size: contain; }',
    '.__dumi-default-layout[data-route^="/en-US"] .__dumi-default-navbar-logo { background-image:url("logo.svg"); background-size: contain; }',
    '.__dumi-default-dark { display: none }',
    '@keyframes levitate { 0% { transform: translate3d(0,-2.3%,0);} 50% {transform: translate3d(0,2.3%,0);} 100% {transform: translate3d(0,-2.3%,0);}}',
    '.img-animation { animation: levitate 5s ease-in-out infinite forwards }',
    '.__dumi-default-code-block{ background-color: #F9F9F9 }',
    '.preview-image {box-shadow: 0 0 30px 0px rgba(0, 0, 0, 0.3); border-radius: 6px; margin: 10px 20px 20px 0; width: 360px;}',
    '.preview-tips {margin-left: 50px;margin-top: 10px;display: flex; flex-direction: column; max-width: 500px;}',
    '.preview-code {margin-top: 10px; background-color: #f9f9f9; padding: 10px; color: #000;font-family: Consolas, Monaco, "Andale Mono", "Ubuntu Mono", monospace;}',
    '.preview-qrcode { margin-top: 20px; width: 200px; height: 200px;}',
    '.preview-title {font-weight: bold; margin: 0 0 20px;}',
    '.preview-row {margin-bottom: 20px;}',
    '.markdown{ padding-bottom: 200px }',
    'code[class*="language-"], pre[class*="language-"]{ color: #333 }',
    '.markdown :not(pre) code { font-size: 14px }',
    '.markdown pre { font-size: 13px }',
    'a:hover { color: #F6AF1F !important }',
    '.__dumi-default-layout{ overflow-x: hidden; }',
    '.__dumi-default-layout-content a { color: #F6AF1F !important }',
    'a.active { color: #F6AF1F !important }',
    '.support-frame img{ margin: 0; }',
    'ul li > span::before { background-color: #F6AF1F !important }',
    '.__dumi-default-layout-features{ max-width: 1180px !important }',
    'ul li a::before, nav > span > a.active::after { opacity: 0 !important; background-color: #F6AF1F !important }',
    '.__dumi-default-layout-content{ max-width: none !important }',
    '.__dumi-default-layout-hero + .__dumi-default-layout-content{ margin-top: 0px !important }',
    '.__dumi-default-layout[data-route="/"] .markdown{ padding-bottom: 0px !important;padding-left: 0px !important; padding-right: 0px !important }',
    '.__dumi-default-layout[data-route="/en-US"] .markdown{ padding-bottom: 0px !important;padding-left: 0px !important; padding-right: 0px !important }',
    '.__dumi-default-layout[data-route="/"] .__dumi-default-menu, .__dumi-default-layout[data-route="/en-US"] .__dumi-default-menu { display: none !important; }',
    '.__dumi-default-layout[data-route="/"] .__dumi-default-layout-content, .__dumi-default-layout[data-route="/en-US"] .__dumi-default-layout-content { padding-left: 0 !important; }',
    '.__dumi-default-layout-features{ max-width: 1180px }',
    '.__dumi-default-menu-list > li > a::after{ background-color: #F6AF1F !important }',
    '::-webkit-scrollbar{ display:none }',
    '.__dumi-default-layout-footer-meta { margin-left: 380px !important; margin-right: 200px; padding-bottom: 15px; }',
    '.__dumi-default-layout-footer-meta span { display: none !important }',
    '.__dumi-default-layout-hero{ display: none !important }',
    '.__dumi-default-layout { padding-left: 0px !important;padding-right: 0px !important; padding-bottom: 0px !important }',
    '.footer-container a { color: #FFFFFF !important }',
    '.__dumi-default-layout[data-route="/"] .__dumi-default-layout-footer { display: none }',
    '.__dumi-default-layout[data-route="/en-US"] .__dumi-default-layout-footer { display: none }',
    '.__dumi-default-navbar-logo{ width: 120px; height: 32px; background-size: contain; background-repeat: no-repeat; }',
    '.ct-hero { background: linear-gradient(180deg, #f8fafc 0%, #ffffff 100%); padding: 48px 24px 64px; }',
    '.ct-hero-inner { max-width: 1100px; margin: 0 auto; display: grid; grid-template-columns: 1fr 320px; gap: 48px; align-items: center; }',
    '.ct-hero-label { display: inline-block; margin-bottom: 16px; padding: 6px 14px; border-radius: 999px; background: #fff7e6; color: #d48806; font-size: 13px; font-weight: 500; border: 1px solid #ffe7ba; }',
    '.ct-hero-title { margin: 0 0 16px; color: #0b1b3e; font-size: 56px; line-height: 1.1; font-weight: 700; letter-spacing: -0.02em; }',
    '.ct-hero-desc { margin: 0 0 28px; max-width: 560px; color: #4a5568; font-size: 18px; line-height: 1.75; }',
    '.ct-protocols { display: flex; flex-wrap: wrap; gap: 12px; margin-bottom: 32px; }',
    '.ct-protocol { display: inline-flex; align-items: center; gap: 10px; padding: 10px 16px; border-radius: 12px; background: #fff; border: 1px solid #e8edf3; box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04); transition: border-color .2s, box-shadow .2s; }',
    '.ct-protocol:hover { border-color: #f6af1f; box-shadow: 0 4px 12px rgba(246, 175, 31, 0.15); }',
    '.ct-protocol img { display: block; width: 72px; height: 24px; object-fit: contain; object-position: center; }',
    '.ct-protocol span { color: #334155; font-size: 14px; font-weight: 600; }',
    '.ct-actions { display: flex; flex-wrap: wrap; gap: 12px; align-items: center; }',
    '.ct-btn { display: inline-flex; align-items: center; justify-content: center; min-width: 140px; height: 44px; padding: 0 24px; border-radius: 22px; font-size: 15px; font-weight: 600; text-decoration: none !important; transition: transform .15s, box-shadow .15s; }',
    '.ct-btn-primary { color: #fff !important; background: #f6af1f; border: 1px solid #f6af1f; box-shadow: 0 4px 14px rgba(246, 175, 31, 0.35); }',
    '.ct-btn-primary:hover { transform: translateY(-1px); box-shadow: 0 6px 18px rgba(246, 175, 31, 0.4); }',
    '.ct-btn-ghost { color: #334155 !important; background: #fff; border: 1px solid #e2e8f0; }',
    '.ct-btn-ghost:hover { border-color: #cbd5e1; background: #f8fafc; }',
    '.ct-hero-aside { display: flex; justify-content: center; align-items: center; }',
    '.ct-hero-logo { width: 100%; max-width: 280px; height: auto; opacity: 0.95; }',
    '.ct-features { background: #121212; padding: 56px 24px 72px; }',
    '.ct-features-inner { max-width: 1100px; margin: 0 auto; display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 20px; }',
    '.ct-feature { padding: 24px; border-radius: 12px; background: rgba(255,255,255,0.04); border: 1px solid rgba(255,255,255,0.08); }',
    '.ct-feature h3 { margin: 0 0 10px; color: #fff; font-size: 16px; font-weight: 600; }',
    '.ct-feature p { margin: 0; color: rgba(255,255,255,0.55); font-size: 14px; line-height: 1.7; }',
    '.ct-footer { background: #0f0f0f; color: #fff; padding: 48px 24px 32px; }',
    '.ct-footer-inner { max-width: 1100px; margin: 0 auto 24px; display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 32px; }',
    '.ct-footer-col h4 { margin: 0 0 16px; color: rgba(255,255,255,0.45); font-size: 14px; font-weight: 500; }',
    '.ct-footer-col a { display: block; margin-bottom: 10px; color: #fff !important; font-size: 14px; opacity: 0.9; }',
    '.ct-footer-col a:hover { opacity: 1; color: #f6af1f !important; }',
    '.ct-footer-copy { max-width: 1100px; margin: 0 auto; text-align: center; color: rgba(255,255,255,0.35); font-size: 13px; }',
    '.__dumi-default-layout-features > dl dt { margin-bottom: 8px !important }',
    '.sponsors img { filter: grayscale(100%); opacity: 0.66; }',
    '.sponsors img:hover { filter: grayscale(0%); opacity: 1; }',
    'video:focus {outline: -webkit-focus-ring-color auto 0px;}',
    'video::-webkit-media-controls {overflow: hidden !important;};video::-webkit-media-controls-enclosure {width: calc(100% + 32px);margin-left: auto;}',
    'a:hover { text-decoration:none !important;}',
    '@media only screen and (min-width: 767px) {\
      .code-preview {display: flex; justify-content: left;max-width: 1000px}\
      .__dumi-default-menu { width: 350px !important }\
      .markdown{ padding-left: 380px !important }\
      .footer-block-content{ flex-grow: 1; }\
      .footer-wrapper{ max-width: 1180px;margin: 0 auto;padding: 100px 0 0px;display: flex;flex-wrap: wrap; }\
      .markdown{ padding-right: 200px }\
    }',
    '@media only screen and (max-width: 767px) {\
      .preview-tips{ display: none; }\
      .code-preview {display: flex; flex-direction: column;}\
      .__dumi-default-layout[data-route="/"]{ padding-left: 0 !important; padding-right: 0 !important }\
      .__dumi-default-layout[data-route="/en-US"]{ padding-left: 0 !important; padding-right: 0 !important }\
      .__dumi-default-layout-features dl { padding-left: 80px !important;width: 170px;margin: 30px auto !important; display: table; }\
      .ct-hero { padding: 32px 20px 48px; }\
      .ct-hero-inner { grid-template-columns: 1fr; gap: 32px; }\
      .ct-hero-title { font-size: 40px; text-align: center; }\
      .ct-hero-desc { text-align: center; margin-left: auto; margin-right: auto; }\
      .ct-hero-label { display: table; margin-left: auto; margin-right: auto; }\
      .ct-protocols { justify-content: center; }\
      .ct-actions { justify-content: center; }\
      .ct-hero-aside { order: -1; }\
      .ct-hero-logo { max-width: 200px; }\
      .ct-features-inner { grid-template-columns: 1fr; }\
      .ct-footer-inner { grid-template-columns: 1fr; text-align: center; }\
      .__dumi-default-navbar-logo{ width: 96px; height: 28px; }\
    }',
  ],
  scripts: [
    // 数据统计
    {
      src: 'https://s9.cnzz.com/z_stat.php?id=1279786237&web_id=1279786237',
      defer: true,
    },
  ],
  navs: {
    'zh-CN': [
      {
        title: '教程',
        path: '/guide/getting-started',
      },
      {
        title: 'GITHUB',
        path: 'https://github.com/yujiaao/c-tools',
      },
    ],
    'en-US': [
      {
        title: 'GUIDE',
        path: '/en-US/guide/getting-started',
      },
      {
        title: 'GITHUB',
        path: 'https://github.com/yujiaao/c-tools',
      },
    ],
  },
  menus: {
    '/guide': [
      {
        title: '代码生成指引',
        path: '/guide',
      },
      {
        title: '快速开始',
        path: '/guide/getting-started',
      },
      {
        title: '配置说明',
        path: '/guide/configuration',
      },
      {
        title: '常见问题',
        path: '/guide/faq',
      },
    ],
    '/en-US/guide': [
      {
        title: 'Quick start',
        path: '/en-US/guide/getting-started',
      },
      {
        title: 'Configuration',
        path: '/en-US/guide/configuration',
      },
      {
        title: 'FAQ',
        path: '/en-US/guide/faq',
      },
    ],
  },
});
