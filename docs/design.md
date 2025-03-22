# Diseño de Travel Planner

## Arquitectura

La aplicación sigue el patrón de arquitectura MVVM (Model-View-ViewModel) junto con Clean Architecture para mantener un código limpio y mantenible.

### Capas de la Aplicación

1. **Presentación (UI)**
   - Activities/Fragments
   - Composables
   - ViewModels
   - Estados UI

2. **Dominio**
   - Casos de Uso
   - Modelos de Dominio
   - Interfaces de Repositorio

3. **Datos**
   - Implementaciones de Repositorio
   - Fuentes de Datos
   - Modelos de Datos

## Modelo de Datos

```mermaid
classDiagram
    class Trip {
        +String id
        +String name
        +String description
        +Date startDate
        +Date endDate
        +List<Destination> destinations
        +Budget budget
    }

    class Destination {
        +String id
        +String name
        +String location
        +List<Activity> activities
        +Accommodation accommodation
    }

    class Activity {
        +String id
        +String name
        +String description
        +DateTime startTime
        +DateTime endTime
        +Location location
        +Double cost
    }

    class Accommodation {
        +String id
        +String name
        +String address
        +Double cost
        +DateTime checkIn
        +DateTime checkOut
    }

    class Budget {
        +Double total
        +Double spent
        +String currency
        +List<Expense> expenses
    }

    class Expense {
        +String id
        +String description
        +Double amount
        +Date date
        +String category
    }

    class User {
        +String id
        +String name
        +String email
        +List<Trip> trips
        +UserPreferences preferences
    }

    Trip "1" *-- "many" Destination
    Destination "1" *-- "many" Activity
    Destination "1" *-- "1" Accommodation
    Trip "1" *-- "1" Budget
    Budget "1" *-- "many" Expense
    User "1" *-- "many" Trip
```

## Tecnologías Principales

1. **UI/UX**
   - Jetpack Compose
   - Material Design 3
   - Navigation Component

2. **Persistencia de Datos**
   - Room Database
   - DataStore Preferences

3. **Inyección de Dependencias**
   - Dagger Hilt

4. **Concurrencia**
   - Coroutines
   - Flow

5. **Networking**
   - Retrofit
   - OkHttp

## Patrones de Diseño

1. Repository Pattern
2. Factory Pattern
3. Observer Pattern (Flow)
4. Dependency Injection
5. Builder Pattern

## Estrategia de Testing

1. **Unit Tests**
   - ViewModels
   - Use Cases
   - Repositories

2. **Integration Tests**
   - Database
   - API

3. **UI Tests**
   - Compose UI Testing
   - End-to-End Tests 