The list endpoints return 10 records at a time. They use cursor based pagination with a `lastId` query parameter instead of page numbers.

This works for:

- `GET /api/v1/breeds?lastId={lastId}`
- `GET /api/v1/breeds/{breedId}/subbreeds?lastId={lastId}`

## How it works

Results are sorted by `id`. `lastId` tells the API where to continue from. It returns the next 10 records with an `id` greater than `lastId`.

1. Make the first call without `lastId` (it defaults to `0`).
2. Take the `id` of the last item in the response.
3. Pass it as `lastId` in the next call.
4. Repeat until you get fewer than 10 items, or an empty list. That means there is nothing more to load.

## Example

First page:

```
GET /api/v1/breeds
```

Say the last item in the response has `"id": 10`. Next page:

```
GET /api/v1/breeds?lastId=10
```

Same thing for sub-breeds:

```
GET /api/v1/breeds/1787/subbreeds?lastId=0
```

This is how the `Load More` button in the UI works.

## Why not page numbers

With page numbers (offset pagination) the database reads and skips all the rows before the page you asked for. The bigger the page number, the slower it gets.

With `lastId` the query goes directly to the next rows using the `id` index, so it stays fast even on large data.

The trade-off is that you can't jump to a random page, like page 7. You can only move forward from the last item you have.
