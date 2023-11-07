import java.security.MessageDigest
import java.time.LocalDateTime

/**
 * Calculates hash for given string
 * @param data String to calculate hash for
 * @return SHA1 hash of given string
 */
fun calcHash(data: String): String =
    MessageDigest.getInstance("SHA-1")
        .digest(data.toByteArray())
        .joinToString("") { "%02x".format(it) }

/**
 * Represents either left or right value
 * @param A Type of left value
 * @param B Type of right value
 */
sealed class Either<out A, out B> {
    data class Left<out A>(val a: A) : Either<A, Nothing>()
    data class Right<out B>(val b: B) : Either<Nothing, B>()
}

/**
 * Represents blob object
 * @constructor Creates blob object and calculates its hash
 * @param data Data of blob
 */
data class Blob(val data: String) {
    val hash = calcHash(data)
}

/**
 * Represents tree object
 * @constructor Creates tree object and calculates its hash
 * @param entries Entries of tree
 */
data class Tree(val entries: MutableMap<String, Either<Blob, Tree>> = mutableMapOf()) {
    val hash = calcHash(entries.toString())
}

/**
 * Represents commit object
 * @constructor Creates commit object, sets commit time and hash
 * @param tree Tree of data
 * @param author Commit author
 * @param message Commit message
 * @param time Timestamp
 * @property hash Hash of commit
 *
 */
data class Commit(val tree: Tree, val author: String, val message: String, val time: LocalDateTime = LocalDateTime.now()) {
    val hash = calcHash("$tree$author$message$time")
}

/**
 * Simple git implementation
 * @constructor Creates git object
 * @property commits List of commits
 * @property commitTree Tree of commits
 */
class Git {
    private val commits = mutableListOf<Commit>()
    /**
     * Unless tree is not specified in addFiles and commit methods,
     * this tree will be used
     */
    private var commitTree: Tree? = null

    /**
     * Adds files to commit tree
     * @param tree Tree to add files to
     * @param files Map of files to add
     */
    fun addFiles(tree: Tree? = null, files: Map<String, Either<Blob, Tree>>) {
        commitTree = tree ?: Tree()
        commitTree!!.entries.putAll(files)
    }

    /**
     * Commits changes
     * @param tree Tree to commit
     * @param author Author of commit
     * @param message Commit message
     * @return Commit object
     * @throws IllegalStateException if tree is empty
     */
    fun commit(tree: Tree? = null, author: String, message: String) {
        if (commitTree == null) commitTree = tree ?: Tree()

        if (commitTree!!.entries.isEmpty())
            throw IllegalStateException("Tree is empty, nothing to commit.")

        commits.add(Commit(commitTree!!, author, message))
        commitTree = null
    }

    /**
     * Lists all commits
     * @return List of commits
     */
    fun listCommits() = commits

    /**
     * Finds commit by hash
     * @param hash Hash of commit
     * @return Commit object or null if not found
     */
    fun findCommitByHash(hash: String) = commits.find { it.hash == hash }
}