# Phase 2 SEO implementation

## Indexable pages

- `/{locale}`
- `/{locale}/movies`
- `/{locale}/genres`
- `/{locale}/genre/{slug}`
- `/{locale}/movie/{localizedSlug}`
- localized About, Contact, Privacy and Terms pages

## Noindex pages

- search results
- filtered catalog query combinations
- watch utility route
- admin/private pages
- error pages

## Localized movie pages

Each movie has EN/FR/AR translation records and a stable localized slug. Movie pages generate self-canonical URLs and alternate hreflang URLs for all available translations plus `x-default`.

## Structured data

- Home: Organization + WebSite
- Movie: Movie + BreadcrumbList
- Catalog and genre pages: ItemList

Structured data is generated from the same visible database content used to render the page.

## Production requirements

- Set `SITE_BASE_URL` to the final HTTPS domain.
- Replace demo image/content references with licensed production assets.
- Validate representative pages with search-engine structured-data tools after public deployment.
- Sitemap output is already split into `sitemap-pages.xml` and paged `sitemap-movies-N.xml` files behind `sitemap.xml`. Each movie sitemap is capped at 15,000 movies (up to 45,000 localized movie URLs).

## Slug changes

When an existing localized movie slug changes, the old slug is stored in `movie_slug_redirects`. Requests to the old published URL receive an HTTP 301 permanent redirect to the movie's current localized URL. Historical slugs are reserved and cannot be reused for another current URL.
