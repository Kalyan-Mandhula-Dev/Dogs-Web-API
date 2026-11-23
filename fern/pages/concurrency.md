Two or more users can try to edit the same breed or sub-breed at the same time. To stop one user silently overwriting another user's change, the API uses optimistic locking with a `version` field.

## The version field

Every breed and sub-breed has a `version`. It starts at `0` and goes up by one each time the record is updated.

You must send the `version` you last read when you:

- Update: `PUT /api/v1/breeds/{id}` and `PUT /api/v1/breeds/{breedId}/subbreeds/{id}` (in the request body)
- Delete: `DELETE /api/v1/breeds/{id}?version={version}` and `DELETE /api/v1/breeds/{breedId}/subbreeds/{id}?version={version}` (as a query parameter)

The API compares your `version` with the one in the database. If they match, the change is saved. If they don't match, someone else changed the record after you read it, and your request is rejected with `409 Conflict`.

## Example

Get the breed and note its `version`:

*Request*
```
GET /api/v1/breeds/1787
```

*Response* (`200 OK`)
```json
{
  "id": 1787,
  "name": "corgi",
  "version": 0,
  ...
}
```

Update it with that `version`:

*Request*
```json
PUT /api/v1/breeds/1787

{
  "name": "welsh corgi",
  "version": 0
}
```

This succeeds and the response has `"version": 1`.

If someone else sends an update with `"version": 0` after that, they get:

*Response* (`409 Conflict`)
```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Breed was modified by another user, please refresh and try again",
  "timestamp": "2026-11-23T09:45:03.112Z",
  "requestId": "3f9a1c2e"
}
```

Same applies for sub-breeds.

## What to do on 409

1. Get the record again to read the latest data and `version`.
2. Check if your change still makes sense with the new data.
3. Send the request again with the new `version`.


The same applies to delete. If the `version` query parameter on a delete is old, the delete is rejected with `409 Conflict` and nothing is removed.
