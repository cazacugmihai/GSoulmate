package com.macrobit.redis.gsoulmate

import static com.macrobit.redis.gsoulmate.Soulmate.MIN_COMPLETE
import static com.macrobit.redis.gsoulmate.Soulmate.DELIMITERS

class Helpers {

    static def prefixes_for_phrase(phrase) {
        normalize(phrase).split(DELIMITERS).collect { w ->
            (MIN_COMPLETE - 1..w.size() - 1).collect {w[0..it]}
        }.flatten().unique()
    }

    static def normalize(str) {
        str.toLowerCase().replaceAll('/[^a-z0-9 ]/i', '').trim()
    }
    
}

