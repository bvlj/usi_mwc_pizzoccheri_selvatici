package ch.usi.inf.mwc.cusi.utils

abstract class SingletonHolder<out T, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator

    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A) =
        instance ?: synchronized(this) {
            instance ?: creator!!(arg).also { instance = it }
        }
}
