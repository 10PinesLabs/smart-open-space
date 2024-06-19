package com.sos.smartopenspace.translators


interface TranslatorFrom<A, B> {
    fun translateFrom(domain: A): B
    fun translateAllFrom(domainLs: List<A>): List<B> = domainLs.map { translateFrom(it) }
}

interface TranslatorTo<A, B> {
    fun translateTo(dto: B): A
    fun translateAllTo(dtoLs: List<B>): List<A> = dtoLs.map { translateTo(it) }
}

interface Translator<A, B> : TranslatorFrom<A, B>, TranslatorTo<A, B>