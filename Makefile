






COMPOSE = docker compose

.PHONY: all up down build rebuild logs ps clean fclean re

all: up

up:
	$(COMPOSE) up -d --build

down:
	$(COMPOSE) down

build:
	$(COMPOSE) build

rebuild:
	$(COMPOSE) build --no-cache

logs:
	$(COMPOSE) logs -f

ps:
	$(COMPOSE) ps

clean:
	$(COMPOSE) down -v

fclean:
	$(COMPOSE) down -v --rmi all --remove-orphans

re: fclean up
