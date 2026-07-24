---
title: TableGear - 表结构驱动的 Java 数据访问工具链

hero:
  title: TableGear
  desc: 表结构驱动的 Java 数据访问工具链
  actions:
    - text: 快速开始
      link: /guide/getting-started

footer: Apache License 2.0 | Copyright © 2019-present<br />
---

<section class="ct-hero">
  <div class="ct-hero-inner">
    <div class="ct-hero-main">
      <span class="ct-hero-label">告别 ORM 黑盒 · 保留 SQL 掌控力</span>
      <h1 class="ct-hero-title">TableGear</h1>
      <p class="ct-hero-desc">
        从数据库表自动生成 POJO、DAL、Manager、Service、Controller。
        轻量 JDBC 运行时 + 链式查询——为熟悉 Hibernate / MyBatis 的团队而设计。
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
        <a class="ct-btn ct-btn-primary" href="/guide/from-orm">从 Hibernate / MyBatis 而来</a>
        <a class="ct-btn ct-btn-ghost" href="/guide/getting-started">5 分钟上手</a>
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
      <h3>生成即用</h3>
      <p>单表 CRUD、分页 REST、Swagger 注解一次生成——不必再维护 Mapper XML</p>
    </article>
    <article class="ct-feature">
      <h3>链式查询</h3>
      <p>Manager.ar() 动态条件，类型安全字段常量，调试透明如 JDBC</p>
    </article>
    <article class="ct-feature">
      <h3>增量演进</h3>
      <p>表结构 diff 后只重生变更表，Manager 手写逻辑不被覆盖</p>
    </article>
    <article class="ct-feature">
      <h3>JDBC / R2DBC</h3>
      <p>同一配置切换阻塞与响应式栈，无需重写数据访问层</p>
    </article>
    <article class="ct-feature">
      <h3>企业级扩展</h3>
      <p>读写分离、字段加解密、虚拟视图 view_list，配置即可启用</p>
    </article>
    <article class="ct-feature">
      <h3>轻量依赖</h3>
      <p>核心 c-dbtools，无 Hibernate SessionFactory，无 MyBatis 引擎</p>
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
      <a href="/guide/from-orm">从 Hibernate / MyBatis 而来</a>
      <a href="/guide/getting-started">快速开始</a>
      <a href="/guide/features">核心特性</a>
      <a href="/guide/configuration">配置说明</a>
    </div>
  </div>
  <p class="ct-footer-copy">Apache License 2.0 · Copyright © 2019-present</p>
</footer>
