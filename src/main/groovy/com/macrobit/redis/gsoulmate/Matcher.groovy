package com.macrobit.redis.gsoulmate

import groovy.json.JsonSlurper
import groovy.transform.InheritConstructors
import static com.macrobit.redis.gsoulmate.Soulmate.*

@InheritConstructors
class Matcher extends Base {

    def matches_for_term(term, options = [:]) {
        options = [limit: 5, cache: true].plus(options)

        def words = Helpers.normalize(term).split(DELIMITERS).findAll {
            it.size() >= MIN_COMPLETE && !STOP_WORDS.contains(it)
        }.sort()

        if (!words) return []

        def cachekey = "${cachebase}:" + words.join('|') as String

        if (!options.cache || !Soulmate.gedis.exists(cachekey)) {
            def interkeys = words.collect { "${base}:${it}" }
            Soulmate.gedis.zinterstore(cachekey, interkeys as String[])
            Soulmate.gedis.expire(cachekey, 10 * 60) // expire after 10 minutes
        }

        def ids = Soulmate.gedis.zrevrange(cachekey, 0, options.limit - 1)
        if (ids) {
            def results = Soulmate.gedis.hmget(database, ids as String[])
            results = results.findAll { it } // handle cached results for ids which have since been deleted
            def json = new JsonSlurper()
            results.collect { json.parseText(it) }
        } else {
            []
        }
    }

}
