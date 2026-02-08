# 项目依赖优化分析报告

> 生成时间：2025年2月  
> 分析目标：找出体积大但使用率低的依赖，提供前10个优化建议

---

## 一、分析摘要

基于 `mvn dependency:tree`、`dependency:copy-dependencies` 和代码 grep 分析，从 c-dbtools、c-jmesa、c-cache 等核心模块提取出以下「大而少用」的依赖。

---

## 二、前10个待优化依赖（按体积与使用率综合排序）

### 1. **spring-boot-starter-actuator** (含 micrometer-core)
- **引入模块**：c-dbtools
- **传递体积**：spring-boot-actuator ~0.66MB，micrometer-core ~0.63MB，HdrHistogram、LatencyUtils 等
- **代码使用**：**未发现直接 import 或使用** Micrometer、Actuator API
- **结论**：为监控/指标功能引入，多数场景下可移除
- **建议**：
  - 从 c-dbtools 中移除 `spring-boot-starter-actuator`
  - 若仅需 Spring Boot 环境，改为 `spring-boot-starter` 或仅依赖 `spring-context`、`spring-jdbc` 等最小集

### 2. **easymock** (4.11 MB)
- **引入模块**：c-dbtools, c-tools
- **体积**：4.11 MB
- **代码使用**：仅在 3 个测试类中使用
- **建议**：
  - 将 Easymock 相关测试迁移到 Mockito（项目已使用）
  - 移除 easymock 依赖，可减少约 4MB

### 3. **db2jcc** (4.04 MB)
- **引入模块**：c-dbtools
- **scope**：test
- **代码使用**：无直接 import
- **结论**：仅用于 DB2 数据库测试，大多数项目不连接 DB2
- **建议**：改为 `provided` 或使用 Maven Profile，仅在需要 DB2 测试时引入

### 4. **byte-buddy** (3.77 MB)
- **引入方式**：Mockito 的传递依赖
- **scope**：test
- **说明**：Mockito 必需，不能直接删除
- **建议**：保持现状；若需减少 test 依赖体积，可考虑 mockito-inline 等轻量替代（需评估兼容性）

### 5. **druid** (3.65 MB)
- **引入模块**：c-dbtools
- **scope**：provided
- **代码使用**：仅 `DruidDataSourceAdapter.java` 使用
- **结论**：作为可选数据源实现，已 provided，打包不会带入
- **建议**：维持 provided；若项目统一使用 HikariCP，可评估是否保留 Druid 适配器

### 6. **derby** (3.37 MB)
- **引入模块**：c-dbtools
- **scope**：test
- **代码使用**：无直接 import
- **结论**：用于嵌入式数据库测试
- **建议**：项目已使用 H2 做测试；若 Derby 仅作兼容性测试，可改为 Profile 按需引入

### 7. **guava** (2.9 MB)
- **引入模块**：c-dbtools, c-estools, c-jmesa 等
- **代码使用**：约 18 处
- **说明**：使用较多，但 Guava 体积大，可评估是否部分功能可用 JDK/其他库替代
- **建议**：
  - 统计实际使用的 Guava 类（如 Lists, Maps, Preconditions）
  - 对简单场景用 `List.of()`、`Map.of()`、`Objects.requireNonNull` 等 JDK API 替代
  - 无法替代的部分保留 Guava

### 8. **bcprov-jdk15on / BouncyCastle** (5.69 MB) —— 来自 c-jmesa
- **引入方式**：jmesa → itext → bouncycastle
- **代码使用**：jmesa 的 PDF 导出功能间接使用
- **建议**：若项目不使用 JMesa 的 PDF 导出，可对 jmesa 排除 itext、bouncycastle 等；或评估是否可用轻量 PDF 库

### 9. **poi-ooxml / poi** (约 5.36 MB 合计) —— 来自 c-jmesa
- **引入方式**：jmesa 用于 Excel 导出
- **代码使用**：c-jmesa 中 `SimpleTable.exportExcel` 使用
- **建议**：
  - 若仅有少数 Excel 导出需求，可评估 super-csv 或轻量 Excel 库
  - 或对不需要 Excel 的子模块 exclude poi、poi-ooxml

### 10. **html2pdf / itext 系列** (约 3 MB+) —— 来自 c-jmesa
- **引入方式**：jmesa 的 PDF 导出
- **代码使用**：通过 jmesa 间接使用
- **建议**：若不使用 JMesa PDF 导出，在 jmesa 依赖中 exclude itext 相关包

---

## 三、其他值得关注的依赖

| 依赖 | 体积 | 使用情况 | 建议 |
|------|------|----------|------|
| jgroups | 2.05 MB | 仅 c-cache 的 oscache 集群支持使用 | 若不使用集群缓存，可排除 |
| lettuce-core | 1.77 MB | spring-boot-starter-data-redis | 若仅用 Jedis，可排除 lettuce |
| reactor-core | 1.66 MB | c-cache 的 spring-data-redis | 非响应式场景可评估排除 |
| oscache | - | c-cache 核心使用 | 考虑迁移到 Caffeine（已引入）减少 legacy 依赖 |
| javaparser | - | 仅 tablegen 的 JavaSourceMerger 使用 | 模块化后可考虑 optional |

---

## 四、分模块优化建议

### 4.1 c-dbtools
```
推荐操作：
1. 移除 spring-boot-starter-actuator，改用 spring-boot-starter 或最小 Spring 依赖
2. 移除 easymock，统一使用 mockito
3. db2jcc、derby 改为 profile 按需引入
```

### 4.2 c-jmesa
```
推荐操作：
1. 对 jmesa 依赖做 exclusions，按需排除：
   - itext / html2pdf（若不用 PDF 导出）
   - poi / poi-ooxml（若不用 Excel 导出或可换轻量库）
   - bouncycastle（跟随 itext 排除）
2. 评估 jmesa 是否可拆分「核心」与「导出」子模块
```

### 4.3 c-cache
```
推荐操作：
1. 评估用 Caffeine 完全替代 oscache，移除 oscache、jgroups
2. 若不使用 Redis 集群，评估 spring-boot-starter-data-redis 与 lettuce 的必要性
```

### 4.4 全局
```
1. 统一并收敛 commons-io、commons-lang3、commons-text 的版本
2. 将部分 test 依赖（如 junit-vintage-engine、junit-platform-suite-*）按需通过 Profile 引入
3. 在根 pom 或 BOM 中集中管理版本，避免各模块重复声明
```

---

## 五、快速验证命令

```bash
# 依赖树
mvn dependency:tree

# 未使用/未声明依赖
mvn dependency:analyze

# 复制依赖并查看 JAR 大小（以 c-dbtools 为例）
cd c-dbtools && mvn dependency:copy-dependencies -DoutputDirectory=target/dependency
# Windows PowerShell:
Get-ChildItem target\dependency -Filter *.jar | Sort-Object Length -Descending | Select-Object Name, @{N='MB';E={[math]::Round($_.Length/1MB,2)}}
```

---

## 六、预期收益（估算）

| 优化项 | 预估减少体积 |
|--------|--------------|
| 移除 actuator + micrometer | ~1.5 MB |
| 移除 easymock | ~4 MB |
| db2jcc / derby 改为 optional | ~7 MB（按需时） |
| jmesa 排除 itext/poi（若不需要导出） | ~10+ MB |
| **合计（保守估计）** | **约 15–25 MB** |

实际收益取决于最终采纳的优化项和模块组合方式。
