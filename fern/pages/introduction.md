Dogs Web API lets you manage dog breeds and their sub-breeds. You can create, read, update and delete breeds and sub-breeds, and import a list of breeds from a JSON file.

## Base URL

All the API endpoints are under:

```
https://dogsweb.duckdns.org/api/v1
```

## Requests and responses

- Requests and responses use JSON. Send `Content-Type: application/json` when you send a body.
- The only exception is the import endpoint, which takes a file upload (`multipart/form-data`).
- There is no user login. Anyone with the URL can call the APIs.

## Resources

| Resource | Path |
|----------|------|
| Breeds | `/api/v1/breeds` |
| Sub-breeds | `/api/v1/breeds/{breedId}/subbreeds` |

A sub-breed always belongs to one breed, so sub-breed endpoints have the breed id in the path.

Every breed and sub-breed has an `id` and a `version`. You need the `id` to work with a record and the `version` to update or delete it (see [Concurrency](./concurrency.md)).

## Health check

To check if the API is up: [https://dogsweb.duckdns.org/actuator/health](https://dogsweb.duckdns.org/actuator/health)