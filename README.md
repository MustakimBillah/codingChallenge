# codingChallenge

This project is about fetching 20 data from 2 different source (10 each) and expose a combined result from rest controller.

Sources are:
- [webcomic XKCD](https://xkcd.com/json.html)
- [RSS feed for PDL](http://feeds.feedburner.com/PoorlyDrawnLines)


## Docker Commands

First we need to create docker image from project directory

```sh
docker build -f Dockerfile -t coding-challenge .
```

then we have to do docker compose

```sh
docker-compose up
```
