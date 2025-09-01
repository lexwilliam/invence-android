package com.lexwilliam.firebase.utils

object FirestoreConfig {
    const val COLLECTION_BRANCH = "branch"
    const val COLLECTION_PRODUCT_CATEGORY = "product_category"
    const val COLLECTION_TRANSACTION = "transaction"
    const val COLLECTION_USER = "user"
    const val COLLECTION_COMPANY = "company"
    const val COLLECTION_LOG = "log"
    const val COLLECTION_ORDER = "order"
    const val COLLECTION_EMPLOYEE_SHIFT = "employee_shift"

    object Field {
        const val USER_UUID = "user_uuid"
        const val CREATED_AT = "created_at"
        const val DELETED_AT = "deleted_at"
    }
}