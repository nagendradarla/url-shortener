# HTTP contract: click counts

Base: same process as existing `POST /shorten` and `GET /{code}`.
Bodies are UTF-8 plain text unless noted.

## GET /stats/{code}

Returns the current click count for a known short code. Does **not** increment the count and does **not** redirect.

### Success

- Status: `200`
- Body: decimal integer, e.g. `0` or `3`
- `{code}`: existing short code

### Not found

- Status: `404`
- Body: `Not found`
- When `{code}` is unknown, blank, or missing

### Method

- `GET` only. Other methods: `405 Method Not Allowed`

## GET /{code} (existing, behavior addendum)

Successful resolve (`302` + `Location`) **increments** that code’s click count by 1.
Unknown code remains `404 Not found` and does **not** create or increment a count.

## POST /shorten (unchanged)

Still returns the short code. First successful shorten initializes count to 0. Repeat shorten of the same URL does not reset the count.

## Examples

```text
POST /shorten
https://example.com/page
→ 200  aB3

GET /stats/aB3
→ 200  0

GET /aB3
→ 302  Location: https://example.com/page

GET /stats/aB3
→ 200  1

GET /stats/doesNotExist
→ 404  Not found
```
