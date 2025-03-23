# Guía de Contribución

## Estrategia de Ramas

Seguimos el modelo GitFlow para la gestión de ramas:

## Flujo de Trabajo

1. Crear una nueva rama desde `develop`:
```bash
git checkout develop
git pull origin develop
git checkout -b feature/nueva-funcionalidad
```

2. Realizar cambios y commits:
```bash
git add .
git commit -m "feat: descripción del cambio"
```

3. Mantener la rama actualizada:
```bash
git fetch origin develop
git rebase origin/develop
```

4. Crear Pull Request a `develop`

## Convenciones de Commits

Seguimos Conventional Commits:

- `feat`: Nueva funcionalidad
- `fix`: Corrección de errores
- `docs`: Cambios en documentación
- `style`: Cambios de formato
- `refactor`: Refactorización de código
- `test`: Cambios en tests
- `chore`: Tareas de mantenimiento

Ejemplo:
```
feat: añadir sistema de autenticación
```

## Estándares de Código

1. **Kotlin**
   - Seguir las convenciones oficiales de Kotlin
   - Usar ktlint para formateo
   - Documentar funciones públicas

2. **Android**
   - Seguir las guías de Material Design
   - Usar ViewBinding/DataBinding
   - Implementar arquitectura MVVM

3. **Testing**
   - Mantener cobertura mínima del 70%
   - Tests unitarios para ViewModels y Use Cases
   - Tests de UI para flujos críticos

## Proceso de Review

1. El código debe pasar CI/CD
2. Requiere al menos 1 aprobación
3. No conflictos con `develop`
4. Tests pasando al 100%

## Releases

1. Crear rama `release/vX.Y.Z`
2. Actualizar versión en archivos
3. Generar changelog
4. Merge a `main` y `develop`
5. Tag en GitHub 
