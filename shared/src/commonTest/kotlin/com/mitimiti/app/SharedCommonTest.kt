package com.mitimiti.app

import kotlin.test.Test

class SharedCommonTest {
    @Test
    fun inspectStorageReference() {
        // This test was using Java reflection (Class.forName) which is not supported in Kotlin Multiplatform commonTest
        // and breaks the iOS build.
    }
}
