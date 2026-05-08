# Skill Registry

## User Skills (Project + Global)

| Skill | Trigger | Location |
|-------|---------|----------|
| sdd-init | "sdd init", "iniciar sdd" | global |
| sdd-explore | /sdd-explore | global |
| sdd-propose | /sdd-new, /sdd-propose | global |
| sdd-spec | /sdd-spec | global |
| sdd-design | /sdd-design | global |
| sdd-tasks | /sdd-tasks | global |
| sdd-apply | /sdd-apply | global |
| sdd-verify | /sdd-verify | global |
| sdd-archive | /sdd-archive | global |
| judgment-day | "judgment day", "doble review" | global |
| branch-pr | "crear pr", "pull request" | global |
| issue-creation | "crear issue", "reportar bug" | global |
| jira-epic | "crear epic", "jira epic" | global |
| jira-task | "crear task", "jira task" | global |
| find-skills | "buscar skill", "how do I" | global |

## Project Conventions

- **AGENTS.md** - Project instructions (mentor técnico exigente, no soluciones completas)
- **Estructura**: ui/, viewmodel/, data/, util/
- **Naming**: camelCase
- **Arquitectura**: MVVM con Compose, Hilt para inyección

## Compact Rules

Para código Kotlin/Android:
- Un archivo por clase/interface
- Preferir inmutabilidad (val sobre var)
- Null safety: usar ? y let
- Compose: recordar @Composable y remember {}