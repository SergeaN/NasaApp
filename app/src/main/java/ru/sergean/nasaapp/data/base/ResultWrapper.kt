package ru.sergean.nasaapp.data.base

sealed interface ResultWrapper<S> {
    data class Success<S>(val data: S) : ResultWrapper<S>
    data class Failure<S>(val message: String) : ResultWrapper<S>
}