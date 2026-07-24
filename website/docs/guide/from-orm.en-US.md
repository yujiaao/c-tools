# From Hibernate / MyBatis

TableGear generates layered Java code from database tables and provides a lightweight JDBC runtime (`c-dbtools`) with ActiveRecord-style queries.

| You know | TableGear |
|----------|-----------|
| JPA Entity | `pojo/` |
| Mapper / Repository | `dal/` + `business/*Manager` |
| Mapper XML / `@Query` | Generated CRUD + `Manager.ar()` chains |
| Service / Controller | Optional generated layers |

**Extension point**: write business logic in `*Manager`, set `over_write_all=no`.

See the [Chinese guide](./from-orm) for full comparison and migration steps, or [Quick start](./getting-started).
