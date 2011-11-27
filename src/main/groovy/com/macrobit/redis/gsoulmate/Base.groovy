package com.macrobit.redis.gsoulmate

class Base {

    String term

    Base(term) {
        this.term = Helpers.normalize(term)
    }

    def getBase() {
        "soulmate-index:${term}"
    }

    def getDatabase() {
        "soulmate-data:${term}"
    }

    def getCachebase() {
        "soulmate-cache:${term}"
    }

}
