<h1 align="center">
  Smart Open Space
</h1>
<p align="center">
  <img src="/other/logo.svg" width="150" height="150" />
</p>
<p align="center">
  Organizá tu Open Space! :sunglasses:
</p>

<hr />

[![Heroku][heroku-badge]][heroku]
[![Build Status][build-badge]][build]
[![Dependabot Status][dependabot-badge]][dependabot]
[![Backlog][backlog-badge]][backlog]
[![License: GPLv3][license-badge]][license]
[![Issues][issues-badge]][issues]

## :book: Índice
- [:-1: Problema](#-1-problema)
- [:trophy: Solución](#trophy-solución)
- [:tada: Diferencial](#tada-diferencial)
- [:wrench: Instalación](#wrench-instalación)
- [:scroll: Documentación](#scroll-documentación)
- [:computer: Demo](#computer-demo)
- [:cop: Licencia](#cop-licencia)

## :-1: Problema
- Los asistentes de un Open Space no logran captar los datos de una charla, en la agenda se cambian de sala u horario, se superponen charlas del mismo tópico, y algunas salas suelen llenarse muy rápido.

## :trophy: Solución
- **Smart Open Space** es una web app que permite ver los datos de una charla por un proyector mientras su orador está
exponiendo, me ofrece una agenda actualizada y, optimizada por tópicos y agrupando las charlas muy requeridas en espacios más grandes.

## :tada: Diferencial
- A diferencia del método actual, Pizarrón + Google SpreadSheet, el nuestro no requiere carga manual, se encuentra siempre actualizado, y sugiere optimizaciones inteligentes.

## :wrench: Instalación
### Antes de empezar, vas a necesitar:
  - [Git][git]
  - [PostgreSQL][postgresql] o Docker
  - [JDK 8 update 60 o superior][java8] (Asegurate que la variable de entorno `JAVA_HOME` apunte a la carpeta `jdk1.8.0` que sacaste de la descarga del JDK).
  - [NodeJS][node]
  - [Yarn][yarn]

### Descargar el código fuente
```sh
git clone git@github.com:AugustoConti/smart-open-space.git
cd smart-open-space
```

### Levantar backend

#### Con Docker:
```sh
docker-compose up
```
Esto va a generar una carpeta `./db-data` con el volumen.

#### Sin docker, con postgreSQL local
Crear la base de datos, ejemplo:
```sh
psql -c 'create database SOS;' -U postgres
```

#### Config
- Crear el archivo `application-default.properties` en la ruta `/back/src/main/resources/`. Configurando url, usuario y contraseña:

Si usaste Docker, el puerto y las credenciales serian:
```groovy
spring.datasource.url=jdbc:postgresql://localhost:6543/sos
spring.datasource.username=openminded
spring.datasource.password=openheart
```

Si usas postgresql directo:
```groovy
spring.datasource.url=jdbc:postgresql://localhost:5432/sos
spring.datasource.username=postgres
spring.datasource.password=root
```

Ademas para ambos casos hay que agregar al final:
```groovy
logging.appender.email.username=""
logging.appender.email.password=""
logging.appender.email.to=""
```

#### Ejecucion
```sh
cd back && ./gradlew bootRun
```

### Levantar frontend
Asegurate de tener la version de node correspondiente:
```sh
cd front && nvm use
```

Si no tenes yarn instalado:
```sh
npm install -g yarn
```

Para instalar dependencias y levantar el proyecto:
```sh
yarn && yarn watch
```

## :scroll: Documentación

### Diagrama de Arquitectura

#### Visualización del Open Space

![Diagrama de arquitectura](/other/Arquitectura.png)

- Frontend:
  - **App.js**: Punto de entrada de la aplicación.
  - **Routes**: Detecta la ruta, y elige qué componente que corresponde renderizar.
  - **OpenSpace.js**: Renderiza la pantalla con los datos del Open Space.
  - **os-client.js**: Conseguir los datos del Open Space, conectandose con el backend.
- Backend:
  - **OpenSpaceController**: Exponer los endpoints del OpenSpaceService, como REST-Json.
  - **OpenSpaceService**: Exponer un servicio para manipular un Open Space.
  - **OpenSpaceRepository**: Persistir y recuperar objetos OpenSpace de la base de datos.
  - **OpenSpace**: Objeto que representa un Open Space.

### Casos de uso
#### Entrega 1
![Caso de uso entrega 1](/other/CasoDeUso.png)
- Organizador:
  - **Crear Open Space**: Nombre, fecha, horarios y salas.
- Orador:
  - **Registro / Login**: Registrarse con nombre, email y contraseña. Loguearse con email y contraseña
  - **Cargar charla**: con título y descripción en un Open Space.
  - **Agendar charla**: en una sala y un horario disponible en el Open Space.
- Asistente:
  - **Ver agenda**: con todas las charlas en su horario y sala de un Open Space.
  - **Ver detalle de charla**: Título, descripción, orador, sala y horario.

#### Entrega 2
![Caso de uso entrega 2](/other/CasoDeUso2.png)
- Organizador:
  - **Iniciar Marketplace**: Habilitar encolamiento de los oradores para poder exponer su charla.
  - **Mostrar modo proyección**: Mientras orador expone, mostrar datos de su charla.
- Orador:
  - **Encolarse para exponer**: Ponerse en la fila, para exponer su charla.

#### Entrega 3
![Caso de uso entrega 3](/other/CasoDeUso3.png)
- Organizador:
  - **Finalizar Marketplace**: Deshabilitar encolamiento para que no se puedan agendar más charlas.
- Orador:
  - **Ingresar con mail**: Ingresar a la app solo con email y nombre.

#### Entrega 4
![Caso de uso entrega 4](/other/CasoDeUso4.png)
- Organizador:
  - **Crear Open Space** (modificado): Cargar estructura de slots (charla - otro).
  - **Gestionar charlas**: Cargar, encolar y agendar charlas de cualquier orador.
  - **Intercambiar charlas**: de cualquier orador a otra sala y horario.

## :computer: Demo
[![Youtube demo][demo-prev]][demo-link]

## :cop: LICENCIA
- [GPLv3](LICENSE)

[backlog]: https://trello.com/b/A3IsSe1r/smartopenspace
[backlog-badge]: https://img.shields.io/badge/trello-backlog-blue?style=flat-square&logo=trello
[build]: https://travis-ci.org/AugustoConti/smart-open-space
[build-badge]: https://img.shields.io/travis/AugustoConti/smart-open-space?logo=travis&style=flat-square
[demo-link]:https://www.youtube.com/watch?v=cm3D5IztoL0
[demo-prev]:https://img.youtube.com/vi/cm3D5IztoL0/0.jpg
[dependabot]: https://dependabot.com
[dependabot-badge]: https://api.dependabot.com/badges/status?host=github&repo=AugustoConti/smart-open-space
[git]: https://help.github.com/set-up-git-redirect
[heroku]: https://smartopenspace.herokuapp.com
[heroku-badge]: https://img.shields.io/badge/heroku-deploy-ff69b4?style=flat-square&logo=heroku
[issues]: https://github.com/AugustoConti/smart-open-space/issues
[issues-badge]: https://img.shields.io/github/issues-raw/AugustoConti/smart-open-space?style=flat-square
[java8]: https://www.oracle.com/technetwork/java/javase/downloads/index.html
[license]: LICENCIA
[license-badge]: https://img.shields.io/github/license/AugustoConti/smart-open-space?style=flat-square
[node]: https://nodejs.org
[postgresql]: https://www.postgresql.org/download/
[yarn]: https://yarnpkg.com/en/docs/install
