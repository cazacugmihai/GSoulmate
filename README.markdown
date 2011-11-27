GSoulmate
=========

This is a Groovy port of <a href="https://github.com/seatgeek/soulmate">Soulmate</a> project (a tool to help solve the common problem of developing a fast autocomplete feature - using <a href="http://redis.io/">Redis</a>).

### Loader:

    Soulmate.init()

    Soulmate.load('venue', [
            ["id": 1, "term":"Dodger Stadium",   "score":85, "data":["url":"/dodger-stadium-tickets/",  "subtitle":"AAA"]],
            ["id":28, "term":"Angel Stadium",    "score":85, "data":["url":"/angel-stadium-tickets/",   "subtitle":"BBB"]],
            ["id":30, "term":"Chase Field",      "score":85, "data":["url":"/chase-field-tickets/",     "subtitle":"CCC"]],
            ["id":29, "term":"Sun Life Stadium", "score":84, "data":["url":"/sun-life-stadium-tickets/","subtitle":"DDD"]],
            ["id": 2, "term":"Turner Field",     "score":83, "data":["url":"/turner-field-tickets/",    "subtitle":"EEE"]]
    ])

### Query:

    Soulmate.init()

    def json = Server.search([types: ['venue'], term: 'Field'])
    println JsonOutput.prettyPrint(json)

will prints:

    {
        "term": "Field",
        "results": {
            "venue": [
                {
                    "id": 30,
                    "term": "Chase Field",
                    "data": {
                        "subtitle": "CCC",
                        "url": "/chase-field-tickets/"
                    },
                    "score": 85
                },
                {
                    "id": 2,
                    "term": "Turner Field",
                    "data": {
                        "subtitle": "EEE",
                        "url": "/turner-field-tickets/"
                    },
                    "score": 83
                }
            ]
        }
    }