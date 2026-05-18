package com.lemonpie.mapping

interface Dao<M> {
    fun toModel(): M
}