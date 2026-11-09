package com.mbr.devicedoctor.domain.reader

sealed interface ReaderResult<out T> {
    data class Ok<T>(val value: T) : ReaderResult<T>

    data class PermissionMissing(val permission: String) : ReaderResult<Nothing>

    data class NotFound(val what: String) : ReaderResult<Nothing>
}
