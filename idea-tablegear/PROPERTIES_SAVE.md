# 保存 tablegen*.properties 时保留文件结构

保存 `tablegen*.properties` / `es*.properties` 时，请**不要**使用 `java.util.Properties#store()`，否则会丢失 key 顺序和注释。

## 正确做法

在配置编辑器（或任何写回 properties 的代码）中，将当前配置以 `Map<String, String>` 形式收集后，调用：

```kotlin
import com.bixuebihui.tablegear.util.TableGearPropertiesIo

// 保存时
TableGearPropertiesIo.savePreservingFormat(file, keyValueMap)
```

或直接使用底层工具（可指定编码）：

```kotlin
import com.bixuebihui.tablegear.util.PropertiesFormatPreserving

PropertiesFormatPreserving.savePreservingFormat(file, keyValueMap, Charsets.UTF_8)
```

这样会：

- 保留原有 **key 的出现顺序**
- 保留所有 **注释**（`# ...`）和**空行**
- 仅更新已有 key 的 value；新增 key 会追加到文件末尾

## 实现说明

- `PropertiesFormatPreserving`：按行解析（注释 / 空行 / key=value），合并新值后按原顺序写回。
- `TableGearPropertiesIo`：对外的保存入口，供配置编辑器保存时调用。

若保存逻辑在 `TableGenConfigDialog` 或 `TableGenConfigModel` 中，请把原先的 `Properties.store(outputStream)` 改为上述调用，并传入从表单/模型收集的 `Map<String, String>`。
