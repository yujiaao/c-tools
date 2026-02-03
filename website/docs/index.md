---
title: C-Tools - 基于 JDBC/R2DBC 标准的轻量级 Java ORM 代码生成工具

hero:
  title: C-Tools
  desc: 基于 JDBC/R2DBC 标准的轻量级 Java ORM 代码生成工具
  actions:
    - text: 快速开始
      link: /guide/getting-started

footer: Open-source MIT Licensed | Copyright © 2021<br />
---

<div class="homepage-root">
  <div style="max-width: 1180px;position: relative;z-index: 2;margin: 0 auto;padding: 40px 20px;">
    <div class="homepage-title">
      C-Tools 
      <span class="english-title">C-Tools </span>
    </div>
    <div class="homepage-subtitle" >
      基于 JDBC/R2DBC 标准的轻量级 Java ORM 代码生成工具
    </div>
    <div class="support-frame" >
      <a style="position: relative" href="/guide/getting-started#jdbc">
        <img src="https://www.oracle.com/a/ocom/img/jdbc.svg" width=120 />
        <img src="https://gw.alicdn.com/tfs/TB15rQzexD1gK0jSZFsXXbldVXa-200-200.svg" style="width: 15px;right: -8px;position: absolute;bottom: -4px;border: 1px solid #fff;border-radius: 14px;background-color: #fff;">
      </a>
      <a style="position: relative" href="/guide/getting-started#r2dbc">
        <img src="https://r2dbc.io/images/PVLG-R2DBC-Logo-RGB.png" width=120 />
        <img src="https://gw.alicdn.com/tfs/TB15rQzexD1gK0jSZFsXXbldVXa-200-200.svg" style="width: 15px;right: -8px;position: absolute;bottom: -4px;border: 1px solid #fff;border-radius: 14px;background-color: #fff;">
      </a>
    </div>
    <div class="quick-start" >
      <a class="quick-start-btn" href="/guide/getting-started" >
        <button style="width: 150px;margin-right: 20px;color: #fff;background: #F6AF1F;height: 44px;font-size: 16px;border: 1px solid #F6AF1F;border-radius: 22px;box-sizing: border-box;cursor: pointer;outline: none;" >快速开始</button>
      </a>
      <div class="github-btn" >
        <a rel="noopener" href="https://github.com/yujiaao/c-tools" >
          <div class="github-btn-content" style="display: flex;justify-content: center;align-items: center;line-height:20px;color:#4d5164;width: 150px;margin-right: 0;height: 44px;font-size: 16px;background-color:#f6f6f6;border-color:rgb(246, 246, 246);border-radius: 22px;box-sizing: border-box;cursor: pointer;outline: none;" >
            <svg style="vertical-align:top; width: 20px;margin-right: 10px" aria-labelledby="simpleicons-github-dark-icon" lang="" role="img" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><title id="simpleicons-github-dark-icon" lang="en">GitHub Dark icon</title><path fill="#7F8C8D" d="M12 .297c-6.63 0-12 5.373-12 12 0 5.303 3.438 9.8 8.205 11.385.6.113.82-.258.82-.577 0-.285-.01-1.04-.015-2.04-3.338.724-4.042-1.61-4.042-1.61C4.422 18.07 3.633 17.7 3.633 17.7c-1.087-.744.084-.729.084-.729 1.205.084 1.838 1.236 1.838 1.236 1.07 1.835 2.809 1.305 3.495.998.108-.776.417-1.305.76-1.605-2.665-.3-5.466-1.332-5.466-5.93 0-1.31.465-2.38 1.235-3.22-.135-.303-.54-1.523.105-3.176 0 0 1.005-.322 3.3 1.23.96-.267 1.98-.399 3-.405 1.02.006 2.04.138 3 .405 2.28-1.552 3.285-1.23 3.285-1.23.645 1.653.24 2.873.12 3.176.765.84 1.23 1.91 1.23 3.22 0 4.61-2.805 5.625-5.475 5.92.42.36.81 1.096.81 2.22 0 1.606-.015 2.896-.015 3.286 0 .315.21.69.825.57C20.565 22.092 24 17.592 24 12.297c0-6.627-5.373-12-12-12"></path></svg>
            GITHUB
          </div>
        </a>
      </div>
    </div>
  </div>
</div>

