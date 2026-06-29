package com.mitimiti.app

import kotlin.test.Test

class SharedCommonTest {
    @Test
    fun inspectStorageReference() {
        println("=== StorageReference MEMBERS ===")
        try {
            val clazz = Class.forName("dev.gitlive.firebase.storage.StorageReference")
            for (method in clazz.declaredMethods) {
                val params = method.parameterTypes.joinToString { it.name }
                println("Method: ${method.name}($params) returns ${method.returnType.name}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        println("=== Data MEMBERS ===")
        try {
            val clazz = Class.forName("dev.gitlive.firebase.storage.Data")
            for (method in clazz.declaredMethods) {
                val params = method.parameterTypes.joinToString { it.name }
                println("Method: ${method.name}($params) returns ${method.returnType.name}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
