# Data Model: Click Counts on Resolve

## Entities

### ShortCode (existing)

| Field | Rules |
|-------|--------|
| code | Non-blank Base62 token; unique while the process runs |
| destinationUrl | Valid http/https URL with a host |

Relationships: one ShortCode has exactly one ClickCount.

Created by successful `shorten()`. Unchanged by this feature except that creation also initializes ClickCount at 0.

### ClickCount (new)

| Field | Rules |
|-------|--------|
| code | Same key as ShortCode; must already exist |
| value | Integer ≥ 0; starts at 0; +1 per successful resolve only |

Validation:
- No ClickCount row without a ShortCode.
- Unknown code → no entity, lookup is not-found (not 0).
- Failed resolve does not mutate value.
- Count lookup does not mutate value.
- Process restart drops all ClickCount (and ShortCode) data.

## State transitions

```text
shorten(new URL)     → ClickCount.value = 0
shorten(same URL)    → no change to ClickCount
resolve(known)       → value := value + 1, return destination
resolve(unknown)     → no entity created, empty result
clickCount(known)    → return value (no transition)
clickCount(unknown)  → empty / not-found
```

## Concurrency

Increments for the same code must not be lost. Implementation: `AtomicLong.incrementAndGet()` (or equivalent atomic map update) on the existing counter only.
