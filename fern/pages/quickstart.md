This page shows how to create a breed and then list the breeds. Each example shows the request and the response body. You can try them with the "Try it" button in the API Reference.

The list starts empty until someone adds breeds, so we create one first.

## 1. Create a breed

Send the breed `name`. `subBreeds` is optional.

*Request*
```json
POST /api/v1/breeds

{
  "name": "corgi",
  "subBreeds": ["cardigan", "pembroke"]
}
```

*Response* (`201 Created`)
```json
{
  "id": 1787,
  "name": "corgi",
  "version": 0,
  "subBreeds": ["cardigan", "pembroke"],
  "createdAt": "2026-11-23T09:40:12.533234Z",
  "modifiedAt": "2026-11-23T09:40:12.533234Z"
}
```

Note the `id` and `version`. You need them to update or delete this breed later.

Rules for the request:

- `name` is required, can't be blank and can be up to 100 characters.
- Breed names must be unique. Creating a breed that already exists returns `409 Conflict`.
- Sub-breed names in the same request must be different. Duplicates return `400 Bad Request`.

## 2. List breeds

*Request*
```
GET /api/v1/breeds
```

*Response* (`200 OK`)
```json
[
  {
    "id": 1787,
    "name": "corgi",
    "version": 0,
    "subBreeds": ["cardigan", "pembroke"],
    "createdAt": "2026-11-23T09:40:12Z",
    "modifiedAt": "2026-11-23T09:40:12Z"
  }
]
```

You get up to 10 breeds at a time. To get more, see [Pagination](./pagination.md).

## What next

- Add a sub-breed to an existing breed with `POST /api/v1/breeds/{breedId}/subbreeds`.
- Update a breed with `PUT /api/v1/breeds/{id}`, read [Concurrency](./concurrency.md).
- See every endpoint in the API Reference.
