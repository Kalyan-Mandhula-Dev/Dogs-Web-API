# Dogs Web API

A full-stack breeds and sub-breeds management application built with Spring Boot, React, MySQL, and Docker. Users can import and perform CRUD operations on the breeds and sub-breeds.


## Live Demo

- **Live app:** [Dogs Web API](http://52.65.180.236:5173/)
- **API Docs (Swagger UI):** Browse and try out the endpoints: [Swagger UI](http://52.65.180.236:8080/swagger-ui/index.html)

> **Note:** List starts empty until you add breeds or import them from docs/dogs.json

## Features

- **CRUD operations:** Create, Read, Update and Delete breeds and sub-breeds.
- **Import:** Supports importing breeds (example: `docs/dogs.json`)
- **Pagination:** Cursor based pagination.
- **Server side validation:** Failures or errors are displayed in popups.
- **Optimistic locking:** Preventing conflicting edits when two or more users edit same item at once.
- **Health check:** Exposes actuator endpoints to view the health status (`/actuator/health`).

## Tech Stack

- **Backend:** Java, Spring Boot
- **Frontend:** React
- **Database:** MySQL, Flyway
- **DevOps:** Docker, Docker Compose, AWS EC2
- **Testing:** JUnit, Mockito, k6

## Architecture

A Spring Boot service with MySQL database and React with Vite for frontend.

| Service | Responsibility |
|---------|---------------|
| dogs-api | Breed and sub-breeds management |
| dogs-ui | User interface |

### Service Ports

| Service | Port | Publicly Accessible |
|---------|-----|----------------------|
| dogs-api | 8080 | Yes |
| dogs-ui | 5173 | Yes |


## How to Use

### UI Walkthrough
1. Open the live app, click `Import breeds` button and choose the json file and click `Import` to import the breeds.

   ![Importing breed guide](docs/images/Dog%20Breeds%20Import%20Guide.png)

2. Browse the breeds list using `Load More` button.

3. Add breeds using `Add breed` button, edit and delete the breeds using buttons in `Action` column.

   ![Breeds view after import](docs/images/Add%20Edit%20Delete%20Breeds.png)

4. Click on any breed name (example : `hound`) to view, add, edit and delete the sub-breeds.

   ![Sub breeds view](docs/images/Sub%20breeds%20page.png)

5. Any failures or errors are displayed in the browser popups.

### APIs

- **Base URL:** <http://52.65.180.236:8080/>
- **Full interactive docs:** [Swagger UI](http://52.65.180.236:8080/swagger-ui/index.html)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/v1/breeds | Create a breed |
| GET | /api/v1/breeds?lastId={lastId} | List breeds (Cursor based pagination) |
| PUT | /api/v1/breeds/{id} | Update a breed |
| DELETE | /api/v1/breeds/{id} | Delete a breed |
| POST | /api/v1/breeds/import | Import breeds |
| POST | /api/v1/breeds/{breedId}/subbreeds | Create a sub-breed |
| GET | /api/v1/breeds/{breedId}/subbreeds?lastId={lastId} | List sub-breeds (Cursor based pagination) |
| PUT | /api/v1/breeds/{breedId}/subbreeds/{id} | Update a sub-breed |
| DELETE | /api/v1/breeds/{breedId}/subbreeds/{id} | Delete a sub-breed |
| GET | /actuator/health | Application health check |


**Create a breed**

*Request*
``` json
POST /api/v1/breeds

{
  "name": "Corgi",
  "subBreeds": [
    "american", "european"
  ]
}
```

*Response*
``` json
{
  "createdAt": "2026-11-19T22:51:00.833234",
  "id": 1787,
  "modifiedAt": "2026-11-19T22:51:00.833234",
  "name": "Corgi",
  "subBreeds": [
    "american",
    "european"
  ],
  "version": 0
}
```

**Update a breed (Optimistic Locking)**

*Request*
``` json
PUT /api/v1/breeds/1787

{
  "name": "Labrador",
  "version": 0
}
```

Send the `version` you last read. If someone else updated the breed before your request did, the request is rejected with `409 Conflict` and you need to reload the latest data before retrying.


## Run Locally With Docker Compose

### Prerequisites

- Docker with Docker compose (Docker Desktop)
- Git
- Ports **5173** (UI), **8080** (API) and **3306** (MySQL) free on your machine

### Steps

1. Clone the repository:

   ``` bash
   git clone https://github.com/Kalyan-Mandhula-Dev/Dogs-Web-API.git
   cd Dogs-Web-API
   ```

2. Create the backend environment file. Containers read their settings from this files, because these values are not stored in `docker-compose.yaml`:

   ``` bash
   cp .env.example .env
   ```

   Update the values in `.env` (app host, database username, password and root password).


3. Create the UI environment file. Vite reads `VITE_*` values when the UI is built:

   ``` bash
   cp dogs-ui/.env.example dogs-ui/.env
   ```
   Set the API URL in `dogs-ui/.env`:

   ```
   Example: for local environment
   VITE_DOGS_API_URL=http://localhost:8080/api
   ```

    The React app runs in the browser, so the API URL must be an address the browser can reach (such as `http://localhost:8080/api`), not a Docker service name.

4. Build and start everthing:

   ``` bash
   docker compose up --build -d
   ```

   Multi-stage `Dockerfile` builds the jar not need to build jar explicitly.

5. Open the application at <http://localhost:5173>:

   The breeds list starts empty. Add the breeds or import from `dogs.json`.

To stop everything, run `docker compose down`.


## Configuration

Backend settings go in `.env` at the project root. The UI setting goes in `dogs-ui/.env`.

| Variable | Description |
|----------|-------------|
| `DATABASE_USERNAME` | Database user the app connects as |
| `DATABASE_PASSWORD` | Password for that user |
| `DATABASE_ROOT_PASSWORD` | MySQL root password, used by the database container |
| `DOGS_UI_URL` | UI address allowed to call the APIs e.g. `http://localhost:5173` |
| `VITE_DOGS_API_URL` | API address the browser calls (`dogs-ui/.env`), e.g. `http://localhost:8080/api` |


## Tests

### Unit Tests

The docker build skips the tests (`-DskipTests`) to keep the image build fast, so run them separately. They dont need Docker or running database.

``` bash
cd dogs-api
./mvnw test
```

Results are printed in the terminal.

### k6 Test (Optimistic Locking)

This test sends conflicting updates to the same breed to show that only one succeeds, which prevents data inconsistency.

**Prerequisites:** k6 installed and the app running locally. Don't run it against the live demo, because it edits real data. The script targets `http://localhost:8080/api/v1/breeds` (`PUT Request`) by default.


1. Create a breed or pick an existing one, and note its `id`, `name` and `version`.

2. Open `dogs-api/performance-tests/OptimisticLockTest.js` and replace the placeholders with those values:

   ```javascript
   const BODY = JSON.stringify({
     name: '<breed-name>',
     version: <breed-version>
   });

   const response = http.put(BASE_URL + '/<breed-id>', BODY, PARAMS);
   ```

3. Run the test:

   ```bash
   cd dogs-api/performance-tests
   k6 run OptimisticLockTest.js
   ```

Only one of the conflicting requests succeeds. The others fail with `409 Conflict`.

The successful request increments the breed's `version`, so update `version` in the script before running the test again.

## Project Structure

```text
Dogs-Web-API/
├── dogs-api/              # Spring Boot backend
│   ├── src/main/java/...  # controller, service, repository, entity, dto, exception, config
│   ├── src/main/resources # config and Flyway migrations
│   ├── src/test/          # JUnit and Mockito tests
│   └── performance-tests/ # k6 script
├── dogs-ui/               # React and Vite frontend
├── docker-compose.yaml
└── .env.example
```

## Deployment

The app is deployed on a single AWS EC2 instance using Docker Compose.

1. Launched an Ubuntu EC2 instance (t3.small) with a key pair for SSH access and a root volume large enough for Docker images and MySQL data.
2. Configured the security group to allow SSH (port 22), the UI (5173) and the API (8080).
3. Attached an Elastic IP so the address stays the same across stop and start.
4. Connected over SSH and installed Git and Docker.
5. Cloned the repository.
6. Created the production `.env` file on the server (root).
7. Started everything:

   ```bash
   docker compose up --build -d
   ```

### Redeploying

SSH into the instance, then:

```bash
cd Dogs-Web-API
git pull
docker compose up --build -d
```

Compose rebuilds only the changed images and recreates their containers. The database volume is kept, so data survives redeploys.

## Design Decisions

- **Separate breeds and sub-breeds tables (one-to-many):** Avoids repeating breed data on every sub-breed row, keeps updates simple.
- **Cursor-based pagination (`lastId`):** The query asks directly the next page data using the index instead of scanning them and skipping like offset pagination, so it stays fast on large data. The trade-off is no jumping to arbitrary page numbers.
- **Optimistic locking (`version` field):** Handles conflicting updates of the same record. The second update gets `409 Conflict` instead of overwriting the first.
- **All-or-nothing import:** The data in file is validated and saved in a single transaction, avoiding partial data saving.

## Limitations and Future Work

- **Filtering and search:** The breeds list cannot be filtered or searched any field yet.
- **Sorting:** Results are returned in a fixed order, with no sorting.
- **Authentication:** There is no login, anyone with the link can view and change data. JWT-based auth would be next step.