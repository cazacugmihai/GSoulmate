package com.macrobit.redis.gsoulmate

import groovy.json.JsonBuilder
import redis.clients.gedis.Gedis
import java.util.regex.Pattern

class Soulmate {

    public static int MIN_COMPLETE
    public static List STOP_WORDS
    public static def DELIMITERS
    public static Gedis gedis

    /**
     *
     * @param map - connection attributes:
     * <ul>
     *     <li>shardInfo</li>
     *     <li>host, port, timeout</li>
     *     <li>host, port</li>
     *     <li>host</li>
     * </ul>
     * @return
     */
    static void init(Map map = [host: 'localhost'],
                     int minComplete = 2,
                     List stopWords = [],
                     def delimiters = /\s|,/) {
        gedis = new Gedis(map)
        MIN_COMPLETE = minComplete
        STOP_WORDS = stopWords
        DELIMITERS = delimiters
    }

    static def createLoader(term) {
        new Loader(term)
    }

    static def load(term, items) {
        createLoader(term).load(items)
    }

    static def search(params) {
        def limit = (params.limit ?: 5) as int
        def types = params.types.collect { Helpers.normalize(it) }
        def term = params.term

        def results = [:]
        types.each {
            def matcher = new Matcher(it)
            results[it] = matcher.matches_for_term(term, [limit: limit])
        }

        new JsonBuilder([
                term: params.term,
                results: results
        ]).toString()
    }

}
