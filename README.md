


Repositorio de código fuente:
https://github.com/davidortizalbornoz/tenpo_challenge.git

Repositorio de Imagenes DockerHub:
https://hub.docker.com/r/davidortizalbornoz/tempo-challenge



# Entorno docker-compose

Nos posicionamos en el directorio base o raíz del proyecto

Esto descargará las imágenes necesarias
>docker-compose pull

* image: davidortizalbornoz/tempo-challenge:1.0.0
* image: dpage/pgadmin4:7.8
* image: redis:7.2.7-alpine3.21
* image: postgres:16-alpine3.20


Esto iniciará los contenedores:
>docker-compose up -d
* Aplicativo: SpringBoot 3.2.12 - Java 21
* Redis: 7.2.7
* Postgres: 16
* PgAdmin4: 7.8

<img src="/./img/mycontainers.jpg" />

## Contexto

### Retroalimentación de caché

La instrucción dice textualmente:

_**El porcentaje obtenido del servicio externo debe almacenarse en memoria (caché) y considerarse válido durante 30 minutos**_

¿¿ Que hice al respecto ??:

1. Al momento de arranque del aplicativo, ejecutará una llamada al servicio Mock para obtener el porcentaje a aplicar.
2. Incorporé adicionalmente un proceso cíclico que se ejecutará cada 30 minutos para obtener el porcentaje desde el servicio Mock.
3. En cada llamada al servicio Mock este actualizará las claves en Redis:
4. Esto mantendrá siempre un flujo de actualización constante del caché 

Las claves que incorporaremos a Redis son las siguientes:

* FIXED_AMOUNT_TENPO = 35 (Expira en 30 minutos)
* LATEST = 35 (No tiene tiempo de expiración)


<img src="/./img/diagrama_feed_cache.jpg" />