# Arquitectura Clean + MVVM para FlyAway

Este documento describe la nueva arquitectura Clean+MVVM implementada en la aplicación FlyAway, explicando los principios de diseño, la estructura del proyecto y las responsabilidades de cada capa.

## Estructura del Proyecto

```
com.example.flyaway/
│
├── data/                  # Capa de datos
│   ├── local/             # Fuentes de datos locales (Room, SharedPreferences)
│   ├── remote/            # Fuentes de datos remotas (Retrofit, Firebase)
│   ├── repository/        # Implementaciones de repositorio
│   └── mapper/            # Mappers entre modelos de dominio y modelos de datos
│
├── domain/                # Capa de dominio (reglas de negocio)
│   ├── model/             # Modelos/entidades de dominio
│   ├── repository/        # Interfaces de repositorio
│   └── usecase/           # Casos de uso
│       ├── trip/
│       ├── day/
│       └── activity/
│
├── presentation/          # Capa de presentación (UI)
│   ├── home/              # Componentes de la pantalla de inicio
│   ├── tripdetails/       # Componentes de la pantalla de detalles de viaje
│   ├── createtrip/        # Componentes para crear/editar viajes
│   └── common/            # Componentes UI compartidos
│
├── di/                    # Módulos de inyección de dependencias
├── ui/                    # Recursos UI (tema, colores, etc.)
│   └── theme/             # Definición del tema de la aplicación
│
└── util/                  # Utilidades y extensiones
```

## Principios de la Arquitectura Clean

La arquitectura Clean separa la aplicación en capas con responsabilidades claras:

### Capa de Dominio

- Contiene la lógica de negocio y las reglas de la aplicación
- Es independiente de frameworks y plataformas
- Define interfaces de repositorio que serán implementadas en la capa de datos
- Contiene casos de uso que encapsulan operaciones de negocio específicas
- Los modelos de dominio son clases POJO sin dependencias externas

### Capa de Datos

- Implementa las interfaces de repositorio definidas en la capa de dominio
- Gestiona diversas fuentes de datos (locales, remotas)
- Maneja la persistencia y recuperación de datos
- Utiliza mappers para convertir entre modelos de datos y modelos de dominio

### Capa de Presentación (MVVM)

- **Model**: Datos y lógica de negocio a través de casos de uso del dominio
- **View**: Pantallas y componentes UI que muestran datos al usuario
- **ViewModel**: Intermediario entre la Vista y el Modelo, expone estados observables

## Flujo de Datos

1. La **Vista** observa los estados del ViewModel y dispara eventos
2. El **ViewModel** recibe eventos, invoca casos de uso y actualiza el estado
3. Los **Casos de Uso** encapsulan operaciones de negocio específicas
4. El **Repositorio** coordina fuentes de datos y aplica reglas de negocio
5. Las **Fuentes de Datos** manejan el acceso a datos concretos

## Beneficios de esta Arquitectura

- **Mantenibilidad**: Código más fácil de entender y modificar
- **Testabilidad**: Facilita la implementación de pruebas unitarias y de integración
- **Escalabilidad**: Permite agregar nuevas características sin afectar el código existente
- **Independencia de frameworks**: El dominio es independiente de implementaciones concretas

## Inyección de Dependencias

Utilizamos Dagger-Hilt para la inyección de dependencias, lo que nos permite:

- Proporcionar implementaciones concretas para interfaces abstractas
- Gestionar el ciclo de vida de los objetos (singleton, scoped, etc.)
- Facilitar pruebas con inyección de mocks 