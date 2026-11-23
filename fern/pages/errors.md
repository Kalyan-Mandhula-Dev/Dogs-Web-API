When a request fails, the API returns an HTTP error status and a JSON body in the same format for every error.

## Error format

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "The requested breed does not exist, please refresh and try again",
  "timestamp": "2026-11-23T09:50:21.402Z",
  "requestId": "a1b2c3d4"
}
```

| Field | Description |
|-------|-------------|
| `status` | HTTP status code |
| `error` | Name of the HTTP status |
| `message` | What went wrong |
| `timestamp` | When the error happened |
| `requestId` | Id of this request, used to find it in the logs |

## Status codes

| Status | When |
|--------|------|
| `400 Bad Request` | Validation failed (example: `name: must not be blank`), the body or a path value is invalid, duplicate sub-breeds in the same request, invalid import file, or file too large |
| `404 Not Found` | The breed or sub-breed doesn't exist, or the sub-breed doesn't belong to the breed in the path |
| `409 Conflict` | The breed or sub-breed already exists, or the `version` is old |
| `500 Internal Server Error` | Something unexpected failed on the server|


## Request id

Every response has an `X-Request-Id` header, and error responses also have the same value in `requestId`.

The server writes this id in its logs for every line of that request. If something goes wrong and you need help, share the `requestId` so the request can be found in the logs quickly.
