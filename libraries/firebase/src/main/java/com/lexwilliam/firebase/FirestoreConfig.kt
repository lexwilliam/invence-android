package com.lexwilliam.firebase

object FirestoreConfig {
    const val COLLECTION_BRANCH = "branch"
    const val COLLECTION_PRODUCT_CATEGORY = "product_category"
    const val COLLECTION_TRANSACTION = "transaction"
    const val COLLECTION_USER = "user"
    const val COLLECTION_SUPPLIER = "supplier"
    const val COLLECTION_COMPANY = "company"

    object Field {
        const val BRANCH_UUID = "branch_uuid"
        const val DELETED_AT = "deleted_at"
    }
}