# Explore With Me

* PR основной части https://github.com/angelich/java-explore-with-me/pull/2
* PR дополнительной функциональности https://github.com/angelich/java-explore-with-me/pull/3

Приложение дает возможность делиться информацией об интересных событиях и помогать найти компанию для участия в них.

Приложение состоит из двух модулей - основного и модуля статистики. Каждый публичный запрос по конкретному событию сохраняется в
сервисе статистики и может быть использован для аналитики.

Для корректной работы основного приложения необходим запуск сервиса статистики.

# Запуск проекта

Собрать пакеты:
> mvn clean package

Собрать docker-контейнеры:
> docker-compose build

Запустить в docker-контейнерах:
> docker compose up

