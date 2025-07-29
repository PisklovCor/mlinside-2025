# Docker Commands Cheatsheet

## Основные команды для работы с образами

### Загрузка образов
```bash
# Скачать образ из Docker Hub
docker pull nginx
docker pull ubuntu:20.04

# Посмотреть список локальных образов
docker images
docker image ls

# Удалить образ
docker rmi nginx
docker rmi -f nginx  # принудительное удаление
```

### Создание образов
```bash
# Создать образ из Dockerfile
docker build -t myapp:latest .
docker build -t myapp:v1.0 -f Dockerfile.prod .

# Создать образ из контейнера
docker commit container_name new_image_name
```

## Управление контейнерами

### Запуск контейнеров
```bash
# Запустить контейнер
docker run nginx
docker run -d nginx                    # в фоновом режиме
docker run -it ubuntu bash            # интерактивный режим
docker run --name my-nginx nginx      # с именем
docker run -p 8080:80 nginx          # проброс портов
docker run -v /host/path:/container/path nginx  # монтирование папок

# Запустить с переменными окружения
docker run -e MY_VAR=value nginx
docker run --env-file .env nginx

# Запустить с ограничениями ресурсов
docker run -m 512m --cpus="1.5" nginx
```

### Управление запущенными контейнерами
```bash
# Посмотреть запущенные контейнеры
docker ps
docker ps -a          # все контейнеры (включая остановленные)

# Остановить контейнер
docker stop container_name
docker stop container_id
docker kill container_name  # принудительная остановка

# Перезапустить контейнер
docker restart container_name

# Удалить контейнер
docker rm container_name
docker rm -f container_name    # принудительное удаление запущенного
docker rm $(docker ps -a -q)  # удалить все остановленные контейнеры
```

### Работа с запущенными контейнерами
```bash
# Подключиться к контейнеру
docker exec -it container_name bash
docker exec -it container_name sh

# Выполнить команду в контейнере
docker exec container_name ls -la
docker exec -u root container_name apt update

# Посмотреть логи
docker logs container_name
docker logs -f container_name     # следить за логами в реальном времени
docker logs --tail 50 container_name  # последние 50 строк

# Скопировать файлы
docker cp file.txt container_name:/path/to/destination
docker cp container_name:/path/to/file.txt ./local/path
```

## Мониторинг и диагностика

### Информация о системе
```bash
# Информация о Docker
docker version
docker info
docker system df       # использование дискового пространства

# Статистика использования ресурсов
docker stats
docker stats container_name
```

### Инспекция объектов
```bash
# Подробная информация о контейнере
docker inspect container_name
docker inspect --format='{{.State.Status}}' container_name

# Информация об образе
docker inspect image_name

# Процессы в контейнере
docker top container_name
```

## Работа с томами (volumes)

```bash
# Создать том
docker volume create my_volume

# Посмотреть тома
docker volume ls

# Информация о томе
docker volume inspect my_volume

# Использовать том при запуске
docker run -v my_volume:/data nginx

# Удалить том
docker volume rm my_volume
docker volume prune    # удалить неиспользуемые тома
```

## Работа с сетями

```bash
# Посмотреть сети
docker network ls

# Создать сеть
docker network create my_network

# Подключить контейнер к сети
docker run --network my_network nginx
docker network connect my_network container_name

# Информация о сети
docker network inspect my_network

# Удалить сеть
docker network rm my_network
```

## Очистка системы

```bash
# Удалить остановленные контейнеры
docker container prune

# Удалить неиспользуемые образы
docker image prune
docker image prune -a  # включая образы без тегов

# Удалить неиспользуемые тома
docker volume prune

# Удалить неиспользуемые сети
docker network prune

# Глобальная очистка
docker system prune        # контейнеры, сети, образы
docker system prune -a     # включая неиспользуемые образы
docker system prune --volumes  # включая тома
```

## Docker Compose (базовые команды)

```bash
# Запустить сервисы
docker-compose up
docker-compose up -d       # в фоновом режиме
docker-compose up --build # с пересборкой образов

# Остановить сервисы
docker-compose stop
docker-compose down        # остановить и удалить контейнеры
docker-compose down -v     # включая тома

# Посмотреть статус
docker-compose ps

# Логи сервисов
docker-compose logs
docker-compose logs service_name
```

## Полезные команды-однострочники

```bash
# Остановить все контейнеры
docker stop $(docker ps -q)

# Удалить все контейнеры
docker rm $(docker ps -a -q)

# Удалить все образы
docker rmi $(docker images -q)

# Получить IP адрес контейнера
docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' container_name

# Войти в контейнер как root
docker exec -it --user root container_name bash

# Создать образ и сразу запустить
docker build -t myapp . && docker run -p 8080:80 myapp
```

## Flags и опции

### Основные флаги для docker run
- `-d` - запуск в фоновом режиме
- `-it` - интерактивный режим с TTY
- `-p` - проброс портов (host:container)
- `-v` - монтирование томов
- `--name` - имя контейнера
- `-e` - переменные окружения
- `--rm` - автоудаление после остановки
- `--restart` - политика перезапуска (no, always, unless-stopped, on-failure)

### Полезные форматы для --format
```bash
# Только ID контейнеров
docker ps --format "table {{.ID}}\t{{.Names}}\t{{.Status}}"

# Кастомный вывод
docker images --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}"
```