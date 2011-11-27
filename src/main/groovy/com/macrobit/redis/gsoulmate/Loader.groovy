package com.macrobit.redis.gsoulmate

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.transform.InheritConstructors

@InheritConstructors
class Loader extends Base {

    /**
     * Mandatory keys:
     * <ul>
     *     <li>id</li>
     *     <li>term</li>
     *     <li>score</li>
     * </ul>
     *
     * Optional keys:
     * <ul>
     *     <li>aliases</li>
     *     <li>data</li>
     * </ul>
     *
     * @param items a map
     * @return
     */
    def load(items) {
        // delete the sorted sets for this type
        def phrases = Soulmate.gedis.smembers(base)
        Soulmate.gedis.withPipeline {
            phrases.each {
                del("${base}:${it}")
            }
        }

        // Redis can continue serving cached requests for this type while the reload is
        // occurring. Some requests may be cached incorrectly as empty set (for requests
        // which come in after the above delete, but before the loading completes). But
        // everything will work itself out as soon as the cache expires again.

        // delete the data stored for this type
        Soulmate.gedis.del(database)

        items.each {
            add(it, [skip_duplicate_check: true])
        }
    }

    // 'id', 'term', 'score', 'aliases', 'data'
    def add(item, opts = [:]) {
        opts = [skip_duplicate_check: false].plus(opts)

        if (!item.id || !item.term) throw new IllegalArgumentException()

        // kill any old items with this id
        if (!opts.skip_duplicate_check) {
            remove('id': item.id)
        }

        Soulmate.gedis.withPipeline {
            // store the raw data in a separate key to reduce memory usage
            hset(database, item.id as String, new JsonBuilder(item).toString())
            def phrase = ([item.term] + (item.aliases ?: [])).join(' ')
            Helpers.prefixes_for_phrase(phrase).each {
                sadd(base, it) // remember this prefix in a master set
                zadd("${base}:${it}" as String, item.score, item.id as String) // store the id of this term in the index
            }
        }
    }

    // remove only cares about an item's id, but for consistency takes an object
    def remove(item) {
        def prev_item = Soulmate.gedis.hget(database, item.id)
        if (prev_item) {
            prev_item = new JsonSlurper().parseText(prev_item)
            // undo the operations done in add
            Soulmate.gedis.withPipeline {
                hdel(database, prev_item.id)
                def phrase = ([prev_item.term] + (prev_item.aliases ?: [])).join(' ')
                Helpers.prefixes_for_phrase(phrase).each {
                    srem(base, it)
                    zrem("${base}:${it}", prev_item.id)
                }
            }
        }
    }

}