<div style="background-color: #121212">
  <div class="__dumi-default-layout-features">
    <dl style="background-image: url(&quot;https://img.alicdn.com/imgextra/i1/O1CN012OrzRG1pPioDlcQaW_!!6000000005353-2-tps-56-50.png&quot;);">
      <dt style="color: #FFF">多协议支持</dt>
      <dd><div class="markdown"><p style="color: #FFF;opacity:.5;">支持 JDBC 和 R2DBC 两种标准</p></div></dd>
    </dl>
    <dl style="background-image: url(&quot;https://img.alicdn.com/imgextra/i3/O1CN01mx2JdY1pW8Lv73Q8h_!!6000000005367-2-tps-55-61.png&quot;);">
      <dt style="color: #FFF">代码生成</dt>
      <dd><div class="markdown"><p style="color: #FFF;opacity:.5;">从数据库表自动生成 ORM 代码</p></div></dd>
    </dl>
    <dl style="background-image: url(&quot;https://img.alicdn.com/imgextra/i1/O1CN01H91jck262DxHKaNiT_!!6000000007603-2-tps-60-48.png&quot;);">
      <dt style="color: #FFF">增量更新</dt>
      <dd><div class="markdown"><p style="color: #FFF;opacity:.5;">支持增量生成，避免覆盖手写代码</p></div></dd>
    </dl>
    <dl style="background-image: url(&quot;https://img.alicdn.com/imgextra/i1/O1CN01iAlAFw1SBJBEjn328_!!6000000002208-2-tps-56-56.png&quot;);">
      <dt style="color: #FFF">多数据库</dt>
      <dd><div class="markdown"><p style="color: #FFF;opacity:.5;">支持 MySQL、PostgreSQL、Oracle 等</p></div></dd>
    </dl>
    <dl style="background-image: url(&quot;https://img.alicdn.com/imgextra/i3/O1CN01VbINwS1v3B0Sh9J1L_!!6000000006116-2-tps-52-52.png&quot;);">
      <dt style="color: #FFF">读写分离</dt>
      <dd><div class="markdown"><p style="color: #FFF;opacity:.5;">支持应用层读写分离</p></div></dd>
    </dl>
    <dl style="background-image: url(&quot;https://img.alicdn.com/imgextra/i1/O1CN01bL1bUW1oVO5k7Sp9m_!!6000000005230-2-tps-54-55.png&quot;);">
      <dt style="color: #FFF">轻量级</dt>
      <dd><div class="markdown"><p style="color: #FFF;opacity:.5;">不依赖 Hibernate 和 MyBatis</p></div></dd>
    </dl>
  </div>
</div>

<div class="footer-container" style="padding-bottom: 18px;background-color: #141313;color: #fff;font-size: 14px;font-family: PingFangSC-Regular;">
  <div class="footer-wrapper" >
    <div class="footer-block-content">
      <div style="font-size: 16px;opacity: .5;">社区</div>
      <div style="margin-top: 30px;">
        <ul style="list-style-type: none;padding: 0;margin:0;">
          <li style="width: 200px;margin-bottom: 15px;">
            <img src="//gw.alicdn.com/tfs/TB1H5bLeaL7gK0jSZFBXXXZZpXa-78-78.jpg" style="vertical-align: middle; margin-right: 5px; max-width: 16px; max-height: 16px;">
            <a style="color:#ffffff;" target="_blank" href="https://github.com/yujiaao/c-tools"> GitHub </a>
          </li>
        </ul>
      </div>
    </div>
    <div class="footer-block-content" >
      <div style="font-size: 16px;opacity: .5;">帮助</div>
      <div style="margin-top: 30px;">
        <ul style="list-style-type: none;padding: 0;margin:0;">
          <li style="width: 200px;margin-bottom: 15px;">
            <a style="color:#ffffff;" target="_blank" href="https://github.com/yujiaao/c-tools/issues"> 反馈问题 </a>
          </li>
        </ul>
      </div>
    </div>
    <div class="footer-block-content">
      <div style="font-size: 16px;opacity: .5;">项目信息</div>
        <div style="margin-top: 30px;">
          <ul style="list-style-type: none;padding: 0;margin:0;">
            <li style="width: 200px;margin-bottom: 15px;">
              <a style="color:#ffffff;" href="/guide">  文档 </a>
            </li>
          </ul>
        </div>
      </div>
    </div>
    <div style="display:flex;margin-top: 18px">
      <div style="margin: 0px auto;">Apache License 2.0 | Copyright © 2019-present</div>
    </div>
  </div>
</div>
