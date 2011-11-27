package com.macrobit.redis.gsoulmate

import groovy.json.JsonOutput

Soulmate.init()

Soulmate.load('venue', [
        ["id": 1, "term":"Dodger Stadium",   "score":85, "data":["url":"/dodger-stadium-tickets/",  "subtitle":"AAA"]],
        ["id":28, "term":"Angel Stadium",    "score":85, "data":["url":"/angel-stadium-tickets/",   "subtitle":"BBB"]],
        ["id":30, "term":"Chase Field",      "score":85, "data":["url":"/chase-field-tickets/",     "subtitle":"CCC"]],
        ["id":29, "term":"Sun Life Stadium", "score":84, "data":["url":"/sun-life-stadium-tickets/","subtitle":"DDD"]],
        ["id": 2, "term":"Turner Field",     "score":83, "data":["url":"/turner-field-tickets/",    "subtitle":"EEE"]]
])

def json = Soulmate.search([types: ['venue'], term: 'Field'])
//println new JsonSlurper().parseText(json)
println JsonOutput.prettyPrint(json)