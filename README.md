# SoundCloud Downloader
An android application to download tracks/playlists from SoundCloud.

### As on 31 Aug 2018
```
7200+ users, 1,38,000+ tracks downloaded
```

### Demo

[![IMAGE](https://raw.githubusercontent.com/theapache64/SoundCloud-Downloader/master/youtube.png)](https://www.youtube.com/watch?v=qv0OWufJOoU)

### Download

You can download the latest stable APK from [here](https://github.com/theapache64/SoundCloud-Downloader/releases)

#### TODOs

- Nothing for now. 
- Algorithm walk through.

#### Sad part :(

- 2 times suspended and 1 time rejected from Google PlayStore.

#### Statistics Queries

- Installation per date

```sql

SELECT
  DATE(u.created_at) AS date,
  COUNT(u.id) AS total_users_joined
FROM
  users u
GROUP BY
  DATE(u.created_at)
ORDER BY
  date DESC;
```

- User stats

```sql
SELECT
  u.id,
  u.name,
  u.email,
  u.imei,
  COUNT(DISTINCT r.id) AS total_requests,
  COUNT(DISTINCT dr.id) AS total_downloads,
  COUNT(DISTINCT t.id) AS total_tracks,
  u.is_active,
  (
  SELECT
    soundcloud_url
  FROM
    requests
  WHERE
    user_id = u.id
  ORDER BY
    id DESC
  LIMIT 1
) AS last_hit
FROM
  users u
LEFT JOIN
  requests r ON r.user_id = u.id
LEFT JOIN
  download_requests dr ON dr.request_id = r.id
LEFT JOIN
  tracks t ON t.request_id = r.id
GROUP BY
  u.id
ORDER BY
  total_requests DESC;

```

#### Bugs?

- Found one? shoot a mail to theapache64@gmail.com or create a repo issue.
 
