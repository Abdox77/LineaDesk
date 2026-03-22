## LineaDesk
🔗 **Live:** https://lineadesk.codes

[![Live Demo](https://img.shields.io/badge/Live-lineadesk.codes-blue)](https://lineadesk.codes)

A full-stack **Developer Productivity & Project Management Dashboard** that combines project management, task tracking, focus sessions (Pomodoro timer), habit tracking, and journaling into a unified, dark-mode-first web application.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-green)
![React](https://img.shields.io/badge/React-19-blue)
![TypeScript](https://img.shields.io/badge/TypeScript-✓-blue)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1)

---

## Table of Contents

- [Getting Started](#getting-started)
- [Tech Stack](#tech-stack)
- [Features](#features)
- [Project Structure](#project-structure)
- [API Endpoints](#api-endpoints)
- [Authentication](#authentication)
- [Architecture](#architecture)

---

## Getting Started

### Prerequisites

- [Docker](https://www.docker.com/) & Docker Compose

### Configuration

The project uses `.env` files for sensitive configuration. These files are git-ignored.

#### 1. Root `.env` (used by Docker Compose)

Create a `.env` file in the project root:

```env
GITHUB_CLIENT_ID=your_github_oauth_client_id
GITHUB_CLIENT_SECRET=your_github_oauth_client_secret
```

These are injected into the backend container via `docker-compose.yml`.

#### 2. Backend `.env` (for local development without Docker)

A `.env.example` is provided in `backend/`:

```bash
cp backend/.env.example backend/.env
```

```env
DB_URL=jdbc:mysql://localhost:3306/LineaDesk?serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION=3600000
GITHUB_CLIENT_ID=your_github_oauth_client_id
GITHUB_CLIENT_SECRET=your_github_oauth_client_secret
```

| Variable               | Description                                             |
|------------------------|---------------------------------------------------------|
| `DB_URL`               | JDBC connection string for MySQL                        |
| `DB_USERNAME`          | MySQL username                                          |
| `DB_PASSWORD`          | MySQL password                                          |
| `JWT_SECRET`           | Secret key used to sign JWT tokens                      |
| `JWT_EXPIRATION`       | Token expiration time in milliseconds (default: 1 hour) |
| `GITHUB_CLIENT_ID`     | GitHub OAuth App client ID                              |
| `GITHUB_CLIENT_SECRET` | GitHub OAuth App client secret                          |

> **GitHub OAuth Setup**: Create an OAuth App at [github.com/settings/developers](https://github.com/settings/developers) and set the **Authorization callback URL** to `http://localhost:9000/login/oauth2/code/github`.

#### 3. Docker Compose Environment

When running with Docker, the following are configured in `docker-compose.yml` and can be overridden:

| Variable                     | Service  | Default                          |
|------------------------------|----------|----------------------------------|
| `SPRING_DATASOURCE_URL`     | backend  | `jdbc:mysql://db:3306/LineaDesk` |
| `SPRING_DATASOURCE_USERNAME`| backend  | `amohdi_linea`                   |
| `SPRING_DATASOURCE_PASSWORD`| backend  | `linea_desk2026`                 |
| `CORS_ALLOWED_ORIGINS`      | backend  | `http://localhost:3000`          |
| `FRONTEND_BASE_URL`         | backend  | `http://localhost:3000`          |
| `GITHUB_CLIENT_ID`          | backend  | Read from root `.env`            |
| `GITHUB_CLIENT_SECRET`      | backend  | Read from root `.env`            |
| `REACT_APP_API_BASE`        | frontend | `http://localhost:9000`          |
| `MYSQL_DATABASE`            | db       | `LineaDesk`                      |
| `MYSQL_USER`                | db       | `amohdi_linea`                   |
| `MYSQL_PASSWORD`            | db       | `linea_desk2026`                 |

### Run with Make

```bash
make up          # Build and start all services
make down        # Stop all services
make logs        # Follow container logs
make rebuild     # Rebuild without cache
make clean       # Stop and remove volumes
make fclean      # Full clean: stop, remove volumes & images
make re          # Full restart (fclean + up)
make ps          # List running containers
```

### Run with Docker Compose

```bash
docker compose up -d --build
```

### Access the Application

| Service  | URL                    |
|----------|------------------------|
| Frontend | http://localhost:3000   |
| Backend  | http://localhost:9000   |
| Database | localhost:3307          |

---

## Tech Stack

### Frontend

| Technology         | Purpose                        |
|--------------------|--------------------------------|
| React 19 + TypeScript | UI framework                |
| React Router DOM 7 | Client-side routing            |
| Tailwind CSS 3     | Utility-first styling          |
| Axios              | HTTP client with interceptors  |
| @dnd-kit           | Drag-and-drop task reordering  |
| react-markdown     | Journal markdown rendering     |
| Material Symbols   | Icon set                       |

### Backend

| Technology         | Purpose                        |
|--------------------|--------------------------------|
| Spring Boot 4.1    | REST API framework             |
| Java 21            | Language runtime               |
| Spring Data JPA    | Database access & ORM          |
| Spring Security    | Authentication & authorization |
| JWT (jjwt)         | Token-based auth               |
| OAuth2 Client      | GitHub OAuth integration       |
| MySQL 8.0          | Relational database            |
| Maven              | Build & dependency management  |

### Infrastructure

| Technology         | Purpose                        |
|--------------------|--------------------------------|
| Docker Compose     | Multi-container orchestration  |
| Nginx              | Frontend static file serving   |
| MySQL (container)  | Persistent database            |

---

## Features

### Dashboard
- GitHub-style activity heatmap (52-week contribution graph)
- Current streak display
- Quick access to recent projects
- Daily focus card with task and habit progress

### Projects & Tasks
- Full CRUD for projects with states (Pending / In Progress / Finished)
- Task management with importance levels (Normal / Medium / Important / Crucial)
- Drag-and-drop task reordering
- Subtask support (parent-child task relationships)
- Bulk task operations (delete, state updates)
- Task filtering by state, importance, and search
- GitHub repository link integration (commits & PRs)
- Project invite system with shareable token links
- Multi-member collaboration and member management

### Focus Sessions (Pomodoro Timer)
- Customizable focus, short break, and long break durations
- Configurable sessions before long break
- Timer controls: start, pause, resume, complete early
- Visual progress bar
- Task list with in-session completion
- Session counter tracked per project

### Habit Tracking
- Create habits in three categories: Fitness, Mental Wellbeing, Intellectual
- Daily completion logging with 30-day calendar view
- Streak calculation and best streak display
- Color-coded habit types

### Journal & Notes
- Multiple journals with public/private visibility
- Multi-page journals with markdown editor
- Formatting toolbar (bold, italic, headers, code blocks, lists, links)
- Live preview toggle
- Auto-save with 1-second debounce
- Export pages as `.md` files
- Search and sort pages

### User Settings
- Profile management (username, email, avatar)
- Password change with validation
- Pomodoro timer customization
- Dark / Light theme toggle

---

## Project Structure

```
LineaDesk/
├── docker-compose.yml          # Service orchestration
├── Makefile                    # Dev shortcuts
├── architecture/               # Design docs & diagrams
│
├── backend/                    # Spring Boot API
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/linea_desk/   # Controllers, Services, Entities, Repos
│       │   └── resources/
│       │       └── application.properties
│       └── test/
│
├── frontend/                   # React SPA
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── package.json
│   └── src/
│       ├── api/                # API client, endpoints, types
│       ├── auth/               # AuthContext, RequireAuth guard
│       ├── components/         # Reusable UI components
│       └── pages/              # Route page components
│
└── dev_dashboard_design/       # UI design mockups
```

---

## API Endpoints

### Authentication
| Method | Endpoint                         | Description              |
|--------|----------------------------------|--------------------------|
| POST   | `/auth/login`                    | Email/password login     |
| POST   | `/auth/signup`                   | User registration        |
| GET    | `/oauth2/authorization/github`   | GitHub OAuth initiation  |

### User
| Method | Endpoint              | Description            |
|--------|-----------------------|------------------------|
| PUT    | `/api/user/profile`   | Update profile         |
| PUT    | `/api/user/password`  | Change password        |

### Projects
| Method | Endpoint                              | Description             |
|--------|---------------------------------------|-------------------------|
| POST   | `/api/project`                        | Create project          |
| GET    | `/api/projects`                       | List user projects      |
| GET    | `/api/project/{id}`                   | Get project details     |
| PUT    | `/api/project/{id}`                   | Update project          |
| DELETE | `/api/project/{id}`                   | Delete project          |
| POST   | `/api/project/{id}/invite`            | Generate invite token   |
| POST   | `/api/projects/join`                  | Join via invite token   |
| GET    | `/api/project/{id}/members`           | List project members    |
| DELETE | `/api/project/{id}/member/{userId}`   | Remove member           |

### Tasks
| Method | Endpoint                | Description                |
|--------|-------------------------|----------------------------|
| POST   | `/api/task`             | Create task                |
| GET    | `/api/task/{id}`        | Get task                   |
| PUT    | `/api/task/{id}`        | Update task                |
| DELETE | `/api/task/{id}`        | Delete task                |
| PUT    | `/api/tasks/reorder`    | Reorder tasks (batch)      |
| DELETE | `/api/tasks/bulk`       | Bulk delete tasks          |
| PUT    | `/api/tasks/bulk-state` | Bulk update task states     |

### Habits
| Method | Endpoint                 | Description                    |
|--------|--------------------------|--------------------------------|
| GET    | `/api/habits`            | List habits                    |
| POST   | `/api/habit`             | Create habit                   |
| PUT    | `/api/habit/{id}`        | Update habit                   |
| DELETE | `/api/habit/{id}`        | Delete habit                   |
| POST   | `/api/habit/{id}/log`    | Log habit completion for date  |
| DELETE | `/api/habit/{id}/log`    | Unlog habit for date           |
| GET    | `/api/habit/{id}/logs`   | Get logs for date range        |
| GET    | `/api/habits/logs`       | Get all logs for date range    |

### Journals & Pages
| Method | Endpoint          | Description          |
|--------|-------------------|----------------------|
| GET    | `/api/journals`   | List journals        |
| POST   | `/api/journal`    | Create journal       |
| POST   | `/api/page`       | Create page          |
| PUT    | `/api/page/{id}`  | Update page          |
| DELETE | `/api/page/{id}`  | Delete page          |

---

## Authentication

LineaDesk supports two authentication methods:

### Email & Password
1. Register or log in with credentials
2. Backend validates and issues a JWT token (1-hour expiry)
3. Token is stored in `localStorage` and attached to all API requests via an Axios interceptor
4. On `401` responses, the user is automatically logged out

### GitHub OAuth2
1. Click **Continue with GitHub** on the login page
2. Backend handles the OAuth2 flow via Spring Security
3. On success, redirects to `/oauth/callback` with token and user info
4. Frontend parses the callback params and establishes the session

---

## Architecture

### Data Model

Core entities and their relationships:

- **User** — aggregate root owning projects, habits, and journals
- **Project** — contains tasks; supports multiple members via invites
- **Task** — belongs to a project; supports subtasks (self-referencing parent)
- **Habit** — categorized as Fitness, Mental Wellbeing, or Intellectual; tracks daily logs
- **Journal** — contains multiple pages; supports public/private visibility
- **Page** — markdown content belonging to a journal

### Enums

| Enum                | Values                                 |
|---------------------|----------------------------------------|
| `PROJECT_STATE`     | PENDING, IN_PROGRESS, FINISHED         |
| `TASK_STATE`        | PENDING, IN_PROGRESS, FINISHED         |
| `TASK_IMPORTANCE`   | NORMAL, MEDIUM, IMPORTANT, CRUCIAL     |
| `HABIT_TYPE`        | FITNESS, MENTAL_WELLBEING, INTELLECTUAL|
| `JOURNAL_VISIBILITY`| PUBLIC, PRIVATE                        |

### Docker Services

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Frontend   │────▶│   Backend    │────▶│    MySQL     │
│  (Nginx:3000)│     │ (Spring:9000)│     │   (:3307)    │
└──────────────┘     └──────────────┘     └──────────────┘
         └────────────────┴─────────────────┘
                   lineadesk network
```
