package com.minhdtm.example.weapose.base

import kotlin.reflect.full.functions
import kotlin.reflect.jvm.isAccessible

fun Any.callPrivate(methodName: String, vararg args: Any?): Any? {
    val privateMethod = this::class.functions.firstOrNull { t ->
        t.name == methodName
    }

    val argList = args.toMutableList()
    (argList as ArrayList).add(0, this)
    val argArr = argList.toArray()

    if (privateMethod != null) {
        privateMethod.isAccessible = true
        return privateMethod.call(*argArr)
    } else {
        throw NoSuchMethodException("Method $methodName does not exist in ${this::class.qualifiedName}")
    }
}
