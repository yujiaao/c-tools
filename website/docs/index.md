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

<section class="ct-hero">
  <div class="ct-hero-inner">
    <div class="ct-hero-main">
      <span class="ct-hero-label">Java ORM 代码生成工具</span>
      <h1 class="ct-hero-title">C-Tools</h1>
      <p class="ct-hero-desc">
        基于 JDBC / R2DBC 标准，从数据库表自动生成 POJO、DAL、Service、Controller 等分层代码。
      </p>
      <div class="ct-protocols">
        <a class="ct-protocol" href="/guide/getting-started#jdbc">
          <img src="https://www.oracle.com/a/ocom/img/jdbc.svg" alt="JDBC" />
          <span>JDBC</span>
        </a>
        <a class="ct-protocol" href="/guide/getting-started#r2dbc">
          <img src="https://r2dbc.io/images/PVLG-R2DBC-Logo-RGB.png" alt="R2DBC" />
          <span>R2DBC</span>
        </a>
      </div>
      <div class="ct-actions">
        <a class="ct-btn ct-btn-primary" href="/guide/getting-started">快速开始</a>
        <a class="ct-btn ct-btn-ghost" href="https://github.com/yujiaao/c-tools" target="_blank" rel="noopener noreferrer">GitHub</a>
      </div>
    </div>
    <div class="ct-hero-aside" aria-hidden="true">
      <img class="ct-hero-logo" src="./assets/c-tools.svg" alt="" />
    </div>
  </div>
</section>

<section class="ct-features">
  <div class="ct-features-inner">
    <article class="ct-feature">
      <h3>多协议支持</h3>
      <p>同一套配置即可生成 JDBC 与 R2DBC（Project Reactor）代码</p>
    </article>
    <article class="ct-feature">
      <h3>代码生成</h3>
      <p>从数据库元信息自动生成 POJO、DAL、Service、Controller</p>
    </article>
    <article class="ct-feature">
      <h3>增量更新</h3>
      <p>对比 schema 缓存，仅对变更表重新生成，避免覆盖手写逻辑</p>
    </article>
    <article class="ct-feature">
      <h3>多数据库</h3>
      <p>支持 MySQL、PostgreSQL、Oracle、SQL Server 等主流数据库</p>
    </article>
    <article class="ct-feature">
      <h3>读写分离</h3>
      <p>通过配置实现应用层读写分离与字段加解密</p>
    </article>
    <article class="ct-feature">
      <h3>轻量级</h3>
      <p>不依赖 Hibernate、MyBatis 等重型 ORM 框架</p>
    </article>
  </div>
</section>

<footer class="ct-footer">
  <div class="ct-footer-inner">
    <div class="ct-footer-col">
      <h4>社区</h4>
      <a href="https://github.com/yujiaao/c-tools" target="_blank" rel="noopener noreferrer">GitHub</a>
    </div>
    <div class="ct-footer-col">
      <h4>帮助</h4>
      <a href="https://github.com/yujiaao/c-tools/issues" target="_blank" rel="noopener noreferrer">反馈问题</a>
    </div>
    <div class="ct-footer-col">
      <h4>文档</h4>
      <a href="/guide/getting-started">快速开始</a>
      <a href="/guide/configuration">配置说明</a>
      <a href="/guide/faq">常见问题</a>
    </div>
  </div>
  <p class="ct-footer-copy">Apache License 2.0 · Copyright © 2019-present</p>
</footer>
