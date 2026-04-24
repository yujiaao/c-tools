# Idea-TableGear 第四轮瘦身计划

当前体积基线：`27.35 MB`（`idea-tablegear-1.0.3.zip`）。

## 目标

- 先做低风险瘦身，确保核心功能可用：
  - 打开配置面板
  - 连接数据库
  - 生成代码
- 每项改动都执行：`clean buildPlugin` + `test` + 最小回归检查

## 候选项（按优先级）

| 优先级 | 项目 | 预估收益 | 风险 | 备注 |
|---|---|---:|---|---|
| P1 | 排除 `commons-configuration2` 相关残留链路（已做，复核稳定性） | 已获得 | 低 | 保留现状，重点回归 |
| P1 | 排除 `spring-context-support` | 0.3~0.8 MB | 低~中 | 多用于扩展支持，主流程可能不依赖 |
| P1 | 排除 `javax.mail-api` | 0.1~0.3 MB | 低 | 插件主流程通常不使用邮件能力 |
| P2 | 排除 `commons-beanutils` + `commons-collections`（旧） | 0.8~1.3 MB | 中 | 若某些反射/Bean 映射路径被用到会报错 |
| P2 | 排除 `javaparser-symbol-solver-core`（保留 `javaparser-core`） | 0.3~0.6 MB | 中 | 若仅语法级处理可行，符号求解会受影响 |
| P3 | 排除 `spring-context` / `spring-beans`（仅保留 `spring-jdbc` 必需） | 1~2 MB | 高 | 需更细粒度验证，容易缺类 |
| P3 | 拆分 ES 能力到可选模块（核心包不带 ES） | 3~8 MB | 高（改造） | 收益大，但需要结构调整 |

## 执行顺序（建议）

1. `spring-context-support` + `javax.mail-api`（低风险试探）
2. 验证体积、构建、测试
3. 再尝试 `commons-beanutils`/`commons-collections`（中风险）
4. 如出现缺类，精准回加并记录“最小可运行依赖集”

## 回归清单（每轮）

- [ ] `./gradlew clean buildPlugin`
- [ ] `./gradlew test`
- [ ] 打开配置编辑器（UI）
- [ ] 连接数据库（DB）
- [ ] 生成代码（Gen）

