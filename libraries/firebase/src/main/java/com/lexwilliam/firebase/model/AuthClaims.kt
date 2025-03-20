package com.lexwilliam.firebase.model

enum class AuthClaims(val path: String) {
    BRANCH_UUID("branch_uuid"),
    USER_UUID("user_uuid"),
    ROLE("role")
}