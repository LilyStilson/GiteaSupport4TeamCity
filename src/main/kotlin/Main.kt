import java.security.MessageDigest
import java.time.LocalDateTime

fun calcHash(data: String): String =
    MessageDigest.getInstance("SHA-1")
        .digest(data.toByteArray())
        .joinToString("") { "%02x".format(it) }


sealed class Either<out A, out B> {
    data class Left<out A>(val a: A) : Either<A, Nothing>()
    data class Right<out B>(val b: B) : Either<Nothing, B>()
}


data class Blob(val data: String) {
    val hash = calcHash(data);
}

data class Tree(val entries: MutableMap<String, Either<Blob, Tree>> = mutableMapOf()) {
    val hash = calcHash(entries.toString())
}

data class Commit(val tree: Tree, val author: String, val message: String, val time: LocalDateTime = LocalDateTime.now()) {
    val hash = calcHash("$tree$author$message$time")
}

class Git {
    private val commits = mutableListOf<Commit>()

    fun commit(tree: Tree, author: String, message: String) {
        commits.add(Commit(tree, author, message))
    }

    fun listCommits() = commits

    fun findCommitByHash(hash: String) = commits.find { it.hash == hash }
}

fun main(args: Array<String>) { }